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
package io.hivemind.configuration;

import io.hivemind.constant.ConsistencyModel;
import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.HiveException;
import io.hivemind.exception.NotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for hive configuration
 *
 * @author Patrick-4488
 */
public class SynchronizerConfigurationTest {

    private static final String URI = "http://unitesting.com/mock";
    private static final int EXPECTED_PERIOD_BETWEEN_REQUEST = 30;

    @Test
    public void synchronizerConfigurationFromNullValues() {
        String uri = null;
        ConsistencyModel model = null;

        assertDoesNotThrow(() -> {
            SynchronizerConfiguration config = new SynchronizerConfiguration(uri, model);

            assertNotNull(config);
            assertTrue(EXPECTED_PERIOD_BETWEEN_REQUEST == config.getPeriodBetweenRequests());
        });
    }

    @Test
    public void synchronizerConfigurationFromUnsupportedValue() {
        HiveCeption expected = HiveCeption.CONSISTENCY_MODEL_NOT_SUPPORTED;
        String uri = null;
        ConsistencyModel model = ConsistencyModel.SEQUENTIAL_CONSISTENCY;

        HiveException exception = assertThrows(NotSupportedException.class, () -> {
            new SynchronizerConfiguration(uri, model);
        });

        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void synchronizerConfigurationFromSupportedValueAndHavingUri() {
        String uri = URI;
        ConsistencyModel model = ConsistencyModel.EVENTUAL_CONSISTENCY;

        assertDoesNotThrow(() -> {
            SynchronizerConfiguration config = new SynchronizerConfiguration(uri, model);

            assertNotNull(config);
            assertTrue(EXPECTED_PERIOD_BETWEEN_REQUEST == config.getPeriodBetweenRequests());
        });
    }
}
