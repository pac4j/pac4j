/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.provider;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.provider.impl.YahooProvider;
import org.scribe.up.test.util.SingleUserSession;

/**
 * This class tests the OAuth credential retrieval in the {@link org.scribe.up.provider.BaseOAuth10Provider} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestBaseOAuth10Provider extends TestCase {
    
    private static final String VERIFIER = "verifier";
    
    private static final String VERIFIER2 = "verifier2";
    
    private static final String TOKEN = "token";
    
    private static final String TOKEN2 = "token2";
    
    private static final String SECRET = "secret";
    
    private BaseOAuth10Provider getProvider() {
        YahooProvider provider = new YahooProvider();
        provider.setKey("key");
        provider.setSecret("secret");
        provider.setCallbackUrl("callbackUrl");
        return provider;
    }
    
    public void testNoTokenNoVerifier() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        assertNull(getProvider().getCredential(null, parameters));
    }
    
    public void testNoToken() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] verifiers = new String[] {
            VERIFIER
        };
        parameters.put(BaseOAuth10Provider.OAUTH_VERIFIER, verifiers);
        assertNull(getProvider().getCredential(null, parameters));
    }
    
    public void testNoVerifier() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] tokens = new String[] {
            TOKEN
        };
        parameters.put(BaseOAuth10Provider.OAUTH_TOKEN, tokens);
        assertNull(getProvider().getCredential(null, parameters));
    }
    
    public void testOk() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] verifiers = new String[] {
            VERIFIER
        };
        String[] tokens = new String[] {
            TOKEN
        };
        parameters.put(BaseOAuth10Provider.OAUTH_VERIFIER, verifiers);
        parameters.put(BaseOAuth10Provider.OAUTH_TOKEN, tokens);
        SingleUserSession singleUserSession = new SingleUserSession();
        singleUserSession.setAttribute(getProvider().getType() + "#" + BaseOAuth10Provider.REQUEST_TOKEN,
                                       new Token(TOKEN, SECRET));
        OAuthCredential oauthCredential = getProvider().getCredential(singleUserSession, parameters);
        assertNotNull(oauthCredential);
        assertEquals(TOKEN, oauthCredential.getToken());
        assertEquals(VERIFIER, oauthCredential.getVerifier());
        Token tokenRequest = oauthCredential.getRequestToken();
        assertEquals(TOKEN, tokenRequest.getToken());
        assertEquals(SECRET, tokenRequest.getSecret());
    }
    
    public void testTwoTokens() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] verifiers = new String[] {
            VERIFIER
        };
        String[] tokens = new String[] {
            TOKEN, TOKEN2
        };
        parameters.put(BaseOAuth10Provider.OAUTH_VERIFIER, verifiers);
        parameters.put(BaseOAuth10Provider.OAUTH_TOKEN, tokens);
        assertNull(getProvider().getCredential(null, parameters));
    }
    
    public void testTwoVerifiers() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] verifiers = new String[] {
            VERIFIER, VERIFIER2
        };
        String[] tokens = new String[] {
            TOKEN
        };
        parameters.put(BaseOAuth10Provider.OAUTH_VERIFIER, verifiers);
        parameters.put(BaseOAuth10Provider.OAUTH_TOKEN, tokens);
        assertNull(getProvider().getCredential(null, parameters));
    }
}
