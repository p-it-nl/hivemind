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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Some service to show an example of how to layer the code architecture
 *
 * @author Patrick-4488
 */
public class SomeService {

    private final List<SomeData> someData;

    public SomeService(final int amountDataObjects) {
        someData = new ArrayList<>();

        for (int i = 0; i < amountDataObjects; i++) {
            someData.add(new SomeData(i, 1, "data", "someMoreDate", (i % 2 == 0)));
        }
    }

    /**
     * @return all data
     */
    public List<SomeData> getAllData() {
        return someData;
    }

    /**
     * @param predicate the predicate
     * @return the result of the predicate
     */
    public List<SomeData> getData(final Predicate<SomeData> predicate) {
        return someData.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * @param dataToRemove data to remove
     */
    public void deleteData(final List<SomeData> dataToRemove) {
        for (SomeData data : dataToRemove) {
            someData.removeIf(s -> s.getId().equals(data.getId()) && s.getVersion().equals(data.getVersion()));
        }

    }

    /**
     * @param receivedData data to add
     */
    public void addDatas(final List<SomeData> receivedData) {
        someData.addAll(receivedData);
    }

}
