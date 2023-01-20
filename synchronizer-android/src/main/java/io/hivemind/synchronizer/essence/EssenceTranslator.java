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
package io.hivemind.synchronizer.essence;

import io.hivemind.synchronizer.HiveEssenceDataProvider;
import io.hivemind.synchronizer.HiveEssencePart;
import io.hivemind.synchronizer.HiveResource;
import io.hivemind.synchronizer.exception.HiveCeption;
import io.hivemind.synchronizer.exception.InvalidEssenceException;
import io.hivemind.synchronizer.exception.NotSupportedException;
import io.hivemind.synchronizer.validator.EssenceValidator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Essence translator translates resources to essence and vice versa
 *
 * @author Patrick-4488
 */
public class EssenceTranslator {

    // FUTURE_WORK: move to configuration?
    private static final byte SEPARATOR = ";".getBytes()[0];
    private static final byte SPLITTER = ",".getBytes()[0];

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveEssenceDataProvider.class);

    /**
     * Determine the resources that correspond to the essence
     *
     * @param essence the essence to determine for
     * @return the found resources or empty
     * @throws InvalidEssenceException when an invalid essence has been provided
     */
    public List<HiveResource> interpolateResourcesFromEssence(final byte[] essence) throws InvalidEssenceException {
        List<HiveResource> resources = new ArrayList<>();
        if (essence != null && essence.length > 0) {
            new EssenceValidator().validateEssence(essence);

            HiveEssencePart resource = new HiveEssencePart();
            try ( ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                for (byte essenceByte : essence) {
                    if (essenceByte == SEPARATOR) {
                        resource.setVersion(baos.toByteArray());
                        baos.reset();
                        resources.add(resource);
                        resource = new HiveEssencePart();
                    } else if (essenceByte == SPLITTER) {
                        resource.setId(baos.toByteArray());
                        baos.reset();
                    } else {
                        baos.write(essenceByte);
                    }
                }
            } catch (IOException ex) {
                LOGGER.error("Failed to determine essence part from essence", ex);
            }
        } else {
            LOGGER.error("Attempted to interpolate resources, but no essence was provided");
        }

        return resources;
    }

    /**
     * Determine the essence based on resources
     *
     * @param resources the resources to generate the essence for
     * @return the essence or empty
     */
    public byte[] determineEssenceForResources(final List<HiveResource> resources) {
        byte[] essence = new byte[0];
        if (resources != null && !resources.isEmpty()) {
            try ( ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                for (HiveResource resource : resources) {
                    baos.write(getByteValue(resource.getId()));
                    baos.write(SPLITTER);
                    baos.write(getByteValue(resource.getVersion()));
                    baos.write(SEPARATOR);
                }

                essence = baos.toByteArray();
            } catch (IOException | NotSupportedException ex) {
                LOGGER.error("Failed to determine essence from object", ex);
            }
        } else {
            LOGGER.warn("Attempted to determine essence but no resources were provided");
        }

        return essence;
    }

    // FUTURE_WORK: Java 19 -> use pattern matching
    private byte[] getByteValue(final Object o) throws NotSupportedException {
        if (o instanceof Number num) {
            return num.toString().getBytes();
        } else if (o instanceof byte[]) {
            return (byte[]) o;
        } else {
            throw new NotSupportedException(HiveCeption.HIVE_RESOURCE_ID_OR_VERSION_NOT_NUMBER);
        }
    }
}
