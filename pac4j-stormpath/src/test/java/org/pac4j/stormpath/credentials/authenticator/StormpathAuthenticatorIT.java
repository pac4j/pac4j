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
package org.pac4j.stormpath.credentials.authenticator;

import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.GroupMembershipList;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.stormpath.profile.StormpathProfile;

import static org.junit.Assert.*;

/**
 * Tests the {@link StormpathAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class StormpathAuthenticatorIT implements TestsConstants {

    @Test
    public void testInitKo() {
        final StormpathAuthenticator authenticator = new StormpathAuthenticator(VALUE, VALUE, VALUE);
        TestsHelper.initShouldFail(authenticator, "An exception is caught trying to access Stormpath cloud. Please verify that your provided Stormpath <accessId>, <secretKey>, and <applicationId> are correct. Original Stormpath error: HTTP 401, Stormpath 401 (http://www.stormpath.com/docs/quickstart/connect): Authentication with a valid API Key is required.");
    }

    @Test
    public void testFullAuthentication() {
        // luminous-smoke1
        final StormpathAuthenticator authenticator = new StormpathAuthenticator("77NW47MHGJV5DA8R5UA5YORE0",
                "nPCDRYPPxhBNpq1HT9Gr85hB7fCACQXSHx0aCuG6D/Q", "2MahZGmC0Rcl7gYkVIea94");
        authenticator.init(null);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("leleuj", "Pac4jtest", CLIENT_NAME);
        authenticator.validate(credentials);
        final StormpathProfile profile = (StormpathProfile) credentials.getUserProfile();
        assertNotNull(profile);

        assertEquals("leleuj", profile.getId());
        assertEquals("LELEU", profile.getFamilyName());
        assertEquals("Jerome", profile.getFirstName());
        assertEquals("Jr", profile.getMiddleName());
        final GroupList groups = profile.getGroups();
        assertEquals("https://api.stormpath.com/v1/accounts/33t9mYagXIJyddNMZV5489/groups", groups.getHref());
        final GroupMembershipList groupMemberships = profile.getGroupMemberships();
        assertEquals("https://api.stormpath.com/v1/accounts/33t9mYagXIJyddNMZV5489/groupMemberships", groupMemberships.getHref());
        assertEquals("test@test.com", profile.getEmail());
    }
}
