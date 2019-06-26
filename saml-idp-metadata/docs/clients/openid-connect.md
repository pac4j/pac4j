---
layout: doc
title: OpenID Connect
---

*pac4j* allows you to login using the OpenID Connect protocol v1.0.

It has been tested with various OpenID Connect providers: Google, AzureAD, Okta, IdentityServer3 (and 4), MitreID, Keycloak…

## 1) Dependency

You need to use the following module: `pac4j-oidc`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) Clients

For any OpenID Connect identity provider, you should use the generic [OidcClient](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/OidcClient.java) (or one of its subclasses) and the [`OidcConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/config/OidcConfiguration.java) to define the appropriate configuration.

Before *pac4j* v1.9.2, the configuration was directly set at the client level.

**Example**:

```java
OidcConfiguration config = new OidcConfiguration();
config.setClientId(clientId);
config.setSecret(secret);
config.setDiscoveryURI(discoveryUri);
OidcClient oidcClient = new OidcClient(config);
```

In some cases (when the discovery url is already known for example), you can use a specific client like for [Google](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/GoogleOidcClient.java),
[Azure Active Directory](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AzureAdClient.java) or [Keycloak](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/KeycloakOidcClient.java).

**Example**:

```java
OidcConfiguration configuration = new OidcConfiguration();
configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
configuration.setDiscoveryURI("https://login.microsoftonline.com/38c46e5a-21f0-46e5-940d-3ca06fd1a330/.well-known/openid-configuration");
AzureAdClient client = new AzureAdClient(configuration);
```

The `clientId` and `secret` will be provided by the OpenID Connect provider, as well as the `discoveryUri` (to read the metadata of the identity provider). If you do not define the `discoveryUri`, you'll need to provide the provider metadata via the `setProviderMetadata` method.

An [`OidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/OidcProfile.java) is returned after a successful authentication (or one of its subclasses: [`AzureAdProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/azuread/AzureAdProfile.java), [`GoogleOidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/google/GoogleOidcProfile.java)
or  [`KeycloakOidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/keycloak/KeycloakOidcProfile.java)). All the attributes returned in the ID Token will be available in the `OidcProfile` even if you can get the ID token directly via the `getIdToken()` method.

You can define the flow you want to use via the `setResponseType` and `setResponseMode` methods:

```java
// implicit flow
config.setResponseType("id_token");
config.setResponseMode("form_post");
```

By default, the `response_type` is set to `code` (the authorization code flow) and the `response_mode` is empty.

You can define the scope to use with the `setScope` method:

```java
config.setScope("openid email profile phone");
```

You can request to use the `nonce` parameter to reinforce security via:

```java
config.setUseNonce(true);
```

## 3) Advanced configuration

You can define how the client credentials (`clientId` and `secret`)  are passed to the token endpoint with the `setClientAuthenticationMethod` method:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
```

When validating the IDToken in the login process, you can set a clock skew:

```java
// 1 minute
config.setMaxClockSkew(60);
```

You can also choose your preferred algorithm to sign the JSON web tokens:

```java
config.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
```

You can finally set additional parameters by using the `addCustomParam(String key, String value)` method:

```java
// select display mode: page, popup, touch, and wap
config.addCustomParam("display", "popup");
// select prompt mode: none, consent, select_account
config.addCustomParam("prompt", "none");
```
