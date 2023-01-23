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
package io.hivemind.synchronizer.validator;

import io.hivemind.synchronizer.exception.HiveCeption;
import io.hivemind.synchronizer.exception.HiveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for essence validator
 *
 * @author Patrick-4488
 */
public class EssenceValidatorTest {

    private static final byte[] ESSENCE_EMPTY = "".getBytes();
    private static final byte[] ESSENCE_INVALID = "mock".getBytes();
    private static final byte[] ESSENCE_INVALID_BETWEEN = "1,1;mock;2,1;".getBytes();
    private static final byte[] ESSENCE_VALID_START_INVALID_END = "1,1;2,1;mock".getBytes();
    private static final byte[] ESSENCE_VALID = "1,1;2,1;".getBytes();
    private static final byte[] ESSENCE_VALID_NOT_ENDING_WITH_SEPARATOR = "1,1;1,2".getBytes();
    private static final byte[] ESSENCE_VALID_LONG = """
        73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;
        """.getBytes();

    @Test
    public void validateEssenceBeingNull() {
        EssenceValidator classUnderTest = new EssenceValidator();
        assertDoesNotThrow(() -> classUnderTest.validateEssence(null));
    }

    @Test
    public void validateEssenceBeingEmpty() {
        EssenceValidator classUnderTest = new EssenceValidator();
        assertDoesNotThrow(() -> classUnderTest.validateEssence(ESSENCE_EMPTY));
    }

    @Test
    public void validateEssenceValid() {
        EssenceValidator classUnderTest = new EssenceValidator();
        assertDoesNotThrow(() -> classUnderTest.validateEssence(ESSENCE_VALID));
    }

    @Test
    public void validateEssenceValidWithoutEndingWithSeparator() {
        EssenceValidator classUnderTest = new EssenceValidator();
        assertDoesNotThrow(() -> classUnderTest.validateEssence(ESSENCE_VALID_NOT_ENDING_WITH_SEPARATOR));
    }

    @Test
    public void validateEssenceBeingInvalid() {
        EssenceValidator classUnderTest = new EssenceValidator();
        HiveCeption expected = HiveCeption.INVALID_ESSENCE;

        HiveException exception = assertThrows(HiveException.class, () -> classUnderTest.validateEssence(ESSENCE_INVALID));

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void validateEssenceValidStartInvalidEnd() {
        EssenceValidator classUnderTest = new EssenceValidator();
        HiveCeption expected = HiveCeption.INVALID_ESSENCE;

        HiveException exception = assertThrows(HiveException.class, () -> classUnderTest.validateEssence(ESSENCE_VALID_START_INVALID_END));

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void validateEssenceValidStartInvalidBetween() {
        EssenceValidator classUnderTest = new EssenceValidator();
        HiveCeption expected = HiveCeption.INVALID_ESSENCE;

        HiveException exception = assertThrows(HiveException.class, () -> classUnderTest.validateEssence(ESSENCE_INVALID_BETWEEN));

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void validateEssenceBeingLongValid() {
        EssenceValidator classUnderTest = new EssenceValidator();
        assertDoesNotThrow(() -> classUnderTest.validateEssence(ESSENCE_VALID_LONG));
    }
}
