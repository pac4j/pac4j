---
layout: doc
title: Security configuration&#58;
---

## 1) The `Config` component

In most `pac4j` implementations, the security configuration can be defined via a [`Config`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java) object.

It gathers the required:

- [Clients](clients.html)
- [Authorizers](authorizers.html)
- [Matchers](matchers.html)

**Example:**

```java
FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(salt));

Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient, parameterClient);

config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
config.addAuthorizer("custom", new CustomAuthorizer());

config.addMatcher("excludedPath", new ExcludedPathMatcher("^/facebook/notprotected\\.jsp$"));
```

You can also use an intermediate [`Clients`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Clients.java) object to build the `Config` one.

**Example:**

```java
Clients clients = new Clients("http://localhost:8080/callback", facebookClient, twitterClient, parameterClient);

Config config = new Config(clients);
```

In that case, you can define for **all** clients:

- a [`CallbackUrlResolver`](clients.html#the-callback-url): `clients.setCallbackUrlResolver(callbackUrlResolver);`
- a [default client](clients.html#the-callback-url): `clients.setDefaultClient(facebookClient);`
- an [`AjaxRequestResolver`](clients.html#ajax-requests): `clients.setAjaxRequestResolver(ajaxRequestResolver);`
- an [`AuthorizationGenerator`](clients.html#compute-roles-and-permissions): `clients.addAuthorizationGenerator(authorizationGenerator);`

## 2) The `pac4j-config` module

The `pac4j-config` module gathers all the *pac4j* facilities to define this `Config` object.
Currently, there is only one component which allows you to build the clients from a set of properties: the [`PropertiesConfigFactory`](https://github.com/pac4j/pac4j/blob/master/pac4j-config/src/main/java/org/pac4j/config/client/PropertiesConfigFactory.java).
It is used by Dropwizard, CAS and Knox.

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
    ldap.useSsl: false
    ldap.dnFormat: uid=%s,ou=Users,o=58e69adc0914b437324e7632,dc=jumpcloud,dc=com
    ldap.usersDn: ou=Users,o=58e69adc0914b437324e7632,dc=jumpcloud,dc=com
    ldap.principalAttributeId: uid
    ldap.principalAttributes: firstName,lastName
    ldap.enhanceWithEntryResolver: false
    formClient.loginUrl: /login.html
    formClient.authenticator: ldap
```

Here are the properties you can use to define the clients (and authenticators):

| Property names | Usage |
|---------------|--------
| `anonymous` | To define the `AnonymousClient`, the value is ignored |
| `saml.keystorePassword`, `saml.privateKeyPassword`, `saml.keystorePath`, `saml.identityProviderMetadataPath`, `saml.maximumAuthenticationLifetime`, `saml.serviceProviderEntityId`, `saml.serviceProviderMetadataPath`, `saml.destinationBindingType` | To define a `SAML2Client` |
| `cas.loginUrl`, `cas.protocol` | To define a `CasClient` |
| `oidc.type` (`google` or `azure`), `oidc.id`, `oidc.secret`, `oidc.scope`, `oidc.discoveryUri`, `oidc.useNonce`, `oidc.preferredJwsAlgorithm`, `oidc.maxClockSkew`, `oidc.clientAuthenticationMethod`, `oidc.customParamKey1`, `oidc.customParamValue1`, `oidc.customParamKey2`,`oidc.customParamValue2` | To define an OpenID connect client |
| `formClient.authenticator`, `formClient.loginUrl`, `formClient.usernameParameter` `formClient.passwordParameter` | To define a `FormClient`|
| `indirectBasicAuth.authenticator`, `indirectBasicAuth.realName` | To define an `IndirectBasicAuthClient`|
| `ldap.type`, `ldap.dnFormat`, `ldap.principalAttributes`,`ldap.principalAttributeId`, `ldap.principalAttributePassword`, `ldap.subtreeSearch`, `ldap.usersDn`, `ldap.userFilter`, `ldap.enhanceWithEntryResolver`, `ldap.trustCertificates`, `ldap.keystore`, `ldap.keystorePassword`, `ldap.keystoreType`, `ldap.minPoolSize`, `ldap.maxPoolSize`, `ldap.poolPassivator`, `ldap.validateOnCheckout`, `ldap.validatePeriodically`, `ldap.validatePeriod`, `ldap.failFast`, `ldap.idleTime`, `ldap.prunePeriod`, `ldap.blockWaitTime`, `ldap.url`, `ldap.useSsl`, `ldap.useStartTls`, `ldap.connectTimeout`, `ldap.providerClass`, `ldap.allowMultipleDns`, `ldap.bindDn`, `ldap.bindCredential`, `ldap.saslRealm`, `ldap.saslMechanism`, `ldap.saslAuthorizationId`, `ldap.saslSecurityStrength`, `ldap.saslQualityOfProtection` | To define a `LdapAuthenticator` |
| `facebook.id`, `facebook.secret`, `facebook.scope`, `facebook.fields` | To define a `FacebookClient` |
| `twitter.id`, `twitter.secret` | To define a `TwitterClient` |
| `github.id`, `github.secret` | To define a `GitHubClient` |
| `dropbox.id`, `dropbox.secret` | To define a `DropBoxClient` |
| `windowslive.id`, `windowslive.secret` | To define a `WindowsLiveClient` |
| `yahoo.id`, `yahoo.secret` | To define a `YahooClient` |
| `linkedin.id`, `linkedin.secret`, `linkedin.fields`, `linkedin.scope` | To define a `LinkedIn2Client` |
| `foursquare.id`, `foursquare.secret` | To define a `FoursquareClient` |
| `google.id`, `google.secret`, `google.scope` | To define a `Google2Client` |
{:.table-striped}

<p />

Notice that:

- you can define multiple clients of the same type by adding a number at the end of the properties: `facebook.id.5`, `facebook.secret.5`...

- the `.authenticator` property must be the name of an `Authenticator` like `ldap` or `ldap.1` or the implicit values: `testUsernamePassword` or `testToken`.
