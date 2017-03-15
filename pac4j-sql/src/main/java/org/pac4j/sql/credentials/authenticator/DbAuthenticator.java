package org.pac4j.sql.credentials.authenticator;

import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.sql.profile.service.DbProfileService;

import javax.sql.DataSource;

/**
 * Use the {@link DbProfileService} instead.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 * @deprecated
 */
@Deprecated
public class DbAuthenticator extends DbProfileService {

    public DbAuthenticator() {}

    public DbAuthenticator(final DataSource dataSource) {
        super(dataSource);
    }

    public DbAuthenticator(final DataSource dataSource, final String attributes) {
        super(dataSource, attributes);
    }

    public DbAuthenticator(final DataSource dataSource, final String attributes, final PasswordEncoder passwordEncoder) {
        super(dataSource, attributes, passwordEncoder);
    }
}
