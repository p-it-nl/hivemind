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
package io.hivemind.synchronizer.data.conversion;

import io.hivemind.synchronizer.HiveResource;
import java.util.List;

/**
 * Base converter functionality, allowing to enable / disable reflection
 *
 * @author Patrick-4488
 */
public abstract class ResourceConverter implements Converter {

    protected boolean withReflection = true;
    protected boolean continueOnException = false;

    @Override
    public abstract byte[] toBytes(final Object... objects) throws Exception;

    @Override
    public abstract List<HiveResource> fromBytes(final byte[] data);

    /**
     * Since reflection can hinder performance, especially on Android it is
     * worthwhile to attempt to provide conversion without reflection
     */
    public void disableReflection() {
        withReflection = false;
    }

    /**
     * Enable reflection, this is default
     */
    public void enableReflection() {
        withReflection = true;
    }

    /**
     * When called, the converter will continue best effort to generate a
     * result. The generated result might be incomplete, due to exceptions
     * preventing to retrieve parts of the information
     */
    public void continueOnException() {
        continueOnException = true;
    }

    /**
     * Stop execution when a exception occurs, this will result in an exception
     * thrown and not having a result. This is default
     */
    public void stopOnException() {
        continueOnException = false;
    }
}
