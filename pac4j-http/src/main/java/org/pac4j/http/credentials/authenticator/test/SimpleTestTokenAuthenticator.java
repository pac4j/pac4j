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
package org.pac4j.http.credentials.authenticator.test;

import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;

/**
 * This class is a simple test authenticator: token must not be blank.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class SimpleTestTokenAuthenticator implements TokenAuthenticator {

    @Override
    public void validate(final TokenCredentials credentials) {
        if (credentials == null) {
            throw new CredentialsException("credentials must not be null");
        }
        if (CommonHelper.isBlank(credentials.getToken())) {
            throw new CredentialsException("token must not be blank");
        }
    }
}
