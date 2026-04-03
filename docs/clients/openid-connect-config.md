---
layout: doc
title: OpenID Connect / Advanced configuration
---

See also:

<p> &nbsp; &#9656; <a href="openid-connect-clients.html">Basic configuration and OIDC clients</a></p>
<p> &nbsp; &#9656; <a href="openid-connect-federation.html">OIDC federation support</a></p>

<hr/>

The advanced configuration options are available:


## 1) Client authentication method

### a) `client_secret_basic` / `client_secret_post`

You can define how the client credentials (`clientId` and `secret`) are passed to the token endpoint with the `setClientAuthenticationMethod` method:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC); // or CLIENT_SECRET_POST
```


### b) `client_secret_jwt`

You can use the `CLIENT_SECRET_JWT` authentication method by providing the `ClientSecretJwtClientAuthnMethodConfig` component:

```java
oidcConfiguration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);

val clientSecretJwtConfig = new ClientSecretJwtClientAuthnMethodConfig(new URI("http://audience"), JWSAlgorithm.HS256);
oidcConfiguration.setClientSecretJwtClientAuthnMethodConfig(clientSecretJwtConfig);
```


### c) `private_key_jwt`

You can also use the `PRIVATE_KEY_JWT` authentication method by providing the `PrivateKeyJWTClientAuthnMethodConfig` component:

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

It uses a JWKS built on the fly:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
val privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(new JwksProperties());
privateKeyJwtConfig.getJwks().setJwksPath("file:./metadata/clientauthprivatekeyjwt.jwks");
privateKeyJwtConfig.getJwks().setKid("myprivatekeyjwt");
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

Or it can use the default RP JWKS (this should be the right option in most cases):

```java
val rpJwks = config.getRpJwks();
rpJwks.setJwksPath("file:./metadata/rpjwks.jwks");
rpJwks.setKid("defaultjwks0326");
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
val privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
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


## 2) State/Nonce

Custom `state` values may be defined in the configuration using the below methods:

```java
config.setWithState(true);
config.setStateData("custom-state-value");
```

The `nonce` for ID tokens can be ignored on refresh. This can be done using:

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

The key used is the one stored in the RP JWKS (`config.getRpJwks`) so it must be defined.


## 4) Tokens

The `OidcProfile` expiration is driven by the access token. When the access token is received, its lifetime is extracted:
either from the `expires_in` field returned by the token endpoint, or, if not present, from the `exp` claim of the access token JWT itself.

This value is used to set the profile's expiration date. As a consequence, `profile.isExpired()` returns `true` when the access token has expired,
which in turn triggers the refresh token flow (if a refresh token is available) to silently obtain a new access token and keep the user's session alive.

By default, the local session expires when the access token does, but this can be disabled using:

```java
config.setExpireSessionWithToken(false);
```

The additional param `TokenExpirationAdvance` allows to set the time in seconds, previous to the token expiration, in which the expiration is advanced. By default it is `0` seconds.

```java
config.setTokenExpirationAdvance(10);
```

When validating the IDToken in the login process, you can set a clock skew:

```java
// 1 minute
config.setMaxClockSkew(60);
```

To reinforce security, the `none` alogithm for ID tokens (meaning no signature validation) must be explicitly accepted by using:

```java
config.setAllowUnsignedIdTokens(true);
```


## 5) Other settings

You can finally set additional parameters by using the `addCustomParam(String key, String value)` method:

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

For security, the logout requests are validated. This can be disabled using:

```java
config.setLogoutValidation(false);
```

You can enable the PAR mechanism via:

```java
config.setPushedAuthorizationRequest(true);
```
