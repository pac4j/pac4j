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

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuthCredentials;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * This class tests the OAuth credential retrieval in the {@link org.pac4j.oauth.client.BaseOAuth20Client} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuth20ClientTests implements TestsConstants {
    
    private BaseOAuth20Client getClient() {
        final GitHubClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testNoCode() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create());
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testOk() throws RequiresHttpAction {
        final OAuthCredentials oauthCredential = (OAuthCredentials) getClient()
            .getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE, CODE));
        assertNotNull(oauthCredential);
        assertEquals(CODE, oauthCredential.getVerifier());
    }

    @Test
    public void testState() throws MalformedURLException, RequiresHttpAction {
        BaseOAuth20StateClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setState("OK");
        URL url = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        assertTrue(url.getQuery().contains("state=OK"));
    }

    @Test
    public void testGetRedirectionGithub() throws RequiresHttpAction {
        String url = getClient().getRedirectAction(MockWebContext.create()).getLocation();
        assertTrue(url != null && !url.isEmpty());
    }
}
