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
package io.hivemind.synchronizer.configuration;

import io.hivemind.synchronizer.constant.ConsistencyModel;
import io.hivemind.synchronizer.exception.HiveCeption;
import io.hivemind.synchronizer.exception.NotSupportedException;

/**
 * Configuration for the synchronizer
 *
 * @author Patrick-4488
 */
public final class SynchronizerConfiguration {

    private int periodBetweenRequests;
    private ConsistencyModel consistencyModel;

    private final String uri;

    public SynchronizerConfiguration(final String uri, final ConsistencyModel consistencyModel) throws NotSupportedException {
        this.uri = uri;
        setConsistencyModel(consistencyModel);
    }

    /**
     * The default is based on the consistency model and can be seen as a
     * suggestion:
     * <ul>
     * <li>LOW_FREQUENCY_EVENTUAL_CONSISTENCY -> 300</li>
     * <li>EVENTUAL_CONSISTENCY -> 30</li>
     * <li>STRONG_EVENUTAL_CONSISTENCY -> 30</li>
     * <li>PROCESSOR_CONSISTENCY -> not applicable</li>
     * <li>PROCESSOR_CONSISTENCY -> 10</li>
     * </ul>
     *
     * @return the period between request in seconds
     */
    public int getPeriodBetweenRequests() {
        return periodBetweenRequests;
    }

    /**
     * Set period between requests in seconds<br>
     * Use this method only when you want to divert from the default
     *
     * @param periodBetweenRequests the period
     */
    public void setPeriodBetweenRequests(final int periodBetweenRequests) {
        this.periodBetweenRequests = periodBetweenRequests;
    }

    /**
     * @return the consistency model
     */
    public ConsistencyModel getConsistencyModel() {
        return consistencyModel;
    }

    /**
     * Change the consistency model<br>
     * <p>
     * Important: when setting a consistency model that is not supported by the
     * consistency model used by the server. The synchronization might be
     * unsynced.</p>
     *
     * @see ConsistencyModel
     * @param consistencyModel the consistency model to use, will default to
     * EVENTUAL_CONSISTENCY when null
     * @throws NotSupportedException when the requested consistency is not
     * implemented (yet)
     */
    public void setConsistencyModel(ConsistencyModel consistencyModel) throws NotSupportedException {
        this.consistencyModel = (consistencyModel != null ? consistencyModel : ConsistencyModel.EVENTUAL_CONSISTENCY);

        periodBetweenRequests = switch (this.consistencyModel) {
            case LOW_FREQUENCY_EVENTUAL_CONSISTENCY -> {
                throw new NotSupportedException(HiveCeption.CONSISTENCY_MODEL_NOT_SUPPORTED);
                //yield 300;//NOSONAR
            }
            case EVENTUAL_CONSISTENCY -> {
                yield 30;
            }
            case STRONG_EVENUTAL_CONSISTENCY, PROCESSOR_CONSISTENCY, SEQUENTIAL_CONSISTENCY ->
                throw new NotSupportedException(HiveCeption.CONSISTENCY_MODEL_NOT_SUPPORTED);
            default ->
                throw new NotSupportedException(HiveCeption.CONSISTENCY_MODEL_NOT_SUPPORTED);
        };
    }

    /**
     * @return the uri (protocol://[user:pass@]host(name)[:port]/path)
     */
    public String getUri() {
        return uri;
    }
}
