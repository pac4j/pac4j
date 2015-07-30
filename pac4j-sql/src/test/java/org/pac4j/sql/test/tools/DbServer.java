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
package org.pac4j.sql.test.tools;

import org.h2.jdbcx.JdbcConnectionPool;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.password.PasswordEncoder;
import org.pac4j.http.credentials.password.SaltedSha512PasswordEncoder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;

/**
 * Simulates a basic DB server.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DbServer implements TestsConstants{

    private static DataSource ds;

    static {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test", "username", "password");
        final DBI dbi = new DBI(ds);
        final Handle h = dbi.open();
        final PasswordEncoder encoder = new SaltedSha512PasswordEncoder(SALT);
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
