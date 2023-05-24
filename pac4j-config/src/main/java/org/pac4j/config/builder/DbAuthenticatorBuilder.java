package org.pac4j.config.builder;

import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.sql.profile.service.DbProfileService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * Builder for the RDBMS authenticator.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class DbAuthenticatorBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for DbAuthenticatorBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public DbAuthenticatorBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryBuildDbAuthenticator.</p>
     *
     * @param authenticators a {@link Map} object
     * @param encoders a {@link Map} object
     */
    public void tryBuildDbAuthenticator(final Map<String, Authenticator> authenticators, final Map<String, PasswordEncoder> encoders) {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            if (containsProperty(DB_DATASOURCE_CLASS_NAME, i) || containsProperty(DB_JDBC_URL, i)) {
                try {
                    val ds = buildDataSource(i);
                    val authenticator = new DbProfileService(ds);
                    if (containsProperty(DB_ATTRIBUTES, i)) {
                        authenticator.setAttributes(getProperty(DB_ATTRIBUTES, i));
                    }
                    if (containsProperty(DB_USER_ID_ATTRIBUTE, i)) {
                        authenticator.setIdAttribute(getProperty(DB_USER_ID_ATTRIBUTE, i));
                    }
                    if (containsProperty(DB_USERNAME_ATTRIBUTE, i)) {
                        authenticator.setUsernameAttribute(getProperty(DB_USERNAME_ATTRIBUTE, i));
                    }
                    if (containsProperty(DB_USER_PASSWORD_ATTRIBUTE, i)) {
                        authenticator.setPasswordAttribute(getProperty(DB_USER_PASSWORD_ATTRIBUTE, i));
                    }
                    if (containsProperty(DB_USERS_TABLE, i)) {
                        authenticator.setUsersTable(getProperty(DB_USERS_TABLE, i));
                    }
                    if (containsProperty(DB_PASSWORD_ENCODER, i)) {
                        authenticator.setPasswordEncoder(encoders.get(getProperty(DB_PASSWORD_ENCODER, i)));
                    }
                    authenticators.put(concat("db", i), authenticator);
                } catch (final SQLException e) {
                    throw new TechnicalException(e);
                }
            }
        }
    }

    private DataSource buildDataSource(final int i) throws SQLException {
        val ds = new HikariDataSource();
        if (containsProperty(DB_DATASOURCE_CLASS_NAME, i)) {
            ds.setDataSourceClassName(getProperty(DB_DATASOURCE_CLASS_NAME, i));
        } else if (containsProperty(DB_JDBC_URL, i)) {
            ds.setJdbcUrl(getProperty(DB_JDBC_URL, i));
        }
        if (containsProperty(DB_USERNAME, i)) {
            ds.setUsername(getProperty(DB_USERNAME, i));
        }
        if (containsProperty(DB_PASSWORD, i)) {
            ds.setPassword(getProperty(DB_PASSWORD, i));
        }

        if (containsProperty(DB_AUTO_COMMIT, i)) {
            ds.setAutoCommit(getPropertyAsBoolean(DB_AUTO_COMMIT, i));
        }
        if (containsProperty(DB_CONNECTION_TIMEOUT, i)) {
            ds.setConnectionTimeout(getPropertyAsLong(DB_CONNECTION_TIMEOUT, i));
        }
        if (containsProperty(DB_IDLE_TIMEOUT, i)) {
            ds.setIdleTimeout(getPropertyAsLong(DB_IDLE_TIMEOUT, i));
        }
        if (containsProperty(DB_MAX_LIFETIME, i)) {
            ds.setMaxLifetime(getPropertyAsLong(DB_MAX_LIFETIME, i));
        }
        if (containsProperty(DB_CONNECTION_TEST_QUERY, i)) {
            ds.setConnectionTestQuery(getProperty(DB_CONNECTION_TEST_QUERY, i));
        }
        if (containsProperty(DB_MINIMUM_IDLE, i)) {
            ds.setMinimumIdle(getPropertyAsInteger(DB_MINIMUM_IDLE, i));
        }
        if (containsProperty(DB_MAXIMUM_POOL_SIZE, i)) {
            ds.setMaximumPoolSize(getPropertyAsInteger(DB_MAXIMUM_POOL_SIZE, i));
        }
        if (containsProperty(DB_POOL_NAME, i)) {
            ds.setPoolName(getProperty(DB_POOL_NAME, i));
        }

        if (containsProperty(DB_INITIALIZATION_FAIL_TIMEOUT, i)) {
            ds.setInitializationFailTimeout(getPropertyAsLong(DB_INITIALIZATION_FAIL_TIMEOUT, i));
        }
        if (containsProperty(DB_ISOLATE_INTERNAL_QUERIES, i)) {
            ds.setIsolateInternalQueries(getPropertyAsBoolean(DB_ISOLATE_INTERNAL_QUERIES, i));
        }
        if (containsProperty(DB_ALLOW_POOL_SUSPENSION, i)) {
            ds.setAllowPoolSuspension(getPropertyAsBoolean(DB_ALLOW_POOL_SUSPENSION, i));
        }
        if (containsProperty(DB_READ_ONLY, i)) {
            ds.setReadOnly(getPropertyAsBoolean(DB_READ_ONLY, i));
        }
        if (containsProperty(DB_REGISTER_MBEANS, i)) {
            ds.setRegisterMbeans(getPropertyAsBoolean(DB_REGISTER_MBEANS, i));
        }
        if (containsProperty(DB_CATALOG, i)) {
            ds.setCatalog(getProperty(DB_CATALOG, i));
        }
        if (containsProperty(DB_CONNECTION_INIT_SQL, i)) {
            ds.setConnectionInitSql(getProperty(DB_CONNECTION_INIT_SQL, i));
        }
        if (containsProperty(DB_DRIVER_CLASS_NAME, i)) {
            ds.setDriverClassName(getProperty(DB_DRIVER_CLASS_NAME, i));
        }
        if (containsProperty(DB_TRANSACTION_ISOLATION, i)) {
            ds.setTransactionIsolation(getProperty(DB_TRANSACTION_ISOLATION, i));
        }
        if (containsProperty(DB_VALIDATION_TIMEOUT, i)) {
            ds.setValidationTimeout(getPropertyAsLong(DB_VALIDATION_TIMEOUT, i));
        }
        if (containsProperty(DB_LEAK_DETECTION_THRESHOLD, i)) {
            ds.setLeakDetectionThreshold(getPropertyAsLong(DB_LEAK_DETECTION_THRESHOLD, i));
        }
        for (var j = 1; j <= MAX_NUM_CUSTOM_PROPERTIES; j++) {
            if (containsProperty(DB_CUSTOM_PARAM_KEY + j, i)) {
                ds.addDataSourceProperty(getProperty(DB_CUSTOM_PARAM_KEY + j, i), getProperty(DB_CUSTOM_PARAM_VALUE + j, i));
            }
        }

        if (containsProperty(DB_LOGIN_TIMEOUT, i)) {
            ds.setLoginTimeout(getPropertyAsInteger(DB_LOGIN_TIMEOUT, i));
        }
        if (containsProperty(DB_DATASOURCE_JNDI, i)) {
            ds.setDataSourceJNDI(getProperty(DB_DATASOURCE_JNDI, i));
        }

        return ds;
    }
}
