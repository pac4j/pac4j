/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.core.client;

import junit.framework.TestCase;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link BaseClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestBaseClient extends TestCase implements TestsConstants {
    
    public void testClone() {
        BaseClient<Credentials, CommonProfile> oldClient = new MockBaseClient<Credentials, CommonProfile>(TYPE);
        oldClient.setCallbackUrl(CALLBACK_URL);
        oldClient.setFailureUrl(FAILURE_URL);
        BaseClient<Credentials, CommonProfile> newClient = oldClient.clone();
        assertEquals(oldClient.getType(), newClient.getType());
        assertEquals(oldClient.getCallbackUrl(), newClient.getCallbackUrl());
        assertEquals(oldClient.getFailureUrl(), newClient.getFailureUrl());
    }
}
