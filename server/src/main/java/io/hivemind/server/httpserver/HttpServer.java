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

import io.hivemind.HiveServer;
import io.hivemind.configuration.HiveConfig;
import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.UnstartableException;
import java.io.IOException;
import java.net.InetSocketAddress;

import static java.lang.System.Logger.Level.ERROR;

/**
 * Httpserver implementation for hive server
 *
 * @author Patrick-4488
 */
public class HttpServer implements HiveServer {

    private final HiveConfig config;
    private com.sun.net.httpserver.HttpServer server;//NOSONAR, com.sun is fine

    private static final System.Logger LOGGER = System.getLogger(HttpServer.class.getName());

    public HttpServer(final HiveConfig config) {
        this.config = config;
    }

    @Override
    public void start() throws UnstartableException {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(config.getPort()), 0);//NOSONAR, com.sun is fine
            server.setExecutor(config.getPoolExecutor());
            server.createContext("/", new HiveHandler(config));
            server.createContext("/manager", new ManagerHandler());
            server.start();
        } catch (IOException ex) {
            LOGGER.log(ERROR, "Not able to start httpserver", ex);
            throw new UnstartableException(HiveCeption.HTTP_SERVER_FAILED_TO_BOOT);
        }
    }

    @Override
    public void stop() {
        server.stop(60);
    }
}
