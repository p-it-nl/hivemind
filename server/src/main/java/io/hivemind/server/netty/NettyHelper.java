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

import io.hivemind.constant.ContentType;
import io.hivemind.helper.RequestHelper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import java.util.List;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Helper for Netty requests
 *
 * @author Patrick-4488
 */
public class NettyHelper extends RequestHelper<HttpMessage> {

    @Override
    public String determineTraceparent(final HttpMessage request) {
        return super.determineTraceparent(request.headers().getAll(KEY_TRACEPARENT));
    }

    // FUTURE_WORK: Add unit tests and combine, its to similair to below
    @Override
    public boolean isHiveEssenceRequest(final HttpMessage request) {
        List<String> contentTypes = request.headers().getAll(KEY_CONTENT_TYPE);
        if (contentTypes != null && !contentTypes.isEmpty()) {
            for (String contentType : contentTypes) {
                if (contentType.contains(COMMA)) {
                    String[] contentTypesWithin = contentType.split(COMMA);
                    for (String contentTypeWithin : contentTypesWithin) {
                        if (ContentType.HIVE_ESSENCE == ContentType.enumFor(contentTypeWithin)) {
                            return true;
                        }
                    }
                } else if (ContentType.HIVE_ESSENCE == ContentType.enumFor(contentType)) {
                    return true;
                }
            }
        }

        return false;
    }

    // FUTURE_WORK: Add unit tests and combine, its to similair to above
    @Override
    public String determineRequestedType(final HttpMessage request) {
        List<String> contentTypes = request.headers().getAll(KEY_CONTENT_TYPE);
        if (contentTypes != null && !contentTypes.isEmpty()) {
            for (String contentType : contentTypes) {
                if (contentType.contains(COMMA)) {
                    String[] contentTypesWithin = contentType.split(COMMA);
                    for (String contentTypeWithin : contentTypesWithin) {
                        if (ContentType.HIVE_ESSENCE != ContentType.enumFor(contentTypeWithin)) {
                            return contentTypeWithin.trim();
                        }
                    }
                } else if (ContentType.HIVE_ESSENCE != ContentType.enumFor(contentType)) {
                    return contentType.trim();
                }
            }
        }

        return null;
    }

    @Override
    public void setTraceparent(final String traceparent, final HttpMessage request) {
        request.headers().add(KEY_TRACEPARENT, traceparent);
    }

    @Override
    public void setContentType(final String requestedType, final HttpMessage request) {
        request.headers().add(KEY_CONTENT_TYPE, requestedType);
    }

    /**
     * Create base netty response with base headers
     *
     * @param responseData the response data to create the response for
     * @param request the request to create response for
     * @return the response
     */
    public FullHttpResponse createBaseResponse(final byte[] responseData, final HttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK, Unpooled.copiedBuffer(responseData));

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        return response;
    }

}
