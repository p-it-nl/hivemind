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

    /**
     * Generates a traceparent<br>
     * (version id)-(trace id)-(parent id)-(trace options) Example:
     * 00-b1d2a5ec5ae1edfe18cd0669c26c5ff6-dfc43bb3f9f8c4dc-00
     *
     * @return the new traceparent
     */
    public synchronized static String generate() {
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
