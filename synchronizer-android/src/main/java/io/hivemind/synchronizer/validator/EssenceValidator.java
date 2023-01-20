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
import io.hivemind.synchronizer.exception.InvalidEssenceException;
import java.util.regex.Pattern;

/**
 * Validator to determine if input is a valid essence
 *
 * @author Patrick-4488
 */
public class EssenceValidator {

    private static final Pattern PATTERN = Pattern.compile("([^\\d,;]+)");

    /**
     * Validate if an essence is valid<br>
     * Will accept null and empty as valid
     *
     * FUTURE_WORK: in future maybe we should support identifiers that contain
     * letters next to just plain id's. If that is the case we should change
     * this validation. For now its super simple.<br>
     * FUTURE_WORK: Currently this uses a simple Regex, which performs decently
     * fine. If at some point optimizations are required this is a candidate to
     * change to bitwise comparison which will avoid heap memory allocations by
     * reading the object in an array without doing any intermediate allocations
     *
     * @param essence the essence to validate
     * @throws InvalidEssenceException when the essence is not valid
     */
    public void validateEssence(final byte[] essence) throws InvalidEssenceException {
        if (essence != null && essence.length > 0) {
            String essenceString = new String(essence).trim();
            if (PATTERN.matcher(essenceString).find()) {
                throw new InvalidEssenceException(HiveCeption.INVALID_ESSENCE);
            }
        } else {
            /**
             * Accepting null and empty as valid, since its part of the
             * functionality to allow synchronization from nothing
             */
        }
    }
}
