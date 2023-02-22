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

import io.hivemind.constant.ContentType;
import io.hivemind.data.comparison.ComparisonResult;
import io.hivemind.data.comparison.EssenceComparator;
import io.hivemind.data.comparison.EssenceValidator;
import io.hivemind.constant.Outcome;
import io.hivemind.exception.InvalidEssenceException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Data processor processes data received from requests
 *
 * @author Patrick-4488
 */
public class DataProcessor {

    private Map.Entry<String, ObservedData> latestObserved;
    private final Map<String, Map<String, ObservedData>> dataRequest;
    private final Map<String, ObservedData> priorityRequest;
    private final Map<String, List<ObservedData>> storedData;
    private final Map<String, PreparedData> preparedData;

    private static DataProcessor instance;

    private static final System.Logger LOGGER = System.getLogger(DataProcessor.class.getName());

    private DataProcessor() {
        dataRequest = new HashMap<>();
        priorityRequest = new HashMap<>();
        storedData = new HashMap<>();
        preparedData = new HashMap<>();
    }

    /**
     * returns instance data processor
     *
     * @return the data processor of which there is only one
     */
    public static synchronized DataProcessor getInstance() {
        if (instance == null) {
            instance = new DataProcessor();
        }

        return instance;
    }

    /**
     * Process data received.When the received data has content type:
     * <ul>
     * <li>
     * HIVE_ESSENCE; Will compare the given hive essence with the latest
     * received if this is available. When the received essence is ahead, it
     * will be stored for other receivers. If it is behind, it will request data
     * to be prepared from the synchronizer that has the latest based on the
     * difference.
     * </li>
     * <li>
     * OTHER;JSON; When receiving other data types it indicates receiving data
     * that is destined for one or more synchronizers. This data is prepared to
     * be received by the synchronizers for the next incoming request from the
     * synchronizer and will be cleared after.
     * </li>
     * </ul>
     *
     * @param data the data (hive essence / data to provide)
     * @param contentType the content type (@see ContentType)
     * @param traceparent the traceparent
     * @return the PreparedData or DataRequest or null
     * @throws io.hivemind.exception.InvalidEssenceException when invalid
     * essence has been received
     * @see EssenceValidator
     */
    public synchronized PreparedData processData(final byte[] data, final ContentType contentType, final String traceparent) throws InvalidEssenceException {
        LOGGER.log(INFO, "Processing data received from {0} having {1} bytes of "
                + "content", traceparent, (data != null ? data.length : "0"));

        if (!priorityRequest.containsKey(traceparent)) {
            if (contentType == null) {
                LOGGER.log(WARNING, "No content-type received", contentType);
            } else {
                switch (contentType) {
                    case HIVE_ESSENCE -> {
                        processHiveEssence(data, traceparent);
                    }
                    case OTHER,JSON -> {
                        if (data != null && data.length != 0) {
                            processDataReceived(data, contentType, traceparent);
                        } else {
                            LOGGER.log(WARNING, "Received data request from {0} but without any data", traceparent);
                        }
                    }
                    default ->
                        LOGGER.log(WARNING, "Unexpected content-type received, being: {0}", contentType);
                }
            }

            storeData(data, traceparent);
        }
        PreparedData dataResult = determineDataResult(priorityRequest.containsKey(traceparent), traceparent);

        LOGGER.log(INFO, "Finished processing data received from {0}, resulting in: {1}", traceparent,
                (dataResult == null ? "null" : dataResult.getClass().getSimpleName()));

        return dataResult;
    }

    /**
     * Remove data of previous received state while remaining references. This
     * will free up memory without impacting desired functionality
     */
    public void cleanOlderState() {
        for (Map.Entry<String, List<ObservedData>> synchronizer : storedData.entrySet()) {
            List<ObservedData> observedData = synchronizer.getValue();
            int sizeToClean = observedData.size() - 2;
            for (int i = 0; i < sizeToClean; i++) {
                if (i != sizeToClean - 1) {
                    observedData.get(i).close();
                } else {
                    // Retaining last two observed data objects 
                }
            }
        }
    }

    /**
     * Clear all state, this will reset all data and will have the Hivemind
     * start fresh
     */
    public void clearAllState() {
        latestObserved = null;
        dataRequest.clear();
        storedData.clear();
        preparedData.clear();
        priorityRequest.clear();
    }

    private void processHiveEssence(final byte[] data, final String traceparent) throws InvalidEssenceException {
        new EssenceValidator().validateEssence(data);

        if (latestObserved != null) {
            ObservedData observedData = new ObservedData(data);
            ObservedData latestObservedData = latestObserved.getValue();
            EssenceComparator comparator = new EssenceComparator();
            ComparisonResult result = comparator.compare(latestObservedData, observedData);

            Outcome outcome = result.getOutcome();
            if (Outcome.BEHIND == outcome) {
                latestObserved = new SimpleEntry<>(traceparent, observedData);
            } else if (Outcome.AHEAD == outcome) {
                processEssenceAhead(result, comparator, latestObservedData, observedData, traceparent);
            }
        }
    }

