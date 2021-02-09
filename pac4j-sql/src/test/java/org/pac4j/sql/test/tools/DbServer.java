package org.pac4j.sql.test.tools;

import org.h2.jdbcx.JdbcConnectionPool;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

/**
 * Simulates a basic DB server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DbServer implements TestsConstants {

    public final static PasswordEncoder PASSWORD_ENCODER = new SpringSecurityPasswordEncoder(new StandardPasswordEncoder(SALT));

    private static DataSource ds;

    static {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test", Pac4jConstants.USERNAME, Pac4jConstants.PASSWORD);
        final var dbi = new DBI(ds);
        final var h = dbi.open();
        final var password = PASSWORD_ENCODER.encode(PASSWORD);
        h.execute("create table users (" + AbstractProfileService.ID + " int primary key, " + Pac4jConstants.USERNAME +  " varchar(100), "
            + Pac4jConstants.PASSWORD + " varchar(300), " + FIRSTNAME + " varchar(100), " + AbstractProfileService.LINKEDID
            + " varchar(100), " + AbstractProfileService.SERIALIZED_PROFILE + " varchar(6000))");
        h.execute("insert into users values(1, '" + GOOD_USERNAME + "', '" + password + "', '" + FIRSTNAME_VALUE + "', '', '')");
        h.execute("insert into users values(2, '" + MULTIPLE_USERNAME + "', '" + password + "', '', '', '')");
        h.execute("insert into users values(3, '" + MULTIPLE_USERNAME + "', '" + password + "', '', '', '')");
        h.close();
    }

    public static DataSource getInstance() {
        return ds;
    }
}
