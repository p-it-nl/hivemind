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

/**
 * Data that has been prepared to be returned to or send by a synchronizer
 *
 * @author Patrick-4488
 */
public class PreparedData extends ObservedData {

    /**
     * Prepared data request for an essence
     *
     * @param data the data to create for
     */
    public PreparedData(final byte[] data) {
        super(data);
    }

    /**
     * Prepared data request for an essence
     *
     * @param data the data to create for
     * @param requestedType the type of data (as requested)
     */
    public PreparedData(final byte[] data, final String requestedType) {
        super(data, requestedType);
    }
}
