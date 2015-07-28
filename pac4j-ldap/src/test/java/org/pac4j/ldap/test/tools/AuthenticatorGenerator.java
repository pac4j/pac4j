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
public class AuthenticatorGenerator {

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
