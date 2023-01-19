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
package io.hivemind.constant;

/**
 * The consistency model<br>
 *
 * FUTURE_WORK: Move this to documentation <br>
 * Chose the consistency model based on:
 * <ul>
 * <li>Available RAM/Cpu's</li>
 * <li>Latency</li>
 * <li>Connection type</li>
 * <li>...</li>
 * </ul>
 *
 * @author Patrick-4488
 */
public enum ConsistencyModel {

    /**
     * <p>
     * Works the same as eventual consistency, but will less frequently attempt
     * to update the replica. This might result in a longer time to become
     * consistent but will significantly lower the amount of requests. This is
     * useful when on low latency or on a mobile environment that is not
     * connected to wi-fi.
     * </p>
     * Server: if a server is running in this model, all synchronizers will have
     * to be consistent with this model. Enforcing low frequency.
     * <br>
     * Synchronizer: allows to be specified the time between requests and all
     * requests are optional
     */
    LOW_FREQUENCY_EVENTUAL_CONSISTENCY,
    /**
     * <p>
     * Uses optimistic replication. Replicas are allowed to diverge. Replica's
     * are available without consistency guarantee, over time while the systems
     * functions long enough, the state of the replica becomes consistent.
     * </p>
     * Server: if a server is running in this model, all synchronizers can use
     * either the same or lower, meaning a synchronizer may use
     * LOW_FREQUENCY_EVENTUAL_CONSISTENCY when server is running in
     * EVENTUAL_CONSISTENCY
     * <br>
     * Synchronizer: all requests are optional, no forced updates will be
     * received
     */
    EVENTUAL_CONSISTENCY,
    /**
     * <p>
     * FUTURE_WORK: Validate conflict-free replicated data types works well
     * </p>
     * Server: -
     * <br>
     * Synchronizer: priority requests and data request must be performed
     * directly
     */
    STRONG_EVENUTAL_CONSISTENCY,
    /**
     * <p>
     * FUTURE_WORK: Implement this with pointer database
     * </p>
     */
    PROCESSOR_CONSISTENCY,
    /**
     * <p>
     * Will guarantee that each replica will be consistent with a single atomic
     * replica that has write atomicity. Before a write from a replica, all
     * reads are preformed in single FIFO queue order. Sequential consistency
     * uses linearizability.
     *
     * FUTURE_WORK: Implement linearizability (read before write and use
     * priority requests)
     * </p>
     * Server: -
     * <br>
     * Synchronizer: priority requests and data request must be performed
     * directly. A write will result in a direct data request and prepare
     * priority requests for all synchronizers
     */
    SEQUENTIAL_CONSISTENCY;

}
