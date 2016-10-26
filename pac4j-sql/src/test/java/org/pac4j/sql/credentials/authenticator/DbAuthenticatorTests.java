package org.pac4j.sql.credentials.authenticator;

import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsHelper;
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

    @Test
    public void testNullPasswordEncoder() throws HttpAction, CredentialsException {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, FIRSTNAME);
        TestsHelper.expectException(() -> authenticator.validate(null, null), TechnicalException.class, "passwordEncoder cannot be null");
    }

    @Test
    public void testNullAttribute() throws HttpAction, CredentialsException {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, null, DbServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.validate(null, null), TechnicalException.class, "attributes cannot be null");
    }

    @Test
    public void testNullDataSource() throws HttpAction, CredentialsException {
        final DbAuthenticator authenticator = new DbAuthenticator(null, FIRSTNAME);
        authenticator.setPasswordEncoder(DbServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> authenticator.validate(null, null), TechnicalException.class, "dataSource cannot be null");
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) throws HttpAction, CredentialsException {
        final DbAuthenticator authenticator = new DbAuthenticator(ds, attribute);
        authenticator.setPasswordEncoder(DbServer.PASSWORD_ENCODER);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, CLIENT_NAME);
        authenticator.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() throws HttpAction, CredentialsException {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() throws HttpAction, CredentialsException {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
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
        TestsHelper.expectException(() -> login(GOOD_USERNAME, PASSWORD + "bad", ""), BadCredentialsException.class, "Bad credentials for: jle");
    }
}
