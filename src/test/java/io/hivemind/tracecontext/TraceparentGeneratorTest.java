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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for traceparent generator
 *
 * @author Patrick-4488
 */
public class TraceparentGeneratorTest {

    @Test
    public void generateTraceparent() {
        int expectedLength = 55;
        String traceparent = TraceparentGenerator.generate();

        assertNotNull(traceparent);
        assertFalse(traceparent.isEmpty());
        assertEquals(expectedLength, traceparent.length());
    }
}
