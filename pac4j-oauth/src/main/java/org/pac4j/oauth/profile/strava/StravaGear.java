package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

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
 * <p/>
 * <p/>
 *
 * @author Adrian Papusoi
 */
public class StravaGear extends JsonObject {
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
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.primary = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "primary");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.resourceState = (Integer) JsonHelper.convert(Converters.integerConverter, json, "resource_state");
        this.distance = (Long) JsonHelper.convert(Converters.longConverter, json, "distance");
    }


}
