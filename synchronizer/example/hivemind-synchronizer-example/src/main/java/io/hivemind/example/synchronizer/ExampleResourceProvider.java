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

import io.hivemind.synchronizer.HiveResource;
import io.hivemind.synchronizer.ResourceProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Example of a resource provider
 *
 * @author Patrick-4488
 */
public class ExampleResourceProvider implements ResourceProvider {

    private final SomeService someService;

    private static final System.Logger LOGGER = System.getLogger(ExampleResourceProvider.class.getName());

    public ExampleResourceProvider(final SomeService someService) {
        this.someService = someService;
    }

    @Override
    public List<HiveResource> provideAllResources() {
        List<SomeData> someData = someService.getAllData();

        return List.copyOf(someData);
    }

    @Override
    public List<HiveResource> provideResources(final List<HiveResource> resources) {
        // Find corresponding data objects based on ids and versions requested
        MatchesIdAndVersionPredicate predicate = new MatchesIdAndVersionPredicate();
        for (HiveResource r : resources) {
            predicate.addToMap(Long.parseLong(r.getIdString()), Long.parseLong(r.getVersionString()));
        }
        List<SomeData> requestedHippos = someService.getData(predicate);
        return List.copyOf(requestedHippos);
    }

    @Override
    public void deleteAllResourcesExcept(final List<HiveResource> resources) {
        // Find data objects not in the list, this is due to a priority request indicated data has been removed
        IsNotInMapPredicate predicate = new IsNotInMapPredicate();
        for (HiveResource r : resources) {
            predicate.addToMap(Long.parseLong(r.getIdString()), Long.parseLong(r.getVersionString()));
        }

        List<SomeData> dataToRemove = someService.getData(predicate);
        someService.deleteData(dataToRemove);
    }

    @Override
    public void saveData(final byte[] bytes) {
        List<SomeData> receivedData = null;
        try ( ByteArrayInputStream bais = new ByteArrayInputStream(bytes);  ObjectInputStream ois = new ObjectInputStream(bais);) {
            receivedData = (List<SomeData>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(ERROR, "Failed to convert byte array to object", ex);
        }

        if (receivedData != null && !receivedData.isEmpty()) {
            someService.addDatas(receivedData);
        } else {
            LOGGER.log(WARNING, """
                Save data was called but either no data was provided or the byte 
                array conversion did not result in any objects""");
        }
    }
}
