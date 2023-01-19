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

/**
 * Example of hive resource, this instance provides only a set of example values
 * of data. The HiveResource may contain any variables and methods you require.
 *
 * @author Patrick-4488
 */
public class SomeData extends HiveResource {

    private final long id;
    private final long version;
    private final String data;
    private final String someMoreData;
    private final boolean someBooleanData;

    public SomeData(final long id, final long version, final String data, final String someMoreData, final boolean someBooleanData) {
        this.id = id;
        this.version = version;
        this.data = data;
        this.someMoreData = someMoreData;
        this.someBooleanData = someBooleanData;
    }
    
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Object getVersion() {
        return version;
    }

    public String getData() {
        return data;
    }

    public String getSomeMoreData() {
        return someMoreData;
    }

    public boolean isSomeBooleanData() {
        return someBooleanData;
    }    
}
