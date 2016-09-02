---
layout: doc
title: LDAP
---

*pac4j* allows you to validate username / password on a LDAP.

## 1) Dependency

You need to use the following module: `pac4j-ldap`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-ldap</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `LdapAuthenticator`

The [`LdapAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-ldap/src/main/java/org/pac4j/ldap/credentials/authenticator/LdapAuthenticator.java) must be used for LDAP authentication.

It can be defined for HTTP clients which deal with `UsernamePasswordCredentials`.

It is based on the great [Ldpative](http://www.ldaptive.org/) library and built from a `org.ldaptive.auth.Authenticator`.

You can define the returned attributes from the LDAP via the `attributes` parameter in the constructor: `LdapAuthenticator(Authenticator ldaptiveAuthenticator, String attributes)` or in the setter: `setAttributes(String attributes)` of the `LdapAuthenticator`. The `attributes` parameter is a list of attributes names separated by commas like `cn,sn`.

After a successful credentials validation, it "returns" a [`LdapProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-ldap/src/main/java/org/pac4j/ldap/profile/LdapProfile.java).

**Example**:

```java
// ldaptive:
FormatDnResolver dnResolver = new FormatDnResolver();
dnResolver.setFormat(LdapServer.CN + "=%s," + LdapServer.BASE_PEOPLE_DN);
ConnectionConfig connectionConfig = new ConnectionConfig();
connectionConfig.setConnectTimeout(500);
connectionConfig.setResponseTimeout(1000);
connectionConfig.setLdapUrl("ldap://localhost:" + LdapServer.PORT);
DefaultConnectionFactory connectionFactory = new DefaultConnectionFactory();
connectionFactory.setConnectionConfig(connectionConfig);
PoolConfig poolConfig = new PoolConfig();
poolConfig.setMinPoolSize(1);
poolConfig.setMaxPoolSize(2);
poolConfig.setValidateOnCheckOut(true);
poolConfig.setValidateOnCheckIn(true);
poolConfig.setValidatePeriodically(false);
SearchValidator searchValidator = new SearchValidator();
IdlePruneStrategy pruneStrategy = new IdlePruneStrategy();
BlockingConnectionPool connectionPool = new BlockingConnectionPool();
connectionPool.setPoolConfig(poolConfig);
connectionPool.setBlockWaitTime(1000);
connectionPool.setValidator(searchValidator);
connectionPool.setPruneStrategy(pruneStrategy);
connectionPool.setConnectionFactory(connectionFactory);
connectionPool.initialize();
PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
pooledConnectionFactory.setConnectionPool(connectionPool);
PooledBindAuthenticationHandler handler = new PooledBindAuthenticationHandler();
handler.setConnectionFactory(pooledConnectionFactory);
Authenticator ldaptiveAuthenticator = new Authenticator();
ldaptiveAuthenticator.setDnResolver(dnResolver);
ldaptiveAuthenticator.setAuthenticationHandler(handler);
// pac4j:
LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(ldaptiveAuthenticator);
```
