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
import org.pac4j.core.util.CommonHelper;

/**
 * Checks an access regarding roles (require any role OR require all roles).
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HasAccessRoleAuthorizer<U extends CommonProfile> extends OrMultiAuthorizer<U> {

    public HasAccessRoleAuthorizer(final String requireAnyRole, final String requireAllRoles) {
        super(buildAuthorizers(requireAnyRole, requireAllRoles));
    }

    @Override
    public boolean isAuthorized(WebContext context, U profile) {
        // if no configuration, grant access by default
        if (authorizers == null) {
            return true;
        }
        return super.isAuthorized(context, profile);
    }

    private static Authorizer[] buildAuthorizers(final String requireAnyRole, final String requireAllRoles) {
        // if no configuration, no authorizers to grant access by default
        if (CommonHelper.isBlank(requireAnyRole) && CommonHelper.isBlank((requireAllRoles))) {
            return null;
        }

        Authorizer[] authorizers = new Authorizer[2];
        if (CommonHelper.isNotBlank(requireAnyRole)) {
            authorizers[0] = new RequireAnyRoleAuthorizer(requireAnyRole.split(","));
        } else {
            authorizers[0] = null;
        }
        if (CommonHelper.isNotBlank(requireAllRoles)) {
            authorizers[1] = new RequireAllRolesAuthorizer(requireAllRoles.split(","));
        } else {
            authorizers[1] = null;
        }
        return authorizers;
    }
}
