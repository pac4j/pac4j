package org.pac4j.mongo.credentials.authenticator;

import com.mongodb.MongoClient;
import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import static org.junit.Assert.*;

/**
 * Tests the {@link MongoAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class MongoAuthenticatorIT implements TestsConstants {

    private final static int PORT = 37017;

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
    public void testNullAttribute() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), null, MongoServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "attributes cannot be null");
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
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersDatabase cannot be null");
    }

    @Test
    public void testNullCollection() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsersCollection(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersCollection cannot be null");
    }

    @Test
    public void testNullUsername() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setUsernameAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usernameAttribute cannot be null");
    }

    @Test
    public void testNullPassword() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, MongoServer.PASSWORD_ENCODER);
        authenticator.setPasswordAttribute(null);
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD, CLIENT_NAME);
        TestsHelper.expectException(() -> authenticator.validate(credentials, null), TechnicalException.class, "passwordAttribute cannot be null");
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
}
