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
package io.hivemind.data.comparision;

import io.hivemind.data.Data;
import io.hivemind.data.ObservedData;
import io.hivemind.data.comparison.ComparisonResult;
import io.hivemind.data.comparison.EssenceComparator;
import io.hivemind.constant.Outcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for essence comparator
 *
 * @author Patrick-4488
 */
@ExtendWith(MockitoExtension.class)
public class EssenceComparatorTest {

    private EssenceComparator classUnderTest;

    private static final byte[] DIFF_SHORT_PLUS_UPDATE = "2,1;3,1;4,1;5,1;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DIFF_SHORT_PLUS_ONE = "3,1;".getBytes();
    private static final byte[] DIFF_SHORT_TO_LONG = "3,1;4,1;5,1;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DIFF_ONE_VERSION_CHANGE = "4,2;".getBytes();
    private static final byte[] DIFF_TWO_VERSION_CHANGE = "4,2;5,2;".getBytes();
    private static final byte[] DIFF_TWO_VERSION_CHANGE_SEPARATE = "2,2;8,2;".getBytes();
    private static final byte[] DIFF_MORE_THEN_TWO_NUMBERS = "11,1;13,1;14,1;15,1;16,1;17,1;18,1;19,1;".getBytes();
    private static final byte[] DIFF_SHORT_PLUS_UPDATE_IN_FRONT = "1,2;".getBytes();
    private static final byte[] DIFF_MORE_THEN_TWO = "73,2;".getBytes();
    private static final byte[] DIFF_UPDATE_IN_FRONT = "74,1;".getBytes();
    private static final byte[] DIFF_TWO_MORE_THEN_TWO_TO_VERY_LONG = "71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();

    private static final byte[] DATA_BYTE_ZERO = "0;".getBytes();
    private static final byte[] DATA_SHORT = "1,1;2,1;".getBytes();
    private static final byte[] DATA_SHORT_PLUS_ONE = "1,1;2,1;3,1;".getBytes();
    private static final byte[] DATA_SHORT_PLUS_ONE_ZERO = "1,1;2,1;0".getBytes();
    private static final byte[] DATA_SHORT_PLUS_UPDATE = "1,1;2,2;".getBytes();
    private static final byte[] DATA_SHORT_PLUS_UPDATE_IN_FRONT = "1,2;2,1;".getBytes();
    private static final byte[] DATA_SHORT_MORE_THEN_TWO_NUMBERS_PLUS_UPDATE = "11,2;12,1;".getBytes();
    private static final byte[] DATA_LONG_MORE_THEN_TWO_NUMBERS = "11,1;12,1;13,1;14,1;15,1;16,1;17,1;18,1;19,1;".getBytes();
    private static final byte[] DATA_LONG = "1,1;2,1;3,1;4,1;5,1;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DATA_LONG_UPDATE_IN_FRONT = "74,1;1,1;2,1;3,1;4,1;5,1;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DATA_ONE_VERSION_CHANGE = "1,1;2,1;3,1;4,2;5,1;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DATA_TWO_VERSION_CHANGE = "1,1;2,1;3,1;4,2;5,2;6,1;7,1;8,1;9,1;".getBytes();
    private static final byte[] DATA_TWO_VERSION_CHANGE_SEPARATE = "1,1;2,2;3,1;4,1;5,1;6,1;7,1;8,2;9,1;".getBytes();
    private static final byte[] DATA_MORE_THEN_TWO_BEHIND = "73,1;72,1;".getBytes();
    private static final byte[] DATA_MORE_THEN_TWO_AHEAD = "73,2;72,1;".getBytes();
    private static final byte[] DATA_VERY_LONG = "73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();
    private static final byte[] DATA_VERY_LONG_UPDATE_IN_FRONT = "74,1;73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();

    @BeforeEach
    public void setup() {
        classUnderTest = new EssenceComparator();
    }

