package org.pac4j.mongo.profile.service;

import com.mongodb.MongoClient;
import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.mongo.credentials.authenticator.MongoAuthenticator;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link MongoAuthenticator}.
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
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME);
        authenticator.setPasswordEncoder(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullMongoClient() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(null, FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "mongoClient cannot be null");
    }

    @Test
    public void testNullDatabase() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersDatabase(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersDatabase cannot be blank");
    }

    @Test
    public void testNullCollection() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersCollection(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersCollection cannot be blank");
    }

    @Test
    public void testNullUsername() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsernameAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usernameAttribute cannot be blank");
    }

    @Test
    public void testNullPassword() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setPasswordAttribute(null);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        TestsHelper.expectException(() -> authenticator.validate(credentials, null), TechnicalException.class, "passwordAttribute cannot be blank");
    }

    private MongoClient getClient() {
        return new MongoClient("localhost", PORT);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) throws HttpAction, CredentialsException{
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), attribute);
        authenticator.setPasswordEncoder(MongoServer.PASSWORD_ENCODER);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() throws HttpAction, CredentialsException {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() throws HttpAction, CredentialsException {
        final UsernamePasswordCredentials credentials = login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testMultipleUsername() throws HttpAction, CredentialsException {
        TestsHelper.expectException(() -> login(MULTIPLE_USERNAME, PASSWORD, ""), MultipleAccountsFoundException.class, "Too many accounts found for: misagh");
    }

    @Test
    public void testBadUsername() throws HttpAction, CredentialsException {
        TestsHelper.expectException(() -> login(BAD_USERNAME, PASSWORD, ""), AccountNotFoundException.class, "No account found for: michael");
    }

    @Test
    public void testBadPassword() throws HttpAction, CredentialsException {
        TestsHelper.expectException(() ->login(GOOD_USERNAME, PASSWORD + "bad", ""), BadCredentialsException.class, "Bad credentials for: jle");
    }

    @Test
    public void testCreateUpdateFindDelete() throws HttpAction, CredentialsException {
        final MongoProfile profile = new MongoProfile();
        profile.setId(MONGO_ID);
        profile.setLinkedId(MONGO_LINKEDID);
        profile.addAttribute(USERNAME, MONGO_USER);
        final MongoProfileService mongoProfileService = new MongoProfileService(getClient());
        mongoProfileService.setPasswordEncoder(MongoServer.PASSWORD_ENCODER);
        // create
        mongoProfileService.create(profile, MONGO_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(MONGO_USER, MONGO_PASS, CLIENT_NAME);
        mongoProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
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
        assertEquals(1, profile2.getAttributes().size());
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

    private List<Map<String, Object>> getData(final MongoProfileService service, final String id) {
        return service.read(null, ID, id);
    }
}
