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
package io.hivemind.synchronizer.data;

import io.hivemind.synchronizer.data.conversion.json.RepresentsGetterForField;
import io.hivemind.synchronizer.HiveResource;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Hippo model
 *
 * @author Patrick-4488
 */
public class Hippo implements HiveResource {

    private long id;
    private long version;
    private String hippoText;
    private List<String> tags;
    private LocalDateTime creationDate;
    private Map<String, String> something;
    private SimpleEntry<String, String> someEntry;
    private Time time;
    
    public Hippo() {
    }

    public Hippo(
            final long id,
            final long version,
            final String hippoText,
            final LocalDateTime creationDate,
            final List<String> tags) {
        this.id = id;
        this.hippoText = hippoText;
        this.creationDate = creationDate;
        this.tags = tags;
    }

    @Override
    public Object getId() {
        return id;
    }

    /**
     * @return the id as long
     */
    public long getIdAsLong() {//NOSONAR, doing some more here then sonar understands
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(final long version) {
        this.version = version;
    }

    /**
     * Up the version by 1
     */
    public void upVersion() {
        version += 1;
    }

    @Override
    public Object getVersion() {
        return version;
    }

    /**
     * @return the version as long
     */
    public long getVersionAsLong() {//NOSONAR, doing some more here then sonar understands
        return version;
    }

    /**
     * @return the hippo
     */
    @RepresentsGetterForField(field = "hippoText")
    public String getHippo() {
        return hippoText;
    }

    /**
     * @param hippoText set hippo text
     */
    public void setHippo(final String hippoText) {
        this.hippoText = hippoText;
    }

    /**
     * @return the tags or empty
     */
    public List<String> getTags() {
        return (tags != null ? tags : new ArrayList<>());
    }

    /**
     * @param tags set the tags
     */
    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creation date to set
     */
    public void setCreationDate(final LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Map<String, String> getSomething() {
        return something;
    }

    public void setSomething(Map<String, String> something) {
        this.something = something;
    }

    public SimpleEntry<String, String> getSomeEntry() {
        return someEntry;
    }

    public void setSomeEntry(SimpleEntry<String, String> someEntry) {
        this.someEntry = someEntry;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    
    
    @Override
    public String toString() {
        return "id: " + id + "\n"
                + "version: " + version + "\n"
                + "hippo: " + hippoText + "\n"
                + "tags: " + (tags != null ? tags.toString() : "") + "\n"
                + "creationDate: "
                + (creationDate != null ? creationDate.toString() : "");
    }
}
