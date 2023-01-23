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
package io.hivemind.data.conversion;

/**
 * Converter for long
 *
 * @author Patrick-4488
 */
public class LongConverter extends Converter<Long> {

    private LongConverter() {
    }

    /**
     * Create a new instance of this converter.<br>
     * This prevents public access to converter implementations and enforces
     * usage of Converter.forType()
     *
     * @return new LongConverter
     */
    protected static Converter<Long> instance() {
        return new LongConverter();
    }

    @Override
    public byte[] convert(Long object) {
        if (object == null) {
            return new byte[0];
        }

        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte) (object & 0xFF);
            object >>= Byte.SIZE;
        }

        return result;
    }

    @Override
    public Long fromBytes(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }

        return result;
    }
}
