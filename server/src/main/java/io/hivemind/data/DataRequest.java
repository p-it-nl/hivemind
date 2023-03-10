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
 * When data is to be requested from a synchronizer, this object allows passing
 * an essence that corresponds to the requested data
 *
 * @author Patrick-4488
 */
public class DataRequest extends PreparedData {

    /**
     * New data request for an essence
     *
     * @param essence the essence to create for
     */
    public DataRequest(final byte[] essence) {
        super(essence);
    }

    /**
     * New data request for an essence
     *
     * @param essence the essence to create for
     * @param requestedType the (specific) requested content type
     */
    public DataRequest(final byte[] essence, final String requestedType) {
        super(essence, requestedType);
    }
}
