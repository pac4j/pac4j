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

import junit.framework.TestCase;

import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class tests the type returned by the BaseOAuthProvider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestBaseOAuthProvider extends TestCase {
    
    public void testType() {
        BaseOAuth10Provider provider = new YahooProvider();
        assertEquals("YahooProvider", provider.getType());
    }
}
