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
package io.hivemind.tracecontext;

import java.security.SecureRandom;

/**
 * Generator for generating traceparent based on W3C recommendation for trace
 * context which can be found here:
 * https://www.w3.org/TR/trace-context/#trace-flags
 *
 * @author Patrick-4488
 */
public class TraceparentGenerator {

    private static final String VERSION = "00";
    private static final String UNSAMPLED = "00";
    private static final String TRACE_BETWEEN = "-";
    private static final char[] HEX_OPTIONS = "0123456789abcdef".toCharArray();

    private TraceparentGenerator() {
    }

    /**
     * Generates a traceparent<br>
     * (version id)-(trace id)-(parent id)-(trace options) Example:
     * 00-b1d2a5ec5ae1edfe18cd0669c26c5ff6-dfc43bb3f9f8c4dc-00
     *
     * @return the new traceparent
     */
    public static synchronized String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(VERSION);
        sb.append(TRACE_BETWEEN);
        sb.append(bytesToHex(new byte[16]));
        sb.append(TRACE_BETWEEN);
        sb.append(bytesToHex(new byte[8]));
        sb.append(TRACE_BETWEEN);
        sb.append(UNSAMPLED);

        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        new SecureRandom().nextBytes(bytes);
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_OPTIONS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_OPTIONS[v & 0x0F];
        }

        return new String(hexChars);
    }
}
