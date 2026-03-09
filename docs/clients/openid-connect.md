---
layout: doc
title: OpenID Connect
---

*pac4j* allows you to login using the OpenID Connect protocol v1.0.

It has been tested with various OpenID Connect providers: Google, AzureAD, Okta, IdentityServer3 (and 4), MitreID, Keycloak 4.6...

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

### a) Indirect clients

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

### b) Direct clients

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


## 3) Federation

Since v6.4.0, pac4j supports the [OpenID Connect Federation v1.0](https://openid.net/specs/openid-federation-1_0.html).

### a) Federation endpoint

To enable the federation endpoint at the RP (application) level, you need to configure a set of private/public keys:
- either via a keystore (like for the SAML protocol):

```java
oidcConfig.getFederation().getKeystore().setKeystorePath("file:./metadata/oidcfede.keystore");
oidcConfig.getFederation().getKeystore().setKeystorePassword("changeit");
oidcConfig.getFederation().getKeystore().setPrivateKeyPassword("changeit");
```

You have several additional settings in the `KeystoreProperties`.

- or via a JWKS:

```java
oidcConfig.getFederation().getJwks().setJwksPath("file:./metadata/oidcfede.jwks");
oidcConfig.getFederation().getJwks().setKid("mykeyoidcfede26");
```

In both cases (keystore or JWKS), if it doesn't exist, it will be created (for a file setting).

`OidcFederationProperties` controls both the RP entity statement generated by the federation endpoint and the federation-based OP resolution.

Available properties are:

- `keystore` (`KeystoreProperties`): keystore-based signing material for the entity statement. By default, pac4j sets the certificate prefix to `oidcfede-signing-cert`, the certificate validity to 1 year, and uses a filesystem keystore generator.
- `jwks` (`JwksProperties`): JWKS-based signing material for the entity statement. If both JWKS and keystore are configured, JWKS is used first.
- `entityConfigurationGenerator` (`EntityConfigurationGenerator`): component used by the endpoint to generate the entity configuration. If not set, `OidcClient` assigns `DefaultEntityConfigurationGenerator`.
- `validityInDays` (`int`, default: `90`): validity duration of the generated entity statement (`exp - iat`).
- `entityId` (`String`, optional): entity identifier used as `iss`, `sub`, and `aud` in the generated statement. If not set, the callback URL is used.
- `applicationType` (`String`, default: `web`): value of the `application_type` RP metadata claim (`web` or `native`).
- `responseTypes` (`List<String>`, default: `["code"]`): value of the `response_types` RP metadata claim.
- `grantTypes` (`List<String>`, default: `["authorization_code"]`): value of the `grant_types` RP metadata claim.
- `scopes` (`List<String>`, default: `["openid", "email", "profile"]`): value of the `scope` RP metadata claim (serialized as a space-separated string).
- `clientName` (`String`, optional): value of the `client_name` RP metadata claim.
- `contacts` (`List<String>`, default: empty list): value of the `contacts` RP metadata claim when at least one contact is provided.
- `trustAnchors` (`List<OidcTrustAnchorProperties>`, default: empty list): trust anchors used to resolve trust chains (`taIssuer` and `taJwksUrl` for each anchor).
- `targetIssuer` (`String`): OP entity identifier to resolve via federation. When set, federation mode is used instead of discovery URI resolution.

At least one signing source must be configured with a resource/path (`jwks` or `keystore`) to generate the entity configuration.

You must use the `EntityConfigurationGenerator` component to retrieve the entity configuration:

**Spring Boot example**:

```java
    @RequestMapping(value = "/.well-known/openid-federation", produces = DefaultEntityConfigurationGenerator.CONTENT_TYPE)
    @ResponseBody
    public String oidcFederation() throws HttpAction {
        val oidcClient = (OidcClient) config.getClients().findClient("OidcClient").get();
        return oidcClient.getConfiguration().getFederation().getEntityConfigurationGenerator().generate();
    }
```


### b) Using trust anchors

When using federation, you must not define the `discoveryURI`. You must only define the trust anchors and the target entity (the OP) in the federation space.

```java
val federation = oidcConfig.getFederation();

federation.setTargetIssuer("http://localhost:8080/op");

val trust = new OidcTrustAnchorProperties();
trust.setTaIssuer("http://localhost:8081/ta");
trust.setTaJwksUrl("http://localhost:8081/ta/jwks.json");
federation.getTrustAnchors().add(trust);
```

The federation metadata resolver performs a blocking load on first use, then refreshes metadata in the background when the trust chain expires.


### c) Explicit / automatic client registration

If the RP is not yet registered and its `clientId` is left blank, pac4j supports both client registration modes and validates them against the OP metadata.

Priority is given to **automatic mode** if supported by the OP. In this case, the **entity statement** is sent via the `client_assertion` parameter in the authorization request URL.

Otherwise, if only **explicit mode** is supported and the `federation_registration_endpoint` exists, pac4j calls it to retrieve a `client_id` (and `client_secret`).

This information is displayed in the logs as follows: `/!\ Explicit registration of the client 'http://rp' returned XXX. This information will not be repeated. You MUST add this value to your configuration before the next application startup!`. These values must be manually updated in the configuration.


## 4) Advanced configuration

### a) Client authentication method

You can define how the client credentials (`clientId` and `secret`) are passed to the token endpoint with the `setClientAuthenticationMethod` method:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC); // or CLIENT_SECRET_POST
```

You can use the `CLIENT_SECRET_JWT` authentication method by providing the `ClientSecretJwtClientAuthnMethodConfig` component:

```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);

