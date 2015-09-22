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
package org.pac4j.core.authorization;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has all the roles.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllRolesAuthorizer<U extends UserProfile> implements Authorizer<U> {

    private Set<String> roles;

    public RequireAllRolesAuthorizer() { }

    public RequireAllRolesAuthorizer(final String... roles) {
        setRoles(roles);
    }

    public RequireAllRolesAuthorizer(final List<String> roles) {
        setRoles(roles);
    }

    public RequireAllRolesAuthorizer(final Set<String> roles) {
        this.roles = roles;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthorized(final WebContext context, final U profile) {
        if (roles == null || roles.size() == 0) {
            return true;
        }
        final List<String> profileRoles = profile.getRoles();
        for (final String role : roles) {
            if (!profileRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(final Set<String> roles) {
        this.roles = roles;
    }

    public void setRoles(final List<String> roles) {
        if (roles != null) {
            this.roles = new HashSet<>(roles);
        }
    }

    public void setRoles(final String... roles) {
        if (roles != null) {
            setRoles(Arrays.asList(roles));
        }
    }
}
