/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile.strava;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Encapsulates a strava bike or shoe.
 * Example:
 * {
 * "id": "b1534132",
 * "primary": true,
 * "name": "Kona Dr Good",
 * "resource_state": 2,
 * "distance": 1303618
 * }
 *
 * @author Adrian Papusoi
 */
public class StravaGear extends JsonObject {

    private static final long serialVersionUID = -5738356602119292294L;

    private String id;
    private Boolean primary;
    private String name;

    private Integer resourceState;
    private Long distance;

    public Boolean isPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(Integer resourceState) {
        this.resourceState = resourceState;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @Override
    protected void buildFromJson(final JsonNode json) {
        id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        primary = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "primary");
        name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        resourceState = (Integer) JsonHelper.convert(Converters.integerConverter, json, "resource_state");
        distance = (Long) JsonHelper.convert(Converters.longConverter, json, "distance");
    }

}
