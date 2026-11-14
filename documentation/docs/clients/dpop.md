---
layout: doc
title: DPoP
seo_title: "DPoP (RFC 9449) for OAuth and OpenID Connect in Java | pac4j"
description: "Enable OAuth 2.0 Demonstrating Proof of Possession (DPoP) with pac4j: bind the access tokens of your Java OAuth and OIDC clients to a key pair."
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

**Example (OAuth):**

```java
OAuth20Configuration config = new OAuth20Configuration();
config.setHttpInterceptor(new DPoPHttpInterceptor());
```

The same interceptor handles both protocols: it adds a DPoP proof to every request sent to the [OAuth server](oauth.html#4-http-client-and-requests)
or to the [OpenID provider](openid-connect-config.html) (pushed authorization, token and UserInfo requests).

The proofs are signed by a key pair saved in the web session and generated on first use: the tokens are bound to it, and the following requests
(UserInfo, refresh) prove its possession. A web session is thus required: without a web context or a session store, the interceptor fails.

The signature algorithm is `ES256` by default and can be changed, for an algorithm of the EC or RSA family:

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
The `PROTOCOL_MESSAGE.DPOP` logger is a child of `PROTOCOL_MESSAGE`, which enables the messages of all protocols at once.
Messages are logged as they travel: enable these logs to diagnose, not permanently in production.

## 3) Server support

The server side of DPoP (validating the proof of an incoming request and checking that the access token is bound to its key) is not available yet.
