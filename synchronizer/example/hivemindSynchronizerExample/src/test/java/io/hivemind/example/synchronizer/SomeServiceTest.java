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

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Some tests for some service
 *
 * @author Patrick
 */
public class SomeServiceTest {

    private SomeService classUnderTest;

    private static final int AMOUNT_OF_DATA_OBJECTS = 10;

    @BeforeEach
    public void setUp() {
        classUnderTest = new SomeService(AMOUNT_OF_DATA_OBJECTS);
    }

    @Test
    public void getAllData() {
        List<SomeData> datas = classUnderTest.getAllData();

        assertEquals(AMOUNT_OF_DATA_OBJECTS, datas.size());
    }

    @Test
    public void getData() {
        long smallerThen = 3;

        List<SomeData> datas = classUnderTest.getData(s -> (long) s.getId() < smallerThen);

        assertEquals((smallerThen), datas.size());
    }

    @Test
    public void deleteData() {
        SomeData toDelete = new SomeData(1, 1, "data", "someMoreDate", false);

        classUnderTest.deleteData(List.of(toDelete));
        List<SomeData> datas = classUnderTest.getAllData();

        assertEquals((AMOUNT_OF_DATA_OBJECTS - 1), datas.size());
    }

    @Test
    public void addDatas() {
        SomeData toAdd = new SomeData(11, 1, "data", "someMoreDate", false);
        SomeData toAddSecond = new SomeData(12, 1, "data", "someMoreDate", true);

        List<SomeData> someDatas = List.of(toAdd, toAddSecond);
        classUnderTest.addDatas(someDatas);
        List<SomeData> datas = classUnderTest.getAllData();

        assertEquals((AMOUNT_OF_DATA_OBJECTS + 2), datas.size());
    }
}
