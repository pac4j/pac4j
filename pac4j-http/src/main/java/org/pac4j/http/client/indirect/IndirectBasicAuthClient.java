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

import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.extractor.BasicAuthExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * <p>This class is the client to authenticate users through HTTP basic auth. It was previously named: <code>BasicAuthClient</code>.</p>
 * <p>For authentication, the user is redirected to the callback url. If the user is not authenticated by basic auth, a
 * specific exception : {@link RequiresHttpAction} is returned which must be handled by the application to force
 * authentication.</p>
 * <p>It returns a {@link org.pac4j.http.profile.HttpProfile}.</p>
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class IndirectBasicAuthClient extends IndirectHttpClient<UsernamePasswordCredentials> {

    private String headerName = HttpConstants.AUTHORIZATION_HEADER;

    private String prefixHeader = "Basic ";

    private String realmName = "authentication required";

    public IndirectBasicAuthClient() { }

    public IndirectBasicAuthClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public IndirectBasicAuthClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator,
                                   final ProfileCreator profileCreator) {
        setAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        extractor = new BasicAuthExtractor(headerName, prefixHeader, getName());
        super.internalInit(context);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotBlank("realmName", this.realmName);
    }

    @Override
    protected IndirectBasicAuthClient newClient() {
        return new IndirectBasicAuthClient();
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        return RedirectAction.redirect(computeFinalCallbackUrl(context));
    }

    @Override
    protected UsernamePasswordCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials;
        try {
            // retrieve credentials
            credentials = extractor.extract(context);
            logger.debug("credentials : {}", credentials);
            
            if (credentials == null) {
              throw RequiresHttpAction.unauthorized("Requires authentication", context, this.realmName);
            }
            
            // validate credentials
            getAuthenticator().validate(credentials);
        } catch (final CredentialsException e) {
            throw RequiresHttpAction.unauthorized("Requires authentication", context,
                    this.realmName);
        }

        return credentials;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(), "realmName",
                this.realmName, "headerName", this.headerName, "prefixHeader", this.prefixHeader, "authenticator",
                getAuthenticator(), "profileCreator", getProfileCreator());
    }

    @Override
    protected boolean isDirectRedirection() {
        return true;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.BASICAUTH_BASED;
    }
}
