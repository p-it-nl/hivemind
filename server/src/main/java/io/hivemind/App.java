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
package io.hivemind;

import io.hivemind.configuration.HiveConfig;
import io.hivemind.configuration.LogConfig;
import io.hivemind.exception.HiveException;
import io.hivemind.server.netty.NettyServer;

import static java.lang.System.Logger.Level.INFO;

/**
 * Main class, starts the application
 *
 * @author Patrick-4488
 */
public class App {

    private static final System.Logger LOGGER = System.getLogger(App.class.getName());

    /**
     * Initializes the Hivemind server
     *
     * TODO: Decide to use TLS here (or just the proxy)
     *
     * @param args arguments
     * @throws io.hivemind.exception.HiveException exception when starting up
     */
    public static void main(final String[] args) throws HiveException {
        LogConfig.configure();

        LOGGER.log(INFO, "Starting Hivemind....");

        HiveConfig config = HiveConfig.getInstance();
        HiveServer server;
        switch (config.getServerType()) {
            case NETTY -> {
                LOGGER.log(INFO, "Using Netty");
                server = new NettyServer(config);
            }
            case HTTPSERVER, DEFAULT -> {
                LOGGER.log(INFO, "Using httpserver");
                server = new io.hivemind.server.httpserver.HttpServer(config);
            }
            default -> {
                LOGGER.log(INFO, "Using httpserver");
                server = new io.hivemind.server.httpserver.HttpServer(config);
            }
        }

        server.start();

        LOGGER.log(INFO, "....Hivemind is up and running at port {0}", String.valueOf(config.getPort()));
    }
}
