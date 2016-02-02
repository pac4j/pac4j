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
package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.Token;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link OAuthCredentials} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthCredentialsTests implements TestsConstants {

    private final static Token REQUEST_TOKEN = new Token(TOKEN, SECRET);

    @Test
    public void testOAuthCredentials() {
        final OAuthCredentials credentials = new OAuthCredentials(REQUEST_TOKEN, TOKEN, VERIFIER, TYPE);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        assertEquals(TYPE, credentials.getClientName());
        final Token requestToken = credentials.getRequestToken();
        assertEquals(TOKEN, requestToken.getToken());
        assertEquals(SECRET, requestToken.getSecret());
        // test serialization
        final JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();
        final byte[] bytes = javaSerializationHelper.serializeToBytes(credentials);
        final OAuthCredentials credentials2 = (OAuthCredentials) javaSerializationHelper.unserializeFromBytes(bytes);
        assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        assertEquals(credentials.getToken(), credentials2.getToken());
        assertEquals(credentials.getVerifier(), credentials2.getVerifier());
        assertEquals(credentials.getClientName(), credentials2.getClientName());
    }
}
