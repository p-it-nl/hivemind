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

import io.hivemind.synchronizer.configuration.SynchronizerConfiguration;
import io.hivemind.synchronizer.constant.ContentType;
import io.hivemind.synchronizer.data.PreparedData;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Synchronization task that is used to communicate with the Hivemind
 *
 * @author Patrick-4488
 */
public class SynchronizeTask implements Runnable {

    private String traceparent;
    private PreparedData dataToSend;
    private ContentType contentTypeOfDataToSend;
    private boolean isDataRequest;

    private final HttpClient client;
    private final EssenceDataProvider essenceDataProvider;
    private final SynchronizerConfiguration config;

    private static final String KEY_TRACEPARENT = "traceparent";
    private static final String KEY_CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_SEPARATOR = ", ";

    private static final System.Logger LOGGER = System.getLogger(SynchronizeTask.class.getName());

    public SynchronizeTask(final HiveEssenceDataProvider essenceDataProvider, final SynchronizerConfiguration config) {
        this.client = HttpClient.newHttpClient();
        this.essenceDataProvider = essenceDataProvider;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            LOGGER.log(INFO, "Starting synchronization");

            // FUTURE_WORK: If consistency model is > eventual -> send data request immediatly
            HttpRequest request;
            if (dataToSend != null && dataToSend.hasData()) {
                isDataRequest = true;
                request = buildDataRequest();
            } else {
                isDataRequest = false;
                request = buildEssenceRequest(essenceDataProvider.determineEssence());
            }
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            processResponse(response);

            LOGGER.log(INFO, "Synchronization finished");
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.log(INFO, "Synchronization task failed", ex);
        }
    }

    private HttpRequest buildDataRequest() {
        HttpRequest.Builder buidler = HttpRequest.newBuilder();
        buidler.uri(URI.create(config.getUri()))
                .POST(HttpRequest.BodyPublishers.ofByteArray(dataToSend.getData()))
                .header(KEY_CONTENT_TYPE, (contentTypeOfDataToSend != null
                        ? contentTypeOfDataToSend.getValue() : ContentType.SERIALIZED.getValue()));
        if (traceparent != null && !traceparent.isEmpty()) {
            buidler.header(KEY_TRACEPARENT, traceparent);
        }

        return buidler.build();
    }

    private HttpRequest buildEssenceRequest(final byte[] bytes) {
        HttpRequest.Builder buidler = HttpRequest.newBuilder();
        buidler.uri(URI.create(config.getUri()))
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .header(KEY_CONTENT_TYPE, determineEssenceRequestContentType());
        if (traceparent != null && !traceparent.isEmpty()) {
            buidler.header(KEY_TRACEPARENT, traceparent);
        }

        return buidler.build();
    }

    private String determineEssenceRequestContentType() {
        StringBuilder sb = new StringBuilder();
        sb.append(ContentType.HIVE_ESSENCE.getValue());

        ContentType configured = config.getContentType();
        if (configured != null && ContentType.SERIALIZED != configured) {
            sb.append(CONTENT_TYPE_SEPARATOR);
            sb.append(configured.getValue());
        }

        return sb.toString();
    }

    // FUTURE_WORK: Now reading all bytes to the heap, better in some cases to buffer
    private void processResponse(final HttpResponse<byte[]> response) {
        if (traceparent == null || traceparent.isEmpty()) {
            updateTraceparent(response);
        }

        if (isSuccessful(response)) {
            readResponse(response);
        } else {
            LOGGER.log(ERROR, """
                Unexpected response code received from Hivemind, this points to 
                an error that needs to be looked into. Synchronization will continue 
                trying to synchronize (and as such attempt to solve the error)""");
        }
    }

    private void updateTraceparent(final HttpResponse<byte[]> response) {
        List<String> traceparentValues = response.headers().allValues(KEY_TRACEPARENT);
        if (!traceparentValues.isEmpty()) {
            traceparent = traceparentValues.get(traceparentValues.size() - 1);
        } else {
            LOGGER.log(WARNING, """
                Response from Hivemind received that did not have a traceparent 
                set, this is an issue worth looking into""");
        }
    }

    private boolean isSuccessful(final HttpResponse<byte[]> response) {
        int code = response.statusCode();
        return code == 200 || code == 204 || code == 409;
    }

    private void readResponse(final HttpResponse<byte[]> response) {
        switch (response.statusCode()) {
            case 204 ->
                LOGGER.log(INFO, "Synchronization task succeeded, application is up to date");
            case 200 -> {
                ContentType contentType = getContentType(response);
                if (ContentType.HIVE_ESSENCE == contentType) {
                    LOGGER.log(INFO, "Synchronization task succeeded, application received data request");

                    byte[] respondingEssence = response.body();
                    if (respondingEssence != null && respondingEssence.length > 0) {
                        dataToSend = new PreparedData(essenceDataProvider.getDataForEssence(respondingEssence));
                    }
                } else {
                    LOGGER.log(INFO, "Synchronization task succeeded, application received data");

                    essenceDataProvider.saveData(response.body(), contentType);
                }
            }
            case 409 -> {
                LOGGER.log(INFO, "Synchronization task succeeded, application received priority request");
                essenceDataProvider.processPriorityEssence(response.body());
            }
            default -> {
                LOGGER.log(ERROR, "Unexpected response code received from Hivemind, this points to inproper code since the status code has been deemed succesfull");
            }
        }

        if (isDataRequest) {
            dataToSend.close();
        }
    }

    private ContentType getContentType(final HttpResponse<byte[]> response) {
        List<String> contentTypeValues = response.headers().allValues(KEY_CONTENT_TYPE);
        if (!contentTypeValues.isEmpty()) {
            return ContentType.enumFor(contentTypeValues.get(contentTypeValues.size() - 1));
        } else {
            LOGGER.log(WARNING, """
                Response from Hivemind received that did not have a content type 
                set, this is an issue worth looking into""");
        }

        return null;
    }
}
