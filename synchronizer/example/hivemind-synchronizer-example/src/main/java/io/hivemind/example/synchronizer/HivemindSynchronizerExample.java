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
package io.hivemind.example.synchronizer;

import io.hivemind.synchronizer.HiveSynchronizer;
import io.hivemind.synchronizer.ResourceProvider;
import io.hivemind.synchronizer.configuration.SynchronizerConfiguration;
import io.hivemind.synchronizer.constant.ConsistencyModel;
import io.hivemind.synchronizer.exception.HiveSynchronizationException;
import io.hivemind.synchronizer.exception.NotSupportedException;

import static java.lang.System.Logger.Level.ERROR;

/**
 * Main class for synchronizer example
 *
 * @author Patrick
 */
public class HivemindSynchronizerExample {

    private static final System.Logger LOGGER = System.getLogger(HivemindSynchronizerExample.class.getName());

    /**
     * Starts the application
     *
     * @param args the arguments to start with, not expecting any
     */
    public static void main(final String[] args) {
        try {
            SynchronizerConfiguration config = new SynchronizerConfiguration("http://localhost:8000", ConsistencyModel.EVENTUAL_CONSISTENCY);
            config.setPeriodBetweenRequests(15);

            SomeService service = new SomeService(10);
            ResourceProvider provider = new ExampleResourceProvider(service);

            HiveSynchronizer synchronizer = new HiveSynchronizer(provider, config);

            synchronizer.startSynchronization();
        } catch (NotSupportedException | HiveSynchronizationException ex) {
            LOGGER.log(ERROR, "Error starting sychronizer, unsupported consistency model requested", ex);
        }
    }
}
