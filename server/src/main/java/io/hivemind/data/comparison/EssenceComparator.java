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
package io.hivemind.data.comparison;

import io.hivemind.constant.Outcome;
import io.hivemind.data.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.System.Logger.Level.ERROR;
import java.util.List;

/**
 * Comparator used to determine whether A or B is behind, equal or before
 *
 * @author Patrick-4488
 */
public class EssenceComparator {

    // FUTURE_WORK: move to configuration?
    private static final String SEPARATOR = ";";

    private static final System.Logger LOGGER = System.getLogger(EssenceComparator.class.getName());

    /**
     * Compare data A with data B to determine whether or not not data is ahead,
     * equal or behind and which data
     * <p>
     * Notice: this method will compare any given data but is mend for essences
     * </p>
     * IMPORTANT: Do not make static, comparisons will be often and concurrent.
     * Having a separate comparator per request will prevent tons of headaches
     *
     * @param a data A
     * @param b data B
     * @return the comparison result
     */
    public ComparisonResult compare(final Data a, final Data b) {
        ComparisonResult result = compareObjects(a, b);
        if (result == null && a != null && b != null) {
            result = compareData(a, b);
        }

        return result;
    }

    private ComparisonResult compareObjects(final Data a, final Data b) {
        if (a != null && a.hasData() && (b == null || !b.hasData())) {
            return new ComparisonResult(Outcome.AHEAD, a.getData());
        } else if ((a == null || !a.hasData()) && (b != null && b.hasData())) {
            return new ComparisonResult(Outcome.BEHIND, b.getData());
        } else if (a == null && b == null) {
            return new ComparisonResult(Outcome.EQUAL, null);
        } else {
            return null;
        }
    }

    private ComparisonResult compareData(final Data a, final Data b) {
        byte[] dataA = a.getData();
        byte[] dataB = b.getData();

        if (dataA == dataB || Arrays.equals(dataA, dataB)) {
            return new ComparisonResult(Outcome.EQUAL, null);
        } else {
            try ( ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                try ( ByteArrayOutputStream mirror = new ByteArrayOutputStream()) {
                    Outcome outcome = findDifferences(dataA, dataB, os, mirror);
                    if (dataA.length == dataB.length) {
                        outcome = determineLatest(outcome, os, mirror);
                    }

                    return new ComparisonResult(outcome, os.toByteArray());
                }
            } catch (IOException ex) {
                LOGGER.log(ERROR, "Error while trying to write bytes during comparision", ex);
            }
        }

        return null;
    }

    /**
     * This will find all the difference reading forward from index 0 to index
     * of longest data (a,b). When a first difference has been found, will write
     * either of the bytes from A or B depending on the temporary outcome.
     * <p>
     * Consider the following differences to be found:<br>
     * - A has byte{i} and B does not have byte{i} -> A is ahead, will write
     * bytes A as difference<br>
     * - B has byte{i} and A does not have byte{i} -> A is behind, will write
     * bytes B as difference<br>
     * - A has byte{i} and B has byte{i} but they differ -> if A length is
     * bigger then B length, A is ahead* else A is behind* -> write both bytes
     * to either difference (os) or mirror till the end of the longest data<br>
     * </p>
     * <p>
     * *this merely indicates that based on the hive essence this result is
     * determined. Whether or not this is truly the case requires more checks
     * like validating that the hive essence of A has previously been received
     * by B. Indicating a update.
     * </p>
     */
    private Outcome findDifferences(final byte[] dataA, final byte[] dataB, final ByteArrayOutputStream os, final ByteArrayOutputStream mirror) throws IOException {
        Outcome outcome;

        String sequenceA = new String(dataA);
        String sequenceB = new String(dataB);

        Set<String> partsA = new LinkedHashSet<>();
        partsA.addAll(List.of(sequenceA.split(SEPARATOR)));

        Set<String> differences = new LinkedHashSet<>(partsA);
        for (String partB : sequenceB.split(SEPARATOR)) {
            if (partsA.contains(partB)) {
                differences.remove(partB);
            } else {
                differences.add(partB);
            }
        }

        int countA = 0;
        int countB = 0;
        byte[] separatorByte = SEPARATOR.getBytes();
        for (String diff : differences) {
            if (partsA.contains(diff)) {
                mirror.write(diff.getBytes());
                mirror.write(separatorByte);

                countA++;
            } else {
                os.write(diff.getBytes());
                os.write(separatorByte);

                countB++;
            }
        }

        if (countA > countB) {
            outcome = Outcome.AHEAD;
            os.reset();
            mirror.writeTo(os);
        } else {
            outcome = Outcome.BEHIND;
        }

        return outcome;
    }

    /**
     * When one or more versions has updated in essences with the same length,
     * it is unclear whether A is ahead or behind. This method calculates
     * whether A or B is ahead and will adjust the outcome and the resulting
     * difference accordingly. <br>
     * <p>
     * Given A -> 11;23;33;41; and B -> 11;21;31;41; -> the difference will be
     * -> 21;31 (B) and mirror 23;33 (A) since when equal in length it is at
     * first assumed that A is behind. After calculation is found that actually
     * A is ahead and the outcome is changed accordingly</p>
     */
    private Outcome determineLatest(final Outcome temporaryOutcome, final ByteArrayOutputStream os, final ByteArrayOutputStream mirror) throws IOException {
        Outcome outcome = temporaryOutcome;
        byte[] diffResult = os.toByteArray();
        byte[] mirrorResult = mirror.toByteArray();
        int totalD = 0;
        int totalM = 0;
        for (int i = 0; i < diffResult.length; i++) {
            totalD += diffResult[i];
            totalM += mirrorResult[i];

            if (totalD > totalM) {
                outcome = Outcome.BEHIND;
            } else if (totalD < totalM) {
                outcome = Outcome.AHEAD;
                os.reset();
                os.write(mirror.toByteArray());
            }

            if (totalD != totalM) {
                break;
            }
        }

        return outcome;
    }
}
