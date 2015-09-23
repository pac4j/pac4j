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
import org.pac4j.http.credentials.TokenCredentials;

import java.util.Collection;

/**
 * Extracts a cookie value from the request context.
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieExtractor implements Extractor<TokenCredentials> {

    private final String cookieName;

    private final String clientName;

    public CookieExtractor(final String cookieName, final String clientName) {
        this.cookieName = cookieName;
        this.clientName = clientName;
    }

    @Override
    public TokenCredentials extract(final WebContext context) {
        final Collection<Cookie> col = context.getRequestCookies();
        for (final Cookie c : col) {
            if (c.getName().equals(this.cookieName)) {
                return new TokenCredentials(c.getValue(), clientName);
            }
        }
        return null;

    }
}
