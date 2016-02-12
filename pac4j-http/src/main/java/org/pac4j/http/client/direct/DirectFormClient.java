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
package org.pac4j.http.client.direct;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.extractor.FormExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * <p>This class is the client to authenticate users, based on form HTTP parameters.</p>
 * <p>It returns a {@link org.pac4j.http.profile.HttpProfile}.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
public class DirectFormClient extends DirectHttpClient<UsernamePasswordCredentials> {

    public final static String DEFAULT_USERNAME_PARAMETER = "username";

    private String usernameParameter = DEFAULT_USERNAME_PARAMETER;

    public final static String DEFAULT_PASSWORD_PARAMETER = "password";

    private String passwordParameter = DEFAULT_PASSWORD_PARAMETER;

    public DirectFormClient() {
    }

    public DirectFormClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectFormClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator,
                            final ProfileCreator profileCreator) {
        setAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        extractor = new FormExtractor(usernameParameter, passwordParameter, getName());
        super.internalInit(context);
    }

    public String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return this.passwordParameter;
    }

    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "usernameParameter", this.usernameParameter,
                "passwordParameter", this.passwordParameter, "authenticator", getAuthenticator(), "profileCreator",
                getProfileCreator());
    }
}
