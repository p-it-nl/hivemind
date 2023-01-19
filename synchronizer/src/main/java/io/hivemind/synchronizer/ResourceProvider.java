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
package io.hivemind.synchronizer;

import java.util.List;

/**
 * Resource provider is called on to retrieve data which is used to distribute
 * essence or data to the Hivemind
 *
 * @author Patrick-4488
 */
public interface ResourceProvider {

    /**
     * This method is called when the synchronizer is determining the hive
     * essence for this synchronizer. The provided list may be the list of
     * entire objects but is better suited to be a list of all the objects
     * having only the id and version or references to the objects. This will
     * prevent unnecessary duplication of data on the heap.
     *
     * @return the list of objects
     */
    public List<HiveResource> provideAllResources();

    /**
     * This method is called when the synchronizer is preparing data to be send
     * to the Hivemind.This method should return the full object as represented
     * within the application as is to be shared with other synchronizers.
     *
     * @param requestedResources the resources requested, which contains object
     * with only having the id and version, and are missing the other data
     * @return the list of objects with the other data
     */
    public List<HiveResource> provideResources(final List<HiveResource> requestedResources);

    /**
     * As part of a priority request a priority essence is provided. This is an
     * essence that has to be processed before synchronization can continue.
     * This is mostly due to deletion of objects and results in calling this
     * method
     *
     * @param resourcesToKeep the resources that have not been removed (update
     * from other synchronizer(s))
     */
    public void deleteAllResourcesExcept(final List<HiveResource> resourcesToKeep);

    /**
     * Save incoming data
     *
     * @param data the data received
     */
    public void saveData(final byte[] data);
}
