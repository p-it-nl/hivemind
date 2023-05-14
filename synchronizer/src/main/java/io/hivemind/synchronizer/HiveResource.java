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

/**
 * Hive resources is: the singular object (can be part of a collection) to
 * synchronize with the hive.
 *
 * @author Patrick-4488
 */
public interface HiveResource {

    /**
     * Since the id is being send as a byte array of strings use this method to
     * retrieve the string value set when receiving this object from the hive
     * mind.
     * <p>
     * When the object is received from the hive mind it only has the id and
     * version, no other data is known since this has been determined based on
     * the hive essence</P>
     * <p>
     * This id and version can be converted to String since they were originally
     * send by the synchronizer as String (no matter the starting type).</p>
     * <p>
     * Use the string result from this method to cast to the specific type
     * required for the hive resource implementation. If you for example are
     * using long as ID's for your resources, convert this String to long</p>
     *
     * @return the string value of id
     */
    default String getIdString() {
        byte[] idBytes = (byte[]) getId();
        if (idBytes != null) {
            return new String(idBytes);
        } else {
            return null;
        }
    }

    /**
     * Since the version is being send as a byte array of strings use this
     * method to retrieve the string value set when receiving this object from
     * the hive mind.
     * <p>
     * When the object is received from the hive mind it only has the id and
     * version, no other data is known since this has been determined based on
     * the hive essence</P>
     * <p>
     * This id and version can be converted to String since they were originally
     * send by the synchronizer as String (no matter the starting type).</p>
     * <p>
     * Use the string result from this method to cast to the specific type
     * required for the hive resource implementation. If you for example are
     * using long as versions for your resources, convert this String to
     * long</p>
     *
     * @return the string value of version
     */
    default String getVersionString() {
        byte[] versionBytes = (byte[]) getVersion();
        if (versionBytes != null) {
            return new String(versionBytes);
        } else {
            return null;
        }
    }

    /**
     * Retrieve the id for the resource which is expected to be a number. If not
     * a number a exception will occur when generating an essence
     *
     * @return the id
     */
    public Object getId();

    /**
     * Retrieve the version for the resource which is expected to be a number.
     * If not a number a exception will occur when generating an essence
     *
     * @return the version
     */
    public Object getVersion();
}
