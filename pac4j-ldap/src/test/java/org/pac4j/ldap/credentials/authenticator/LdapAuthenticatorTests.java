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
package org.pac4j.ldap.credentials.authenticator;

import org.junit.*;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.ldap.profile.LdapProfile;
import org.pac4j.ldap.test.tools.AuthenticatorGenerator;
import org.pac4j.ldap.test.tools.LdapServer;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests the {@link LdapAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class LdapAuthenticatorTests implements TestsConstants {

    private LdapServer ldapServer;

    private Authenticator authenticator;

    @Before
    public void setUp() {
        ldapServer = new LdapServer();
        ldapServer.start();
        authenticator = AuthenticatorGenerator.create();
    }

    @After
    public void tearDown() {
        ldapServer.stop();
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthenticator() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator();

        ldapAuthenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAttributes() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, null);

        ldapAuthenticator.validate(null);
    }

    @Test(expected = BadCredentialsException.class)
    public void authentFailed() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials);
    }

    @Test
    public void authentSuccessNoAttribute() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials);

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(0, ldapProfile.getAttributes().size());
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, LdapServer.CN + "," + LdapServer.SN);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials);

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(2, ldapProfile.getAttributes().size());
        assertEquals(GOOD_USERNAME, ldapProfile.getAttribute(LdapServer.CN));
        assertEquals(FIRSTNAME_VALUE, ldapProfile.getAttribute(LdapServer.SN));
    }

    @Test
    public void authentSuccessMultiAttribute() {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, LdapServer.CN + "," + LdapServer.SN + "," + LdapServer.ROLE);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME2, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials);

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME2, ldapProfile.getId());
        assertEquals(2, ldapProfile.getAttributes().size());
        assertEquals(GOOD_USERNAME2, ldapProfile.getAttribute(LdapServer.CN));
        assertNull(ldapProfile.getAttribute(LdapServer.SN));
        final Collection<String> attributes = (Collection<String>) ldapProfile.getAttribute(LdapServer.ROLE);
        assertEquals(2, attributes.size());
        assertTrue(attributes.contains(LdapServer.ROLE1));
        assertTrue(attributes.contains(LdapServer.ROLE2));
    }
}
