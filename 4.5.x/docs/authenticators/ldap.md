---
layout: doc
title: LDAP
---

*pac4j* allows you to validate username/password and create, update and delete users on a LDAP.

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

## 2) `LdapProfileService`

The [`LdapProfileService`](https://github.com/pac4j/pac4j/blob/master/pac4j-ldap/src/main/java/org/pac4j/ldap/profile/service/LdapProfileService.java) allows you to:

- validate a username/password on a LDAP (it can be defined as the `Authenticator` for HTTP clients which deal with `UsernamePasswordCredentials`)
- create, update or delete a user in the LDAP.

It works with a [`LdapProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-ldap/src/main/java/org/pac4j/ldap/profile/LdapProfile.java).

It is based on the great [Ldpative](http://www.ldaptive.org/) library and built from a `org.ldaptive.ConnectionFactory` and a `org.ldaptive.auth.Authenticator`.

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
LdapProfileService ldapProfileService  = new LdapProfileService(connectionFactory, ldaptiveAuthenticator);
```

The base users DN can be changed via the `setUsersDn` method. As well as the `id`, `username` and `password` LDAP attribute names using the `setIdAttribute`, `setUsernameAttribute` and `setPasswordAttribute` methods.

The attributes of the user profile can be managed in the LDAP in two ways:

- either each attribute is explicitly mapped in a specific LDAP attribute and all these attributes are defined as a list of names separated by commas via the `setAttributes` method (it's the legacy mode existing since version 1.9)
- or the whole user profile is serialized and saved in the `serializedprofile` LDAP attribute.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Starting with v3.9.0 in the 3.x stream, v4.2.0 in the 4.x stream and v5.0, the <code>serializedprofile</code> is written in JSON instead of using the Java serialization.</div>
