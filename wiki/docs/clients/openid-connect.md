---
layout: doc
title: OpenID Connect
---

### Module: `pac4j-oidc`

For the OpenID Connect support, you should use the generic [OidcClient](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/OidcClient.java). In some cases (when the discovery url is already known for example), you can use a specific client like for [Google](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/GoogleOidcClient.java) or [Azure Active Directory](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AzureAdClient.java).

**Example**:

```java
OidcConfiguration oidcConfiguration = new OidcConfiguration();
oidcConfiguration.setClientId(clientId);
oidcConfiguration.setSecret(secret);
GoogleOidcClient googleOidcClient = new GoogleOidcClient(oidcConfiguration);
```

---

To integrate an application with an OpenID Connect Provider server, you should use the `OidcClient` (or one of its subclasses) and the [`OidcConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/config/OidcConfiguration.java):

```java
final OidcConfiguration config = new OidcConfiguration();
config.setClientId(clientId);
config.setSecret(secret);
config.setDiscoveryURI(discoveryUri);
final OidcClient oidcClient = new OidcClient(config);
```

The `clientId` and `secret` will be provided by the OpenID Connect provider, as well as the `discoveryUri` (to read the metadata of the identity provider).

An `OidcProfile` is returned after a successful authentication (or one of its subclasses: `AzureAdProfile` or `GoogleOidcProfile`). All the attributes returned in the ID Token will be available in the `OidcProfile` even if you can get the ID token directly via the `getIdToken()` method.

You can define the flow you want to use via the `setResponseType` and `setResponseMode` methods:

```java
// implicit flow
config.setResponseType("id_token");
config.setResponseMode("form_post");
```

By default, the `response_type` is set to `code` and the `response_mode` is empty.

You can define the scope to use with the `setScope` method:

```java
config.setScope("openid email profile phone");
```

You can request to use the `nonce` parameter to reinforce security via:

```java
config.setUseNonce(true);
```

### Additional configuration:

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
