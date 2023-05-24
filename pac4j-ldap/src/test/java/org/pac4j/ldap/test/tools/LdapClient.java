package org.pac4j.ldap.test.tools;

import lombok.val;
import org.ldaptive.*;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.FormatDnResolver;
import org.ldaptive.auth.SimpleBindAuthenticationHandler;
import org.ldaptive.pool.IdlePruneStrategy;
import org.ldaptive.pool.PruneStrategy;

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
        val dnResolver = new FormatDnResolver();
        dnResolver.setFormat(LdapServer.CN + "=%s," + LdapServer.BASE_PEOPLE_DN);

        val connectionConfig = new ConnectionConfig();
        connectionConfig.setConnectTimeout(Duration.ofMillis(500));
        connectionConfig.setResponseTimeout(Duration.ofSeconds(1));
        connectionConfig.setLdapUrl("ldap://localhost:" + port);

        connectionFactory = new DefaultConnectionFactory(connectionConfig);

        ConnectionValidator searchValidator = new SearchConnectionValidator();

        PruneStrategy pruneStrategy = new IdlePruneStrategy();

        val pooledConnectionFactory = new PooledConnectionFactory(connectionConfig);
        pooledConnectionFactory.setMinPoolSize(1);
        pooledConnectionFactory.setMaxPoolSize(2);
        pooledConnectionFactory.setValidateOnCheckOut(true);
        pooledConnectionFactory.setValidateOnCheckIn(true);
        pooledConnectionFactory.setValidatePeriodically(false);
        pooledConnectionFactory.setBlockWaitTime(Duration.ofSeconds(1));
        pooledConnectionFactory.setValidator(searchValidator);
        pooledConnectionFactory.setPruneStrategy(pruneStrategy);
        pooledConnectionFactory.initialize();

        val handler = new SimpleBindAuthenticationHandler();
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
