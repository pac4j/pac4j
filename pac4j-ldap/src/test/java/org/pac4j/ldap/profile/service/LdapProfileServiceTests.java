package org.pac4j.ldap.profile.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.auth.Authenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.ldap.profile.LdapProfile;
import org.pac4j.ldap.test.tools.LdapClient;
import org.pac4j.ldap.test.tools.LdapServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link LdapProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class LdapProfileServiceTests implements TestsConstants {

    private static final String LDAP_ID = "ldapid";
    private static final String LDAP_LINKED_ID = "ldapLinkedId";
    private static final String LDAP_PASS = "ldapPass";
    private static final String LDAP_PASS2 = "ldapPass2";
    private static final String LDAP_USER = "ldapUser";
    private static final String LDAP_USER2 = "ldapUser2";

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
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, null, LdapServer.BASE_PEOPLE_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "ldapAuthenticator cannot be null");
    }

    @Test
    public void testNullConnectionFactory() {
        final LdapProfileService ldapProfileService = new LdapProfileService(null, authenticator, LdapServer.BASE_PEOPLE_DN);
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "connectionFactory cannot be null");
    }

    @Test
    public void testBlankUsersDn() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, "");
        TestsHelper.expectException(() -> ldapProfileService.init(), TechnicalException.class, "usersDn cannot be blank");
    }


    @Test(expected = BadCredentialsException.class)
    public void authentFailed() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.BASE_PEOPLE_DN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD);
        ldapProfileService.validate(credentials, null);
    }

    @Test
    public void authentSuccessNoAttribute() {
        final LdapProfileService ldapProfileService =
            new LdapProfileService(connectionFactory, authenticator, "", LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        ldapProfileService.validate(credentials, null);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof LdapProfile);
        final LdapProfile ldapProfile = (LdapProfile) profile;
        assertEquals(GOOD_USERNAME, ldapProfile.getId());
        assertEquals(0, ldapProfile.getAttributes().size());
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final LdapProfileService ldapProfileService =
            new LdapProfileService(connectionFactory, authenticator, LdapServer.SN, LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
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
    public void authentSuccessMultiAttribute() {
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.SN + ","
            + LdapServer.ROLE, LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setUsernameAttribute(LdapServer.CN);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME2, PASSWORD);
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

    @Test
    public void testCreateUpdateFindDelete() {
        final LdapProfile profile = new LdapProfile();
        profile.setId(LDAP_ID);
        profile.setLinkedId(LDAP_LINKED_ID);
        profile.addAttribute(USERNAME, LDAP_USER);
        final LdapProfileService ldapProfileService = new LdapProfileService(connectionFactory, authenticator, LdapServer.BASE_PEOPLE_DN);
        ldapProfileService.setIdAttribute(LdapServer.CN);
        ldapProfileService.setUsernameAttribute(LdapServer.SN);
        ldapProfileService.setPasswordAttribute("userPassword");
        // create
        ldapProfileService.create(profile, LDAP_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(LDAP_ID, LDAP_PASS);
        ldapProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(ldapProfileService, LDAP_ID);
        assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        assertEquals(4, result.size());
        assertEquals(LDAP_ID, result.get(LdapServer.CN));
        assertEquals(LDAP_LINKED_ID, result.get(AbstractProfileService.LINKEDID));
        assertNotNull(result.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(LDAP_USER, result.get(LdapServer.SN));
        // findById
        final LdapProfile profile2 = ldapProfileService.findById(LDAP_ID);
        assertEquals(LDAP_ID, profile2.getId());
        assertEquals(LDAP_LINKED_ID, profile2.getLinkedId());
        assertEquals(LDAP_USER, profile2.getUsername());
        assertEquals(1, profile2.getAttributes().size());
        // update
        profile.addAttribute(USERNAME, LDAP_USER2);
        ldapProfileService.update(profile, LDAP_PASS2);
        final List<Map<String, Object>> results2 = getData(ldapProfileService, LDAP_ID);
        assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        assertEquals(4, result2.size());
        assertEquals(LDAP_ID, result2.get(LdapServer.CN));
        assertEquals(LDAP_LINKED_ID, result2.get(AbstractProfileService.LINKEDID));
        assertNotNull(result2.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(LDAP_USER2, result2.get(LdapServer.SN));
        // check credentials
        final UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials(LDAP_ID, LDAP_PASS2);
        ldapProfileService.validate(credentials2, null);
        final CommonProfile profile3 = credentials.getUserProfile();
        assertNotNull(profile3);
        // remove
        ldapProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(ldapProfileService, LDAP_ID);
        assertEquals(0, results3.size());
    }

    private List<Map<String, Object>> getData(final LdapProfileService ldapProfileService, final String id) {
        return ldapProfileService.read(Arrays.asList(LdapServer.CN, LdapServer.SN, "id", "username", "linkedid", "password",
            "serializedprofile"), LdapServer.CN, id);
    }
}
