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

/**
 * The essence data provider is used to determine the essence of a given set of
 * data and processes received data based on essence.It is the only endpoint for
 * the synchronization and is the only object to be provided by the consuming
 * application.<br>
 * The essence data provider must be passed to the constructor of the
 * synchronizer and will then be connected to synchronization.
 *
 * NOTICE: This is interface is package private to limit usage within the
 * synchronizer only. This to prevent unexpected abstraction from synchronizers.
 *
 * @author Patrick-4488
 */
interface EssenceDataProvider {

    /**
     * Determine the essence to communicate with the Hivemind.
     * <p>
     * The essence is the essential information needed to determine the state of
     * data. For example: a string containing the id + version + ; of each
     * object in ordered sequence
     * <p>
     * With this example, consider objects<br>
     * { id=2, version=1 };, { id=4, version=2 };, { id=1, version=2 };<br>
     * Then the essence will be: 21;42;12;</p>
     * <p>
     * This indicates that the current version of the data in the application.
     *
     * The possibilities are endless as long as the translation can be done
     * bidirectional
     *
     * @return the hive essence
     */
    public byte[] determineEssence();

    /**
     * Determine the data that corresponds to the given essence. This will be
     * called when the application is requested by Hivemind to provide data. The
     * data requested is determined by the essence.
     * <p>
     * Given example where when essence 42;12; is requested, the corresponding
     * data would be objects with id 4 version 2 and id 1 version 2
     * <p>
     * FUTURE_WORK: Allow to not just return the data, but also the content-type
     * to enable support of everything. Currently just expecting serialized
     * objects
     * </p>
     *
     * @param essence the essence to provide data for
     * @return the data or empty
     */
    public byte[] getDataForEssence(final byte[] essence);

    /**
     * Process priority essence. This is an essence that has to be processed
     * before synchronization can continue. This is mostly due to deletion of
     * objects
     *
     * @param essence the essence to process
     */
    public void processPriorityEssence(final byte[] essence);

    /**
     * Save incoming data
     *
     * @param data the data received
     */
    public void saveData(final byte[] data);
}
