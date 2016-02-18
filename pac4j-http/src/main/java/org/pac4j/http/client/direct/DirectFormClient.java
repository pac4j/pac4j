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

import org.pac4j.core.client.DirectClient2;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;

/**
 * This class is the client to authenticate users, based on form HTTP parameters.
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
public class DirectFormClient extends DirectClient2<UsernamePasswordCredentials, CommonProfile> {

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public DirectFormClient() {}

    public DirectFormClient(final Authenticator usernamePasswordAuthenticator) {
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectFormClient(final String usernameParameter, final String passwordParameter,
                            final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectFormClient(final Authenticator usernamePasswordAuthenticator,
                            final ProfileCreator profileCreator) {
        setAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("usernameParameter", usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", passwordParameter);
        setExtractor(new FormExtractor(usernameParameter, passwordParameter, getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(UsernamePasswordAuthenticator.class);
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
                "passwordParameter", this.passwordParameter, "extractor", getExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
