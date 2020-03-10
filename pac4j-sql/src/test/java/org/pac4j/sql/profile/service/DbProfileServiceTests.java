package org.pac4j.sql.profile.service;

import org.junit.*;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.sql.profile.DbProfile;
import org.pac4j.sql.test.tools.DbServer;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link DbProfileService}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DbProfileServiceTests implements TestsConstants {

    private static final int DB_ID = 100000000;
    private static final String DB_LINKED_ID = "dbLinkedId";
    private static final String DB_PASS = "dbPass";
    private static final String DB_USER = "dbUser";
    private static final String DB_USER2 = "dbUser2";

    private DataSource ds = DbServer.getInstance();

    @Test
    public void testNullPasswordEncoder() {
        final DbProfileService dbProfileService = new DbProfileService(ds, FIRSTNAME);
        TestsHelper.expectException(() -> dbProfileService.validate(null, null), TechnicalException.class,
            "passwordEncoder cannot be null");
    }

    @Test
    public void testNullDataSource() {
        final DbProfileService dbProfileService = new DbProfileService(null, FIRSTNAME);
        dbProfileService.setPasswordEncoder(DbServer.PASSWORD_ENCODER);
        TestsHelper.expectException(() -> dbProfileService.validate(null, null), TechnicalException.class, "dataSource cannot be null");
    }

    private UsernamePasswordCredentials login(final String username, final String password, final String attribute) {
        final DbProfileService dbProfileService = new DbProfileService(ds, attribute);
        dbProfileService.setPasswordEncoder(DbServer.PASSWORD_ENCODER);

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        dbProfileService.validate(credentials, null);

        return credentials;
    }

    @Test
    public void testGoodUsernameAttribute() {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, FIRSTNAME);

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
        assertEquals(GOOD_USERNAME, dbProfile.getId());
        assertEquals(FIRSTNAME_VALUE, dbProfile.getAttribute(FIRSTNAME));
    }

    @Test
    public void testGoodUsernameNoAttribute() {
        final UsernamePasswordCredentials credentials =  login(GOOD_USERNAME, PASSWORD, "");

        final CommonProfile profile = credentials.getUserProfile();
        assertNotNull(profile);
        assertTrue(profile instanceof DbProfile);
        final DbProfile dbProfile = (DbProfile) profile;
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
        TestsHelper.expectException(() -> login(GOOD_USERNAME, PASSWORD + "bad", ""), BadCredentialsException.class,
            "Bad credentials for: jle");
    }

    @Test
    public void testCreateUpdateFindDelete() {
        final DbProfile profile = new DbProfile();
        profile.setId("" + DB_ID);
        profile.setLinkedId(DB_LINKED_ID);
        profile.addAttribute(USERNAME, DB_USER);
        final DbProfileService dbProfileService = new DbProfileService(ds);
        dbProfileService.setPasswordEncoder(DbServer.PASSWORD_ENCODER);
        // create
        dbProfileService.create(profile, DB_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(DB_USER, DB_PASS);
        dbProfileService.validate(credentials, null);
        final CommonProfile profile1 = credentials.getUserProfile();
        assertNotNull(profile1);
        // check data
        final List<Map<String, Object>> results = getData(DB_ID);
        assertEquals(1, results.size());
        final Map<String, Object> result = results.get(0);
        assertEquals(5, result.size());
        assertEquals(DB_ID, result.get(ID));
        assertEquals(DB_LINKED_ID, result.get(AbstractProfileService.LINKEDID));
        assertNotNull(result.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertTrue(DbServer.PASSWORD_ENCODER.matches(DB_PASS, (String) result.get(PASSWORD)));
        assertEquals(DB_USER, result.get(USERNAME));
        // findById
        final DbProfile profile2 = dbProfileService.findById("" + DB_ID);
        assertEquals("" + DB_ID, profile2.getId());
        assertEquals(DB_LINKED_ID, profile2.getLinkedId());
        assertEquals(DB_USER, profile2.getUsername());
        assertEquals(1, profile2.getAttributes().size());
        // update
        profile.addAttribute(USERNAME, DB_USER2);
        dbProfileService.update(profile, null);
        final List<Map<String, Object>> results2 = getData(DB_ID);
        assertEquals(1, results2.size());
        final Map<String, Object> result2 = results2.get(0);
        assertEquals(5, result2.size());
        assertEquals(DB_ID, result2.get(ID));
        assertEquals(DB_LINKED_ID, result2.get(AbstractProfileService.LINKEDID));
        assertNotNull(result2.get(AbstractProfileService.SERIALIZED_PROFILE));
        assertTrue(DbServer.PASSWORD_ENCODER.matches(DB_PASS, (String) result2.get(PASSWORD)));
        assertEquals(DB_USER2, result2.get(USERNAME));
        // remove
        dbProfileService.remove(profile);
        final List<Map<String, Object>> results3 = getData(DB_ID);
        assertEquals(0, results3.size());
    }

    @Test
    public void testChangeUserAndPasswordAttributes() {
        alterTableChangeColumnName(USERNAME, ALT_USER_ATT);
        alterTableChangeColumnName(PASSWORD, ALT_PASS_ATT);
        final DbProfileService dbProfileService = new DbProfileService(ds, DbServer.PASSWORD_ENCODER);
        dbProfileService.setPasswordAttribute(ALT_PASS_ATT);
        dbProfileService.setUsernameAttribute(ALT_USER_ATT);
        final DbProfile profile = new DbProfile();
        profile.setId("" + DB_ID);
        profile.setLinkedId(DB_LINKED_ID);
        profile.addAttribute(USERNAME, DB_USER);
        // create
        dbProfileService.create(profile, DB_PASS);
        // check credentials
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(DB_USER, DB_PASS);
        dbProfileService.validate(credentials, null);
        assertNotNull(credentials.getUserProfile());

        // clean up
        dbProfileService.remove((DbProfile) credentials.getUserProfile());
        alterTableChangeColumnName(ALT_USER_ATT, USERNAME);
        alterTableChangeColumnName(ALT_PASS_ATT, PASSWORD);
    }

    private void alterTableChangeColumnName(final String from, final String to) {
        final DBI dbi = new DBI(ds);
        try ( Handle h = dbi.open()) {
            h.execute("alter table users rename column " + from + " to " + to);
        }
    }

    private List<Map<String, Object>> getData(final int id) {
        final DBI dbi = new DBI(ds);
        Handle h = null;
        try {
            h = dbi.open();
            return h.createQuery("select id,username,linkedid,password,serializedprofile from users where id = :id").bind("id", id).list(2);
        } finally {
            if (h != null) {
                h.close();
            }
        }
    }
}
