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
import org.scribe.up.provider.impl.FacebookProvider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the type returned by the {@link org.scribe.up.provider.BaseOAuthProvider} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestBaseOAuthProvider extends TestCase {
    
    private static final String FAKE_VALUE = "fakeValue";
    
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
        Map<String, String[]> parameters = createParameters(null, null);
        assertTrue(provider.getCredential(null, parameters) instanceof OAuthCredential);
    }
    
    public void testGetCredentialError() {
        BaseOAuthProvider provider = new FacebookProvider();
        for (String key : BaseOAuthProvider.ERROR_PARAMETERS) {
            Map<String, String[]> parameters = createParameters(key, FAKE_VALUE);
            assertNull(provider.getCredential(null, parameters));
        }
    }
}
