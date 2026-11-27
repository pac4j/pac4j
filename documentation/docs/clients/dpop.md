---
layout: doc
title: DPoP
seo_title: "DPoP (RFC 9449) for OAuth and OpenID Connect in Java | pac4j"
description: "Enable OAuth 2.0 Demonstrating Proof of Possession (DPoP) with pac4j: bind the access tokens of your Java OAuth and OIDC clients to a key pair and protect your API with DPoP-bound access tokens."
---

DPoP ([RFC 9449](https://www.rfc-editor.org/rfc/rfc9449.html), *OAuth 2.0 Demonstrating Proof of Possession*) binds the tokens to a key pair held by the client,
so that a stolen token cannot be replayed by someone who does not hold the private key.

- The client generates a **key pair** and keeps the private key.
- With each request, it sends a **DPoP proof** in the `DPoP` header: a short-lived JWT, signed by the private key, carrying the public key, the HTTP method and the URL of the request and, when an access token is sent, its hash.
- The server **binds the tokens** it issues to the public key of the proof, and only accepts an access token presented, with the `DPoP` authorization scheme, along with a proof signed by the matching private key.

## 1) Dependency

You need to use the following module: `pac4j-dpop`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-dpop</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

The DPoP support relies on the Nimbus [JOSE + JWT](https://connect2id.com/products/nimbus-jose-jwt) and [OAuth 2.0 SDK with OpenID Connect extensions](https://connect2id.com/products/nimbus-oauth-openid-connect-sdk) libraries,
which this module brings, even for the OAuth clients.

## 2) Client support

To enable DPoP, add the [`DPoPHttpInterceptor`](https://github.com/pac4j/pac4j/blob/master/pac4j-dpop/src/main/java/org/pac4j/dpop/http/DPoPHttpInterceptor.java) to the OAuth or OIDC configuration.

**Example (OpenID Connect):**

```java
OidcConfiguration config = new OidcConfiguration();
config.setHttpInterceptor(new DPoPHttpInterceptor());
```

In the OIDC authorization code flow, **pac4j signs two distinct DPoP proofs with the same session key** when it exchanges the code and then calls UserInfo. The provider's discovery metadata supplies the endpoint URLs.

1. **Token endpoint** (e.g. `POST /token`). pac4j sends the first proof in the `DPoP` header. Its `htm` and `htu` identify the POST and the token endpoint. It has no `ath` claim because pac4j is still requesting the access token.
2. **UserInfo endpoint** (e.g. `GET /userinfo`). pac4j presents the bound token in `Authorization: DPoP <access token>` and signs a new proof for this GET and its target URI. This proof has its own `jti` and includes `ath`, the hash of the access token.

**Example (OAuth):**

```java
OAuth20Configuration config = new OAuth20Configuration();
config.setHttpInterceptor(new DPoPHttpInterceptor());
```

The same interceptor handles both protocols: it adds a DPoP proof to every request sent to the [OAuth server](oauth.html#4-http-client-and-requests)
or to the [OpenID provider](openid-connect-config.html) (pushed authorization, token and UserInfo requests).

The authorization scheme follows the token type returned by the server: `DPoP` for a DPoP-bound access token,
or `Bearer` if the server issues an unbound access token. Adding a proof does not turn a Bearer token into a DPoP-bound token.

The proofs are signed by a key pair saved in the web session and generated on first use: the tokens are bound to it, and the following requests
(UserInfo, refresh) prove its possession. A web session is thus required: without a web context or a session store, the interceptor fails.
One key pair is kept per signature algorithm in the session, so clients using different algorithms do not overwrite each other's keys.

The signature algorithm is `ES256` by default and can be changed, for `ES384`, `ES512`, `RS256`, `RS384`, `RS512`, `PS256`, `PS384` or `PS512`:

```java
config.setHttpInterceptor(new DPoPHttpInterceptor().setJwsAlgorithm(JWSAlgorithm.PS256));
```

A server may require its own nonce in the proofs. In that case, it refuses the request with the `use_dpop_nonce` error and provides the nonce in the `DPoP-Nonce` header of its response:
the interceptor saves this nonce in the web session and the request is sent again, once, with a new proof carrying it.

The nonce is then added to all the proofs sent to this server, and replaced whenever the server provides a new one, in any response.
One nonce is kept per server, as the authorization server and the resource server (UserInfo or profile endpoint) each provide their own.

The proofs sent and the nonces received are logged raw on the `PROTOCOL_MESSAGE.DPOP` logger, at DEBUG level. The private key is never logged:

```properties
logging.level.PROTOCOL_MESSAGE.DPOP=DEBUG
```

A proof is logged as the JWT sent. To read its header and its claims, decoded, enable the `TRACE` level on the interceptor:

```properties
logging.level.org.pac4j.dpop.http.DPoPHttpInterceptor=TRACE
```

The requests and the responses carrying them are logged by the `PROTOCOL_MESSAGE.OAUTH` and `PROTOCOL_MESSAGE.OIDC` loggers (a request sent again is logged twice).
The protocol logger writes `Resending HTTP request` at DEBUG just before the retry.
The `PROTOCOL_MESSAGE.DPOP` logger is a child of `PROTOCOL_MESSAGE`, which enables the messages of all protocols at once.
Messages are logged as they travel: enable these logs to diagnose, not permanently in production.

## 3) Server support

To protect your web resources (REST API) with DPoP-bound access tokens, use the [`DirectDPoPAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-dpop/src/main/java/org/pac4j/dpop/client/direct/DirectDPoPAuthClient.java).

It is a direct client, similar to the [`DirectBearerAuthClient`](http.html), for an access token sent in the `Authorization` header with the `DPoP` scheme, along with a proof in the `DPoP` header:

```
GET /protectedresource HTTP/1.1
Authorization: DPoP <access token>
DPoP: <proof>
```

**Example:**

```java
JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
jwtAuthenticator.addSignatureConfiguration(new ECSignatureConfiguration(authorizationServerKeyPair));

DirectDPoPAuthClient client = new DirectDPoPAuthClient(jwtAuthenticator);
```

The authentication is done in two steps:

- the [`DPoPAuthExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-dpop/src/main/java/org/pac4j/dpop/credentials/extractor/DPoPAuthExtractor.java) checks the proof and only extracts the access token (as `TokenCredentials`) when the proof is valid
- the authenticator you have defined validates the access token, like for a Bearer token: a [`JwtAuthenticator`](../authenticators/jwt.html) for example.

When a resource accepts both schemes, `DirectBearerAuthClient` rejects a DPoP-bound access token presented as Bearer,
whether the request includes a `DPoP` proof header or omits it. `JwtAuthenticator` reads the `cnf` claim after signature
verification and any configured decryption. A custom authenticator or profile creator that validates opaque tokens by
introspection must call `tokenCredentials.setTokenConfirmation(introspectionResponse.get("cnf"))`
before returning credentials or a profile. The same applies to a custom validator that decrypts access tokens.

### a) Proof validation

The proof is checked according to the [section 4.3 of the RFC](https://www.rfc-editor.org/rfc/rfc9449.html#section-4.3), by the Nimbus SDK:

- its **type** (`dpop+jwt`) and its **signature**, verified with the public key it carries, for an accepted algorithm
- its **claims**: the HTTP method (`htm`) and the URL (`htu`) of the request, without its query string, and its creation time (`iat`)
- the **hash of the access token** (`ath`), which must be the one of the access token sent
- the **key the access token is bound to** (`jkt`), which must be the one of the proof
- its **single use**: a proof already used is refused.

The request URL is the one seen by the application (`WebContext.getRequestURL()`): behind a reverse proxy, make sure that your framework restores the URL called by the client.

The key the access token is bound to is read from the `jkt` member of the `cnf` claim of a JWT access token.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> The <code>cnf</code> claim is read before the access token is validated: the binding only holds if your authenticator checks the signature of the access token.</div>

For the access tokens which are not JWTs, override the `retrieveConfirmation` method of the `DPoPAuthExtractor` to return the key thumbprint provided by the introspection endpoint of the authorization server.

The `DPoPAuthExtractor` can be customized and set on the client:

```java
DPoPAuthExtractor extractor = new DPoPAuthExtractor()
    .setJwsAlgorithms(Set.of(JWSAlgorithm.ES256, JWSAlgorithm.PS256))
    .setMaxAgeSeconds(120);

client.setCredentialsExtractor(extractor);
```

| Property | Default | Description |
|----------|---------|-------------|
| `jwsAlgorithms` | `ES256`, `ES384`, `ES512`, `PS256`, `PS384`, `PS512`, `RS256`, `RS384`, `RS512` | The algorithms accepted for the signature of the proofs |
| `maxAgeSeconds` | `60` | The maximum age of a proof, from its `iat` claim. It cannot be less than `clockSkewSeconds` |
| `clockSkewSeconds` | `60` | The tolerance for a proof whose `iat` claim is in the future |
| `store` | a `ConcurrentMapStore` | Where the identifiers of the proofs are saved, to refuse a proof already used |
| `nonceSecret` | `null` | The secret generating the nonces: when defined, a nonce is required in the proofs |
| `nonceMaxAgeSeconds` | `300` | The maximum age of a nonce |
{:.table-striped}

### b) Replay protection

To refuse a proof already used, a hash of its identifier (`jti` claim) is saved in a [`Store`](../store.html), per key, for the time window in which the proof would be accepted.

By default, it is a `ConcurrentMapStore` keeping the identifiers for `maxAgeSeconds` + `clockSkewSeconds`: it lives in the memory of the JVM.
If your application runs on several nodes, define a store shared between them:

```java
extractor.setStore(mySharedStore);
```

The `Store` has no atomic "set if absent" operation: with a shared store, the same proof sent to two nodes at the very same time may be accepted twice.

### c) Nonce

A nonce can be required in the proofs to limit their lifetime by the time at the server, whatever the clock of the client. It is enabled by defining a secret:

```java
extractor.setNonceSecret("a-long-random-secret-shared-by-all-the-nodes");
```

A request whose proof carries no nonce, or an invalid or expired one, is refused with the `use_dpop_nonce` error and a new nonce in the `DPoP-Nonce` header of the response:
the client sends its request again with a proof carrying this nonce, and uses it for the following requests until it expires (after `nonceMaxAgeSeconds`).

To spare this extra round trip to an active client, a nonce which has passed half of its maximum age is renewed in advance: the response to the accepted request provides a new nonce in its `DPoP-Nonce` header,
which the client uses for its next requests. The `DPoPHttpInterceptor` handles both cases.

The nonce is stateless: `base64url(timestamp + "." + base64url(HMAC-SHA256(secret, timestamp)))`, the timestamp being the time at the server, in seconds.
Nothing is stored and all the nodes sharing the secret accept the nonces of each other.

Without a secret, no nonce is required and a nonce carried by a proof is ignored.

### d) Challenge

When the authentication fails, the `WWW-Authenticate` header of the response carries a `DPoP` challenge with the realm (`realmName` property of the client), the accepted algorithms and, when credentials were sent, the error:

```
WWW-Authenticate: DPoP realm="authentication required", error="invalid_dpop_proof", algs="ES256 ES384 ES512 PS256 PS384 PS512 RS256 RS384 RS512"
```

| Error | Reason |
|-------|--------|
| `invalid_dpop_proof` | The proof is missing, malformed, expired, already used or does not match the request |
| `invalid_token` | The access token is not bound to the key of the proof or is refused by the authenticator |
| `use_dpop_nonce` | A nonce is required: it is provided in the `DPoP-Nonce` header |
{:.table-striped}

The client logs the reason for a refusal at INFO level. At DEBUG level, the `PROTOCOL_MESSAGE.DPOP` logger records the HTTP method and URL for incoming proofs and the nonce values sent in responses.
At DEBUG level, `org.pac4j.dpop.credentials.extractor.DPoPAuthExtractor` logs the HTTP method and signature algorithm after it validates the proof, before the authenticator validates the access token.
