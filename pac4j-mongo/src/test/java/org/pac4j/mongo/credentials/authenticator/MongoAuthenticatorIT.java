package org.pac4j.mongo.credentials.authenticator;

import com.mongodb.MongoClient;
import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.NopPasswordEncoder;
import org.pac4j.core.credentials.password.BasicSaltedSha512PasswordEncoder;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import static org.junit.Assert.*;

/**
 * Tests the {@link MongoAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MongoAuthenticatorIT implements TestsConstants {

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


    @Test(expected = TechnicalException.class)
    public void testNullPasswordEncoder() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME);
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAttribute() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), null, new NopPasswordEncoder());
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullMongoClient() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(null, FIRSTNAME, new NopPasswordEncoder());
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullDatabase() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsersDatabase(null);
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullCollection() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsersCollection(null);
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullUsername() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsernameAttribute(null);
        authenticator.init(null);
        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullPassword() throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setPasswordAttribute(null);
        authenticator.init(null);
        authenticator.validate(null);
    }

    private MongoClient getClient() {
        return new MongoClient("localhost", PORT);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) throws RequiresHttpAction {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), attribute);
        authenticator.setPasswordEncoder(new BasicSaltedSha512PasswordEncoder(SALT));
        authenticator.init(null);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test(expected = MultipleAccountsFoundException.class)
    public void testMultipleUsername() throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials =  login(MULTIPLE_USERNAME, PASSWORD, "");
    }

    @Test(expected = AccountNotFoundException.class)
    public void testBadUsername() throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials =  login(BAD_USERNAME, PASSWORD, "");
    }

    @Test(expected = BadCredentialsException.class)
    public void testBadPassword() throws RequiresHttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD + "bad", "");
    }
}
