package org.pac4j.sql.test.tools;

import org.h2.jdbcx.JdbcConnectionPool;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.credentials.password.BasicSaltedSha512PasswordEncoder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;

/**
 * Simulates a basic DB server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DbServer implements TestsConstants {

    private static DataSource ds;

    static {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test", Pac4jConstants.USERNAME, Pac4jConstants.PASSWORD);
        final DBI dbi = new DBI(ds);
        final Handle h = dbi.open();
        final PasswordEncoder encoder = new BasicSaltedSha512PasswordEncoder(SALT);
        final String password = encoder.encode(PASSWORD);
        h.execute("create table users (id int primary key, username varchar(100), password varchar(300), " + FIRSTNAME + " varchar(100))");
        h.execute("insert into users values(1, '" + GOOD_USERNAME + "', '" + password + "', '" + FIRSTNAME_VALUE + "')");
        h.execute("insert into users values(2, '" + MULTIPLE_USERNAME + "', '" + password + "', '')");
        h.execute("insert into users values(3, '" + MULTIPLE_USERNAME + "', '" + password + "', '')");
        h.close();
    }

    public static DataSource getInstance() {
        return ds;
    }
}
