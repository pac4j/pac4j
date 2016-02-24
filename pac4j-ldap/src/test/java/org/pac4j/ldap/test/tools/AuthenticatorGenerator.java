package org.pac4j.ldap.test.tools;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.FormatDnResolver;
import org.ldaptive.auth.PooledBindAuthenticationHandler;
import org.ldaptive.pool.*;

/**
 * Creates a basic {@link org.ldaptive.auth.Authenticator} to work with the {@link LdapServer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorGenerator {

    public static Authenticator create() {
        final FormatDnResolver dnResolver = new FormatDnResolver();
        dnResolver.setFormat(LdapServer.CN + "=%s," + LdapServer.BASE_PEOPLE_DN);

        final ConnectionConfig connectionConfig = new ConnectionConfig();
        connectionConfig.setConnectTimeout(500);
        connectionConfig.setResponseTimeout(1000);
        connectionConfig.setLdapUrl("ldap://localhost:" + LdapServer.PORT);

        final DefaultConnectionFactory connectionFactory = new DefaultConnectionFactory();
        connectionFactory.setConnectionConfig(connectionConfig);

        final PoolConfig poolConfig = new PoolConfig();
        poolConfig.setMinPoolSize(1);
        poolConfig.setMaxPoolSize(2);
        poolConfig.setValidateOnCheckOut(true);
        poolConfig.setValidateOnCheckIn(true);
        poolConfig.setValidatePeriodically(false);

        final SearchValidator searchValidator = new SearchValidator();

        final IdlePruneStrategy pruneStrategy = new IdlePruneStrategy();

        final BlockingConnectionPool connectionPool = new BlockingConnectionPool();
        connectionPool.setPoolConfig(poolConfig);
        connectionPool.setBlockWaitTime(1000);
        connectionPool.setValidator(searchValidator);
        connectionPool.setPruneStrategy(pruneStrategy);
        connectionPool.setConnectionFactory(connectionFactory);
        connectionPool.initialize();

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionPool(connectionPool);

        final PooledBindAuthenticationHandler handler = new PooledBindAuthenticationHandler();
        handler.setConnectionFactory(pooledConnectionFactory);

        final Authenticator authenticator = new Authenticator();
        authenticator.setDnResolver(dnResolver);
        authenticator.setAuthenticationHandler(handler);
        return authenticator;
    }
}
