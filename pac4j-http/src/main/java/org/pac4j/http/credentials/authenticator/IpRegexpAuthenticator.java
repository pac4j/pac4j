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
package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.TokenCredentials;

import java.util.regex.Pattern;

/**
 * Authenticates users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpRegexpAuthenticator implements TokenAuthenticator {

    private final Pattern pattern;

    public IpRegexpAuthenticator(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public void validate(final TokenCredentials credentials) {
        final String ip = credentials.getToken();

        if (!this.pattern.matcher(ip).matches()) {
            throw new CredentialsException("Unauthorized IP address: " + ip);
        }
    }
}
