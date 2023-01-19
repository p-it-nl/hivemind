/**
 * Copyright (c) p-it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hivemind.synchronizer;

import io.hivemind.configuration.SynchronizerConfiguration;
import io.hivemind.constant.ContentType;
import io.hivemind.data.PreparedData;
import io.hivemind.helper.RequestHelper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synchronization task that is used to communicate with the Hivemind
 *
 * @author Patrick-4488
 */
public class SynchronizeTask implements Runnable {

    private String traceparent;
    private PreparedData dataToSend;
    private boolean isDataRequest;

    private final HttpClient client;
    private final EssenceDataProvider essenceDataProvider;
    private final SynchronizerConfiguration config;

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeTask.class);

    public SynchronizeTask(final HiveEssenceDataProvider essenceDataProvider, final SynchronizerConfiguration config) {
        this.client = HttpClient.newHttpClient();
        this.essenceDataProvider = essenceDataProvider;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting synchronization");

            // FUTURE_WORK: If consistency model is > eventual -> send data request immediatly
            HttpRequest request;
            if (dataToSend != null && dataToSend.hasData()) {
                isDataRequest = true;
                request = buildDataRequest();
            } else {
                isDataRequest = false;
                request = buildEssenceRequest(essenceDataProvider.determineEssence());
            }
            HttpResponse response = client.send(request, BodyHandlers.ofByteArray());
            processResponse(response);

            LOGGER.info("Synchronization finished");
        } catch (IOException | InterruptedException ex) {
            LOGGER.info("Synchronization task failed", ex);
        }
    }

    private HttpRequest buildEssenceRequest(final byte[] bytes) {
        HttpRequest.Builder buidler = HttpRequest.newBuilder();
        buidler.uri(URI.create(config.getUri()))
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .header(RequestHelper.KEY_CONTENT_TYPE, ContentType.HIVE_ESSENCE.getValue());
        if (traceparent != null && !traceparent.isEmpty()) {
            buidler.header(RequestHelper.KEY_TRACEPARENT, traceparent);
        }

        return buidler.build();
    }

    private HttpRequest buildDataRequest() {
        HttpRequest.Builder buidler = HttpRequest.newBuilder();
        buidler.uri(URI.create(config.getUri()))
                .POST(HttpRequest.BodyPublishers.ofByteArray(dataToSend.getData()))
                .header(RequestHelper.KEY_CONTENT_TYPE, ContentType.OTHER.getValue());
        if (traceparent != null && !traceparent.isEmpty()) {
            buidler.header(RequestHelper.KEY_TRACEPARENT, traceparent);
        }

        return buidler.build();
    }

    private void processResponse(final HttpResponse response) {
        if (traceparent == null || traceparent.isEmpty()) {
            updateTraceparent(response);
        }

        if (isSuccessful(response)) {
            switch (response.statusCode()) {
                case 204 ->
                    LOGGER.info("Synchronization task succeeded, application is up to date");
                case 200 -> {
                    ContentType contentType = getContentType(response);
                    if (ContentType.HIVE_ESSENCE == contentType) {
                        LOGGER.info("Synchronization task succeeded, application received data request");

                        byte[] respondingEssence = (byte[]) response.body();
                        if (respondingEssence != null && respondingEssence.length > 0) {
                            dataToSend = new PreparedData(essenceDataProvider.getDataForEssence(respondingEssence));
                        }
                    } else if (ContentType.OTHER == contentType) {
                        LOGGER.info("Synchronization task succeeded, application received data");

                        essenceDataProvider.saveData((byte[]) response.body());
                    }
                }
                case 409 -> {
                    LOGGER.info("Synchronization task succeeded, application received priority request");
                    essenceDataProvider.processPriorityEssence((byte[]) response.body());
                }
            }

            if (isDataRequest) {
                dataToSend.close();
            }
        } else {
            LOGGER.error("""
                Unexpected response code received from Hivemind, this points to 
                an error that needs to be looked into. Synchronization will continue 
                trying to synchronize (and as such attempt to solve the error)""");
        }
    }

    private void updateTraceparent(final HttpResponse response) {
        List<String> traceparentValues = response.headers().allValues(RequestHelper.KEY_TRACEPARENT);
        if (!traceparentValues.isEmpty()) {
            traceparent = traceparentValues.get(traceparentValues.size() - 1);
        } else {
            LOGGER.warn("""
                Response from Hivemind received that did not have a traceparent 
                set, this is an issue worth looking into""");
        }
    }

    private ContentType getContentType(final HttpResponse response) {
        List<String> contentTypeValues = response.headers().allValues(RequestHelper.KEY_CONTENT_TYPE);
        if (!contentTypeValues.isEmpty()) {
            return ContentType.enumFor(contentTypeValues.get(contentTypeValues.size() - 1));
        } else {
            LOGGER.warn("""
                Response from Hivemind received that did not have a content-type 
                set, this is an issue worth looking into""");
        }

        return null;
    }

    private boolean isSuccessful(final HttpResponse response) {
        int code = response.statusCode();
        return code == 200 || code == 204 || code == 409;
    }
}
