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
package io.hivemind.synchronizer;

import io.hivemind.synchronizer.constant.ContentType;
import io.hivemind.synchronizer.essence.EssenceTranslator;
import io.hivemind.synchronizer.exception.InvalidEssenceException;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static java.lang.System.Logger.Level.ERROR;

/**
 * Provider for hippo essence data
 *
 * @see EssenceDataProvider
 * @author Patrick-4488
 */
public class HiveEssenceDataProvider implements EssenceDataProvider {

    private final ResourceProvider resourceProvider;
    private final EssenceTranslator translator;

    private static final System.Logger LOGGER = System.getLogger(HiveEssenceDataProvider.class.getName());

    public HiveEssenceDataProvider(final ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
        this.translator = new EssenceTranslator();
    }

    @Override
    public byte[] determineEssence() {
        return translator.determineEssenceForResources(resourceProvider.provideAllResources());
    }

    @Override
    public byte[] getDataForEssence(final byte[] essence) {
        List<HiveResource> resources = new ArrayList<>();
        try {
            resources = translator.interpolateResourcesFromEssence(essence);
        } catch (InvalidEssenceException ex) {
            LOGGER.log(ERROR, "Failed to process invalid essence, being: {0}",
                    (essence != null ? new String(essence) : "null"), ex);
        }
        resources = resourceProvider.provideResources(resources);

        byte[] data = new byte[0];
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream();  ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(resources);//NOSONAR, in this case List is always ArrayList and there for serializable
            data = baos.toByteArray();
        } catch (IOException ex) {
            LOGGER.log(ERROR, "Failed to convert object to byte array", ex);
        }

        return data;
    }

    /**
     * Process priority essence. This is an essence that has to be processed
     * before synchronization can continue. This is mostly due to deletion of
     * objects
     *
     * @param essence the essence to process
     */
    @Override
    public void processPriorityEssence(final byte[] essence) {
        try {
            resourceProvider.deleteAllResourcesExcept(
                    translator.interpolateResourcesFromEssence(essence));
        } catch (InvalidEssenceException ex) {
            LOGGER.log(ERROR, "Failed to process invalid essence, being: {0}",
                    (essence != null ? new String(essence) : "null"), ex);
        }
    }

    @Override
    public void saveData(final byte[] data, final ContentType contentType) {
        System.out.println("content-type: " + contentType);
        resourceProvider.saveData(data);
    }
}
