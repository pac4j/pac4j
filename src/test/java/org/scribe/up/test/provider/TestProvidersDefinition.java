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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.scribe.up.provider.impl.FacebookProvider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the {@link org.scribe.up.provider.ProvidersDefinition} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public final class TestProvidersDefinition extends TestCase {
    
    private static final String KEY = "key";
    
    private static final String SECRET = "secret";
    
    private static final String URL = "http://url";
    
    private static final String PARAMETER = "oauth_provider_type";
    
    private FacebookProvider newFacebookProvider() {
        FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey(KEY);
        facebookProvider.setSecret(SECRET);
        return facebookProvider;
    }
    
    private YahooProvider newYahooProvider() {
        YahooProvider yahooProvider = new YahooProvider();
        yahooProvider.setKey(KEY);
        yahooProvider.setSecret(SECRET);
        return yahooProvider;
    }
    
    public void testOneProvider() {
        FacebookProvider facebookProvider = newFacebookProvider();
        ProvidersDefinition providersDefinition = new ProvidersDefinition(facebookProvider);
        providersDefinition.setBaseUrl(URL);
        assertNull(facebookProvider.getCallbackUrl());
        providersDefinition.init();
        assertEquals(URL + "?" + PARAMETER + "=" + facebookProvider.getType(), facebookProvider.getCallbackUrl());
        String[] values = new String[] {
            facebookProvider.getType()
        };
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(PARAMETER, values);
        OAuthProvider provider = providersDefinition.findProvider(parameters);
        assertEquals(provider, facebookProvider);
    }
    
    public void testTwoProviders() {
        FacebookProvider facebookProvider = newFacebookProvider();
        YahooProvider yahooProvider = newYahooProvider();
        List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(facebookProvider);
        providers.add(yahooProvider);
        ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setProviders(providers);
        providersDefinition.setBaseUrl(URL);
        assertNull(facebookProvider.getCallbackUrl());
        assertNull(yahooProvider.getCallbackUrl());
        providersDefinition.init();
        assertEquals(URL + "?" + PARAMETER + "=" + facebookProvider.getType(), facebookProvider.getCallbackUrl());
        assertEquals(URL + "?" + PARAMETER + "=" + yahooProvider.getType(), yahooProvider.getCallbackUrl());
        String[] values = new String[] {
            yahooProvider.getType()
        };
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(PARAMETER, values);
        OAuthProvider provider = providersDefinition.findProvider(parameters);
        assertEquals(provider, yahooProvider);
    }
}
