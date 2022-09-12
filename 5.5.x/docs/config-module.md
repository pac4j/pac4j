---
layout: doc
title: Security module&#58;
---

The `pac4j-config` module:

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-config</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

gathers all the *pac4j* facilities to define this `Config` object.
Currently, there is only one component which allows you to build the clients from a set of properties: the [`PropertiesConfigFactory`](https://github.com/pac4j/pac4j/blob/master/pac4j-config/src/main/java/org/pac4j/config/client/PropertiesConfigFactory.java).

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Notice that dependencies must be explicitly declared when necessary (the <code>pac4j-saml</code> module if you want to use SAML, the <code>pac4j-oauth</code> module if you want to use OAuth...)</div>

**Example (YAML dropwizard configuration file):**

```properties
pac4j:
  callbackUrl: /callback
  clientsProperties:
    facebook.id: 145278422258960
    facebook.secret: be21409ba8f39b5dae2a7de525484da8
    saml.keystorePath: resource:samlKeystore.jks
    saml.keystorePassword: pac4j-demo-passwd
    saml.privateKeyPassword: pac4j-demo-passwd
    saml.identityProviderMetadataPath: resource:metadata-okta.xml
    saml.maximumAuthenticationLifetime: 3600
    saml.serviceProviderEntityId: http://localhost:8080/callback?client_name=SAML2Client
    saml.serviceProviderMetadataPath: sp-metadata.xml
    anonymous: fakevalue
    ldap.type: direct
    ldap.url: ldap://ldap.jumpcloud.com:389
    ldap.useStartTls: false
    ldap.dnFormat: uid=%s,ou=Users,o=58e69adc0914b437324e7632,dc=jumpcloud,dc=com
    ldap.usersDn: ou=Users,o=58e69adc0914b437324e7632,dc=jumpcloud,dc=com
    ldap.principalAttributeId: uid
    ldap.principalAttributes: firstName,lastName
    ldap.enhanceWithEntryResolver: false
    formClient.loginUrl: /login.html
    formClient.authenticator: ldap
```

Here are the properties you can use to define the clients (, authenticators and password encoders):

| Available properties | Usage |
|---------------|--------|
| `encoder.spring.type` (`bcrypt`, `noop`, `pbkdf2`, `scrypt` or `standard`), `encoder.spring.bcrypt.length`, `encoder.spring.pbkdf2.secret`, `encoder.spring.pbkdf2.iterations`, `encoder.spring.pbkdf2.hashWidth`, `encoder.spring.scrypt.cpuCost`, `encoder.spring.scrypt.memoryCost`, `encoder.spring.scrypt.parallelization`, `encoder.spring.scrypt.keyLength`, `encoder.spring.scrypt.saltLength` and `encoder.spring.standard.secret` | To define a `SpringPasswordEncoder` based on the provided properties and named `encoder.spring` or `encoder.spring.N` |
| `encoder.shiro` (if no specific properties are required), `encoder.shiro.generatePublicSalt`, `encoder.shiro.hashAlgorithmName`, `encoder.shiro.hashIterations` and `encoder.shiro.privateSalt` | To define a `ShiroPasswordEncoder` based on the provided properties and named `encoder.shiro` or `encoder.shiro.N` |
| `ldap.type`, `ldap.dnFormat`, `ldap.principalAttributes`,`ldap.principalAttributeId`, `ldap.principalAttributePassword`, `ldap.subtreeSearch`, `ldap.usersDn`, `ldap.userFilter`, `ldap.enhanceWithEntryResolver`, `ldap.trustCertificates`, `ldap.keystore`, `ldap.keystorePassword`, `ldap.keystoreType`, `ldap.minPoolSize`, `ldap.maxPoolSize`, `ldap.poolPassivator`, `ldap.validateOnCheckout`, `ldap.validatePeriodically`, `ldap.validatePeriod`, `ldap.failFast`, `ldap.idleTime`, `ldap.prunePeriod`, `ldap.blockWaitTime`, `ldap.url`, `ldap.useStartTls`, `ldap.connectTimeout`, `ldap.providerClass`, `ldap.allowMultipleDns`, `ldap.bindDn`, `ldap.bindCredential`, `ldap.saslRealm`, `ldap.saslMechanism`, `ldap.saslAuthorizationId`, `ldap.saslSecurityStrength` and `ldap.saslQualityOfProtection` | To define a `LdapAuthenticator` based on the provided properties and named `ldap` or `ldap.N` |
| `db.dataSourceClassName`, `db.jdbcUrl`, `db.userAttributes`, `db.userIdAttribute`, `db.usernameAttribute`, `db.userPasswordAttribute`, `db.usersTable`, `db.username`, `db.password`, `db.autoCommit`, `db.connectionTimeout`, `db.idleTimeout`, `db.maxLifetime`, `db.connectionTestQuery`, `db.minimumIdle`, `db.maximumPoolSize`, `db.poolName`, `db.initializationFailTimeout`, `db.isolateInternalQueries`, `db.allowPoolSuspension`, `db.readOnly`, `db.registerMbeans`, `db.catalog`, `db.connectionInitSql`, `db.driverClassName`, `db.transactionIsolation`, `db.validationTimeout`, `db.leakDetectionThreshold`, `db.customParamKey`, `db.customParamValue`, `db.loginTimeout`, `db.dataSourceJndi` and `db.passwordEncoder` | To define a `DbAuthenticator` based on the provided properties and named `db` or `db.N` |
| `rest.url` | To define a `RestAuthenticator` based on the provided properties and named `rest` or `rest.N` |
| `anonymous` | To define the `AnonymousClient`, the value is ignored |
| `directBasicAuth.authenticator` | To define a `DirectBasicAuthClient` based on the provided properties |
| `saml.keystorePassword`, `saml.privateKeyPassword`, `saml.keystorePath`, `saml.identityProviderMetadataPath`, `saml.maximumAuthenticationLifetime`, `saml.serviceProviderEntityId`, `saml.serviceProviderMetadataPath`, `saml.destinationBindingType`, `saml.keystoreAlias` | To define a `SAML2Client` based on the provided properties |
| `cas.loginUrl`, `cas.protocol` | To define a `CasClient` based on the provided properties |
| `oidc.type` (`google` or `azure`), `oidc.azure.tenant` (for the AzureAD tenant), `oidc.id`, `oidc.secret`, `oidc.scope`, `oidc.discoveryUri`, `oidc.useNonce`, `oidc.preferredJwsAlgorithm`, `oidc.maxClockSkew`, `oidc.clientAuthenticationMethod`, `oidc.customParamKey1`, `oidc.customParamValue1`, `oidc.customParamKey2`,`oidc.customParamValue2` | To define an OpenID connect client based on the provided properties |
| `formClient.authenticator`, `formClient.loginUrl`, `formClient.usernameParameter` `formClient.passwordParameter` | To define a `FormClient` based on the provided properties |
| `indirectBasicAuth.authenticator`, `indirectBasicAuth.realName` | To define an `IndirectBasicAuthClient` based on the provided properties |
| `facebook.id`, `facebook.secret`, `facebook.scope`, `facebook.fields` | To define a `FacebookClient` based on the provided properties |
| `twitter.id`, `twitter.secret` | To define a `TwitterClient` based on the provided properties |
| `github.id`, `github.secret` | To define a `GitHubClient` based on the provided properties |
| `dropbox.id`, `dropbox.secret` | To define a `DropBoxClient` based on the provided properties |
| `windowslive.id`, `windowslive.secret` | To define a `WindowsLiveClient` based on the provided properties |
| `yahoo.id`, `yahoo.secret` | To define a `YahooClient` based on the provided properties |
| `linkedin.id`, `linkedin.secret`, `linkedin.fields`, `linkedin.scope` | To define a `LinkedIn2Client` based on the provided properties |
| `foursquare.id`, `foursquare.secret` | To define a `FoursquareClient` based on the provided properties |
| `google.id`, `google.secret`, `google.scope` | To define a `Google2Client` based on the provided properties |
| `oauth2.id`, `oauth2.secret`, `oauth2.authUrl`, `oauth2.tokenUrl`, `oauth2.profileUrl`, `oauth2.profilePath`, `oauth2.profileId`, `oauth2.scope`, `oauth2.withState`, `oauth2.clientAuthenticationMethod` |  To define a `GenericOAuth20Client` based on the provided properties |
{:.striped}

<p />

Notice that:

- you can define multiple clients of the same type by adding a number at the end of the properties: `cas.loginUrl.2`, `oidc.type.5`...

- the `.passwordEncoder` property must be set to the name of an already defined `PasswordEncoder` like `encoder.spring` or `encoder.shiro.3`

- the `.authenticator` property must be set to the name of an already defined `Authenticator` like `ldap` or `db.1` or the implicit values: `testUsernamePassword` or `testToken` (for test authenticators).
