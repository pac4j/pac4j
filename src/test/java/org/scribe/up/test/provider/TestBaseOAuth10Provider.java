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

import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the OAuth credential retrieval in the BaseOAuth10Provider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestBaseOAuth10Provider extends TestCase {
    
    private BaseOAuth10Provider provider = new YahooProvider();
    
    private static final String VERIFIER = "verifier";
    
    private static final String VERIFIER2 = "verifier2";
    
    private static final String TOKEN = "token";
    
    private static final String TOKEN2 = "token2";
    
    public void testNoTokenNoVerifier() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        assertNull(provider.getCredential(parameters));
    }
    
    public void testNoToken() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] verifiers = new String[] {
            VERIFIER
        };
        parameters.put(BaseOAuth10Provider.OAUTH_VERIFIER, verifiers);
        assertNull(provider.getCredential(parameters));
    }
    
    public void testNoVerifier() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] tokens = new String[] {
            TOKEN
        };
        parameters.put(BaseOAuth10Provider.OAUTH_TOKEN, tokens);
        assertNull(provider.getCredential(parameters));
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
        OAuthCredential oauthCredential = provider.getCredential(parameters);
        assertNotNull(oauthCredential);
        assertEquals(TOKEN, oauthCredential.getToken());
        assertEquals(VERIFIER, oauthCredential.getVerifier());
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
        assertNull(provider.getCredential(parameters));
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
        assertNull(provider.getCredential(parameters));
    }
}
