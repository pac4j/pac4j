package org.pac4j.core.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Streams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the user profile retrieved from a provider after successful authentication: it's an identifier (string) and attributes
 * (objects). Additional concepts are the "remember me" nature of the user profile, the associated roles, client name and linked identifier.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@EqualsAndHashCode
@ToString
public class BasicUserProfile implements UserProfile, Externalizable {

    @Serial
    private static final long serialVersionUID = 9020114478664816338L;

    protected transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    private String id;

    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, Object> authenticationAttributes = new HashMap<>();

    @Getter
    @Setter
    private boolean isRemembered = false;

    private Set<String> roles = new HashSet<>();

    @Getter
    @Setter
    private String clientName;

    @Getter
    @Setter
    private String linkedId;

    private final boolean canAttributesBeMerged;

    /**
     * <p>Constructor for BasicUserProfile.</p>
     */
    public BasicUserProfile() {
        this(true);
    }

    /**
     * Create a profile with possibility to merge attributes with the same name and collection-type values.
     *
     * @param canAttributesBeMerged if true - merge attributes with the same name and collection-type values, if false - overwrite them
     * @since 3.1.0
     */
    public BasicUserProfile(final boolean canAttributesBeMerged) {
        this.canAttributesBeMerged = canAttributesBeMerged;
    }

    /**
     * Build a profile from user identifier and attributes.
     *
     * @param id user identifier
     * @param attributes user attributes
     */
    public void build(final Object id, final Map<String, Object> attributes) {
        setId(ProfileHelper.sanitizeIdentifier(id));
        addAttributes(attributes);
    }

    /**
     * Build a profile from user identifier, attributes, and authentication attributes.
     *
     * @param id user identifier
     * @param attributes user attributes
     * @param authenticationAttributes authentication attributes
     */
    public void build(final Object id, final Map<String, Object> attributes, final Map<String, Object> authenticationAttributes ) {
        build(id, attributes);
        addAuthenticationAttributes(authenticationAttributes);
    }

    /**
     * {@inheritDoc}
     *
     * Set the identifier.
     */
    @Override
    public void setId(final String id) {
        CommonHelper.assertNotBlank("id", id);
        this.id = id;
    }

    /**
     * {@inheritDoc}
     *
     * Get the user identifier with a prefix which is the profile type (full class name with package).
     * This identifier is unique through all providers.
     */
    @Override
    @JsonIgnore
    public String getTypedId() {
        return this.getClass().getName() + Pac4jConstants.TYPED_ID_SEPARATOR + this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return null;
    }

