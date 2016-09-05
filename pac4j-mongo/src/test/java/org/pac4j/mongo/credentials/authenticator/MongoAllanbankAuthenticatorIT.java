package org.pac4j.mongo.credentials.authenticator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.BasicSaltedSha512PasswordEncoder;
import org.pac4j.core.credentials.password.NopPasswordEncoder;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoFactory;

/**
 * Tests the {@link MongoAllanbankAuthenticator}.
 *
 * @author Victor Noël
 * @since 1.9.2
 */
public class MongoAllanbankAuthenticatorIT implements TestsConstants {

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
    public void testNullPasswordEncoder() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), FIRSTNAME);
        authenticator.setPasswordEncoder(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullAttribute() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), null,
                new NopPasswordEncoder());
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "attributes cannot be null");
    }

    @Test
    public void testNullMongoClient() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(null, FIRSTNAME,
                new NopPasswordEncoder());
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "mongoClient cannot be null");
    }

    @Test
    public void testNullDatabase() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), FIRSTNAME,
                new NopPasswordEncoder());
        authenticator.setUsersDatabase(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersDatabase cannot be null");
    }

    @Test
    public void testNullCollection() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), FIRSTNAME,
                new NopPasswordEncoder());
        authenticator.setUsersCollection(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usersCollection cannot be null");
    }

    @Test
    public void testNullUsername() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), FIRSTNAME,
                new NopPasswordEncoder());
        authenticator.setUsernameAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "usernameAttribute cannot be null");
    }

    @Test
    public void testNullPassword() throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), FIRSTNAME,
                new NopPasswordEncoder());
        authenticator.setPasswordAttribute(null);
        TestsHelper.expectException(() -> authenticator.init(null), TechnicalException.class, "passwordAttribute cannot be null");
    }

    private MongoClient getClient() {
        return MongoFactory.createClient("mongodb://localhost:" + PORT);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) throws HttpAction {
        final MongoAllanbankAuthenticator authenticator = new MongoAllanbankAuthenticator(getClient(), attribute);
        authenticator.setPasswordEncoder(new BasicSaltedSha512PasswordEncoder(SALT));
        authenticator.init(null);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() throws HttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() throws HttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test(expected = MultipleAccountsFoundException.class)
    public void testMultipleUsername() throws HttpAction {
        login(MULTIPLE_USERNAME, PASSWORD, "");
    }

    @Test(expected = AccountNotFoundException.class)
    public void testBadUsername() throws HttpAction {
        login(BAD_USERNAME, PASSWORD, "");
    }

    @Test(expected = BadCredentialsException.class)
    public void testBadPassword() throws HttpAction {
        login(GOOD_USERNAME, PASSWORD + "bad", "");
    }
}