val clientSecretJwtConfig = new ClientSecretJwtClientAuthnMethodConfig(new URI("http://audience"), JWSAlgorithm.HS256);
oidcConfiguration.setClientSecretJwtClientAuthnMethodConfig(clientSecretJwtConfig);
```

You can also use the `PRIVATE_KEY_JWT` authentication method by providing the `PrivateKeyJWTClientAuthnMethodConfig` component:

**Example 1:**
```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);

val privateKey = org.jasig.cas.client.util.PrivateKeyUtils.createKey("private-key.pem", "RSA");
val privateKeyJwtConfig = new PrivateKeyJWTClientAuthnMethodConfig(JWSAlgorithm.RS256, privateKey, "12345");
oidcConfiguration.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

**Example 2:**
```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);

val jwksProperties = new JwksProperties();
jwksProperties.setJwksPath("classpath:/static/op/keystore.jwks");
jwksProperties.setKid("cas-qGcosGMN");
val signingKey = JwkHelper.loadJwkFromOrCreateJwks(jwksProperties);

val privateKeyJwtConfig = new PrivateKeyJWTClientAuthnMethodConfig(JWSAlgorithm.RS256, ((RSAKey) signingKey).toKeyPair().getPrivate(), "12345");
oidcConfiguration.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

Since version 6.3.2, the privateKeyJWT is recreated when expired, and expiration can be tuned :
```java
    /** Default JWT token expiration time in seconds */
    privateKeyJwtConfig.setValidity(60);
    /** Clock skew used to not reuse a token to close to expire */
    privateKeyJwtConfig.setKeyClockSkew(10);
```
PrivateKeyJWT expiration mechanism can be disabled by setting :
```java
    privateKeyJwtConfig.setUseExpiration(false);
```

Notice that you can define a set of client authentication methods instead of just one via the `setSupportedClientAuthenticationMethods` method.


### b) Other settings

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

Custom `state` values may be defined in the configuration using the below method:

```java
config.setWithState(true);
config.setStateData("custom-state-value");
```

By default, the local session expires when the access token does, but this can be disabled using:

```java
config.setExpireSessionWithToken(false);
```

The additional param `TokenExpirationAdvance` allows to set the time in seconds, previous to the token expiration, in which the expiration is advanced. By default it is `0` seconds.

```java
config.setTokenExpirationAdvance(10);
```

You can disable the call to the user info endpoint using:

```java
config.setCallUserInfoEndpoint(false);
```

To reinforce security, the `none` alogithm for ID tokens (meaning no signature validation) must be explicitly accepted by using:

```java
config.setAllowUnsignedIdTokens(true);
```

Since version 6.0.5 and to reinforce security, the logout requests are validated. This can be disabled using:

```java
config.setLogoutValidation(false);
```

Since version 6.2.2, the nonce for idToken can be ignored on refresh. This can be ignored using:

```java
config.setUseNonceOnRefresh(false);
```
