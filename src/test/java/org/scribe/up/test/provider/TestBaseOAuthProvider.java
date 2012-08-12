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
import org.scribe.up.provider.BaseOAuth20Provider;
import org.scribe.up.provider.BaseOAuthProvider;
import org.scribe.up.provider.impl.DropBoxProvider;
import org.scribe.up.provider.impl.FacebookProvider;
import org.scribe.up.provider.impl.GitHubProvider;
import org.scribe.up.provider.impl.GoogleProvider;
import org.scribe.up.provider.impl.LinkedInProvider;
import org.scribe.up.provider.impl.TwitterProvider;
import org.scribe.up.provider.impl.WindowsLiveProvider;
import org.scribe.up.provider.impl.WordPressProvider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the type returned by the {@link org.scribe.up.provider.BaseOAuthProvider} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestBaseOAuthProvider extends TestCase {
    
    private static final String FAKE_VALUE = "fakeValue";
    
    private static final String KEY = "key";
    
    private static final String SECRET = "secret";
    
    private static final String CALLBACK_URL = "callback_url";
    
    private static final String SCOPE = "scope";
    
    private static final int CONNECT_TIMEOUT = 135;
    
    private static final int READ_TIMEOUT = 2896;
    
    public void testType() {
        BaseOAuth10Provider provider = new YahooProvider();
        assertEquals("YahooProvider", provider.getType());
    }
    
    private void addSingledValueParameter(Map<String, String[]> parameters, String key, String value) {
        String[] values = new String[1];
        values[0] = value;
        parameters.put(key, values);
    }
    
    private Map<String, String[]> createParameters(String key, String value) {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        addSingledValueParameter(parameters, BaseOAuth20Provider.OAUTH_CODE, FAKE_VALUE);
        addSingledValueParameter(parameters, key, value);
        return parameters;
    }
    
    public void testGetCredentialOK() {
        BaseOAuthProvider provider = new FacebookProvider();
        provider.setKey(KEY);
        provider.setSecret(SECRET);
        provider.setCallbackUrl(CALLBACK_URL);
        Map<String, String[]> parameters = createParameters(null, null);
        assertTrue(provider.getCredential(null, parameters) instanceof OAuthCredential);
    }
    
    public void testGetCredentialError() {
        BaseOAuthProvider provider = new FacebookProvider();
        provider.setKey(KEY);
        provider.setSecret(SECRET);
        provider.setCallbackUrl(CALLBACK_URL);
        for (String key : BaseOAuthProvider.ERROR_PARAMETERS) {
            Map<String, String[]> parameters = createParameters(key, FAKE_VALUE);
            assertNull(provider.getCredential(null, parameters));
        }
    }
    
    private BaseOAuthProvider internalTestCloneBaseOAuthProvider(BaseOAuthProvider oldProvider) {
        oldProvider.setKey(KEY);
        oldProvider.setSecret(SECRET);
        oldProvider.setCallbackUrl(CALLBACK_URL);
        oldProvider.setScope(SCOPE);
        oldProvider.setConnectTimeout(CONNECT_TIMEOUT);
        oldProvider.setReadTimeout(READ_TIMEOUT);
        BaseOAuthProvider provider = oldProvider.clone();
        assertEquals(oldProvider.getKey(), provider.getKey());
        assertEquals(oldProvider.getSecret(), provider.getSecret());
        assertEquals(oldProvider.getCallbackUrl(), provider.getCallbackUrl());
        assertEquals(oldProvider.getScope(), provider.getScope());
        assertEquals(oldProvider.getConnectTimeout(), provider.getConnectTimeout());
        assertEquals(oldProvider.getReadTimeout(), provider.getReadTimeout());
        return provider;
    }
    
    public void testCloneDropBoxProvider() {
        internalTestCloneBaseOAuthProvider(new DropBoxProvider());
    }
    
    public void testCloneFacebookProvider() {
        FacebookProvider oldProvider = new FacebookProvider();
        oldProvider.setFriendsReturned(true);
        oldProvider.setMoviesReturned(true);
        oldProvider.setMusicReturned(true);
        oldProvider.setBooksReturned(true);
        oldProvider.setLikesReturned(true);
        oldProvider.setAlbumsReturned(true);
        oldProvider.setEventsReturned(true);
        FacebookProvider provider = (FacebookProvider) internalTestCloneBaseOAuthProvider(oldProvider);
        assertEquals(oldProvider.isFriendsReturned(), provider.isFriendsReturned());
        assertEquals(oldProvider.isMoviesReturned(), provider.isMoviesReturned());
        assertEquals(oldProvider.isMusicReturned(), provider.isMusicReturned());
        assertEquals(oldProvider.isBooksReturned(), provider.isBooksReturned());
        assertEquals(oldProvider.isLikesReturned(), provider.isLikesReturned());
        assertEquals(oldProvider.isAlbumsReturned(), provider.isAlbumsReturned());
        assertEquals(oldProvider.isEventsReturned(), provider.isEventsReturned());
    }
    
    public void testCloneGitHubProvider() {
        internalTestCloneBaseOAuthProvider(new GitHubProvider());
    }
    
    public void testCloneGoogleProvider() {
        internalTestCloneBaseOAuthProvider(new GoogleProvider());
    }
    
    public void testCloneLinkedInProvider() {
        internalTestCloneBaseOAuthProvider(new LinkedInProvider());
    }
    
    public void testCloneTwitterProvider() {
        internalTestCloneBaseOAuthProvider(new TwitterProvider());
    }
    
    public void testCloneWindowsLiveProvider() {
        internalTestCloneBaseOAuthProvider(new WindowsLiveProvider());
    }
    
    public void testCloneWordPressProvider() {
        internalTestCloneBaseOAuthProvider(new WordPressProvider());
    }
    
    public void testCloneYahooProvider() {
        internalTestCloneBaseOAuthProvider(new YahooProvider());
    }
}
