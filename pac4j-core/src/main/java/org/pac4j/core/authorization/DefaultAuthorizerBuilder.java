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

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.util.CommonHelper;

import java.util.Map;

/**
 * Will be removed before the release of pac4j v1.8.
 *
 * @deprecated
 */
@Deprecated
public class DefaultAuthorizerBuilder {

    private static final Authorizer DEFAULT_AUTHORIZER = new IsAuthenticatedAuthorizer();

    public static Authorizer build(final Authorizer authorizer, final String authorizerName, final Map<String, Authorizer> authorizers,
                                   final String requireAnyRole, final String requireAllRoles) {
        // we already have an authorizer
        if (authorizer != null) {
            return authorizer;
        }
        // we have an authorizer name
        if (CommonHelper.isNotBlank(authorizerName)) {
            // we must have authorizers
            CommonHelper.assertNotNull("authorizers", authorizers);
            final Authorizer result = authorizers.get(authorizerName);
            // we must have an authorizer defined for this name
            CommonHelper.assertNotNull("authorizers['" + authorizerName + "']", result);
            return result;
        }
        // we have a requireAnyRole value
        if (CommonHelper.isNotBlank(requireAnyRole)) {
            return new RequireAnyRoleAuthorizer(requireAnyRole.split(Pac4jConstants.ELEMENT_SEPRATOR));
        }
        // we have a requireAllRoles value
        if (CommonHelper.isNotBlank(requireAllRoles)) {
            return new RequireAllRolesAuthorizer(requireAllRoles.split(Pac4jConstants.ELEMENT_SEPRATOR));
        }
        return DEFAULT_AUTHORIZER;
    }
}
