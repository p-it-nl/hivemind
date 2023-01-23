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
package io.hivemind.manager;

import io.hivemind.data.DataProcessor;
import static java.lang.System.Logger.Level.INFO;

/**
 * Hive manager provides methods to observe hive health and keeps constant track
 * of hive health while performing tasks when required
 *
 * @author Patrick-4488
 */
public class HiveManager {

    private final DataProcessor dataProcessor;

    private static final System.Logger LOGGER = System.getLogger(HiveManager.class.getName());

    public HiveManager() {
        this.dataProcessor = DataProcessor.getInstance();
    }

    /**
     * Will remove data that is no longer of any use or used for any activity.
     * e.g. observed data from more then X amount of requests ago. This will
     * free up some memory and keep the hive clean.
     */
    public void clearInertState() {
        LOGGER.log(INFO, "Removing inert state");

        dataProcessor.cleanOlderState();

        LOGGER.log(INFO, "Removed inert state");
    }

    /**
     * Will remove all data, completely resetting the hive and freeing up all
     * memory in use.
     */
    public void clearAllState() {
        LOGGER.log(INFO, "Removing all state");

        dataProcessor.clearAllState();

        LOGGER.log(INFO, "Removed all state");
    }
}
