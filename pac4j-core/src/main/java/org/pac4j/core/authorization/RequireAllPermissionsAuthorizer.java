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
 * Checks an access if the user profile has all the permissions.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllPermissionsAuthorizer<U extends UserProfile> implements Authorizer<U> {

    private Set<String> permissions;

    public RequireAllPermissionsAuthorizer() { }

    public RequireAllPermissionsAuthorizer(final String... permissions) {
        setPermissions(permissions);
    }

    public RequireAllPermissionsAuthorizer(final List<String> permissions) {
        setPermissions(permissions);
    }

    public RequireAllPermissionsAuthorizer(final Set<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthorized(final WebContext context, final U profile) {
        if (permissions == null || permissions.size() == 0) {
            return true;
        }
        final List<String> profilePermissions = profile.getPermissions();
        for (final String permission : permissions) {
            if (!profilePermissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(final Set<String> permissions) {
        this.permissions = permissions;
    }

    public void setPermissions(final List<String> permissions) {
        if (permissions != null) {
            this.permissions = new HashSet<>(permissions);
        }
    }

    public void setPermissions(final String... permissions) {
        if (permissions != null) {
            setPermissions(Arrays.asList(permissions));
        }
    }
}
