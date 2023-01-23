package org.pac4j.couch.profile.service;

import lombok.val;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.ektorp.CouchDbConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.couch.profile.CouchProfile;
import org.pac4j.couch.test.tools.CouchServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link CouchProfileService}.
 *
 * @author Elie Roux
 * @since 2.0.0
 */
public final class CouchProfileServiceTests implements TestsConstants {

    private static final String COUCH_ID_FIELD = CouchProfileService.COUCH_ID;
    private static final String COUCH_ID = "couchId";
    private static final String COUCH_LINKED_ID = "couchLinkedId";
    private static final String COUCH_USER = "couchUser";
    private static final String COUCH_USER2 = "couchUser2";
    private static final String COUCH_PASS = "couchPass";
    private static final String COUCH_PASS2 = "couchPass2";
    private static final String IDPERSON1 = "idperson1";
    private static final String IDPERSON2 = "idperson2";
    private static final String IDPERSON3 = "idperson3";

    public final static PasswordEncoder PASSWORD_ENCODER = new ShiroPasswordEncoder(new DefaultPasswordService());
    private static final CouchServer couchServer = new CouchServer();
    private static final CouchDbConnector couchDbConnector = couchServer.start();


    @BeforeClass
    public static void setUp() {
        val password = PASSWORD_ENCODER.encode(PASSWORD);
        val couchProfileService = new CouchProfileService(couchDbConnector);
        couchProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        // insert sample data
        final Map<String, Object> properties1 = new HashMap<>();
        properties1.put(USERNAME, GOOD_USERNAME);
        properties1.put(FIRSTNAME, FIRSTNAME_VALUE);
        var couchProfile = new CouchProfile();
        couchProfile.build(IDPERSON1, properties1);
        couchProfileService.create(couchProfile, PASSWORD);
        // second person,
        final Map<String, Object> properties2 = new HashMap<>();
        properties2.put(USERNAME, MULTIPLE_USERNAME);
        couchProfile = new CouchProfile();
        couchProfile.build(IDPERSON2, properties2);
        couchProfileService.create(couchProfile, PASSWORD);
        final Map<String, Object> properties3 = new HashMap<>();
        properties3.put(USERNAME, MULTIPLE_USERNAME);
        properties3.put(PASSWORD, password);
        couchProfile = new CouchProfile();
        couchProfile.build(IDPERSON3, properties3);
        couchProfileService.create(couchProfile, PASSWORD);
    }

    @AfterClass
    public static void tearDown() {
        //couchServer.stop();
    }

    @Test
    public void testNullConnector() {
        val couchProfileService = new CouchProfileService(null);
        couchProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        TestsHelper.expectException(couchProfileService::init, TechnicalException.class, "couchDbConnector cannot be null");
    }

    @Test(expected = AccountNotFoundException.class)
    public void authentFailed() {
        val couchProfileService = new CouchProfileService(couchDbConnector);
        couchProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        val credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD);
        couchProfileService.validate(null, credentials);
    }

    @Test
    public void authentSuccessSingleAttribute() {
        val couchProfileService = new CouchProfileService(couchDbConnector);
        couchProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        val credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        couchProfileService.validate(null, credentials);

        val profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof CouchProfile);
        val couchProfile = (CouchProfile) profile;
        assertEquals(GOOD_USERNAME, couchProfile.getUsername());
        assertEquals(2, couchProfile.getAttributes().size());
        assertEquals(FIRSTNAME_VALUE, couchProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testCreateUpdateFindDelete() {
        val profile = new CouchProfile();
        profile.setId(COUCH_ID);
        profile.setLinkedId(COUCH_LINKED_ID);
        profile.addAttribute(USERNAME, COUCH_USER);
        val couchProfileService = new CouchProfileService(couchDbConnector);
        couchProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        // create
        couchProfileService.create(profile, COUCH_PASS);
        // check credentials
        val credentials = new UsernamePasswordCredentials(COUCH_USER, COUCH_PASS);
        couchProfileService.validate(null, credentials);
        val profile1 = credentials.getUserProfile();
        assertNotNull(profile1);
        // check data
        val results = getData(couchProfileService, COUCH_ID);
        assertEquals(1, results.size());
        val result = results.get(0);
        assertEquals(5, result.size());
        assertEquals(COUCH_ID, result.get(COUCH_ID_FIELD));
        assertEquals(COUCH_LINKED_ID, result.get(AbstractProfileService.LINKEDID));
        assertNotNull(result.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(COUCH_USER, result.get(USERNAME));
        // findById
        val profile2 = couchProfileService.findById(COUCH_ID);
        assertEquals(COUCH_ID, profile2.getId());
        assertEquals(COUCH_LINKED_ID, profile2.getLinkedId());
        assertEquals(COUCH_USER, profile2.getUsername());
        assertEquals(1, profile2.getAttributes().size());
        // update with password
        profile.addAttribute(USERNAME, COUCH_USER2);
        couchProfileService.update(profile, COUCH_PASS2);
        var results2 = getData(couchProfileService, COUCH_ID);
        assertEquals(1, results2.size());
        var result2 = results2.get(0);
        assertEquals(5, result2.size());
        assertEquals(COUCH_ID, result2.get(COUCH_ID_FIELD));
        assertEquals(COUCH_LINKED_ID, result2.get(AbstractProfileService.LINKEDID));
        assertNotNull(result2.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(COUCH_USER2, result2.get(USERNAME));
        // check credentials
        val credentials2 = new UsernamePasswordCredentials(COUCH_USER2, COUCH_PASS2);
        couchProfileService.validate(null, credentials2);
        var profile3 = credentials.getUserProfile();
        assertNotNull(profile3);
        // update with no password update
        couchProfileService.update(profile, null);
        results2 = getData(couchProfileService, COUCH_ID);
        assertEquals(1, results2.size());
        result2 = results2.get(0);
        assertEquals(5, result2.size());
        assertEquals(COUCH_USER2, result2.get(USERNAME));
        // check credentials
        couchProfileService.validate(null, credentials2);
        profile3 = credentials.getUserProfile();
        assertNotNull(profile3);
        // remove
        couchProfileService.remove(profile);
        val results3 = getData(couchProfileService, COUCH_ID);
        assertEquals(0, results3.size());
    }

    private List<Map<String, Object>> getData(final CouchProfileService couchProfileService, final String id) {
        return couchProfileService.read(Arrays.asList(COUCH_ID_FIELD, "username", "linkedid", "password",
            "serializedprofile"), COUCH_ID_FIELD, id);
    }
}
