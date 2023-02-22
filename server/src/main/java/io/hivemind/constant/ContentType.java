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

/**
 * Identification for content types the application differs in. Currently only
 * interested in it being hive-essence or not
 *
 * FUTURE_WORK: Implement more content-types, currently only working with
 * serialized objects
 *
 * @author Patrick-4488
 */
public enum ContentType {

    HIVE_ESSENCE("application/hive-essence"),
    JSON("application/json"),
    OTHER("application/ser");

    private final String value;
    private static final String SLASH = "/";

    ContentType(final String value) {
        this.value = value;
    }

    /**
     * @return Get the content-type
     */
    public String getValue() {
        return value;
    }

    /**
     * Determine the content type based on the value. This is the preferred way
     * to create this enum.
     *
     * FUTURE_WORK: The IF method chaining is not future proof, make something
     * better and test is
     *
     * @param value the value to determine the content-type for
     * @return the content-type (either HIVE_ESSENCE or OTHER)
     */
    public static ContentType enumFor(final String value) {
        if (value != null && !value.isEmpty()) {
            String lowerValue = value.toLowerCase();
            String hiveEssence = HIVE_ESSENCE.getValue();
            String json = JSON.getValue();
            String secondPart = hiveEssence.split(SLASH)[1];
            String secondPartJson = json.split(SLASH)[1];
            if (hiveEssence.equals(lowerValue) || secondPart.equals(lowerValue)) {
                return HIVE_ESSENCE;
            } else if (json.equals(lowerValue) || secondPartJson.equals(lowerValue)) {
                return JSON;
            }
        }

        return OTHER;
    }
}
