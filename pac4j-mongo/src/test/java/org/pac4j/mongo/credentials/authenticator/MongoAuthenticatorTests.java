/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.mongo.credentials.authenticator;

import com.mongodb.MongoClient;
import org.junit.*;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.password.NopPasswordEncoder;
import org.pac4j.http.credentials.password.BasicSaltedSha512PasswordEncoder;
import org.pac4j.mongo.profile.MongoProfile;
import org.pac4j.mongo.test.tools.MongoServer;

import static org.junit.Assert.*;

/**
 * Tests the {@link MongoAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MongoAuthenticatorTests implements TestsConstants {

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
    public void testNullPasswordEncoder() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME);

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAttribute() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), null, new NopPasswordEncoder());

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullMongoClient() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(null, FIRSTNAME, new NopPasswordEncoder());

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullDatabase() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsersDatabase(null);

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullCollection() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsersCollection(null);

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullUsername() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setUsernameAttribute(null);

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullPassword() {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), FIRSTNAME, new NopPasswordEncoder());
        authenticator.setPasswordAttribute(null);

        authenticator.validate(null);
    }

    private MongoClient getClient() {
        return new MongoClient("localhost", PORT);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) {
        final MongoAuthenticator authenticator = new MongoAuthenticator(getClient(), attribute);
        authenticator.setPasswordEncoder(new BasicSaltedSha512PasswordEncoder(SALT));

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials);

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
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof MongoProfile);
        final MongoProfile dbProfile = (MongoProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertNull(dbProfile.getAttribute(FIRSTNAME));
    }

    @Test(expected = MultipleAccountsFoundException.class)
    public void testMultipleUsername() {
        final UsernamePasswordCredentials credentials =  login(MULTIPLE_USERNAME, PASSWORD, "");
    }

    @Test(expected = AccountNotFoundException.class)
    public void testBadUsername() {
        final UsernamePasswordCredentials credentials =  login(BAD_USERNAME, PASSWORD, "");
    }

    @Test(expected = BadCredentialsException.class)
    public void testBadPassword() {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD + "bad", "");
    }
}
