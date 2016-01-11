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
package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.authorization.authorizer.AbstractRequireAllAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has all the permissions.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllPermissionsAuthorizer<U extends UserProfile> extends AbstractRequireAllAuthorizer<String, U> {

    public RequireAllPermissionsAuthorizer() { }

    public RequireAllPermissionsAuthorizer(final String... permissions) {
        setElements(permissions);
    }

    public RequireAllPermissionsAuthorizer(final List<String> permissions) {
        setElements(permissions);
    }

    public RequireAllPermissionsAuthorizer(final Set<String> permissions) {
        setElements(permissions);
    }

    @Override
    protected boolean check(final WebContext context, final U profile, final String element) {
        final List<String> profilePermissions = profile.getPermissions();
        return profilePermissions.contains(element);
    }
}
