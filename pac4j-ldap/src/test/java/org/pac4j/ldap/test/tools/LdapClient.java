package org.pac4j.ldap.test.tools;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.FormatDnResolver;
import org.ldaptive.auth.PooledBindAuthenticationHandler;
import org.ldaptive.pool.*;

import java.time.Duration;

/**
 * Basic LDAP client.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class LdapClient {

    private final ConnectionFactory connectionFactory;

    private final Authenticator authenticator;

    public LdapClient(final int port) {
        final FormatDnResolver dnResolver = new FormatDnResolver();
        dnResolver.setFormat(LdapServer.CN + "=%s," + LdapServer.BASE_PEOPLE_DN);

        final ConnectionConfig connectionConfig = new ConnectionConfig();
        connectionConfig.setConnectTimeout(Duration.ofMillis(500));
        connectionConfig.setResponseTimeout(Duration.ofSeconds(1));
        connectionConfig.setLdapUrl("ldap://localhost:" + port);

        connectionFactory = new DefaultConnectionFactory();
        ((DefaultConnectionFactory) connectionFactory).setConnectionConfig(connectionConfig);

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
        connectionPool.setBlockWaitTime(Duration.ofSeconds(1));
        connectionPool.setValidator(searchValidator);
        connectionPool.setPruneStrategy(pruneStrategy);
        connectionPool.setConnectionFactory((DefaultConnectionFactory) connectionFactory);
        connectionPool.initialize();

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionPool(connectionPool);

        final PooledBindAuthenticationHandler handler = new PooledBindAuthenticationHandler();
        handler.setConnectionFactory(pooledConnectionFactory);

        authenticator = new Authenticator();
        authenticator.setDnResolver(dnResolver);
        authenticator.setAuthenticationHandler(handler);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }
}
