/*
  Copyright 2012 Jérôme Leleu

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

import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the credential extraction of the BaseOAuth10Provider.
 * 
 * @author Jérôme Leleu
 */
public class TestBaseOAuth10Provider extends TestCase {
    
    private BaseOAuth10Provider provider = new YahooProvider();
    
    public void testNoTokenNoVerifier() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        assertEquals(null, provider.extractCredentialFromParameters(parameters));
    }
}
