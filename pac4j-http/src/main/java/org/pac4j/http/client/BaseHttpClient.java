/*
  Copyright 2012 - 2014 pac4j organization

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
package org.pac4j.http.client;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.profile.HttpProfile;

/**
 * This class is the base HTTP client to authenticate users through HTTP protocol.
 * <p />
 * The {@link #getAuthenticator()} and {@link #getProfileCreator()} are mandatory for the HTTP protocol.
 * <p />
 * It returns a {@link org.pac4j.http.profile.HttpProfile}.
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseHttpClient<C extends Credentials> extends BaseClient<C, HttpProfile> {

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
    }

    @Override
    protected HttpProfile retrieveUserProfile(final C credentials, final WebContext context) {
        // create the user profile
        final HttpProfile profile = getProfileCreator().create(credentials);
        logger.debug("profile : {}", profile);
        return profile;
    }
}
