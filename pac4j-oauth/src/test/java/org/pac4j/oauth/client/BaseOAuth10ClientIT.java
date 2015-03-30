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
package org.pac4j.oauth.client;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.scribe.model.Token;

/**
 * This class tests the OAuth credential retrieval in the {@link org.pac4j.oauth.client.BaseOAuth10Client} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuth10ClientIT extends TestCase implements TestsConstants {
    
    @SuppressWarnings("rawtypes")
    private BaseOAuth10Client getClient() {
        final YahooClient client = new YahooClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }
    
    public void testNoTokenNoVerifier() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create());
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }
    
    public void testNoToken() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER,
                                                                                   VERIFIER));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }
    
    public void testNoVerifier() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create()
                                           .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }
    
    public void testOk() throws RequiresHttpAction {
        final OAuthCredentials credentials = (OAuthCredentials) getClient()
            .getCredentials(MockWebContext
                                .create()
                                .addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER, VERIFIER)
                                .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN)
                                .addSessionAttribute(getClient().getName() + "#" + BaseOAuth10Client.REQUEST_TOKEN,
                                                     new Token(TOKEN, SECRET)));
        assertNotNull(credentials);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        final Token tokenRequest = credentials.getRequestToken();
        assertEquals(TOKEN, tokenRequest.getToken());
        assertEquals(SECRET, tokenRequest.getSecret());
    }
}
