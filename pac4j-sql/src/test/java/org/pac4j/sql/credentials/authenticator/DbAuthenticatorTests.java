package org.pac4j.sql.credentials.authenticator;

import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.NopPasswordEncoder;
import org.pac4j.core.credentials.password.BasicSaltedSha512PasswordEncoder;
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
public final class DbAuthenticatorTests implements TestsConstants {

    private DataSource ds = DbServer.getInstance();

    @Test(expected = TechnicalException.class)
    public void testNullPasswordEncoder() throws HttpAction {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, FIRSTNAME);
        authenticator.validate(null, null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAttribute() throws HttpAction {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, null);
        authenticator.setPasswordEncoder(new NopPasswordEncoder());
        authenticator.validate(null, null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullDataSource() throws HttpAction {
        final DbAuthenticator authenticator = new DbAuthenticator(null, FIRSTNAME);
        authenticator.setPasswordEncoder(new NopPasswordEncoder());
        authenticator.validate(null, null);
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) throws HttpAction {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, attribute);
        authenticator.setPasswordEncoder(new BasicSaltedSha512PasswordEncoder(SALT));

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() throws HttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() throws HttpAction {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
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
