package org.pac4j.ldap.test.tools;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.SearchConnectionValidator;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.FormatDnResolver;
import org.ldaptive.auth.SimpleBindAuthenticationHandler;
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

        connectionFactory = new DefaultConnectionFactory(connectionConfig);

        final SearchConnectionValidator searchValidator = new SearchConnectionValidator();

        final IdlePruneStrategy pruneStrategy = new IdlePruneStrategy();

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(connectionConfig);
        pooledConnectionFactory.setMinPoolSize(1);
        pooledConnectionFactory.setMaxPoolSize(2);
        pooledConnectionFactory.setValidateOnCheckOut(true);
        pooledConnectionFactory.setValidateOnCheckIn(true);
        pooledConnectionFactory.setValidatePeriodically(false);
        pooledConnectionFactory.setBlockWaitTime(Duration.ofSeconds(1));
        pooledConnectionFactory.setValidator(searchValidator);
        pooledConnectionFactory.setPruneStrategy(pruneStrategy);
        pooledConnectionFactory.initialize();

        final SimpleBindAuthenticationHandler handler = new SimpleBindAuthenticationHandler();
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
