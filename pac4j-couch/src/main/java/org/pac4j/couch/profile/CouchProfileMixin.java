package org.pac4j.couch.profile;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.Set;

/**
 * Annotations for JSON serialization of profiles.
 *
 * @author Timur Duehr
 * @since 3.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public abstract class CouchProfileMixin {

    @JsonProperty(value = "_id",required = true)
    public abstract String getId();

    @JsonProperty(value = "_id",required = true)
    public abstract void setId(final String id);

    @JsonAnyGetter
    public abstract Map<String, Object> getAttributes();

    @JsonAnySetter
    public abstract void addAttribute(final String key, final Object value);

    @JsonProperty("authenticationAttributes")
    public abstract Map<String, Object> getAuthenticationAttributes();

    @JsonProperty("authenticationAttributes")
    public abstract void addAuthenticationAttributes(final Map<String, Object> attributeMap);

    @JsonProperty("roles")
    public abstract Set<String> getRoles();

    @JsonProperty("roles")
    public abstract void setRoles(Set<String> roles);

    @JsonProperty("permissions")
    public abstract Set<String> getPermissions();

    @JsonProperty("permissions")
    public abstract void setPermissions(Set<String> permissions);

    @JsonProperty("isRemembered")
    public abstract boolean isRemembered();

    @JsonProperty("isRemembered")
    public abstract void setRemembered(final boolean rme);

    @JsonProperty("clientName")
    public abstract String getClientName();

    @JsonProperty("clientName")
    public abstract void setClientName(final String clientName);

    @JsonProperty("linkedId")
    public abstract String getLinkedId();

    @JsonProperty("linkedId")
    public abstract void setLinkedId(final String linkedId);
}
