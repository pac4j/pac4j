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
package org.pac4j.http.client.indirect;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.http.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.http.credentials.extractor.Extractor;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.http.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.profile.HttpProfile;

/**
 * <p>This class is the base HTTP client to authenticate <b>indirectly</b> users through HTTP protocol.</p>
 * <p>The {@link #getAuthenticator()} and {@link #getProfileCreator()} are mandatory for the HTTP protocol.</p>
 * <p>It returns a {@link org.pac4j.http.profile.HttpProfile}.</p>
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class IndirectHttpClient<C extends Credentials> extends IndirectClient<C, HttpProfile> {

    protected Extractor<UsernamePasswordCredentials> extractor;

    private Authenticator<C> authenticator;

    private ProfileCreator<C, HttpProfile> profileCreator =  AuthenticatorProfileCreator.INSTANCE;

    @Override
    public IndirectHttpClient<C> clone() {
        final IndirectHttpClient<C> newClient = (IndirectHttpClient<C>) super.clone();
        newClient.setCallbackUrl(this.callbackUrl);
        newClient.setAuthenticator(this.authenticator);
        newClient.setProfileCreator(this.profileCreator);
        return newClient;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("extractor", this.extractor);
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
    }

    @Override
    protected HttpProfile retrieveUserProfile(final C credentials, final WebContext context) {
        // create the user profile
        final HttpProfile profile = getProfileCreator().create(credentials);
        logger.debug("profile: {}", profile);
        return profile;
    }

    public Authenticator<C> getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator<C> authenticator) {
        this.authenticator = authenticator;
    }

    public ProfileCreator<C, HttpProfile> getProfileCreator() {
        return profileCreator;
    }

    public void setProfileCreator(ProfileCreator<C, HttpProfile> profileCreator) {
        this.profileCreator = profileCreator;
    }
}
