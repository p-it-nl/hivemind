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

import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.HiveException;
import io.hivemind.exception.HiveRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for content type constant
 *
 * @author Patrick-4488
 */
@ExtendWith(MockitoExtension.class)
public class ClearTest {

    private static final String VALUE_EMPTY = "";
    private static final String VALUE_ALL = "all";
    private static final String VALUE_INERT = "inert";
    private static final String VALUE_UNEXPECTED = "mock";

    @Test
    public void clearFromNull() {
        HiveCeption expected = HiveCeption.UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER;
        String value = null;

        HiveException exception = assertThrows(HiveException.class, () -> {
            Clear.enumFor(value);
        });

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void clearFromEmpty() {
        HiveCeption expected = HiveCeption.UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER;
        String value = VALUE_EMPTY;

        HiveException exception = assertThrows(HiveException.class, () -> {
            Clear.enumFor(value);
        });

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void clearFromUnexpected() {
        HiveCeption expected = HiveCeption.UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER;
        String value = VALUE_UNEXPECTED;

        HiveException exception = assertThrows(HiveException.class, () -> {
            Clear.enumFor(value);
        });

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void clearFromAll() throws HiveRequestException {
        String value = VALUE_ALL;
        Clear expected = Clear.ALL;

        Clear result = Clear.enumFor(value);

        assertEquals(expected, result);
    }

    @Test
    public void clearFromInert() throws HiveRequestException {
        String value = VALUE_INERT;
        Clear expected = Clear.INERT;

        Clear result = Clear.enumFor(value);

        assertEquals(expected, result);
    }
}
