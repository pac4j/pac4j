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
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;

/**
 * This class tests the {@link BaseOAuthClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public final class BaseOAuthClientIT extends TestCase implements TestsConstants {
    
    public void testDefaultName10() {
        final BaseOAuth10Client client = new YahooClient();
        assertEquals("YahooClient", client.getName());
    }
    
    public void testDefaultName20() {
        final BaseOAuth20Client client = new FacebookClient();
        assertEquals("FacebookClient", client.getName());
    }
    
    public void testDefinedName() {
        final BaseOAuth20Client client = new FacebookClient();
        client.setName(TYPE);
        assertEquals(TYPE, client.getName());
    }
    
    public void testGetCredentialOK() throws RequiresHttpAction {
        final BaseOAuthClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        assertTrue(client.getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE,
                                                                                     FAKE_VALUE)) instanceof OAuthCredentials);
    }
    
    public void testGetCredentialError() throws RequiresHttpAction {
        final BaseOAuthClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE,
                                                                                   FAKE_VALUE);
        for (final String key : OAuthCredentialsException.ERROR_NAMES) {
            context.addRequestParameter(key, FAKE_VALUE);
        }
        try {
            client.getCredentials(context);
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("Failed to retrieve OAuth credentials, error parameters found", e.getMessage());
        }
    }
}
