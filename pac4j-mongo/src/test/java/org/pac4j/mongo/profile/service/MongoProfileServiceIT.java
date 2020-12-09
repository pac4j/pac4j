package org.pac4j.mongo.profile.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.types.ObjectId;
import org.junit.*;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link MongoProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class MongoProfileServiceIT implements TestsConstants {

    private static final int PORT = 37017;
    private static final String MONGO_ID = "mongoId";
    private static final String MONGO_LINKEDID = "mongoLinkedId";
    private static final String MONGO_LINKEDID2 = "mongoLinkedId2";
    private static final String MONGO_USER = "mongoUser";
    private static final String MONGO_PASS = "mongoPass";
    private static final String MONGO_PASS2 = "mongoPass2";


    private final MongoServer mongoServer = new MongoServer();

    @Before
    public void setUp() {
        mongoServer.start(PORT);
    }

    @After
    public void tearDown() {
        mongoServer.stop();
    }

    @Test
    public void testNullPasswordEncoder() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME);
        authenticator.setPasswordEncoder(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullMongoClient() {
        final MongoProfileService authenticator = new MongoProfileService(null, FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "mongoClient cannot be null");
    }

    @Test
    public void testNullDatabase() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersDatabase(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usersDatabase cannot be blank");
    }

    @Test
    public void testNullCollection() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersCollection(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usersCollection cannot be blank");
    }

    @Test
    public void testNullUsername() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsernameAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(), TechnicalException.class, "usernameAttribute cannot be blank");
    }

    @Test
    public void testNullPassword() {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setPasswordAttribute(null);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        TestsHelper.expectException(() -> authenticator.validate(credentials, null), TechnicalException.class,
            "passwordAttribute cannot be blank");
    }

    private MongoClient getClient() {
        return MongoClients.create(String.format("mongodb://localhost:%d", PORT));
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) {
        final MongoProfileService authenticator = new MongoProfileService(getClient(), attribute);
        authenticator.setPasswordEncoder(MongoServer.PASSWORD_ENCODER);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        authenticator.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, "");

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testMultipleUsername() {
        TestsHelper.expectException(() -> login(MULTIPLE_USERNAME, PASSWORD, ""), MultipleAccountsFoundException.class,
            "Too many accounts found for: misagh");
    }

    @Test
    public void testBadUsername() {
        TestsHelper.expectException(() -> login(BAD_USERNAME, PASSWORD, ""), AccountNotFoundException.class,
            "No account found for: michael");
    }

    @Test
    public void testBadPassword() {
        TestsHelper.expectException(() ->login(GOOD_USERNAME, PASSWORD + "bad", ""), BadCredentialsException.class,
            "Bad credentials for: jle");
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final ObjectId objectId = new ObjectId();
        final MongoProfile profile = new MongoProfile();
        profile.setId(MONGO_ID);
        profile.setLinkedId(MONGO_LINKEDID);
        profile.addAttribute(USERNAME, MONGO_USER);
        profile.addAttribute(FIRSTNAME, objectId);
        final MongoProfileService mongoProfileService = new MongoProfileService(getClient());
        mongoProfileService.setPasswordEncoder(MongoServer.PASSWORD_ENCODER);
        // create
        mongoProfileService.create(profile, MONGO_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(MONGO_USER, MONGO_PASS);
        mongoProfileService.validate(credentials, null);
        final UserProfile profile1 = credentials.getUserProfile();
        assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(mongoProfileService, MONGO_ID);
        assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        assertEquals(6, result.size());
        assertEquals(MONGO_ID, result.get(ID));
        assertEquals(MONGO_LINKEDID, result.get(AbstractProfileService.LINKEDID));
        assertNotNull(result.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertTrue(MongoServer.PASSWORD_ENCODER.matches(MONGO_PASS, (String) result.get(PASSWORD)));
        assertEquals(MONGO_USER, result.get(USERNAME));
        // findById
        final MongoProfile profile2 = mongoProfileService.findByLinkedId(MONGO_LINKEDID);
        assertEquals(MONGO_ID, profile2.getId());
        assertEquals(MONGO_LINKEDID, profile2.getLinkedId());
        assertEquals(MONGO_USER, profile2.getUsername());
        assertEquals(objectId, profile2.getAttribute(FIRSTNAME));
        assertEquals(2, profile2.getAttributes().size());
        // update
        profile.setLinkedId(MONGO_LINKEDID2);
        mongoProfileService.update(profile, MONGO_PASS2);
        final List<Map<String, Object>> results2 = getData(mongoProfileService, MONGO_ID);
        assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        assertEquals(6, result2.size());
        assertEquals(MONGO_ID, result2.get(ID));
        assertEquals(MONGO_LINKEDID2, result2.get(AbstractProfileService.LINKEDID));
        assertNotNull(result2.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertTrue(MongoServer.PASSWORD_ENCODER.matches(MONGO_PASS2, (String) result2.get(PASSWORD)));
        assertEquals(MONGO_USER, result2.get(USERNAME));
        // remove
        mongoProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(mongoProfileService, MONGO_ID);
        assertEquals(0, results3.size());
    }

    @Test
    public void testChangeUserAndPasswordAttributes() {
        final MongoProfileService mongoProfileService = new MongoProfileService(getClient(), MongoServer.PASSWORD_ENCODER);
        mongoProfileService.setUsernameAttribute(ALT_USER_ATT);
        mongoProfileService.setPasswordAttribute(ALT_PASS_ATT);
        final ObjectId objectId = new ObjectId();
        final MongoProfile profile = new MongoProfile();
        profile.setId(MONGO_ID);
        profile.setLinkedId(MONGO_LINKEDID);
        profile.addAttribute(USERNAME, MONGO_USER);
        profile.addAttribute(FIRSTNAME, objectId);
        // create
        mongoProfileService.create(profile, MONGO_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(MONGO_USER, MONGO_PASS);
        mongoProfileService.validate(credentials, null);
        assertNotNull(credentials.getUserProfile());
    }

    private List<Map<String, Object>> getData(final MongoProfileService service, final String id) {
        return service.read(null, ID, id);
    }
}