    private boolean isUpdate(final EssenceComparator comparator, final ObservedData latestObservedData, final String traceparent) {
        if (storedData.containsKey(traceparent)) {
            List<ObservedData> previouslyObserved = storedData.get(traceparent);
            if (!previouslyObserved.isEmpty()) {
                ObservedData lastObserved = previouslyObserved.get(previouslyObserved.size() - 1);
                ComparisonResult lastObservedResult = comparator.compare(latestObservedData, lastObserved);
                if (Outcome.EQUAL == lastObservedResult.getOutcome()) {
                    LOGGER.log(DEBUG, """
                                Hive essence received is behind latest, but this 
                                synchronizer has previously been in sync so this 
                                indicates that data has been deleted""");

                    return true;
                } else {
                    LOGGER.log(DEBUG, """
                                Hive essence received is behind latest, since this 
                                synchronizer has not previously been in sync""");
                }
            }
        } else {
            LOGGER.log(DEBUG, """
                        Hive essence received is behind latest, since this synchronizer 
                        has not previously been in sync""");
        }

        return false;
    }

    private void processEssenceAhead(final ComparisonResult result, final EssenceComparator comparator, final ObservedData latestObservedData, final ObservedData observedData, final String traceparent) {
        boolean changeIsUpdate = isUpdate(comparator, latestObservedData, traceparent);
        boolean hasDataToReceive = preparedData.containsKey(traceparent);
        if (changeIsUpdate) {
            latestObserved = new SimpleEntry<>(traceparent, observedData);
            for (String synchronizer : storedData.keySet()) {
                if (!traceparent.equals(synchronizer)) {
                    priorityRequest.put(synchronizer, observedData);
                }
            }
        } else if (!hasDataToReceive) {
            storeDataRequest(new ObservedData(result.getDifference()), traceparent);
        }
    }

    /**
     * FUTURE_WORK: Currently this is FIFO, which is fine for eventual
     * consistency but when using a consistency model more consistent then this
     * is should be that you get the newest update first instead of oldest
     */
    private void storeDataRequest(final ObservedData dataToRequest, final String traceparent) {
        if (dataToRequest != null && dataToRequest.hasData()) {
            String synchronizerHavingLatest = latestObserved.getKey();
            Map<String, ObservedData> receiverRequest;
            if (dataRequest.containsKey(synchronizerHavingLatest)) {
                receiverRequest = dataRequest.get(synchronizerHavingLatest);
            } else {
                receiverRequest = new HashMap<>();
            }
            receiverRequest.put(traceparent, dataToRequest);
            dataRequest.put(synchronizerHavingLatest, receiverRequest);
        } else {
            LOGGER.log(DEBUG, """
                         The data request to store is requesting no data, this happens when 
                         the same synchronizer is sending no data multiple times in a row""");
        }
    }

    private void processDataReceived(final byte[] data, final ContentType contentType, final String traceparent) {
        if (dataRequest.containsKey(traceparent)) {
            Map<String, ObservedData> receiverRequests = dataRequest.get(traceparent);
            if (!receiverRequests.isEmpty()) {
                PreparedData dataForReceiver = new PreparedData(data, contentType);
                for (Map.Entry<String, ObservedData> receiverRequest : receiverRequests.entrySet()) {
                    preparedData.put(receiverRequest.getKey(), dataForReceiver);
                }
            } else {
                LOGGER.log(WARNING, """
                    Data request received from {0} but the receiver request map 
                    is empty.""", traceparent);
            }

            dataRequest.remove(traceparent);
        } else {
            LOGGER.log(WARNING, """
                Data received from {0} but no data seems to have been requested. 
                This might point to a synchronization issue and is worth looking into""",
                    traceparent);
        }
    }

    private void storeData(final byte[] data, final String traceparent) {
        List<ObservedData> observedData;
        if (storedData.containsKey(traceparent)) {
            observedData = storedData.get(traceparent);
        } else {
            observedData = new ArrayList<>();
        }

        ObservedData newest = new ObservedData(data);
        observedData.add(new ObservedData(data));
        storedData.put(traceparent, observedData);

        if (latestObserved == null) {
            latestObserved = new SimpleEntry<>(traceparent, newest);
        }
    }

    private PreparedData determineDataResult(final boolean hasPriorityRequest, final String traceparent) {
        PreparedData dataResult = null;
        if (hasPriorityRequest) {
            ObservedData priorityDiff = priorityRequest.get(traceparent);
            priorityRequest.remove(traceparent);
            return new PriorityRequest(priorityDiff.getData());
        } else {
            boolean hasRequestForData = dataRequest.containsKey(traceparent);
            boolean hasDataToReceive = preparedData.containsKey(traceparent);
            if (hasRequestForData && hasDataToReceive) {
                dataRequest.remove(traceparent);
            } else if (hasRequestForData) {
                Map<String, ObservedData> receiverRequest = dataRequest.get(traceparent);
                ObservedData receiverData = receiverRequest.values().iterator().next();
                dataResult = new DataRequest(receiverData.getData());
            } else if (hasDataToReceive) {
                dataResult = preparedData.get(traceparent);
                preparedData.remove(traceparent);
            }
        }

        return dataResult;
    }
}
