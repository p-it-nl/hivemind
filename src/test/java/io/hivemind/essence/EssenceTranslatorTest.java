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
package io.hivemind.essence;

import io.hivemind.exception.HiveCeption;
import io.hivemind.exception.HiveException;
import io.hivemind.exception.InvalidEssenceException;
import io.hivemind.synchronizer.HiveEssencePart;
import io.hivemind.synchronizer.HiveResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for essence comparator
 *
 * @author Patrick-4488
 */
public class EssenceTranslatorTest {

    private EssenceTranslator classUnderTest;

    private static final byte[] EXPECTED_ID_FIRST = "1".getBytes();
    private static final byte[] EXPECTED_ID_SECOND = "2".getBytes();
    private static final byte[] EXPECTED_VERSION = "1".getBytes();
    private static final byte[] ESSENCE_INVALID = "mock".getBytes();
    private static final byte[] ESSENCE_VALID_ONE = "1,1;".getBytes();
    private static final byte[] ESSENCE_VALID_TWO = "1,1;2,1;".getBytes();

    @BeforeEach
    public void setup() {
        classUnderTest = new EssenceTranslator();
    }

    @Test
    public void interpolateResourcesFromEssenceWithoutEssence() throws InvalidEssenceException {
        byte[] essence = null;

        List<HiveResource> result = classUnderTest.interpolateResourcesFromEssence(essence);

        assertTrue(result.isEmpty());
    }

    @Test
    public void interpolateResourcesFromEssenceWithEmptyEssence() throws InvalidEssenceException {
        byte[] essence = new byte[0];

        List<HiveResource> result = classUnderTest.interpolateResourcesFromEssence(essence);

        assertTrue(result.isEmpty());
    }

    @Test
    public void interpolateResourcesFromEssenceWithEssenceBeingEmptyByteArray() throws InvalidEssenceException {
        byte[] essence = new byte[32];

        List<HiveResource> result = classUnderTest.interpolateResourcesFromEssence(essence);

        assertTrue(result.isEmpty());
    }

    @Test
    public void interpolateResourcesFromEssenceWithEssenceNot() {
        byte[] essence = ESSENCE_INVALID;
        HiveCeption expected = HiveCeption.INVALID_ESSENCE;

        HiveException exception = assertThrows(HiveException.class, () -> {
            classUnderTest.interpolateResourcesFromEssence(essence);
        });

        assertNotNull(exception);
        assertEquals(expected.getMessage(), exception.getLocalizedMessage());
    }

    @Test
    public void interpolateResourcesFromEssenceExpectingOneResource() throws InvalidEssenceException {
        byte[] essence = ESSENCE_VALID_ONE;

        List<HiveResource> result = classUnderTest.interpolateResourcesFromEssence(essence);

        assertFalse(result.isEmpty());
        HiveResource first = result.get(0);
        assertTrue(Arrays.compare(EXPECTED_ID_FIRST, (byte[]) first.getId()) == 0);
        assertTrue(Arrays.compare(EXPECTED_VERSION, (byte[]) first.getVersion()) == 0);
    }

    @Test
    public void interpolateResourcesFromEssenceExpectingTwoResources() throws InvalidEssenceException {
        byte[] essence = ESSENCE_VALID_TWO;

        List<HiveResource> result = classUnderTest.interpolateResourcesFromEssence(essence);

        assertFalse(result.isEmpty());
        HiveResource first = result.get(0);
        assertTrue(Arrays.compare(EXPECTED_ID_FIRST, (byte[]) first.getId()) == 0);
        assertTrue(Arrays.compare(EXPECTED_VERSION, (byte[]) first.getVersion()) == 0);
        HiveResource second = result.get(1);
        assertTrue(Arrays.compare(EXPECTED_ID_SECOND, (byte[]) second.getId()) == 0);
        assertTrue(Arrays.compare(EXPECTED_VERSION, (byte[]) second.getVersion()) == 0);
    }

    @Test
    public void determineEssenceForResourcesWithoutResources() {
        List<HiveResource> resources = null;

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length == 0);
    }

    @Test
    public void determineEssenceForResourcesWithEmptyResources() {
        List<HiveResource> resources = new ArrayList<>();

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length == 0);
    }

    @Test
    public void determineEssenceForResourcesWithOneEmptyResource() {
        List<HiveResource> resources = List.of(getResourceFor(null, null));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length == 0);
    }

    @Test
    public void determineEssenceForResourcesWithOneResourceHavingIdNotHavingVersion() {
        List<HiveResource> resources = List.of(getResourceFor(EXPECTED_ID_FIRST, null));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length == 0);
    }

    @Test
    public void determineEssenceForResourcesWithOneResourceHavingIAndVersion() {
        List<HiveResource> resources = List.of(getResourceFor(EXPECTED_ID_FIRST, EXPECTED_VERSION));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(Arrays.compare(ESSENCE_VALID_ONE, result) == 0);
    }

    @Test
    public void determineEssenceForResourcesWithTwoResourcesHavingIdAndVersion() {
        List<HiveResource> resources = List.of(
                getResourceFor(EXPECTED_ID_FIRST, EXPECTED_VERSION),
                getResourceFor(EXPECTED_ID_SECOND, EXPECTED_VERSION));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(Arrays.compare(ESSENCE_VALID_TWO, result) == 0);
    }

    @Test
    public void determineEssenceForResourcesWithTwoResourcesWithSecondBeingEmpty() {
        List<HiveResource> resources = List.of(
                getResourceFor(EXPECTED_ID_FIRST, EXPECTED_VERSION),
                getResourceFor(null, null));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length == 0);
    }

    @Test
    public void determineEssenceForResourcesWithOneResourceHavingIAndVersionAsLong() {
        List<HiveResource> resources = List.of(new ResourceLong(1L, 1L));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(Arrays.compare(ESSENCE_VALID_ONE, result) == 0);
    }

    @Test
    public void determineEssenceForResourcesWithOneResourceHavingIAndVersionAsInt() {
        List<HiveResource> resources = List.of(new ResourceInt(1, 1));

        byte[] result = classUnderTest.determineEssenceForResources(resources);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(Arrays.compare(ESSENCE_VALID_ONE, result) == 0);
    }

    private HiveResource getResourceFor(final byte[] id, final byte[] version) {
        HiveEssencePart resource = new HiveEssencePart();
        resource.setId(id);
        resource.setVersion(version);

        return resource;
    }

    private class ResourceLong extends HiveResource {

        private final long id;
        private final long version;

        public ResourceLong(final long id, final long version) {
            this.id = id;
            this.version = version;
        }

        @Override
        public Object getId() {
            return id;
        }

        @Override
        public Object getVersion() {
            return version;
        }
    }

    private class ResourceInt extends HiveResource {

        private final int id;
        private final int version;

        public ResourceInt(final int id, final int version) {
            this.id = id;
            this.version = version;
        }

        @Override
        public Object getId() {
            return id;
        }

        @Override
        public Object getVersion() {
            return version;
        }
    }
}
