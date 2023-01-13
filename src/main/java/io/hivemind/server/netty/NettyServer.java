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
package io.hivemind.server.netty;

import io.hivemind.App;
import io.hivemind.HiveServer;
import io.hivemind.configuration.HiveConfig;
import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.UnstartableException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import static java.lang.System.Logger.Level.ERROR;

/**
 * Netty implementation for hive server
 *
 * @author Patrick-4488
 */
public class NettyServer implements HiveServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final HiveConfig config;

    private static final System.Logger LOGGER = System.getLogger(App.class.getName());

    public NettyServer(final HiveConfig config) {
        this.config = config;
    }

    @Override
    public void start() throws UnstartableException {
        bossGroup = new NioEventLoopGroup(config.getMaxThreads(), config.getPoolExecutor());
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerInitializer(config));

            Channel ch = b.bind(config.getPort()).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException ex) {
            LOGGER.log(ERROR, "Not able to start Netty", ex);
            throw new UnstartableException(HiveCeption.NETTY_FAILED_TO_BOOT);
        } finally {
            stop();
        }
    }

    @Override
    public void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
