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
import io.hivemind.data.PreparedData;
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
    public static final String COMMA = ",";
    private static final String CONTENT_TYPE_SEPARATOR = ", ";

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
     * Determine if the request is a hive essence request
     *
     * @param request the 'request' to determine for
     * @return whether this is a hive essence request
     */
    public abstract boolean isHiveEssenceRequest(final R request);

    /**
     * Determine if the synchronizer that send this request is requiring data to
     * be send in a specific format.
     *
     * @param request the 'request' to determine for
     * @return the content type (as string) for this data or null
     */
    public abstract String determineRequestedType(final R request);

    /**
     * Set the traceparent for the response
     *
     * @param traceparent the traceparent to set
     * @param response the 'response' to set the traceparent for
     */
    public abstract void setTraceparent(final String traceparent, final R response);

    /**
     * Set the content type for the response
     *
     * @param requestedType the content type for the request to set
     * @param response the 'response' to set the content type for
     */
    public abstract void setContentType(final String requestedType, final R response);

    /**
     * Determine the content type to set for a data request. This is either only
     * the content type: HIVE_ESSENCE or is: HIVE_ESSENCE, requestedType
     *
     * @param dataRequest the DataRequest to return
     * @return the content type header for this request
     */
    public String determineDataRequestContentType(final PreparedData dataRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(ContentType.HIVE_ESSENCE.getValue());

        String requestedType = dataRequest.getRequestedType();
        if (requestedType != null) {
            sb.append(CONTENT_TYPE_SEPARATOR);
            sb.append(requestedType);
        }

        return sb.toString();
    }

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
