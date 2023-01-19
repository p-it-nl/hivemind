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
package io.hivemind.helper;

import io.hivemind.constant.ContentType;
import io.hivemind.tracecontext.TraceparentGenerator;
import java.util.List;

/**
 * Helper class for common requests functionality. Implemented by specific
 * server helpers
 *
 * @author Patrick-4488
 * @param <R> type of request. e.g. HttpExchange in case of httpserver
 */
public abstract class RequestHelper<R> {

    public static final String KEY_TRACEPARENT = "traceparent";
    public static final String KEY_CONTENT_TYPE = "content-type";

    /**
     * Determine the traceparent for the request. This will return a new
     * traceparent when none is found, the value found or the last value when
     * multiple are found.<br>
     *
     * @param request the 'request' to determine for
     * @return the traceparent for this request
     */
    public abstract String determineTraceparent(final R request);

    /**
     * Determine the content-type of this request.
     *
     * @param request the 'request' to determine for
     * @return the content-type for this request
     */
    public abstract ContentType determineContentType(final R request);

    /**
     * Set the traceparent for the response
     *
     * @param traceparent the traceparent to set
     * @param response the 'response' to set the traceparent for
     */
    public abstract void setTraceparent(final String traceparent, final R response);

    /**
     * Set the content-type for the reponse
     *
     * @param contentType the content-type to set
     * @param response the 'response' to set the content type for
     */
    public abstract void setContentType(final ContentType contentType, final R response);

    protected String determineTraceparent(final List<String> values) {
        String traceparent;
        if (values == null || values.isEmpty()) {
            traceparent = TraceparentGenerator.generate();
        } else if (values.size() == 1) {
            traceparent = fromValue(values.get(0));
        } else {
            traceparent = fromValue(values.get(values.size() - 1));
        }

        return traceparent;
    }

    private static String fromValue(final String traceparent) {
        return (traceparent != null && !traceparent.isEmpty() ? traceparent : TraceparentGenerator.generate());
    }
}
