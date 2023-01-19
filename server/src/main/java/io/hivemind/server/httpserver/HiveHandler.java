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
package io.hivemind.server.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.hivemind.configuration.HiveConfig;
import io.hivemind.constant.ContentType;
import io.hivemind.data.DataProcessor;
import io.hivemind.data.DataRequest;
import io.hivemind.data.PreparedData;
import io.hivemind.data.PriorityRequest;
import io.hivemind.exception.InvalidEssenceException;
import io.hivemind.helper.RequestHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Hive handler provides the endpoint for hive connections
 *
 * @author Patrick-4488
 */
public class HiveHandler implements HttpHandler {

    // FUTURE_WORK: Read the consistency model from config and implement it accordingly
    private final HiveConfig config;
    private final DataProcessor dataProcessor;

    private static final System.Logger LOGGER = System.getLogger(HiveHandler.class.getName());

    public HiveHandler(final HiveConfig config) {
        this.config = config;
        this.dataProcessor = DataProcessor.getInstance();
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        LOGGER.log(INFO, "Request received, processing...");

        if (exchange != null) {
            RequestHelper helper = new HttpserverHelper();
            try ( InputStream is = exchange.getRequestBody()) {
                final byte[] bytes = is.readAllBytes();

                String traceparent = helper.determineTraceparent(exchange);
                helper.setTraceparent(traceparent, exchange);
                try {
                    PreparedData preparedData = dataProcessor.processData(bytes, helper.determineContentType(exchange), traceparent);
                    if (preparedData != null) {
                        LOGGER.log(INFO, "Request succeeded with having data with type: {0}",
                                preparedData.getClass().getSimpleName());

                        byte[] responseData = preparedData.getData();
                        // FUTURE_WORK: Java 19 - switch to pattern matching
                        if (preparedData instanceof DataRequest) {
                            helper.setContentType(ContentType.HIVE_ESSENCE, exchange);
                            exchange.sendResponseHeaders(200, responseData.length);
                        } else if (preparedData instanceof PriorityRequest) {
                            helper.setContentType(ContentType.HIVE_ESSENCE, exchange);
                            exchange.sendResponseHeaders(409, responseData.length);
                        } else {
                            helper.setContentType(ContentType.OTHER, exchange);
                            exchange.sendResponseHeaders(200, responseData.length);
                        }

                        try ( OutputStream os = exchange.getResponseBody()) {
                            os.write(responseData);
                            os.close();
                        }
                    } else {
                        LOGGER.log(INFO, "Request succeeded, no data required, returning 204");
                        exchange.sendResponseHeaders(204, -1);
                    }
                } catch (InvalidEssenceException ex) {
                    byte[] exception = ex.getLocalizedMessage().getBytes();
                    exchange.sendResponseHeaders(400, exception.length);
                    try ( OutputStream os = exchange.getResponseBody()) {
                        os.write(exception);
                        os.close();
                    }
                }
            } catch (IOException ex) {
                LOGGER.log(ERROR, "Error in request", ex);
                throw ex;
            } finally {
                exchange.close();
            }
        } else {
            LOGGER.log(WARNING, "Request received but no HttpExchange has been provided");
        }
    }
}
