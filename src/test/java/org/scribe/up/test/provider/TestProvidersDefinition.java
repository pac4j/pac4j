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
    
    private static final String NEW_PARAMETER_NAME = "keepTheTypeOfTheProvider";
    
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
    
    public void testMissingProvider() {
        ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setBaseUrl(URL);
        try {
            providersDefinition.init();
            fail("init() cannot succeed");
        } catch (IllegalArgumentException e) {
            assertEquals("providers cannot be null", e.getMessage());
        }
    }
    
    public void testMissingBaseUrl() {
        ProvidersDefinition providersDefinition = new ProvidersDefinition();
        List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(newFacebookProvider());
        try {
            providersDefinition.init();
            fail("init() cannot succeed");
        } catch (IllegalArgumentException e) {
            assertEquals("baseUrl cannot be blank", e.getMessage());
        }
    }
    
    public void testOneProvider() {
        FacebookProvider facebookProvider = newFacebookProvider();
        facebookProvider.setCallbackUrl(URL);
        ProvidersDefinition providersDefinition = new ProvidersDefinition(facebookProvider);
        providersDefinition.init();
        providersDefinition.init();
        assertEquals(URL + "?" + ProvidersDefinition.DEFAULT_PROVIDER_TYPE_PARAMETER + "=" + facebookProvider.getType(),
                     facebookProvider.getCallbackUrl());
        String[] values = new String[] {
            facebookProvider.getType()
        };
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(ProvidersDefinition.DEFAULT_PROVIDER_TYPE_PARAMETER, values);
        assertEquals(facebookProvider, providersDefinition.findProvider(parameters));
        assertEquals(facebookProvider, providersDefinition.findProvider(facebookProvider.getType()));
    }
    
    public void testTwoProviders() {
        FacebookProvider facebookProvider = newFacebookProvider();
        YahooProvider yahooProvider = newYahooProvider();
        List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(facebookProvider);
        providers.add(yahooProvider);
        ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setProviderTypeParameter(NEW_PARAMETER_NAME);
        providersDefinition.setProviders(providers);
        providersDefinition.setBaseUrl(URL);
        assertNull(facebookProvider.getCallbackUrl());
        assertNull(yahooProvider.getCallbackUrl());
        providersDefinition.init();
        assertEquals(URL + "?" + NEW_PARAMETER_NAME + "=" + facebookProvider.getType(),
                     facebookProvider.getCallbackUrl());
        assertEquals(URL + "?" + NEW_PARAMETER_NAME + "=" + yahooProvider.getType(), yahooProvider.getCallbackUrl());
        String[] values = new String[] {
            yahooProvider.getType()
        };
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(NEW_PARAMETER_NAME, values);
        assertEquals(yahooProvider, providersDefinition.findProvider(parameters));
        assertEquals(yahooProvider, providersDefinition.findProvider(yahooProvider.getType()));
    }
}
