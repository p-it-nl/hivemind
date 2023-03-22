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
package io.hivemind.synchronizer.constant;

/**
 * Identification for content types the application differs in. The supported
 * content type is per synchronizer, currently supporting:
 * <ul>
 * <li>- Hive essence</li>
 * <li>- JSON</li>
 * </ul>
 * Android currently only supports JSON for data
 *
 * @author Patrick-4488
 */
public enum ContentType {

    HIVE_ESSENCE("application/hive-essence"),
    JSON("application/json");

    private final String value;
    private static final String SLASH = "/";

    ContentType(final String value) {
        this.value = value;
    }

    /**
     * @return Get the content type value
     */
    public String getValue() {
        return value;
    }

    /**
     * Determine the content type based on the value. This is the preferred way
     * to create this enum.
     *
     * @param value the value to determine the content type for
     * @return the content type (either HIVE_ESSENCE or OTHER)
     */
    public static ContentType enumFor(final String value) {
        if (value != null && !value.isEmpty()) {
            String lowerValue = value.toLowerCase();
            String hiveEssence = HIVE_ESSENCE.getValue();
            String secondPart = hiveEssence.split(SLASH)[1];
            if (hiveEssence.equals(lowerValue) || secondPart.equals(lowerValue)) {
                return HIVE_ESSENCE;
            }
        }

        return JSON;
    }
}