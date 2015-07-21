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

import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.http.credentials.password.PasswordEncoder;
import org.pac4j.sql.profile.DbProfile;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Authenticator for users stored in relational database, based on the JDBI library.
 * It creates the user profile and stores it in the credentials for the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DbAuthenticator extends AbstractUsernamePasswordAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DBI dbi;

    protected DataSource dataSource;

    protected String attributes = "";

    protected String startQuery = "select username, password";
    protected String endQuery = " from users where username = :username";

    public DbAuthenticator() {
    }

    public DbAuthenticator(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbAuthenticator(final DataSource dataSource, final String attributes) {
        this.dataSource = dataSource;
        this.attributes = attributes;
    }

    public DbAuthenticator(final DataSource dataSource, final String attributes, final PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.attributes = attributes;
        this.passwordEncoder = passwordEncoder;
    }

    protected void initDbi() {
        if (this.dbi == null) {
            synchronized (this) {
                if (this.dbi == null) {
                    this.dbi = new DBI(this.dataSource);
                }
            }
        }
    }

    public void validate(UsernamePasswordCredentials credentials) {
        CommonHelper.assertNotNull("dataSource", this.dataSource);
        CommonHelper.assertNotNull("attributes", this.attributes);
        CommonHelper.assertNotNull("passwordEncoder", this.passwordEncoder);

        initDbi();

        Handle h = null;
        try {
            h = dbi.open();

            final String username = credentials.getUsername();
            final String query;
            if (CommonHelper.isNotBlank(attributes)) {
                query = startQuery + ", " + attributes + endQuery;
            } else {
                query = startQuery + endQuery;
            }
            final List<Map<String, Object>> results = h.createQuery(query).bind("username", username).list(2);

            if (results == null || results.size() == 0) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (results.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                final Map<String, Object> result = results.get(0);
                final String expectedPassword = passwordEncoder.encode(credentials.getPassword());
                final String returnedPassword = (String) result.get("password");
                if (CommonHelper.areNotEquals(returnedPassword, expectedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    final DbProfile profile = createProfile(username, attributes.split(","), result);
                    credentials.setUserProfile(profile);
                }
            }

        } catch (final TechnicalException e) {
            logger.error("Authentication error", e);
            throw e;
        } catch (final RuntimeException e) {
            logger.error("Cannot fetch username / password from DB", e);
            throw new TechnicalException(e);
        } finally {
            if (h != null) {
                h.close();
            }
        }
    }

    protected DbProfile createProfile(final String username, final String[] attributes, final Map<String, Object> result) {
        final DbProfile profile = new DbProfile();
        profile.setId(username);
        for (String attribute: attributes) {
            profile.addAttribute(attribute, result.get(attribute));
        }
        return profile;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getStartQuery() {
        return startQuery;
    }

    public void setStartQuery(String startQuery) {
        this.startQuery = startQuery;
    }

    public String getEndQuery() {
        return endQuery;
    }

    public void setEndQuery(String endQuery) {
        this.endQuery = endQuery;
    }
}
