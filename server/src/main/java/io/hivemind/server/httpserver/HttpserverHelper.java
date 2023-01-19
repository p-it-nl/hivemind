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
import io.hivemind.constant.ContentType;
import io.hivemind.helper.RequestHelper;
import java.util.List;

/**
 * Helper for httpserver requests
 *
 * @author Patrick-4488
 */
public class HttpserverHelper extends RequestHelper<HttpExchange> {

    @Override
    public String determineTraceparent(final HttpExchange exchange) {
        return determineTraceparent(exchange.getRequestHeaders().get(KEY_TRACEPARENT));
    }

    @Override
    public ContentType determineContentType(final HttpExchange exchange) {
        List<String> contentTypes = exchange.getRequestHeaders().get(KEY_CONTENT_TYPE);
        if (contentTypes != null && !contentTypes.isEmpty()) {
            return ContentType.enumFor(contentTypes.get(contentTypes.size() - 1));
        } else {
            return null;
        }
    }

    @Override
    public void setTraceparent(final String traceparent, final HttpExchange response) {
        response.getResponseHeaders().add(KEY_TRACEPARENT, traceparent);
    }

    @Override
    public void setContentType(final ContentType contentType, final HttpExchange response) {
        response.getResponseHeaders().add(KEY_CONTENT_TYPE, contentType.getValue());
    }

}