    @Test
    public void compareABeingNullAndBBeingNull() {
        Outcome expectedOutcome = Outcome.EQUAL;
        byte[] expectedDifference = null;
        Data a = null;
        Data b = null;

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareAShortAndBBeingNull() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DATA_SHORT;
        Data a = new ObservedData(DATA_SHORT);
        Data b = null;

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareABeingEmptyAndBBeingEmpty() {
        Outcome expectedOutcome = Outcome.EQUAL;
        byte[] expectedDifference = null;
        Data a = new ObservedData(null);
        Data b = new ObservedData(null);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareAContainingShortAndBBeingEmpty() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DATA_SHORT;
        Data a = new ObservedData(DATA_SHORT);
        Data b = new ObservedData(null);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareABeingEmptyAndBContainingShort() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DATA_SHORT;
        Data a = new ObservedData(null);
        Data b = new ObservedData(DATA_SHORT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareAContainingShortAndBContainingShort() {
        Outcome expectedOutcome = Outcome.EQUAL;
        byte[] expectedDifference = null;
        Data a = new ObservedData(DATA_SHORT);
        Data b = new ObservedData(DATA_SHORT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareAContainingShortPlusOneAndBContainingShort() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_SHORT_PLUS_ONE;
        Data a = new ObservedData(DATA_SHORT_PLUS_ONE);
        Data b = new ObservedData(DATA_SHORT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAContainingShortPlusOneZeroAndBContainingShort() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DATA_BYTE_ZERO;
        Data a = new ObservedData(DATA_SHORT_PLUS_ONE_ZERO);
        Data b = new ObservedData(DATA_SHORT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAContainingShortAndBContainingShortPlusOne() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_SHORT_PLUS_ONE;
        Data a = new ObservedData(DATA_SHORT);
        Data b = new ObservedData(DATA_SHORT_PLUS_ONE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAContainingShortAndBContainingShortWithUpdateInFront() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_SHORT_PLUS_UPDATE_IN_FRONT;
        Data a = new ObservedData(DATA_SHORT);
        Data b = new ObservedData(DATA_SHORT_PLUS_UPDATE_IN_FRONT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAContainingLongAndBBeingEmpty() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DATA_LONG;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(null);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertEquals(expectedDifference, result.getDifference());
    }

    @Test
    public void compareAContainingLongAndBContainingShort() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_SHORT_TO_LONG;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_SHORT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAContainingShortAndBContainingLong() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_SHORT_TO_LONG;
        Data a = new ObservedData(DATA_SHORT);
        Data b = new ObservedData(DATA_LONG);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareALongAndBHavingOneVersionChange() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_ONE_VERSION_CHANGE;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_ONE_VERSION_CHANGE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareALongAndBHavingTwoVersionChange() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_TWO_VERSION_CHANGE;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_TWO_VERSION_CHANGE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingTwoVersionChangeAndBHavingLong() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_TWO_VERSION_CHANGE;
        Data a = new ObservedData(DATA_TWO_VERSION_CHANGE);
        Data b = new ObservedData(DATA_LONG);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingLongAndBHavingShortAndUpdate() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_SHORT_PLUS_UPDATE;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_SHORT_PLUS_UPDATE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingShortAndUpdateAndBHavingLong() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_SHORT_PLUS_UPDATE;
        Data a = new ObservedData(DATA_SHORT_PLUS_UPDATE);
        Data b = new ObservedData(DATA_LONG);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingLongAndBHavingLongWithTwoSeparateUpdates() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_TWO_VERSION_CHANGE_SEPARATE;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_TWO_VERSION_CHANGE_SEPARATE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingLongWithTwoSeparateUpdatesAndBHavingLong() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_TWO_VERSION_CHANGE_SEPARATE;
        Data a = new ObservedData(DATA_TWO_VERSION_CHANGE_SEPARATE);
        Data b = new ObservedData(DATA_LONG);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAHavingLongMoreThenTwoNumbersAndBHavingShortWithUpdateAtTheStart() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_MORE_THEN_TWO_NUMBERS;
        Data a = new ObservedData(DATA_LONG_MORE_THEN_TWO_NUMBERS);
        Data b = new ObservedData(DATA_SHORT_MORE_THEN_TWO_NUMBERS_PLUS_UPDATE);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareABehindShortMoreThenTwoNumbersAndBHavingShortMoreThenTwoAhead() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_MORE_THEN_TWO;
        Data a = new ObservedData(DATA_MORE_THEN_TWO_BEHIND);
        Data b = new ObservedData(DATA_MORE_THEN_TWO_AHEAD);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareABehindLongWithUpdateInFrontFromB() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_UPDATE_IN_FRONT;
        Data a = new ObservedData(DATA_LONG);
        Data b = new ObservedData(DATA_LONG_UPDATE_IN_FRONT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareABehindVeryLongWithUpdateInFrontFromB() {
        Outcome expectedOutcome = Outcome.BEHIND;
        byte[] expectedDifference = DIFF_UPDATE_IN_FRONT;
        Data a = new ObservedData(DATA_VERY_LONG);
        Data b = new ObservedData(DATA_VERY_LONG_UPDATE_IN_FRONT);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }

    @Test
    public void compareAAheadWithVeryLongWithBHavingOnlyFirstTwo() {
        Outcome expectedOutcome = Outcome.AHEAD;
        byte[] expectedDifference = DIFF_TWO_MORE_THEN_TWO_TO_VERY_LONG;
        Data a = new ObservedData(DATA_VERY_LONG);
        Data b = new ObservedData(DATA_MORE_THEN_TWO_BEHIND);

        ComparisonResult result = classUnderTest.compare(a, b);

        assertNotNull(result);
        assertEquals(expectedOutcome, result.getOutcome());
        assertNotEquals(expectedDifference, result.getDifference());
        assertTrue(Arrays.equals(expectedDifference, result.getDifference()));
    }
}
