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

package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.http.credentials.CookieCredentials;

import java.util.Collection;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public class CookieExtractor implements Extractor<CookieCredentials> {

    private final String cookieName;

    private final String cookieValue;

    private final String clientName;

    public CookieExtractor(final String cookieName, final String cookieValue, final String clientName) {
        this.cookieName = cookieName;
        this.clientName = clientName;
        this.cookieValue = cookieValue;
    }

    @Override
    public CookieCredentials extract(final WebContext context) {
        final Collection<Cookie> col = context.getRequestCookies();
        if (col.contains(new Cookie(this.cookieName, this.cookieValue))) {
            return null;
        }
        return new CookieCredentials(this.cookieName, this.cookieValue, clientName);
    }
}
