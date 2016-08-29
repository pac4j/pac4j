package org.pac4j.core.profile;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.*;

import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the user profile retrieved from a provider after successful authentication: it's an identifier (string) and attributes
 * (objects). The attributes definition is <code>null</code> (generic profile), it must be defined in subclasses. Additional concepts are the
 * "remember me" nature of the user profile and the associated roles, permissions and client name.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class UserProfile implements Serializable, Externalizable {

    private static final long serialVersionUID = 9020114478664816338L;

    protected transient final Logger logger = LoggerFactory.getLogger(getClass());

    private String id;

    private Map<String, Object> attributes = new HashMap<>();

    public transient static final String SEPARATOR = "#";

    private boolean isRemembered = false;

    private Set<String> roles = new HashSet<>();

    private Set<String> permissions = new HashSet<>();

    private String clientName;

    /**
     * Build a profile from user identifier and attributes.
     * 
     * @param id user identifier
     * @param attributes user attributes
     */
    public void build(final Object id, final Map<String, Object> attributes) {
        setId(id);
        addAttributes(attributes);
    }

    /**
     * Return the attributes definition for this user profile. <code>null</code> for a (generic) user profile.
     * 
     * @return the attributes definition
     */
    public AttributesDefinition getAttributesDefinition() {
        return null;
    }

    /**
     * Add an attribute and perform conversion if necessary.
     * 
     * @param key key of the attribute
     * @param value value of the attribute
     */
    public void addAttribute(final String key, Object value) {
        if (value != null) {
            final AttributesDefinition definition = getAttributesDefinition();
            // no attributes definition -> no conversion
            if (definition == null) {
                logger.debug("no conversion => key: {} / value: {} / {}",
                        new Object[] { key, value, value.getClass() });
                this.attributes.put(key, value);
            } else {
                value = definition.convert(key, value);
                if (value != null) {
                    // for OAuth: convert array as list
                    Object value2;
                    if (value instanceof Object[]) {
                        value2 = new ArrayList(Arrays.asList((Object[]) value));
                    } else {
                        value2 = value;
                    }
                    logger.debug("converted to => key: {} / value: {} / {}",
                            new Object[] { key, value2, value2.getClass() });
                    this.attributes.put(key, value2);
                }
            }
        }
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
     * Remove an attribute byt its key.
     *
     * @param key the key
     */
    public void removeAttribute(final String key) {
        CommonHelper.assertNotNull("key", key);
        attributes.remove(key);
    }

    /**
     * Set the identifier and convert it if necessary.
     * 
     * @param id user identifier
     */
    public void setId(final Object id) {
        CommonHelper.assertNotNull("id", id);

        String sId = id.toString();
        final String oldType = this.getClass().getSimpleName() + SEPARATOR;
        final String type = this.getClass().getName() + SEPARATOR;
        if (sId.startsWith(type)) {
            sId = sId.substring(type.length());
        } else if (sId.startsWith(oldType)) {
            sId = sId.substring(oldType.length());
        }
        logger.debug("identifier: {}", sId);
        this.id = sId;
    }

    /**
     * Get the user identifier. This identifier is unique for this provider but not necessarily through all providers.
     * 
     * @return the user identifier
     */
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
        return this.getClass().getName() + SEPARATOR + this.id;
    }

    /**
     * Get the old typed id with a class name without package. Use {@link #getTypedId()} instead.
     *
     * @return the old typed id.
     * @deprecated
     */
    @Deprecated
    public String getOldTypedId() {
        return this.getClass().getSimpleName() + SEPARATOR + this.id;
    }

    /**
     * Get attributes as immutable map.
     * 
     * @return the immutable attributes
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    /**
     * Return the attribute with name.
     * 
     * @param name attribute name
     * @return the attribute with name
     */
    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }

    /**
     * Check to see if profile contains attribute name.
     *
     * @param name the name
     * @return true/false
     */
    public boolean containsAttribute(final String name) {
        return this.attributes.containsKey(name);
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
    public void addRole(final String role) {
        CommonHelper.assertNotBlank("role", role);
        this.roles.add(role);
    }

    /**
     * Add roles.
     *
     * @param roles the roles to add.
     */
    public void addRoles(final List<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles.addAll(roles);
    }

    /**
     * Add roles.
     *
     * @param roles the roles to add.
     */
    public void addRoles(final Set<String> roles) {
        CommonHelper.assertNotNull("roles", roles);
        this.roles.addAll(roles);
    }

    /**
     * Add a permission.
     * 
     * @param permission the permission to add.
     */
    public void addPermission(final String permission) {
        CommonHelper.assertNotBlank("permission", permission);
        this.permissions.add(permission);
    }

    /**
     * Add permissions.
     *
     * @param permissions the permissions to add.
     */
    public void addPermissions(final List<String> permissions) {
        CommonHelper.assertNotNull("permissions", permissions);
        this.permissions.addAll(permissions);
    }

    /**
     * Add permissions.
     *
     * @param permissions the permissions to add.
     */
    public void addPermissions(final Set<String> permissions) {
        CommonHelper.assertNotNull("permissions", permissions);
        this.permissions.addAll(permissions);
    }

    /**
     * Define if this profile is remembered.
     * 
     * @param rme whether the user is remembered.
     */
    public void setRemembered(final boolean rme) {
        this.isRemembered = rme;
    }

    /**
     * Get the roles of the user.
     * 
     * @return the user roles.
     */
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
     * Get the permissions of the user.
     * 
     * @return the user permissions.
     */
    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(this.permissions);
    }

    /**
     * Is the user remembered?
     * 
     * @return whether the user is remembered.
     */
    public boolean isRemembered() {
        return this.isRemembered;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "id", this.id, "attributes", this.attributes, "roles",
                this.roles, "permissions", this.permissions, "isRemembered", this.isRemembered);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeObject(this.attributes);
        out.writeBoolean(this.isRemembered);
        out.writeObject(this.roles);
        out.writeObject(this.permissions);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = (String) in.readObject();
        this.attributes = (Map) in.readObject();
        this.isRemembered = (boolean) in.readBoolean();
        this.roles = (Set) in.readObject();
        this.permissions = (Set) in.readObject();
    }

    public void clearSensitiveData() {
        // No-op. Allow subtypes to specify which state should be cleared out.
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        CommonHelper.assertNotNull("clientName", clientName);
        this.clientName = clientName;
    }
}
