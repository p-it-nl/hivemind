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
 * Converter for converting to and from bytes.<br>
 * Current implementations include: Long
 * <br>
 * FUTURE_WORK: Add more converters when required
 *
 * @author Patrick-4488
 * @param <T> the implementation data type
 */
public abstract class Converter<T> {

    /**
     * Generate a new converter for a given type.
     * <br>
     * If the type is not supported it will currently return null
     * <br>
     * FUTURE_WORK: Java 19, use pattern matching FUTURE_WORK: maybe more clean
     * to throws NotSupportedException? FUTURE_WORK: Implement support for Long
     * as part of essence instead of only supporting Strings. The byte array for
     * Long is lighter then the byte array for String for the same value. Long
     * being 8 bytes and string being atleast 40 bytes (currently the JVM will
     * allocate 40 bytes for an empty string by default)
     *
     *
     * @param c the type
     * @return the converter
     */
    public static Converter forType(final Class<?> c) {
        if (c != null && c.equals(Long.class)) {
            return LongConverter.instance();
        }

        return null;
    }

    /**
     * Convert {implementation type} to byte[] in an efficient manner (bitwise
     * operations). Using this method will avoid heap memory allocations by
     * putting the object in an array without doing any intermediate
     * allocations<br>
     * Currently supporting: long
     * <br>
     * Alternatively you can use the ByteBuffer, but it will not be as
     * performant
     *
     * @param object the object to convert
     * @return the byte[]
     */
    public abstract byte[] convert(T object);

    /**
     * Convert to {implementation type} from byte[] in an efficient manner
     * (bitwise operations). Using this method will avoid heap memory
     * allocations by putting the object in an array without doing any
     * intermediate allocations<br>
     * Currently supporting: long
     * <br>
     * Alternatively you can use the ByteBuffer, but it will not be as
     * performant
     *
     * @param bytes the bytes to convert
     * @return the {implementation type} result;
     */
    public abstract T fromBytes(final byte[] bytes);

}
