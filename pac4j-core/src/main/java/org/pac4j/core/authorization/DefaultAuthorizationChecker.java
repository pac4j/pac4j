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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default way to check the authorizations.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    public boolean isAuthorized(final WebContext context, final UserProfile profile, final String authorizerName, final Map<String, Authorizer> authorizersMap) {
        final List<Authorizer> authorizers = new ArrayList<>();
        // if we have an authorizer name (which may be a list of authorizer names)
        if (CommonHelper.isNotBlank(authorizerName)) {
            // we must have authorizers
            CommonHelper.assertNotNull("authorizersMap", authorizers);
            final String[] names = authorizerName.split(Pac4jConstants.ELEMENT_SEPRATOR);
            final int nb = names.length;
            for (int i = 0; i < nb; i++) {
                final String name = names[i];
                final Authorizer result = authorizersMap.get(name);
                // we must have an authorizer defined for this name
                CommonHelper.assertNotNull("authorizersMap['" + name + "']", result);
                authorizers.add(result);
            }
        }
        return isAuthorized(context, profile, authorizers);
    }

    public boolean isAuthorized(final WebContext context, final UserProfile profile, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null
        CommonHelper.assertNotNull("profile", profile);
        if (authorizers != null && authorizers.size() > 0) {
            // check authorizations using authorizers: all must be satisfied
            for (Authorizer authorizer : authorizers) {
                if (!authorizer.isAuthorized(context, profile)) {
                    return false;
                }
            }
        }
        return true;
    }
}
