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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private ContentType contentTypeOfDataToSend;
    private boolean isDataRequest;

    private final EssenceDataProvider essenceDataProvider;
    private final SynchronizerConfiguration config;

    private static final String KEY_TRACEPARENT = "traceparent";
    private static final String KEY_CONTENT_TYPE = "content-type";
    private static final String POST = "POST";
    private static final String CONTENT_TYPE_SEPARATOR = ", ";

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveEssenceDataProvider.class);

    public SynchronizeTask(final HiveEssenceDataProvider essenceDataProvider, final SynchronizerConfiguration config) {
        this.essenceDataProvider = essenceDataProvider;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting synchronization");

            // FUTURE_WORK: If consistency model is > eventual -> send data request immediatly
            HttpURLConnection connection;
            if (dataToSend != null && dataToSend.hasData()) {
                isDataRequest = true;
                connection = buildDataRequest();
            } else {
                isDataRequest = false;
                connection = buildEssenceRequest();
            }
            try ( OutputStream os = connection.getOutputStream()) {
                if (isDataRequest) {
                    os.write(dataToSend.getData());
                } else {
                    os.write(essenceDataProvider.determineEssence());
                }
                os.flush();
            }
            processResponse(connection);

            LOGGER.info("Synchronization finished");
        } catch (IOException ex) {
            LOGGER.info("Synchronization task failed", ex);
        }
    }

    private HttpURLConnection buildEssenceRequest() throws IOException {
        HttpURLConnection urlConnection = getBaseRequest();
        urlConnection.addRequestProperty(KEY_CONTENT_TYPE, determineEssenceRequestContentType());

        return urlConnection;
    }

    private HttpURLConnection buildDataRequest() throws IOException {
        HttpURLConnection urlConnection = getBaseRequest();
        urlConnection.addRequestProperty(KEY_CONTENT_TYPE, (contentTypeOfDataToSend != null
                ? contentTypeOfDataToSend.getValue() : ContentType.JSON.getValue()));

        return urlConnection;
    }

    private HttpURLConnection getBaseRequest() throws IOException {
        URL url = new URL(config.getUri());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(POST);
        urlConnection.setDoOutput(true);
        if (traceparent != null && !traceparent.isEmpty()) {
            urlConnection.addRequestProperty(KEY_TRACEPARENT, traceparent);
        }

        return urlConnection;
    }

    private String determineEssenceRequestContentType() {
        StringBuilder sb = new StringBuilder();
        sb.append(ContentType.HIVE_ESSENCE.getValue());
        sb.append(CONTENT_TYPE_SEPARATOR);
        sb.append(config.getContentType().getValue());

        return sb.toString();
    }

    // FUTURE_WORK: Now reading all bytes to the heap, better in some cases to buffer
    private void processResponse(final HttpURLConnection response) throws IOException {
        if (traceparent == null || traceparent.isEmpty()) {
            updateTraceparent(response);
        }

        if (isSuccessful(response)) {
            readResponse(response);
        } else {
            LOGGER.error("""
                Unexpected response code received from Hivemind, this points to 
                an error that needs to be looked into. Synchronization will continue 
                trying to synchronize (and as such attempt to solve the error)""");
        }
    }

    private void updateTraceparent(final HttpURLConnection response) {
        String respondingTraceparent = response.getHeaderField(KEY_TRACEPARENT);
        if (respondingTraceparent != null && !respondingTraceparent.isEmpty()) {
            traceparent = respondingTraceparent;
        } else {
            LOGGER.warn("""
                Response from Hivemind received that did not have a traceparent 
                set, this is an issue worth looking into""");
        }
    }

    private boolean isSuccessful(final HttpURLConnection response) throws IOException {
        int code = response.getResponseCode();
        return code == 200 || code == 204 || code == 409;
    }

    private void readResponse(final HttpURLConnection response) throws IOException {
        switch (response.getResponseCode()) {
            case 204 ->
                LOGGER.info("Synchronization task succeeded, application is up to date");
            case 200 -> {
                ContentType contentType = getContentType(response);
                if (ContentType.HIVE_ESSENCE == contentType) {
                    LOGGER.info("Synchronization task succeeded, application received data request");

                    byte[] respondingEssence = readData(response);
                    if (respondingEssence != null && respondingEssence.length > 0) {
                        dataToSend = new PreparedData(essenceDataProvider.getDataForEssence(respondingEssence));
                    }
                } else {
                    LOGGER.info("Synchronization task succeeded, application received data");

                    essenceDataProvider.saveData(readData(response), contentType);
                }
            }
            case 409 -> {
                LOGGER.info("Synchronization task succeeded, application received priority request");
                essenceDataProvider.processPriorityEssence(readData(response));
            }
            default -> {
                LOGGER.error("Unexpected response code received from Hivemind, this points to inproper code since the status code has been deemed succesfull");
            }
        }

        if (isDataRequest) {
            dataToSend.close();
        }
    }

    private byte[] readData(final HttpURLConnection response) throws IOException {
        byte[] data;
        try ( InputStream input = response.getInputStream();  ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[8192];
            for (int count; (count = input.read(buffer)) > 0;) {
                output.write(buffer, 0, count);
            }
            data = output.toByteArray();
        }

        return data;
    }

    private ContentType getContentType(final HttpURLConnection response) {
        String respondingContentType = response.getHeaderField(KEY_CONTENT_TYPE);
        if (respondingContentType != null && !respondingContentType.isEmpty()) {
            return ContentType.enumFor(respondingContentType.split(CONTENT_TYPE_SEPARATOR)[0]);
        } else {
            LOGGER.warn("""
                Response from Hivemind received that did not have a content type 
                set, this is an issue worth looking into""");
        }

        return null;
    }
}
