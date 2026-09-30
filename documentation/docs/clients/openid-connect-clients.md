---
layout: doc
title: OpenID Connect / Clients
seo_title: "OIDC client configuration for Java applications | pac4j"
description: "Configure pac4j OIDC clients for browser login and direct authentication, including provider discovery and clients for common identity providers."
---

See also:

<p> &nbsp; &#9656; <a href="openid-connect.html">OpenID Connect client for Java</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-config.html">Advanced configuration settings</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-federation.html">OIDC federation support</a></p>

<hr/>

For a browser login, use an indirect client: it sends the user to the provider and handles the callback. If the caller already has an access token, the direct-client example below shows how to retrieve the user profile without a browser redirect.


## 1) Indirect clients

The generic [OidcClient](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/OidcClient.java) works with OpenID Connect providers. Give it an [`OidcConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/config/OidcConfiguration.java) with the credentials your provider issued and its discovery URL:

**Example**:

```java
OidcConfiguration config = new OidcConfiguration();
config.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
config.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
config.setDiscoveryURI("https://login.microsoftonline.com/38c4650d-3ca06fd1a330/.well-known/openid-configuration");
OidcClient oidcClient = new OidcClient(config);
```

Some providers have a dedicated client, which handles details such as the discovery URL for you: [Google](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/GoogleOidcClient.java),
[Microsoft Entra ID (Azure AD)](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AzureAd2Client.java), [Keycloak](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/KeycloakOidcClient.java)
or [Apple](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AppleClient.java).

**Example**:

```java
String tenant = "38c46e5a-21f0-46e5-940d-3ca06fd1a330";
AzureAd2OidcConfiguration configuration = new AzureAd2OidcConfiguration(tenant);
configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
AzureAd2Client client = new AzureAd2Client(configuration);
```

The provider gives you the `clientId` and `secret` when you register the application. Its discovery URL lets pac4j read the provider metadata. For a static configuration without discovery, supply that metadata through `StaticOidcOpMetadataResolver`.

An [`OidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/OidcProfile.java) is returned after a successful authentication (or one of its subclasses: [`AzureAdProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/azuread/AzureAdProfile.java), [`GoogleOidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/google/GoogleOidcProfile.java)
or [`KeycloakOidcProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/profile/keycloak/KeycloakOidcProfile.java)). The profile exposes the attributes from the ID token; call `getIdToken()` when you need the token itself.

The default is the authorization code flow (`response_type=code`), with no explicit `response_mode`. If your provider requires another flow or response mode, set them on the configuration. For example, for the implicit flow:

```java
// implicit flow
config.setResponseType("id_token");
config.setResponseMode("form_post");
```

Use `setScope` to request the information your application needs:

```java
config.setScope("openid email profile phone");
```

To send a `nonce` and check it against the returned ID token:

```java
config.setUseNonce(true);
```

## 2) Direct clients

Here, the caller already has an access token from the provider and sends it with the request. A [HeaderClient](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/HeaderClient.java) reads the token from the header; `oidcClient.getProfileCreator()` uses it to retrieve the user profile from the provider's UserInfo endpoint.

**Example**:

```java
OidcConfiguration config = new OidcConfiguration();
config.setClientId(clientId);
config.setSecret(secret);
config.setDiscoveryURI(discoveryUri);
OidcClient oidcClient = new OidcClient(config);
oidcClient.setCallbackUrl("notused");
oidcClient.init();
HeaderClient client = new HeaderClient("Authorization", "Bearer ", oidcClient.getProfileCreator());
```

The request to the server should have an `Authorization` header with the value as `Bearer {access token}`.
