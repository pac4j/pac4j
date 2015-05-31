/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.http.profile.creator;

import org.junit.Test;
import org.pac4j.http.credentials.HttpCredentials;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.profile.HttpProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link AuthenticatorProfileCreator}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorProfileCreatorTests {

    private final static String TOKEN = "token";
    private final static String CLIENT_NAME = "clientName";

    private final static AuthenticatorProfileCreator creator = new AuthenticatorProfileCreator();

    @Test
    public void testReturnNoProfile() {
        assertNull(creator.create(new TokenCredentials(TOKEN, CLIENT_NAME)));
    }

    @Test
    public void testReturnProfile() {
        final HttpProfile profile = new HttpProfile();
        final HttpCredentials credentials = new TokenCredentials(TOKEN, CLIENT_NAME);
        credentials.setUserProfile(profile);
        final HttpProfile profile2 = creator.create(credentials);
        assertEquals(profile, profile2);
    }
}
