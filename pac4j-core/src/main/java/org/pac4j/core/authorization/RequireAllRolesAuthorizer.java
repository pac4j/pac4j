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
import org.pac4j.core.profile.CommonProfile;

/**
 * Checks an access if the user profile has all the roles.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllRolesAuthorizer<U extends CommonProfile> implements Authorizer<U> {

    private final String[] expectedRoles;

    public RequireAllRolesAuthorizer(final String[] expectedRoles) {
        this.expectedRoles = expectedRoles;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthorized(WebContext context, U profile) {
        if (expectedRoles == null || expectedRoles.length == 0) {
            return true;
        }
        for (final String role : expectedRoles) {
            if (!profile.getRoles().contains(role)) {
                return false;
            }
        }
        return true;
    }
}
