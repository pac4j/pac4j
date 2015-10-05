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
package org.pac4j.core.profile;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pac4j.core.Clearable;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the user profile retrieved from a provider after successful authentication : it's an identifier (string) and attributes
 * (objects). The attributes definition is null (generic profile), it must be defined in subclasses. Additional concepts are the
 * "remember me" nature of the user profile and the roles and permissions associated.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class UserProfile implements Serializable, Externalizable, Clearable {

    private static final long serialVersionUID = 9020114478664816338L;

    protected transient static final Logger logger = LoggerFactory.getLogger(UserProfile.class);

    private String id;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    public transient static final String SEPARATOR = "#";

    private boolean isRemembered = false;

    private List<String> roles = new ArrayList<String>();

    private List<String> permissions = new ArrayList<String>();

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
     * Return the attributes definition for this user profile. Null for this (generic) user profile.
     * 
     * @return the attributes definition
     */
    protected AttributesDefinition getAttributesDefinition() {
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
                logger.debug("no conversion => key : {} / value : {} / {}",
                        new Object[] { key, value, value.getClass() });
                this.attributes.put(key, value);
            } else {
                value = definition.convert(key, value);
                if (value != null) {
                    logger.debug("converted to => key : {} / value : {} / {}",
                            new Object[] { key, value, value.getClass() });
                    this.attributes.put(key, value);
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
        for (final String key : attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
    }

    /**
     * Set the identifier and convert it if necessary.
     * 
     * @param id user identifier
     */
    public void setId(final Object id) {
        if (id != null) {
            String sId = id.toString();
            final String type = this.getClass().getSimpleName();
            if (sId.startsWith(type + SEPARATOR)) {
                sId = sId.substring(type.length() + SEPARATOR.length());
            }
            logger.debug("identifier : {}", sId);
            this.id = sId;
        }
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
     * Get the user identifier with a prefix which is the profile type. This identifier is unique through all providers.
     * 
     * @return the typed user identifier
     */
    public String getTypedId() {
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
        this.roles.add(role);
    }

    /**
     * Add roles.
     *
     * @param roles the roles to add.
     */
    public void addRoles(final List<String> roles) {
        this.roles.addAll(roles);
    }

    /**
     * Add a permission.
     * 
     * @param permission the permission to add.
     */
    public void addPermission(final String permission) {
        this.permissions.add(permission);
    }

    /**
     * Add permissions.
     *
     * @param permissions the permissions to add.
     */
    public void addPermissions(final List<String> permissions) {
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
    public List<String> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }

    /**
     * Get the permissions of the user.
     * 
     * @return the user permissions.
     */
    public List<String> getPermissions() {
        return Collections.unmodifiableList(this.permissions);
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
        this.roles = (List) in.readObject();
        this.permissions = (List) in.readObject();
    }

    @Override
    public void clear() {
        // No-op. Allow subtypes to specify which state should be cleared out.
    }
}
