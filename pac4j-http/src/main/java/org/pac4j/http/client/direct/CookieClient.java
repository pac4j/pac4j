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

import org.pac4j.core.client.ClientType;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.credentials.extractor.CookieExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * Allows direct authentication based on a cookie.
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieClient extends DirectHttpClient<TokenCredentials>  {
    private String cookieName;


    public CookieClient() {
    }

    public CookieClient(final TokenAuthenticator cookieAuthenticator) {
        setAuthenticator(cookieAuthenticator);
    }

    public CookieClient(final TokenAuthenticator cookieAuthenticator,
                        final ProfileCreator profileCreator) {
        setAuthenticator(cookieAuthenticator);
        setProfileCreator(profileCreator);
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("cookieName", this.cookieName);
        extractor = new CookieExtractor(this.cookieName, getName());
        super.internalInit();

    }

    @Override
    protected CookieClient newClient() {
        final CookieClient newClient = new CookieClient();
        newClient.setCookieName(this.cookieName);
        return newClient;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.COOKIE_BASED;
    }
}
