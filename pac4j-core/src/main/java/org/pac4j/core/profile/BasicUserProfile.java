package org.pac4j.core.profile;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Streams;

import java.io.*;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the user profile retrieved from a provider after successful authentication: it's an identifier (string) and attributes
 * (objects). Additional concepts are the "remember me" nature of the user profile, the associated roles, permissions, client name and
 * linked identifier.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class BasicUserProfile implements UserProfile, Externalizable {

    private static final long serialVersionUID = 9020114478664816338L;

    protected transient final Logger logger = LoggerFactory.getLogger(getClass());

    private String id;

    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, Object> authenticationAttributes = new HashMap<>();

    private boolean isRemembered = false;

    private Set<String> roles = new HashSet<>();

    private Set<String> permissions = new HashSet<>();

    private String clientName;

    private String linkedId;

    private final boolean canAttributesBeMerged;

    public BasicUserProfile() {
        this(true);
    }

    /**
     * Create a profile with possibility to merge attributes with the same name and collection-type values.
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
     * Set the identifier.
     *
     * @param id user identifier
     */
    public void setId(final String id) {
        CommonHelper.assertNotBlank("id", id);
        this.id = id;
    }

    /**
     * Get the user identifier. This identifier is unique for this provider but not necessarily through all providers.
     *
     * @return the user identifier
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Get the user identifier with a prefix which is the profile type (full class name with package).
     * This identifier is unique through all providers.
     *
     * @return the typed user identifier
     */
    public String getTypedId() {
        return this.getClass().getName() + Pac4jConstants.TYPED_ID_SEPARATOR + this.id;
    }

    @Override
    public String getUsername() {
        return null;
    }

    private void addAttributeToMap(final Map<String, Object> map, final String key, final Object value)
    {
        if (value != null) {
            logger.debug("adding => key: {} / value: {} / {}", key, value, value.getClass());
            Object valueForMap = getValueForMap(map, key, value);
            map.put(key, valueForMap);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object getValueForMap(final Map<String, Object> map, final String key, final Object preparedValue) {
        // support multiple attribute values (e.g. roles can be received as separate attributes and require merging)
        // https://github.com/pac4j/pac4j/issues/1145
        if (canMergeAttributes(map, key, preparedValue))
        {
            Collection existingCollection = (Collection) map.get(key);
            Collection newCollection = (Collection) preparedValue;
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

    private <T> Collection<T> mergeCollectionAttributes(final Collection<T> existingCollection, final Collection<T> newCollection)
    {
        return Streams.concat(existingCollection.stream(), newCollection.stream()).collect(Collectors.toList());
    }

    /**
     * Add an attribute.
     *
     * If existing attribute value is collection and the new value is collection - merge the collections
     *
     * @param key key of the attribute
     * @param value value of the attribute
     */
    public void addAttribute(final String key, final Object value) {
        addAttributeToMap(this.attributes, key, value);
    }

    /**
     * Add an authentication-related attribute
     *
     * @param key the attribute key
     * @param value the attribute value
     */
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
            for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
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
            for (final Map.Entry<String, Object> entry : attributeMap.entrySet()) {
                addAuthenticationAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
    /**
     * Remove an attribute by its key.
     *
     * @param key the key
     */
    public void removeAttribute(final String key) {
        CommonHelper.assertNotNull("key", key);
        attributes.remove(key);
    }

    /**
     * Remove an authentication attribute by its key
     *
     * @param key the key
     */
    public void removeAuthenticationAttribute(final String key) {
        CommonHelper.assertNotNull("key", key);
        authenticationAttributes.remove(key);
    }

    /**
     * Get all attributes as immutable map.
     *
     * @return the immutable attributes
     */
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
        for (Map.Entry<String, Object> entries : attributeMap.entrySet()) {
            final String key = entries.getKey();
            final Object value = attributeMap.get(key);
            newAttributes.put(key, value);
        }
        return newAttributes;
    }

    /**
     * Return the attribute with name.
     *
     * @param name attribute name
     * @return the attribute with name
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
        final Object value = getAttribute(name);
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
     * Check to see if profile contains attribute name.
     *
     * @param name the name
     * @return true/false
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
    public boolean containsAuthenicationAttribute(final String name) {
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
        final Object attribute = getAttribute(name);
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
        final Object attribute = getAuthenticationAttribute(name);
        return getAttributeByType(name, clazz, attribute);
    }

    private <T> T getAttributeByType(final String name, final Class<T> clazz, final Object attribute) {

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
     * Add a role.
     *
     * @param role the role to add.
     */
    @Override
    public void addRole(final String role) {
        CommonHelper.assertNotBlank("role", role);
        this.roles.add(role);
    }

    /**
     * Add roles.
     *
     * @param roles the roles to add.
     */
    @Override
    public void addRoles(final Collection<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles.addAll(roles);
    }

    /**
     * Get the roles of the user.
     *
     * @return the user roles.
     */
    @Override
    public Set<String> getRoles() {
        return new LinkedHashSet<>(this.roles);
    }

    public void setRoles(Set<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles = roles;
    }

    /**
     * Add a permission.
     *
     * @param permission the permission to add.
     */
    @Override
    public void addPermission(final String permission) {
        CommonHelper.assertNotBlank("permission", permission);
        this.permissions.add(permission);
    }

    /** Add permissions.
     *
     * @param permissions the permissions to add.
     */
    @Override
    public void addPermissions(final Collection<String> permissions) {
        CommonHelper.assertNotNull("permissions", permissions);
        this.permissions.addAll(permissions);
    }

    /**
     * Get the permissions of the user.
     *
     * @return the user permissions.
     */
    @Override
    public Set<String> getPermissions() {
        return new LinkedHashSet<>(this.permissions);
    }

    public void setPermissions(final Set<String> permissions) {
        CommonHelper.assertNotNull("permissions", permissions);
        this.permissions = permissions;
    }

    /**
     * Define if this profile is remembered.
     *
     * @param rme whether the user is remembered.
     */
    @Override
    public void setRemembered(final boolean rme) {
        this.isRemembered = rme;
    }

    /**
     * Is the user remembered?
     *
     * @return whether the user is remembered.
     */
    @Override
    public boolean isRemembered() {
        return this.isRemembered;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "id", this.id, "attributes", this.attributes, "roles",
                this.roles, "permissions", this.permissions, "isRemembered", this.isRemembered,
                "clientName", this.clientName, "linkedId", this.linkedId);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeObject(this.attributes);
        out.writeObject(this.authenticationAttributes);
        out.writeBoolean(this.isRemembered);
        out.writeObject(this.roles);
        out.writeObject(this.permissions);
        out.writeObject(this.clientName);
        out.writeObject(this.linkedId);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = (String) in.readObject();
        this.attributes = (Map<String, Object>) in.readObject();
        this.authenticationAttributes = (Map<String, Object>) in.readObject();
        this.isRemembered = in.readBoolean();
        this.roles = (Set<String>) in.readObject();
        this.permissions = (Set<String>) in.readObject();
        this.clientName = (String) in.readObject();
        this.linkedId = (String) in.readObject();
    }

    /**
     * Remove the specific data retrieved during the login process
     * to only keep the user attributes, roles and permissions.
     */
    public void removeLoginData() {
        // No-op. Allow subtypes to specify which state should be cleared out.
    }

    @Override
    public String getClientName() {
        return clientName;
    }

    @Override
    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String getLinkedId() {
        return linkedId;
    }

    @Override
    public void setLinkedId(final String linkedId) {
        this.linkedId = linkedId;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public Principal asPrincipal() {
        return new Pac4JPrincipal(this);
    }
}
