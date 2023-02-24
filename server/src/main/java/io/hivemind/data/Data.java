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
import java.time.Instant;

/**
 * Data object
 *
 * @author Patrick-4488
 */
public interface Data {

    /**
     * @return whether this object contains data<br>
     * - if not it is either cleaned or representing an empty data object
     */
    public boolean hasData();

    /**
     * @return the data or null
     */
    public byte[] getData();

    /**
     * @return the instant from when this data exists
     */
    public Instant getSince();

    /**
     * @return the requested content type or null
     * @see ContentType
     */
    public String getRequestedType();

}
