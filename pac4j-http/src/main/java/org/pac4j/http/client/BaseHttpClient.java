/*
  Copyright 2012 - 2014 Jerome Leleu

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
import org.pac4j.core.client.Protocol;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.ProfileCreator;
import org.pac4j.http.profile.UsernameProfileCreator;

/**
 * This class is the base HTTP client to authenticate users through HTTP protocol.
 * <p />
 * The username and password inputs must be retrieved through HTTP protocol.
 * <p />
 * To validate credentials, an {@link UsernamePasswordAuthenticator} must be defined through the
 * {@link #setUsernamePasswordAuthenticator(UsernamePasswordAuthenticator)} method.
 * <p />
 * To create the profile, a {@link ProfileCreator} must be defined through the {@link #setProfileCreator(ProfileCreator)} method.
 * <p />
 * It returns a {@link org.pac4j.http.profile.HttpProfile}.
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseHttpClient extends BaseClient<UsernamePasswordCredentials, HttpProfile> {
    
    protected UsernamePasswordAuthenticator usernamePasswordAuthenticator;
    
    private ProfileCreator profileCreator = new UsernameProfileCreator();
    
    @Override
    public BaseHttpClient clone() {
        final BaseHttpClient newClient = (BaseHttpClient) super.clone();
        newClient.setUsernamePasswordAuthenticator(this.usernamePasswordAuthenticator);
        newClient.setProfileCreator(this.profileCreator);
        return newClient;
    }
    
    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("usernamePasswordAuthenticator", this.usernamePasswordAuthenticator);
        CommonHelper.assertNotNull("profileCreator", this.profileCreator);
    }
    
    @Override
    protected HttpProfile retrieveUserProfile(final UsernamePasswordCredentials credentials, final WebContext context) {
        // create user profile
        final HttpProfile profile = this.profileCreator.create(credentials.getUsername());
        logger.debug("profile : {}", profile);
        return profile;
    }
    
    public UsernamePasswordAuthenticator getUsernamePasswordAuthenticator() {
        return this.usernamePasswordAuthenticator;
    }
    
    public void setUsernamePasswordAuthenticator(final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        this.usernamePasswordAuthenticator = usernamePasswordAuthenticator;
    }
    
    public ProfileCreator getProfileCreator() {
        return this.profileCreator;
    }
    
    public void setProfileCreator(final ProfileCreator profileCreator) {
        this.profileCreator = profileCreator;
    }
    
    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }
}
