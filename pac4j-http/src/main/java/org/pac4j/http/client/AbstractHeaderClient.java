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

import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.profile.HttpProfile;

/**
 * This class is the client to authenticate users through HTTP given a provided header.
 * <p />
 * For authentication, the user is redirected to the callback url. If the user is not authenticated by a provided header,
 * a specific exception : {@link RequiresHttpAction} is returned which must be handled by the application to force
 * authentication.
 * <p />
 * It returns a {@link org.pac4j.http.profile.HttpProfile}.
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.7.0
 */
public abstract class AbstractHeaderClient<C extends Credentials> extends BaseHttpClient<C> {

    private String headerName;

    private String prefixHeader;

    private String realmName;

    public AbstractHeaderClient() {
    }

    public AbstractHeaderClient(final Authenticator<C> authenticator) {
        setAuthenticator(authenticator);
    }

    public AbstractHeaderClient(final Authenticator<C> authenticator, final ProfileCreator<C, HttpProfile> profilePopulator) {
        setAuthenticator(authenticator);
        setProfileCreator(profilePopulator);
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotBlank("prefixHeader", this.prefixHeader);
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        return RedirectAction.redirect(getContextualCallbackUrl(context));
    }

    @Override
    protected C retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        final String header = context.getRequestHeader(this.headerName);
        if (header == null || !header.startsWith(this.prefixHeader)) {
            logger.warn("No header found");
            throw RequiresHttpAction.unauthorized("Requires authentication (no header found)", context,
                    this.realmName);
        }

        C credentials = retrieveCredentialsFromHeader(header.substring(this.prefixHeader.length()));
        logger.debug("credentials : {}", credentials);
        try {
            // validate credentials
            getAuthenticator().validate(credentials);
        } catch (final RuntimeException e) {
            logger.error("Credentials validation fails", e);
            throw RequiresHttpAction.unauthorized("Requires authentication (credentials validation fails)", context,
                    this.realmName);
        }

        return credentials;
    }

    protected abstract C retrieveCredentialsFromHeader(final String header);

    @Override
    protected boolean isDirectRedirection() {
        return true;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getPrefixHeader() {
        return this.prefixHeader;
    }

    public void setPrefixHeader(String prefixHeader) {
        this.prefixHeader = prefixHeader;
    }

    public String getRealmName() {
        return this.realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}
