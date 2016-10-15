package org.pac4j.ldap.credentials.authenticator;

import org.junit.*;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsHelper;
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
public final class LdapAuthenticatorTests implements TestsConstants {

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

    @Test
    public void testNullAuthenticator() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator();
        TestsHelper.expectException(() -> ldapAuthenticator.init(null), TechnicalException.class, "ldapAuthenticator cannot be null");
    }

    @Test
    public void testNullAttributes() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, null);
        TestsHelper.expectException(() -> ldapAuthenticator.init(null), TechnicalException.class, "attributes cannot be null");
    }

    @Test(expected = BadCredentialsException.class)
    public void authentFailed() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials, null);
    }

    @Test
    public void authentSuccessNoAttribute() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(0, ldapProfile.getAttributes().size());
    }

    @Test
    public void authentSuccessSingleAttribute() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, LdapServer.CN + "," + LdapServer.SN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(2, ldapProfile.getAttributes().size());
        assertEquals(GOOD_USERNAME, ldapProfile.getAttribute(LdapServer.CN));
        assertEquals(FIRSTNAME_VALUE, ldapProfile.getAttribute(LdapServer.SN));
    }

    @Test
    public void authentSuccessMultiAttribute() throws HttpAction {
        final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(authenticator, LdapServer.CN + "," + LdapServer.SN + "," + LdapServer.ROLE);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME2, PASSWORD, CLIENT_NAME);
        ldapAuthenticator.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
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
