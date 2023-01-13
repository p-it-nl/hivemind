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
package io.hivemind.data;

import io.hivemind.constant.ContentType;
import io.hivemind.exception.InvalidEssenceException;
import io.hivemind.test.util.TestUtil;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for data processor
 *
 * @author Patrick-4488
 */
@ExtendWith(MockitoExtension.class)
public class DataProcessorTest {

    private static DataProcessor classUnderTest;

    private static final byte[] DIFF_SHORT_UPDATE_AFTER_PRIORITY = "73,2;".getBytes();
    private static final byte[] DIFF_LONG_UPDATE = "49,2;".getBytes();
    private static final byte[] DIFF_LONG_UPDATE_IN_FRONT = "74,1;".getBytes();
    private static final byte[] DIFF_SHORT_TO_LONG = "71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();
    private static final byte[] DATA_SHORT = "73,1;72,1;".getBytes();
    private static final byte[] DATA_SHORT_WITH_UPDATE = "73,2;72,1;".getBytes();
    private static final byte[] DATA_LONG = "73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();
    private static final byte[] DATA_LONG_UPDATE = "73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,2;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();
    private static final byte[] DATA_LONG_UPDATE_IN_FRONT = "74,1;73,1;72,1;71,1;70,1;69,1;68,1;67,1;66,1;65,1;64,1;63,1;61,1;60,1;59,1;58,1;57,1;56,1;54,1;53,1;52,1;51,1;49,1;46,1;45,1;34,1;33,1;32,1;29,1;30,1;27,1;26,1;25,1;24,1;23,1;22,1;21,1;20,1;19,1;18,1;17,1;16,1;15,1;14,1;13,1;12,1;11,1;10,1;6,1;5,1;4,1;2,1;1,1;".getBytes();
    private static final byte[] DATA_FROM_A = "mock".getBytes();
    private static final byte[] DATA_FROM_B = "mockb".getBytes();
    private static final byte[] DATA_FROM_A_LONG = "longdatamock".getBytes();
    private static final byte[] DATA_FROM_B_SHORT_UPDATE = "shortmockdataupdate".getBytes();
    private static final byte[] DATA_FROM_B_LONG_UPDATE = "longdatamockupdate".getBytes();
    private static final byte[] DATA_FROM_C_LONG = "longdatamock".getBytes();
    private static final String TRACEPARENT_A = "1a";
    private static final String TRACEPARENT_B = "2b";
    private static final String TRACEPARENT_C = "3c";

    @BeforeAll
    public static void setup() {
        classUnderTest = DataProcessor.getInstance();
    }

    @AfterEach
    public void after() {
        classUnderTest.clearAllState();
    }

    @Test
    public void processDataWithoutAnyValues() throws InvalidEssenceException {
        byte[] data = null;
        String traceparent = null;
        ContentType contentType = null;

        PreparedData preparedData = classUnderTest.processData(data, contentType, traceparent);

        assertNull(preparedData);
    }

    @Test
    public void processDataWithoutDataButHavingContentTypeAndTraceparent() throws InvalidEssenceException {
        byte[] data = null;
        String traceparent = TRACEPARENT_A;
        ContentType contentType = ContentType.HIVE_ESSENCE;

        PreparedData preparedData = classUnderTest.processData(data, contentType, traceparent);

        assertNull(preparedData);
    }

    @Test
    public void processDataWithEmptyDataButHavingContentTypeAndTraceparent() throws InvalidEssenceException {
        byte[] data = new byte[0];
        String traceparent = TRACEPARENT_A;
        ContentType contentType = ContentType.HIVE_ESSENCE;

        PreparedData preparedData = classUnderTest.processData(data, contentType, traceparent);

        assertNull(preparedData);
    }

    @Test
    public void processDataWithDataButNoContentTypeAndTraceparent() throws InvalidEssenceException {
        byte[] data = DATA_SHORT;
        String traceparent = null;
        ContentType contentType = null;

        PreparedData preparedData = classUnderTest.processData(data, contentType, traceparent);

        assertNull(preparedData);
    }

    @Test
    public void processDataReceivingPreparedDataFromAWithBBeingBehindFromNull() throws InvalidEssenceException {
        byte[] essenceA = DATA_SHORT;
        byte[] dataA = DATA_FROM_A;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = null;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, firstResultB, thirdResultA);
        TestUtil.assertAllNotNull(secondResultA, secondResultB);

