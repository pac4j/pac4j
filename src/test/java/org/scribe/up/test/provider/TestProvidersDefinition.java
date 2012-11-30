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
        final FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey(KEY);
        facebookProvider.setSecret(SECRET);
        return facebookProvider;
    }
    
    private YahooProvider newYahooProvider() {
        final YahooProvider yahooProvider = new YahooProvider();
        yahooProvider.setKey(KEY);
        yahooProvider.setSecret(SECRET);
        return yahooProvider;
    }
    
    public void testMissingProvider() {
        final ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setBaseUrl(URL);
        try {
            providersDefinition.init();
            fail("init() cannot succeed");
        } catch (final IllegalArgumentException e) {
            assertEquals("providers cannot be null", e.getMessage());
        }
    }
    
    public void testMissingBaseUrl() {
        final ProvidersDefinition providersDefinition = new ProvidersDefinition();
        final List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(newFacebookProvider());
        try {
            providersDefinition.init();
            fail("init() cannot succeed");
        } catch (final IllegalArgumentException e) {
            assertEquals("baseUrl cannot be blank", e.getMessage());
        }
    }
    
    public void testOneProvider() {
        final FacebookProvider facebookProvider = newFacebookProvider();
        facebookProvider.setCallbackUrl(URL);
        final ProvidersDefinition providersDefinition = new ProvidersDefinition(facebookProvider);
        providersDefinition.init();
        providersDefinition.init();
        assertEquals(URL + "?" + ProvidersDefinition.DEFAULT_PROVIDER_TYPE_PARAMETER + "=" + facebookProvider.getType(),
                     facebookProvider.getCallbackUrl());
        final String[] values = new String[] {
            facebookProvider.getType()
        };
        final Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(ProvidersDefinition.DEFAULT_PROVIDER_TYPE_PARAMETER, values);
        assertEquals(facebookProvider, providersDefinition.findProvider(parameters));
        assertEquals(facebookProvider, providersDefinition.findProvider(facebookProvider.getType()));
    }
    
    public void testTwoProviders() {
        final FacebookProvider facebookProvider = newFacebookProvider();
        final YahooProvider yahooProvider = newYahooProvider();
        final List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(facebookProvider);
        providers.add(yahooProvider);
        final ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setProviderTypeParameter(NEW_PARAMETER_NAME);
        providersDefinition.setProviders(providers);
        providersDefinition.setBaseUrl(URL);
        assertNull(facebookProvider.getCallbackUrl());
        assertNull(yahooProvider.getCallbackUrl());
        providersDefinition.init();
        assertEquals(URL + "?" + NEW_PARAMETER_NAME + "=" + facebookProvider.getType(),
                     facebookProvider.getCallbackUrl());
        assertEquals(URL + "?" + NEW_PARAMETER_NAME + "=" + yahooProvider.getType(), yahooProvider.getCallbackUrl());
        final String[] values = new String[] {
            yahooProvider.getType()
        };
        final Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(NEW_PARAMETER_NAME, values);
        assertEquals(yahooProvider, providersDefinition.findProvider(parameters));
        assertEquals(yahooProvider, providersDefinition.findProvider(yahooProvider.getType()));
    }
    
    public void testDoubleInit() {
        final FacebookProvider facebookProvider = newFacebookProvider();
        facebookProvider.setCallbackUrl(URL);
        final ProvidersDefinition providersDefinition = new ProvidersDefinition(facebookProvider);
        providersDefinition.init();
        final ProvidersDefinition providersDefinition2 = new ProvidersDefinition(facebookProvider);
        providersDefinition2.init();
        assertEquals(URL + "?" + ProvidersDefinition.DEFAULT_PROVIDER_TYPE_PARAMETER + "=" + facebookProvider.getType(),
                     facebookProvider.getCallbackUrl());
    }
    
    public void testAllProviders() {
        final FacebookProvider facebookProvider = newFacebookProvider();
        final YahooProvider yahooProvider = newYahooProvider();
        final List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
        providers.add(facebookProvider);
        providers.add(yahooProvider);
        final ProvidersDefinition providersDefinition = new ProvidersDefinition();
        providersDefinition.setProviders(providers);
        providersDefinition.setBaseUrl(URL);
        final List<OAuthProvider> providers2 = providersDefinition.getAllProviders();
        assertEquals(2, providers2.size());
        assertTrue(providers2.containsAll(providers));
    }
}
