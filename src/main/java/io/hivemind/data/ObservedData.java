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

import java.lang.ref.Cleaner;
import java.time.Instant;

/**
 * Data that has been observed, meaning it has been received, understood and is
 * ready to be used in comparisons
 *
 * @author Patrick-4488
 */
public class ObservedData implements Data, AutoCloseable {

    private final State state;
    private final Cleaner.Cleanable cleanable;

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * Static nested class avoids accidentally retaining the reference
     */
    static class State implements Runnable {

        private byte[] data;
        private final Instant timestamp;

        State(final byte[] data) {
            this.data = data;
            this.timestamp = Instant.now();
        }

        @Override
        public void run() {
            this.data = new byte[0];
        }
    }

    public ObservedData(final byte[] data) {
        this.state = new State(data);
        this.cleanable = cleaner.register(this, state);
    }

    @Override
    public boolean hasData() {
        return this.state.data != null && this.state.data.length > 0;
    }

    @Override
    public byte[] getData() {
        return this.state.data;
    }

    @Override
    public Instant getSince() {
        return this.state.timestamp;
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
