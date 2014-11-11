/*
  Copyright 2012 - 2014 Jerome Leleu

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 9020114478664816338L;
    
    protected transient static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
    
    private String id;
    
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public transient static final String SEPARATOR = "#";
    
    private boolean isRemembered = false;
    
    private final List<String> roles = new ArrayList<String>();
    
    private final List<String> permissions = new ArrayList<String>();
    
    /**
     * Build a profile from user identifier and attributes.
     * 
     * @param id
     * @param attributes
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
     * @param key
     * @param value
     */
    public void addAttribute(final String key, Object value) {
        if (value != null) {
            final AttributesDefinition definition = getAttributesDefinition();
            // no attributes definition -> no conversion
            if (definition == null) {
                logger.debug("no conversion => key : {} / value : {} / {}", new Object[] {
                    key, value, value.getClass()
                });
                this.attributes.put(key, value);
            } else {
                value = definition.convert(key, value);
                if (value != null) {
                    logger.debug("converted to => key : {} / value : {} / {}", new Object[] {
                        key, value, value.getClass()
                    });
                    this.attributes.put(key, value);
                }
            }
        }
    }
    
    /**
     * Add attributes.
     * 
     * @param attributes
     */
    public void addAttributes(final Map<String, Object> attributes) {
        for (final String key : attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
    }
    
    /**
     * Set the identifier and convert it if necessary.
     * 
     * @param id
     */
    public void setId(final Object id) {
        if (id != null) {
            String sId = id.toString();
            final String type = this.getClass().getSimpleName();
            if (type != null && sId.startsWith(type + SEPARATOR)) {
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
     * @param name
     * @return the attribute with name
     */
    public Object getAttribute(final String name) {
        return this.attributes.get(name);
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
     * Add a permission.
     * 
     * @param permission the permission to add.
     */
    public void addPermission(final String permission) {
        this.permissions.add(permission);
    }

    /**
     * Check if the user has one of the expected roles.
     *
     * @param expectedRoles
     * @return
     */
    public boolean hasAnyRole(final String[] expectedRoles) {
        if (expectedRoles == null || expectedRoles.length == 0) {
            return true;
        }
        for (final String role: expectedRoles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the user has all expected roles.
     *
     * @param expectedRoles
     * @return
     */
    public boolean hasAllRoles(final String[] expectedRoles) {
        if (expectedRoles == null || expectedRoles.length == 0) {
            return true;
        }
        for (final String role: expectedRoles) {
            if (!this.roles.contains(role)) {
                return false;
            }
        }
        return true;
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
}
