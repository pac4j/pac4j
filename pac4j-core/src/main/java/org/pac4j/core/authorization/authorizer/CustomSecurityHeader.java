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

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

/**
 * Add a custom security header.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CustomSecurityHeader implements Authorizer<UserProfile> {

    private final String name;

    private final String value;

    public CustomSecurityHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final UserProfile profile) {
        context.setResponseHeader(name, value);
        return true;
    }
}
