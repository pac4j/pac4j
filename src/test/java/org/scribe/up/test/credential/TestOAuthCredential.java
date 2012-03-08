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
package org.scribe.up.test.credential;

import junit.framework.TestCase;

import org.scribe.up.credential.OAuthCredential;

/**
 * This class tests the {@link org.scribe.up.credential.OAuthCredential} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestOAuthCredential extends TestCase {
    
    private final static String TOKEN = "token";
    
    private final static String VERIFIER = "verifier";
    
    private final static String TYPE = "type";
    
    public void testOAuthCredential() {
        OAuthCredential credential = new OAuthCredential(TOKEN, VERIFIER, TYPE);
        assertEquals(TOKEN, credential.getToken());
        assertEquals(VERIFIER, credential.getVerifier());
        assertEquals(TYPE, credential.getProviderType());
    }
}
