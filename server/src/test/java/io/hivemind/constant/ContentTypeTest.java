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
package io.hivemind.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for content type constant
 *
 * @author Patrick-4488
 */
public class ContentTypeTest {

    private static final String VALUE_EMPTY = "";
    private static final String VALUE_APPLICATION_ZIP = "application/zip";
    private static final String VALUE_JSON = "json";
    private static final String VALUE_APPLICATION_JSON = "application/json";
    private static final String VALUE_MOCK = "mock";
    private static final String VALUE_APPLICATION_HIVE_ESSENCE = "application/hive-essence";
    private static final String VALUE_HIVE_ESSENCE = "hive-essence";
    private static final String VALUE_HIVE_ESSENCE_NO_HYPHEN = "hiveessence";

    @Test
    public void contentTypeFromNull() {
        String value = null;
        ContentType expected = ContentType.OTHER;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromEmpty() {
        String value = VALUE_EMPTY;
        ContentType expected = ContentType.OTHER;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromApplicationZip() {
        String value = VALUE_APPLICATION_ZIP;
        ContentType expected = ContentType.OTHER;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromMock() {
        String value = VALUE_MOCK;
        ContentType expected = ContentType.OTHER;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromApplicationHiveEssence() {
        String value = VALUE_APPLICATION_HIVE_ESSENCE;
        ContentType expected = ContentType.HIVE_ESSENCE;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromHiveEssence() {
        String value = VALUE_HIVE_ESSENCE;
        ContentType expected = ContentType.HIVE_ESSENCE;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromHiveEssenceWithoutHyphen() {
        String value = VALUE_HIVE_ESSENCE_NO_HYPHEN;
        ContentType expected = ContentType.OTHER;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromApplicationJson() {
        String value = VALUE_APPLICATION_JSON;
        ContentType expected = ContentType.JSON;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void contentTypeFromJson() {
        String value = VALUE_JSON;
        ContentType expected = ContentType.JSON;

        ContentType result = ContentType.enumFor(value);

        assertEquals(expected, result);
    }
}
