package org.pac4j.core.profile.service;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link InMemoryProfileService}.
 *
 * @author Elie Roux
 * @since 2.1.0
 */
public final class InMemoryProfileServiceTests implements TestsConstants {

    private static final String TEST_ID = "testId";
    private static final String TEST_LINKED_ID = "testLinkedId";
    private static final String TEST_USER = "testUser";
    private static final String TEST_USER2 = "testUser2";
    private static final String TEST_PASS = "testPass";
    private static final String TEST_PASS2 = "testPass2";
    private static final String IDPERSON1 = "idperson1";
    private static final String IDPERSON2 = "idperson2";
    private static final String IDPERSON3 = "idperson3";

    public final static PasswordEncoder PASSWORD_ENCODER = new ShiroPasswordEncoder(new DefaultPasswordService());
    public InMemoryProfileService<CommonProfile> inMemoryProfileService;


    @Before
    public void setUp() {
        inMemoryProfileService = new InMemoryProfileService<>(x -> new CommonProfile());
        inMemoryProfileService.setPasswordEncoder(PASSWORD_ENCODER);
        final var password = PASSWORD_ENCODER.encode(PASSWORD);
        // insert sample data
        final Map<String, Object> properties1 = new HashMap<>();
        properties1.put(USERNAME, GOOD_USERNAME);
        properties1.put(FIRSTNAME, FIRSTNAME_VALUE);
        var profile = new CommonProfile();
        profile.build(IDPERSON1, properties1);
        inMemoryProfileService.create(profile, PASSWORD);
        // second person,
        final Map<String, Object> properties2 = new HashMap<>();
        properties2.put(USERNAME, MULTIPLE_USERNAME);
        profile = new CommonProfile();
        profile.build(IDPERSON2, properties2);
        inMemoryProfileService.create(profile, PASSWORD);
        final Map<String, Object> properties3 = new HashMap<>();
        properties3.put(USERNAME, MULTIPLE_USERNAME);
        properties3.put(PASSWORD, password);
        profile = new CommonProfile();
        profile.build(IDPERSON3, properties3);
        inMemoryProfileService.create(profile, PASSWORD);
    }

    @Test(expected = AccountNotFoundException.class)
    public void authentFailed() {
        final var credentials = new UsernamePasswordCredentials(BAD_USERNAME, PASSWORD);
        inMemoryProfileService.validate(credentials, null, null);
    }

    @Test
    public void authentSuccessSingleAttribute() {
        final var credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        inMemoryProfileService.validate(credentials, null, null);
        final var profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertEquals(GOOD_USERNAME, profile.getUsername());
        assertEquals(2, profile.getAttributes().size());
        assertEquals(FIRSTNAME_VALUE, profile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final var profile = new CommonProfile();
        profile.setId(TEST_ID);
        profile.setLinkedId(TEST_LINKED_ID);
        profile.addAttribute(USERNAME, TEST_USER);
        // create
        inMemoryProfileService.create(profile, TEST_PASS);
        // check credentials
        final var credentials = new UsernamePasswordCredentials(TEST_USER, TEST_PASS);
        inMemoryProfileService.validate(credentials, null, null);
        final var profile1 = credentials.getUserProfile();
        assertNotNull(profile1);
        // check data
        final var results = getData(TEST_ID);
        assertEquals(1, results.size());
        final var result = results.get(0);
        assertEquals(5, result.size());
        assertEquals(TEST_ID, result.get("id"));
        assertEquals(TEST_LINKED_ID, result.get(AbstractProfileService.LINKEDID));
        assertNotNull(result.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(TEST_USER, result.get(USERNAME));
        // findById
        final var profile2 = inMemoryProfileService.findById(TEST_ID);
        assertEquals(TEST_ID, profile2.getId());
        assertEquals(TEST_LINKED_ID, profile2.getLinkedId());
        assertEquals(TEST_USER, profile2.getUsername());
        assertEquals(1, profile2.getAttributes().size());
        // update with password
        profile.addAttribute(USERNAME, TEST_USER2);
        inMemoryProfileService.update(profile, TEST_PASS2);
        var results2 = getData(TEST_ID);
        assertEquals(1, results2.size());
        var result2 = results2.get(0);
        assertEquals(5, result2.size());
        assertEquals(TEST_ID, result2.get("id"));
        assertEquals(TEST_LINKED_ID, result2.get(AbstractProfileService.LINKEDID));
        assertNotNull(result2.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertEquals(TEST_USER2, result2.get(USERNAME));
        // check credentials
        final var credentials2 = new UsernamePasswordCredentials(TEST_USER2, TEST_PASS2);
        inMemoryProfileService.validate(credentials2, null, null);
        final var profile3 = credentials.getUserProfile();
        assertNotNull(profile3);
        // update with no password update
        inMemoryProfileService.update(profile, null);
        results2 = getData(TEST_ID);
        assertEquals(1, results2.size());
        result2 = results2.get(0);
        assertEquals(5, result2.size());
        assertEquals(TEST_USER2, result2.get(USERNAME));
        // check credentials
        inMemoryProfileService.validate(credentials2, null, null);
        final var profile4 = credentials.getUserProfile();
        assertNotNull(profile4);
        // remove
        inMemoryProfileService.remove(profile);
        final var results3 = getData(TEST_ID);
        assertEquals(0, results3.size());
    }

    private List<Map<String, Object>> getData(final String id) {
        return inMemoryProfileService.read(Arrays.asList("id", "username", "linkedid", "password", "serializedprofile"), "id", id);
    }
}