        assertEquals(DataRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(essenceA, secondResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingLongPreparedDataFromAWithBBeingBehindFromShort() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        byte[] dataA = DATA_FROM_A_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, firstResultB, thirdResultA, thirdResultB);
        TestUtil.assertAllNotNull(secondResultA, secondResultB);

        assertEquals(DataRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_SHORT_TO_LONG, secondResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingLongPreparedDataFromAWithBBeingAheadWithShort() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultB);
        TestUtil.assertAllNotNull(secondResultA);

        assertEquals(PriorityRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(essenceB, secondResultA.getData()) == 0);
    }

    @Test
    public void processDataReceivingShortWithUpdatePreparedDataFromBWithBBeingAheadWithShortAndUpdate() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        byte[] essenceAAfterPriority = DATA_SHORT;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT_WITH_UPDATE;
        byte[] dataB = DATA_FROM_B_SHORT_UPDATE;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(essenceAAfterPriority, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData fourthResultB = classUnderTest.processData(dataB, ContentType.OTHER, traceparentB);
        PreparedData fourthResultA = classUnderTest.processData(essenceAAfterPriority, ContentType.HIVE_ESSENCE, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultB, thirdResultA, fourthResultB);
        TestUtil.assertAllNotNull(secondResultA, thirdResultB, fourthResultA);

        assertEquals(PriorityRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(essenceB, secondResultA.getData()) == 0);
        assertEquals(DataRequest.class.getSimpleName(), thirdResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_SHORT_UPDATE_AFTER_PRIORITY, thirdResultB.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), fourthResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataB, fourthResultA.getData()) == 0);
    }

    @Test
    public void processDataReceivingLongWithUpdatePreparedDataFromBWithBBeingAheadWithLongAndUpdate() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_LONG_UPDATE;
        byte[] dataB = DATA_FROM_B_LONG_UPDATE;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultB = classUnderTest.processData(dataB, ContentType.OTHER, traceparentB);
        PreparedData thirdResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultA, thirdResultB);
        TestUtil.assertAllNotNull(secondResultB, thirdResultA);

        assertEquals(DataRequest.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_LONG_UPDATE, secondResultB.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), thirdResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataB, thirdResultA.getData()) == 0);
    }

    @Test
    public void processDataReceivingLongWithUpdatePreparedDataFromAWithBAndCBehindFromNull() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] dataA = DATA_FROM_A;
        byte[] essenceB = null;
        String traceparentB = TRACEPARENT_B;
        byte[] essenceC = null;
        String traceparentC = TRACEPARENT_C;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData firstResultC = classUnderTest.processData(essenceC, ContentType.HIVE_ESSENCE, traceparentC);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultC = classUnderTest.processData(essenceC, ContentType.HIVE_ESSENCE, traceparentC);

        TestUtil.assertAllNull(firstResultA, firstResultB, firstResultC, thirdResultA);
        TestUtil.assertAllNotNull(secondResultA, secondResultB, secondResultC);

        assertEquals(DataRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(DATA_LONG, secondResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultC.getData()) == 0);
    }

    @Test
    public void processDataReceivingLongWithUpdatePreparedDataFromCWithAAndBBehindFromShort() throws InvalidEssenceException {
        byte[] essenceA = DATA_SHORT;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT;
        String traceparentB = TRACEPARENT_B;
        byte[] essenceC = DATA_LONG;
        String traceparentC = TRACEPARENT_C;
        byte[] dataC = DATA_FROM_C_LONG;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData firstResultC = classUnderTest.processData(essenceC, ContentType.HIVE_ESSENCE, traceparentC);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultC = classUnderTest.processData(essenceC, ContentType.HIVE_ESSENCE, traceparentC);
        PreparedData thirdResultC = classUnderTest.processData(dataC, ContentType.OTHER, traceparentC);
        PreparedData thirdResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, firstResultB, firstResultC, secondResultA, secondResultB, thirdResultC);
        TestUtil.assertAllNotNull(secondResultC, thirdResultA, thirdResultB);

        assertEquals(DataRequest.class.getSimpleName(), secondResultC.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_SHORT_TO_LONG, secondResultC.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), thirdResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataC, thirdResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), thirdResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataC, thirdResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingNoDataForAThenLongDataForAThenShortFromB() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT;
        String traceparentB = TRACEPARENT_B;
        byte[] dataA = DATA_FROM_A_LONG;

        PreparedData firstResultA = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData fourthResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, secondResultA, firstResultB, fourthResultA);
        TestUtil.assertAllNotNull(thirdResultA, secondResultB);

        assertEquals(DataRequest.class.getSimpleName(), thirdResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_SHORT_TO_LONG, thirdResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingNoDataForATwoTimesThenLongDataForAThenShortFromB() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_SHORT;
        String traceparentB = TRACEPARENT_B;
        byte[] dataA = DATA_FROM_A_LONG;

        PreparedData firstResultA = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultA = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData fourthResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData fifthResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, secondResultA, thirdResultA, firstResultB, fifthResultA);
        TestUtil.assertAllNotNull(fourthResultA, secondResultB);

        assertEquals(DataRequest.class.getSimpleName(), fourthResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(DIFF_SHORT_TO_LONG, fourthResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingPriorityForAHavingBBeindAheadAndDeletedAll() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        byte[] essenceB = DATA_LONG;
        String traceparentB = TRACEPARENT_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultA);
        TestUtil.assertAllNotNull(secondResultB);

        assertEquals(PriorityRequest.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(secondResultB.getData() == null);
    }

    @Test
    public void processDataReceivingDataFromAAfterBNotRetrievedPreparedDataAfterSecondUpdateA() throws InvalidEssenceException {
        byte[] essenceA = DATA_SHORT;
        byte[] secondEssenceA = DATA_LONG;
        byte[] diffEssenceAAndSecond = DIFF_SHORT_TO_LONG;
        String traceparentA = TRACEPARENT_A;
        String traceparentB = TRACEPARENT_B;
        byte[] dataA = DATA_FROM_A;
        byte[] secondDataA = DATA_FROM_A_LONG;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData thirdResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData fourthResultA = classUnderTest.processData(secondEssenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultB = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData fifthResultA = classUnderTest.processData(secondEssenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData sixedResultA = classUnderTest.processData(secondDataA, ContentType.OTHER, traceparentA);
        PreparedData fourthResultB = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData sevenstResultA = classUnderTest.processData(secondDataA, ContentType.OTHER, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, thirdResultA, fourthResultA, thirdResultB, sixedResultA, sevenstResultA);
        TestUtil.assertAllNotNull(secondResultA, secondResultB, fifthResultA, fourthResultB);

        assertEquals(DataRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(essenceA, secondResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, secondResultB.getData()) == 0);
        assertEquals(DataRequest.class.getSimpleName(), fifthResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(diffEssenceAAndSecond, fifthResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), fourthResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(secondDataA, fourthResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingDataFromAAfterBSendingBehindTwiceResultingInJustOneDataRequest() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        String traceparentB = TRACEPARENT_B;
        byte[] dataA = DATA_FROM_A;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultA = classUnderTest.processData(dataA, ContentType.OTHER, traceparentA);
        PreparedData thirdResultB = classUnderTest.processData(null, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData fourthResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultB, thirdResultA, fourthResultA);
        TestUtil.assertAllNotNull(secondResultA, thirdResultB);

        assertEquals(DataRequest.class.getSimpleName(), secondResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(essenceA, secondResultA.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), thirdResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataA, thirdResultB.getData()) == 0);
    }

    @Test
    public void processDataReceivingDataFromBAfterABehindWithChangeInFrontOfEssence() throws InvalidEssenceException {
        byte[] essenceA = DATA_LONG;
        String traceparentA = TRACEPARENT_A;
        String traceparentB = TRACEPARENT_B;
        byte[] essenceB = DATA_LONG_UPDATE_IN_FRONT;
        byte[] difference = DIFF_LONG_UPDATE_IN_FRONT;
        byte[] dataB = DATA_FROM_B;

        PreparedData firstResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData firstResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData secondResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);
        PreparedData secondResultB = classUnderTest.processData(essenceB, ContentType.HIVE_ESSENCE, traceparentB);
        PreparedData thirdResultB = classUnderTest.processData(dataB, ContentType.OTHER, traceparentB);
        PreparedData fourthResultA = classUnderTest.processData(essenceA, ContentType.HIVE_ESSENCE, traceparentA);

        TestUtil.assertAllNull(firstResultA, firstResultB, secondResultA, thirdResultB);
        TestUtil.assertAllNotNull(secondResultB, fourthResultA);

        assertEquals(DataRequest.class.getSimpleName(), secondResultB.getClass().getSimpleName());
        assertTrue(Arrays.compare(difference, secondResultB.getData()) == 0);
        assertEquals(PreparedData.class.getSimpleName(), fourthResultA.getClass().getSimpleName());
        assertTrue(Arrays.compare(dataB, fourthResultA.getData()) == 0);
    }
}
