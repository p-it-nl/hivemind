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

import io.hivemind.configuration.HiveConfig;
import io.hivemind.constant.Clear;
import io.hivemind.constant.ContentType;
import io.hivemind.data.DataProcessor;
import io.hivemind.data.DataRequest;
import io.hivemind.data.PreparedData;
import io.hivemind.data.PriorityRequest;
import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.HiveRequestException;
import io.hivemind.exception.InvalidEssenceException;
import io.hivemind.service.HiveManagerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

/**
 * Channel to process hive requests
 *
 * @author Patrick-4488
 */
public class HiveHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    private String traceparent;

    // FUTURE_WORK: Read the consistency model from config and implement it accordingly
    private final HiveConfig config;//NOSONAR
    private final DataProcessor dataProcessor;
    private final NettyHelper helper;
    private final ByteBuf bodyBuffer;

    private static final String REQUEST_TYPE = "Request is type: {0}";
    private static final String MANAGER_REQUEST = "/manager";
    private static final System.Logger LOGGER = System.getLogger(HiveHandler.class.getName());

    public HiveHandler(final HiveConfig config) {
        this.config = config;
        dataProcessor = DataProcessor.getInstance();
        helper = new NettyHelper();
        bodyBuffer = Unpooled.buffer();
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) {
        LOGGER.log(INFO, "Request received, processing...");

        if (msg instanceof HttpRequest httpRequest) {
            processHttpRequest(ctx, httpRequest);
        }

        if (msg instanceof HttpContent httpContent && MANAGER_REQUEST.equals(request.uri())) {
            proccessManagementRequest(ctx, httpContent);
        } else if (msg instanceof HttpContent httpContent) {
            processHttpContent(ctx, httpContent);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.log(ERROR, "Error during request", cause);
        ctx.close();
    }

    private void send100Continue(final ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    private void processHttpRequest(final ChannelHandlerContext ctx, final HttpRequest httpRequest) {
        LOGGER.log(INFO, REQUEST_TYPE, HttpRequest.class.getSimpleName());

        request = httpRequest;
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }
        bodyBuffer.clear();
        traceparent = helper.determineTraceparent(request);
    }

    private void processHttpContent(final ChannelHandlerContext ctx, final HttpContent httpContent) {
        LOGGER.log(INFO, REQUEST_TYPE, HttpContent.class.getSimpleName());

        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            bodyBuffer.writeBytes(content);
        }

        if (httpContent instanceof LastHttpContent) {
            LOGGER.log(INFO, REQUEST_TYPE, LastHttpContent.class.getSimpleName());

            byte[] responseData = new byte[0];
            FullHttpResponse response;
            try {
                PreparedData preparedData = dataProcessor.processData(readData(), helper.determineContentType(request), traceparent);

                if (preparedData != null) {
                    response = helper.createBaseResponse(preparedData.getData(), request);

                    LOGGER.log(INFO, "Request succeeded with having data with type: {0}",
                            preparedData.getClass().getSimpleName());

                    helper.setTraceparent(traceparent, response);
                    if (preparedData instanceof DataRequest) {
                        helper.setContentType(ContentType.HIVE_ESSENCE, response);
                    } else if (preparedData instanceof PriorityRequest) {
                        helper.setContentType(ContentType.HIVE_ESSENCE, response);
                        response.setStatus(CONFLICT);
                    } else {
                        helper.setContentType(ContentType.OTHER, response);
                    }
                } else {
                    LOGGER.log(INFO, "Request succeeded, no data required, returning 204");
                    response = helper.createBaseResponse(responseData, request);
                    response.setStatus(NO_CONTENT);
                }
            } catch (InvalidEssenceException ex) {
                responseData = ex.getLocalizedMessage().getBytes();
                response = helper.createBaseResponse(responseData, request);
                response.setStatus(BAD_REQUEST);
            }

            ctx.write(response);

            if (!HttpUtil.isKeepAlive(request)) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }

            bodyBuffer.clear();
            traceparent = null;
        }
    }

    private void proccessManagementRequest(final ChannelHandlerContext ctx, final HttpContent httpContent) {
        LOGGER.log(INFO, REQUEST_TYPE, HttpContent.class.getSimpleName());

        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            bodyBuffer.writeBytes(content);
        }

        if (httpContent instanceof LastHttpContent) {
            LOGGER.log(INFO, REQUEST_TYPE, LastHttpContent.class.getSimpleName());

            FullHttpResponse response;
            try {
                HiveManagerService hiveManager = new HiveManagerService();
                final byte[] bytes = readData();
                if (bytes.length > 0) {
                    switch (Clear.enumFor(new String(bytes))) {
                        case INERT ->
                            hiveManager.clearInertState();
                        case ALL ->
                            hiveManager.clearAllState();
                        default ->
                            hiveManager.clearInertState();
                    }

                    response = helper.createBaseResponse(new byte[0], request);
                } else {
                    throw new HiveRequestException(HiveCeption.UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER);
                }
            } catch (HiveRequestException ex) {
                response = helper.createBaseResponse(ex.getLocalizedMessage().getBytes(), request);
                response.setStatus(BAD_REQUEST);
            }

            ctx.write(response);

            if (!HttpUtil.isKeepAlive(request)) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }

            bodyBuffer.clear();
            traceparent = null;
        }
    }

    private byte[] readData() {
        byte[] bytes;
        int length = bodyBuffer.readableBytes();
        bytes = new byte[length];
        bodyBuffer.getBytes(bodyBuffer.readerIndex(), bytes);

        return bytes;
    }
}
