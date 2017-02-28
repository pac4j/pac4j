package org.pac4j.ldap.profile.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.ldap.profile.LdapProfile;
import org.pac4j.ldap.test.tools.AuthenticatorGenerator;
import org.pac4j.ldap.test.tools.LdapServer;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests the {@link LdapProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class LdapProfileServiceTests implements TestsConstants {

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
    public void testNullAuthenticator() {
        final LdapProfileService ldapProfileService = new LdapProfileService();
        TestsHelper.expectException(() -> ldapProfileService.init(null), TechnicalException.class, "ldapAuthenticator cannot be null");
    }

    @Test(expected = BadCredentialsException.class)
    public void authentFailed() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(authenticator);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessNoAttribute() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(authenticator, "");
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapProfileService.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(0, ldapProfile.getAttributes().size());
    }

    @Test
    public void authentSuccessSingleAttribute() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(authenticator, LdapServer.SN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapProfileService.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(1, ldapProfile.getAttributes().size());
        assertEquals(FIRSTNAME_VALUE, ldapProfile.getAttribute(LdapServer.SN));
    }

    @Test
    public void authentSuccessMultiAttribute() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(authenticator, LdapServer.SN + "," + LdapServer.ROLE);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME2, PASSWORD, CLIENT_NAME);
        ldapProfileService.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME2, ldapProfile.getId());
        assertEquals(1, ldapProfile.getAttributes().size());
        assertNull(ldapProfile.getAttribute(LdapServer.SN));
        final Collection<String> attributes = (Collection<String>) ldapProfile.getAttribute(LdapServer.ROLE);
        assertEquals(2, attributes.size());
        assertTrue(attributes.contains(LdapServer.ROLE1));
        assertTrue(attributes.contains(LdapServer.ROLE2));
    }
}
