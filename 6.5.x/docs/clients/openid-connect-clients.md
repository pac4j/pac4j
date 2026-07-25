---
layout: doc
title: OpenID Connect / Clients
---

See also:

<p> &nbsp; &#9656; <a href="openid-connect-config.html">Advanced configuration settings</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-federation.html">OIDC federation support</a></p>

<hr/>

The following OIDC clients can be configured:


## 1) Indirect clients

For any OpenID Connect identity provider, you should use the generic [OidcClient](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/OidcClient.java) (or one of its subclasses).
It is an indirect client for web browser based authentication.
The configuration is defined via the [`OidcConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/config/OidcConfiguration.java) component.

**Example**:

```java
OidcConfiguration config = new OidcConfiguration();
config.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
config.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
config.setDiscoveryURI("https://login.microsoftonline.com/38c4650d-3ca06fd1a330/.well-known/openid-configuration");
OidcClient oidcClient = new OidcClient(c);
```

In some cases (when the discovery url is already known for example), you can use a specific client like for [Google](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/GoogleOidcClient.java),
[Azure Active Directory](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AzureAd2Client.java), [Keycloak](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/KeycloakOidcClient.java)
or [Apple](https://github.com/pac4j/pac4j/blob/master/pac4j-oidc/src/main/java/org/pac4j/oidc/client/AppleClient.java).

**Example**:

```java
String tenant = "38c46e5a-21f0-46e5-940d-3ca06fd1a330";
AzureAd2OidcConfiguration configuration = new AzureAd2OidcConfiguration(tenant);
configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
AzureAd2Client client = new AzureAd2Client(configuration);
```

The `clientId` and `secret` will be provided by the OpenID Connect provider, as well as the `discoveryUri` (to read the metadata of the identity provider). If you do not define the `discoveryUri`, you'll need to provide the provider metadata by using the `StaticOidcOpMetadataResolver` component.

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

## 2) Direct clients

For direct clients (web services), you can get the `access token` from any OpenID Connect identity provider and use that in your request to get the user profile.

For that, the [HeaderClient](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/HeaderClient.java) would be appropriate, along with the `oidcClient.getProfileCreator()`.

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
