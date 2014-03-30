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

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.ProfileCreator;

/**
 * This class is the client to authenticate users through HTTP basic auth.
 * <p />
 * For authentication, the user is redirected to the callback url. If the user is not authenticated by basic auth, a
 * specific exception : {@link RequiresHttpAction} is returned which must be handled by the application to force
 * authentication.
 * <p />
 * The realm name can be defined using the {@link #setRealmName(String)} method.
 * <p />
 * It returns a {@link org.pac4j.http.profile.HttpProfile}.
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class BasicAuthClient extends BaseHttpClient {

    private String realmName = "authentication required";

    public BasicAuthClient() {
    }

    public BasicAuthClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        setUsernamePasswordAuthenticator(usernamePasswordAuthenticator);
    }

    public BasicAuthClient(final UsernamePasswordAuthenticator usernamePasswordAuthenticator,
            final ProfileCreator profilePopulator) {
        setUsernamePasswordAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profilePopulator);
    }

    @Override
    protected BaseClient<UsernamePasswordCredentials, HttpProfile> newClient() {
        final BasicAuthClient newClient = new BasicAuthClient();
        newClient.setRealmName(this.realmName);
        return newClient;
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotBlank("realmName", this.realmName);
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        return RedirectAction.redirect(getContextualCallbackUrl(context));
    }

    @Override
    protected UsernamePasswordCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        final String header = context.getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith("Basic ")) {
            logger.warn("No basic auth found");
            throw RequiresHttpAction.unauthorized("Requires basic auth (no basic auth header found)", context,
                    this.realmName);
        }
        final String base64Token = header.substring(6);
        final byte[] decoded = Base64.decodeBase64(base64Token);

        String token;
        try {
            token = new String(decoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new CredentialsException("Bad format of the basic auth header");
        }

        final int delim = token.indexOf(":");
        if (delim < 0) {
            throw new CredentialsException("Bad format of the basic auth header");
        }
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(token.substring(0, delim),
                token.substring(delim + 1), getName());
        logger.debug("usernamePasswordCredentials : {}", credentials);
        try {
            // validate credentials
            this.usernamePasswordAuthenticator.validate(credentials);
        } catch (final RuntimeException e) {
            logger.error("Credentials validation fails", e);
            throw RequiresHttpAction.unauthorized("Requires basic auth (credentials validation fails)", context,
                    this.realmName);
        }

        return credentials;
    }

    public String getRealmName() {
        return this.realmName;
    }

    public void setRealmName(final String realmName) {
        this.realmName = realmName;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(), "realmName",
                this.realmName, "usernamePasswordAuthenticator", getUsernamePasswordAuthenticator(), "profileCreator",
                getProfileCreator());
    }

    @Override
    protected boolean isDirectRedirection() {
        return true;
    }
}
