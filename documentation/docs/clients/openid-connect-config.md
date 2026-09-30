---
layout: doc
title: OpenID Connect / Advanced configuration
seo_title: "Advanced OIDC configuration for Java clients | pac4j"
description: "Configure OpenID Connect client authentication, state, nonce, signing algorithms, tokens and key rotation with pac4j-oidc."
---

See also:

<p> &nbsp; &#9656; <a href="openid-connect.html">OpenID Connect client for Java</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-clients.html">Basic configuration and OIDC clients</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-federation.html">OIDC federation support</a></p>

<hr/>

Once the client can log in, you may need to adapt it to your provider: how it authenticates at the token endpoint, which signing algorithms it accepts, or how long the user profile stays valid. These settings belong to `OidcConfiguration`.


## 1) Client authentication method

### a) `client_secret_basic` / `client_secret_post`

At the token endpoint, the client authenticates with its `clientId` and `secret`. Choose the method your provider expects with `setClientAuthenticationMethod`: `client_secret_basic` sends them in the HTTP Authorization header, while `client_secret_post` sends them in the request body.

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC); // or CLIENT_SECRET_POST
```


### b) `client_secret_jwt`

For `client_secret_jwt`, configure `ClientSecretJwtClientAuthnMethodConfig` with the audience and signing algorithm:

```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);

val clientSecretJwtConfig = new ClientSecretJwtClientAuthnMethodConfig(new URI("http://audience"), JWSAlgorithm.HS256);
oidcConfiguration.setClientSecretJwtClientAuthnMethodConfig(clientSecretJwtConfig);
```


### c) `private_key_jwt`

For `private_key_jwt`, the client signs a JWT with its private key. Older configurations use `PrivateKeyJWTClientAuthnMethodConfig`:

**Example:**
```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);

val jwksProperties = new JwksProperties();
jwksProperties.setJwksPath("classpath:/static/op/keystore.jwks");
jwksProperties.setKid("cas-qGcosGMN");
val signingKey = JwkHelper.loadJwkFromOrCreateJwks(jwksProperties);

val privateKeyJwtConfig = new PrivateKeyJWTClientAuthnMethodConfig(JWSAlgorithm.RS256, ((RSAKey) signingKey).toKeyPair().getPrivate(), "12345");
oidcConfiguration.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

Since v6.4.0, this component is deprecated in favor of `PrivateKeyJwtClientAuthnMethodConfig` (notice `Jwt` instead of `JWT`) which is mandatory for federation.

The newer component loads its signing key from a JWKS, which can be created on the fly:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
val privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(new JwksProperties());
privateKeyJwtConfig.getJwks().setJwksPath("file:./metadata/clientauthprivatekeyjwt.jwks");
privateKeyJwtConfig.getJwks().setKid("myprivatekeyjwt");
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

You can also reuse the relying party (RP) JWKS if that is where you keep the application's signing key:

```java
val rpJwks = config.getRpJwks();
rpJwks.setJwksPath("file:./metadata/rpjwks.jwks");
rpJwks.setKid("defaultjwks0326");
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
val privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

Since v6.3.2, pac4j recreates the client authentication JWT when it expires. Set its lifetime and how close to expiration it can be reused:
```java
    /** Default JWT token expiration time in seconds */
    privateKeyJwtConfig.setValidity(60);
    /** Clock skew used to not reuse a token to close to expire */
    privateKeyJwtConfig.setKeyClockSkew(10);
```

To disable this expiration mechanism:

```java
    privateKeyJwtConfig.setUseExpiration(false);
```


If your configuration supports several client authentication methods, supply them with `setSupportedClientAuthenticationMethods`.


## 2) State/Nonce

To supply a custom `state` value:

```java
config.setWithState(true);
config.setStateData("custom-state-value");
```

If the provider does not return the original `nonce` when refreshing an ID token, you can disable that check for refreshes:

```java
config.setUseNonceOnRefresh(false);
```


## 3) Algorithms

You can choose the algorithm (matched against the OP metadata) to verify the ID token signatures:

```java
config.setIdTokenSigningAlgorithm(JWSAlgorithm.RS256);
```

This replaces the deprecated `setPreferredJwsAlgorithm` method.

You can also choose the algorithm (matched against the OP metadata) to sign the request objects:

```java
config.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);
```

The request object is signed with the key from the RP JWKS (`config.getRpJwks()`), so configure that key as well as the algorithm.


## 4) Tokens

The `OidcProfile` expiration is driven by the access token. When the access token is received, its lifetime is extracted:
either from the `expires_in` field returned by the token endpoint, or, if not present, from the `exp` claim of the access token JWT itself.

This value is used to set the profile's expiration date. As a consequence, `profile.isExpired()` returns `true` when the access token has expired,
which in turn triggers the refresh token flow (if a refresh token is available) to silently obtain a new access token and keep the user's session alive.

By default, the local session expires when the access token does, but this can be disabled using:

```java
config.setExpireSessionWithToken(false);
```

To treat the token as expired a little earlier, set `tokenExpirationAdvance` in seconds. The default is `0`; this example advances expiration by 10 seconds:

```java
config.setTokenExpirationAdvance(10);
```

When validating an ID token at login, allow for a difference between the provider's clock and your application's clock with:

```java
// 1 minute
config.setMaxClockSkew(60);
```

Unsigned ID tokens (`alg=none`) are refused unless you explicitly allow them. The following setting accepts them without signature validation:

```java
config.setAllowUnsignedIdTokens(true);
```


## 5) Other settings

For provider parameters without a dedicated setter, use `addCustomParam(String key, String value)`:

```java
// select display mode: page, popup, touch, and wap
config.addCustomParam("display", "popup");
// select prompt mode: none, consent, select_account
config.addCustomParam("prompt", "none");
```

You can disable the call to the user info endpoint using:

```java
config.setCallUserInfoEndpoint(false);
```

Logout requests are validated by default. The following setting disables that validation:

```java
config.setLogoutValidation(false);
```

To use Pushed Authorization Requests (PAR):

```java
config.setPushedAuthorizationRequest(true);
```

To rename claims, supply a mapping from each source claim to its destination name:

```java
config.setMappedClaims(mapping);
```


## 6) Keys rotation

The federation keys are reloaded when the entity statement expires. The generic RP keys used to sign request objects, and the keys used for `private_key_jwt`, are only loaded at startup: changing those files alone does not reload them.
