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
package org.pac4j.sql.credentials.authenticator;

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
import org.pac4j.sql.profile.DbProfile;
import org.pac4j.sql.test.tools.DbServer;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Tests the {@link DbAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DbAuthenticatorTests implements TestsConstants {

    private DataSource ds = DbServer.getInstance();

    @Test(expected = TechnicalException.class)
    public void testNullPasswordEncoder() {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, FIRSTNAME);

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAttribute() {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, null);
        authenticator.setPasswordEncoder(new NopPasswordEncoder());

        authenticator.validate(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullDataSource() {
        final DbAuthenticator authenticator = new DbAuthenticator(null, FIRSTNAME);
        authenticator.setPasswordEncoder(new NopPasswordEncoder());

        authenticator.validate(null);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, attribute);
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
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final UserProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
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
