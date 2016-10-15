package org.pac4j.sql.credentials.authenticator;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.*;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
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
 * It creates the user profile and stores it in the credentials for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DbAuthenticator extends AbstractUsernamePasswordAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DBI dbi;

    protected DataSource dataSource;

    /**
     * This must a list of attribute names separated by commas. No aliasing (AS).
     */
    protected String attributes = "";

    protected String startQuery = "select username, password";
    protected String endQuery = " from users where username = :username";

    public DbAuthenticator() {}

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
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("dataSource", this.dataSource);
        CommonHelper.assertNotNull("attributes", this.attributes);
        this.dbi = new DBI(this.dataSource);

        super.internalInit(context);
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction {

        init(context);

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
            final List<Map<String, Object>> results = h.createQuery(query).bind(Pac4jConstants.USERNAME, username).list(2);

            if (results == null || results.isEmpty()) {
                throw new AccountNotFoundException("No account found for: " + username);
            } else if (results.size() > 1) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            } else {
                final Map<String, Object> result = results.get(0);
                final String returnedPassword = (String) result.get(Pac4jConstants.PASSWORD);
                if (!getPasswordEncoder().matches(credentials.getPassword(), returnedPassword)) {
                    throw new BadCredentialsException("Bad credentials for: " + username);
                } else {
                    final DbProfile profile = createProfile(username, attributes.split(","), result);
                    credentials.setUserProfile(profile);
                }
            }

        } catch (final TechnicalException e) {
            logger.debug("Authentication error", e);
            throw e;
        } catch (final RuntimeException e) {
            throw new TechnicalException("Cannot fetch username / password from DB", e);
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