    private void addAttributeToMap(final Map<String, Object> map, final String key, final Object value) {
        if (value != null) {
            logger.debug("adding => key: {} / value: {} / {}", key, value, value.getClass());
            var valueForMap = getValueForMap(map, key, value);
            map.put(key, valueForMap);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object getValueForMap(final Map<String, Object> map, final String key, final Object preparedValue) {
        // support multiple attribute values (e.g. roles can be received as separate attributes and require merging)
        // https://github.com/pac4j/pac4j/issues/1145
        if (canMergeAttributes(map, key, preparedValue))
        {
            var existingCollection = (Collection) map.get(key);
            var newCollection = (Collection) preparedValue;
            return mergeCollectionAttributes(existingCollection, newCollection);
        } else
        {
            return preparedValue;
        }
    }

    private boolean canMergeAttributes(final Map<String, Object> map, final String key, final Object preparedValue)
    {
        return this.canAttributesBeMerged && preparedValue instanceof Collection && map.get(key) instanceof Collection;
    }

    private static <T> Collection<T> mergeCollectionAttributes(final Collection<T> existingCollection, final Collection<T> newCollection)
    {
        return Streams.concat(existingCollection.stream(), newCollection.stream()).collect(Collectors.toList());
    }

    @Override
    public void addAttribute(final String key, final Object value) {
        addAttributeToMap(this.attributes, key, value);
    }

    @Override
    public void addAuthenticationAttribute(final String key, final Object value) {
        addAttributeToMap(this.authenticationAttributes, key, value);
    }


    /**
     * Add attributes.
     *
     * @param attributes use attributes
     */
    public void addAttributes(final Map<String, Object> attributes) {
        if (attributes != null) {
            for (val entry : attributes.entrySet()) {
                addAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Add authentication attributes.
     *
     * @param attributeMap the authentication attributes
     */
    public void addAuthenticationAttributes(final Map<String, Object> attributeMap) {
        if (attributeMap != null) {
            for (val entry : attributeMap.entrySet()) {
                addAuthenticationAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
    /**
     * {@inheritDoc}
     *
     * Remove an attribute by its key.
     */
    public void removeAttribute(final String key) {
        CommonHelper.assertNotNull("key", key);
        attributes.remove(key);
    }

    /**
     * {@inheritDoc}
     *
     * Remove an authentication attribute by its key
     */
    public void removeAuthenticationAttribute(final String key) {
        CommonHelper.assertNotNull("key", key);
        authenticationAttributes.remove(key);
    }

    /**
     * {@inheritDoc}
     *
     * Get all attributes as immutable map.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return getAttributeMap(this.attributes);
    }

    /**
     * Get all authentication attributes as an immutable map
     *
     * @return the immutable authentication attributes
     */
    public Map<String, Object> getAuthenticationAttributes() {
        return getAttributeMap(this.authenticationAttributes);
    }

    private static Map<String, Object> getAttributeMap(final Map<String, Object> attributeMap) {
        final Map<String, Object> newAttributes = new HashMap<>();
        for (var entries : attributeMap.entrySet()) {
            val key = entries.getKey();
            val value = attributeMap.get(key);
            newAttributes.put(key, value);
        }
        return newAttributes;
    }

    /**
     * {@inheritDoc}
     *
     * Return the attribute with name.
     */
    @Override
    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }

    /**
     * Return the attribute values with name.
     *
     * @param name attribute name
     * @return the attribute values as List of strings.
     */
    public List<String> extractAttributeValues(final String name) {
        val value = getAttribute(name);
        if (value instanceof String) {
            return Collections.singletonList((String) value);
        } else if (value instanceof String[]) {
            return Arrays.asList((String[]) value);
        } else if (value instanceof List) {
            return (List<String>) value;
        } else {
            return null;
        }
    }

    /**
     * Return the authentication attribute with name.
     *
     * @param name authentication attribute name
     * @return the authentication attribute with name
     */
    public Object getAuthenticationAttribute(final String name) {
        return this.authenticationAttributes.get(name);
    }

    /**
     * {@inheritDoc}
     *
     * Check to see if profile contains attribute name.
     */
    @Override
    public boolean containsAttribute(final String name) {
        CommonHelper.assertNotNull("name", name);
        return this.attributes.containsKey(name);
    }

    /**
     * Check to see if profile contains attribute name.
     *
     * @param name the name
     * @return true/false
     */
    public boolean containsAuthenticationAttribute(final String name) {
        CommonHelper.assertNotNull("name", name);
        return this.authenticationAttributes.containsKey(name);
    }

    /**
     * Return the attribute with name.
     *
     * @param name the attribute name
     * @param clazz the class of the attribute
     * @param <T> the type of the attribute
     * @return the attribute by its name
     * @since 1.8
     */
    public <T> T getAttribute(final String name, final Class<T> clazz) {
        val attribute = getAttribute(name);
        return getAttributeByType(name, clazz, attribute);
    }

    /**
     * Return authentication attribute with name
     *
     * @param name Name of authentication attribute
     * @param clazz The class of the authentication attribute
     * @param <T> The type of the authentication attribute
     * @return the named attribute
     */
    public <T> T getAuthenticationAttribute(final String name, final Class<T> clazz)
    {
        val attribute = getAuthenticationAttribute(name);
        return getAttributeByType(name, clazz, attribute);
    }

    private static <T> T getAttributeByType(final String name, final Class<T> clazz, final Object attribute) {

        if (attribute == null) {
            return null;
        }

        if (!clazz.isAssignableFrom(attribute.getClass())) {
            throw new ClassCastException("Attribute [" + name
                    + " is of type " + attribute.getClass()
                    + " when we were expecting " + clazz);
        }

        return (T) attribute;
    }

    /**
     * {@inheritDoc}
     *
     * Add a role.
     */
    @Override
    public void addRole(final String role) {
        CommonHelper.assertNotBlank("role", role);
        this.roles.add(role);
    }

    /**
     * {@inheritDoc}
     *
     * Add roles.
     */
    @Override
    public void addRoles(final Collection<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles.addAll(roles);
    }

    /**
     * {@inheritDoc}
     *
     * Get the roles of the user.
     */
    @Override
    public Set<String> getRoles() {
        return new LinkedHashSet<>(this.roles);
    }

    /**
     * <p>Setter for the field <code>roles</code>.</p>
     *
     * @param roles a {@link Set} object
     */
    public void setRoles(Set<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles = roles;
    }

    /** {@inheritDoc} */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeObject(this.attributes);
        out.writeObject(this.authenticationAttributes);
        out.writeBoolean(this.isRemembered);
        out.writeObject(this.roles);
        out.writeObject(this.clientName);
        out.writeObject(this.linkedId);
    }

    /** {@inheritDoc} */
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = (String) in.readObject();
        this.attributes = (Map<String, Object>) in.readObject();
        this.authenticationAttributes = (Map<String, Object>) in.readObject();
        this.isRemembered = in.readBoolean();
        this.roles = (Set<String>) in.readObject();
        this.clientName = (String) in.readObject();
        this.linkedId = (String) in.readObject();
    }

    /**
     * Remove the specific data retrieved during the login process
     * to only keep the user attributes and roles.
     */
    public void removeLoginData() {
        // No-op. Allow subtypes to specify which state should be cleared out.
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExpired() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Principal asPrincipal() {
        return new Pac4JPrincipal(this);
    }
}
