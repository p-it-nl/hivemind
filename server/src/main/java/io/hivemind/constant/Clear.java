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
import io.hivemind.exception.HiveRequestException;
import io.hivemind.manager.HiveManager;

/**
 * Types of clearing action
 *
 * @author Patrick-4488
 */
public enum Clear {

    /**
     * Will result in clearing all state
     *
     * @see HiveManager
     */
    ALL,
    /**
     * Will result in clearing inert state
     *
     * @see HiveManager
     */
    INERT;

    /**
     * Determine the clear value
     *
     * @param value the value to determine the clear for
     * @return the clear value
     * @throws io.hivemind.exception.HiveRequestException when unexpected value
     * has been received
     */
    public static Clear enumFor(String value) throws HiveRequestException {
        if (value != null) {
            value = value.toLowerCase();

            if (ALL.toString().toLowerCase().equals(value)) {
                return ALL;
            } else if (INERT.toString().toLowerCase().equals(value)) {
                return INERT;
            }
        }

        throw new HiveRequestException(HiveCeption.UNEXPECTED_VALUE_FOR_CLEAR_RECEIVED_BY_MANAGER);
    }
}
