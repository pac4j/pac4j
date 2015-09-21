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
import org.pac4j.http.credentials.CookieCredentials;
import org.pac4j.http.credentials.authenticator.CookieAuthenticator;
import org.pac4j.http.credentials.extractor.CookieExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public class CookieClient extends DirectHttpClient<CookieCredentials>  {
    private String cookieName;
    private String cookieValue;

    public CookieClient() {
    }

    public CookieClient(final CookieAuthenticator cookieAuthenticator) {
        setAuthenticator(cookieAuthenticator);
    }

    public CookieClient(final CookieAuthenticator cookieAuthenticator,
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

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("cookieName", this.cookieName);
        extractor = new CookieExtractor(this.cookieName, this.cookieValue, getName());
        super.internalInit();

    }

    @Override
    protected CookieClient newClient() {
        final CookieClient newClient = new CookieClient();
        newClient.setCookieName(this.cookieName);
        newClient.setCookieValue(this.cookieValue);
        return newClient;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.COOKIE_BASED;
    }
}
