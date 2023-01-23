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

import com.sun.net.httpserver.HttpExchange;//NOSONAR, com.sun is fine
import com.sun.net.httpserver.HttpHandler;//NOSONAR, com.sun is fine
import io.hivemind.constant.Clear;
import io.hivemind.exception.HiveRequestException;
import io.hivemind.manager.HiveManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 *
 * @author Patrick-4488
 */
public class ManagerHandler implements HttpHandler {

    private static final System.Logger LOGGER = System.getLogger(ManagerHandler.class.getName());

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        LOGGER.log(INFO, "Manager request received, processing...");

        if (exchange != null) {
            HiveManager hiveManager = new HiveManager();
            try ( InputStream is = exchange.getRequestBody()) {
                final byte[] bytes = is.readAllBytes();
                if (bytes != null && bytes.length > 0) {
                    switch (Clear.enumFor(new String(bytes))) {
                        case INERT ->
                            hiveManager.clearInertState();
                        case ALL ->
                            hiveManager.clearAllState();
                        default ->
                            hiveManager.clearInertState();
                    }

                    exchange.sendResponseHeaders(200, -1);
                }
            } catch (IOException ex) {
                LOGGER.log(ERROR, "Error in request", ex);
                throw ex;
            } catch (HiveRequestException ex) {
                byte[] exception = ex.getLocalizedMessage().getBytes();
                exchange.sendResponseHeaders(400, exception.length);
                try ( OutputStream os = exchange.getResponseBody()) {
                    os.write(exception);
                }
            } finally {
                exchange.close();
            }
        } else {
            LOGGER.log(WARNING, "Request received but no HttpExchange has been provided");
        }
    }
}
