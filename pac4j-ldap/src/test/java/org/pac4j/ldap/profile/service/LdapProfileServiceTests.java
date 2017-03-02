package org.pac4j.ldap.profile.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ldaptive.ConnectionFactory;
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
import org.pac4j.ldap.test.tools.LdapClient;
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

    private static final String USERS_DN = ",ou=people,dc=ldaptive,dc=org";

    private LdapServer ldapServer;

    private Authenticator authenticator;

    private ConnectionFactory connectionFactory;

    @Before
    public void setUp() {
        ldapServer = new LdapServer();
        ldapServer.start();
        final LdapClient client = new LdapClient();
        authenticator = client.getAuthenticator();
        connectionFactory = client.getConnectionFactory();
    }

    @After
    public void tearDown() {
        ldapServer.stop();
    }

    @Test
    public void testNullAuthenticator() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, null, USERS_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(null), TechnicalException.class, "ldapAuthenticator cannot be null");
    }

    @Test
    public void testNullConnectionFactory() {
        final LdapProfileService ldapProfileService = new LdapProfileService(null, authenticator, USERS_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(null), TechnicalException.class, "connectionFactory cannot be null");
    }

    @Test
    public void testBlankUsersDn() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, "");
        TestsHelper.expectException(() -> ldapProfileService.init(null), TechnicalException.class, "usersDn cannot be blank");
    }


    @Test(expected = BadCredentialsException.class)
    public void authentFailed() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, USERS_DN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD, CLIENT_NAME);
        ldapProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessNoAttribute() throws HttpAction, CredentialsException {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, "", USERS_DN);
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
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.SN, USERS_DN);
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
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.SN + "," + LdapServer.ROLE, USERS_DN);
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
