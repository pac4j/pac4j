/*
 *    Copyright 2012 - 2015 pac4j organization
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.TokenAuthenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.extractor.CookieExtractor;

/**
 * Allows direct authentication based on a cookie.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieClient extends DirectClient2<TokenCredentials, CommonProfile> {

    private String cookieName;

    public CookieClient() {}

    @Deprecated
    public CookieClient(final Authenticator cookieAuthenticator) {
        setAuthenticator(cookieAuthenticator);
    }

    public CookieClient(final String cookieName, final Authenticator cookieAuthenticator) {
        this.cookieName = cookieName;
        setAuthenticator(cookieAuthenticator);
    }

    @Deprecated
    public CookieClient(final Authenticator cookieAuthenticator,
                        final ProfileCreator profileCreator) {
        setAuthenticator(cookieAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("cookieName", this.cookieName);
        setExtractor(new CookieExtractor(this.cookieName, getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(TokenAuthenticator.class);
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
