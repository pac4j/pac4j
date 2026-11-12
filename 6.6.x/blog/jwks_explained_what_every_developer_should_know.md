---
layout: blog
title: JWKS explained&colon; what every developer should know
author: Jérôme LELEU
date: June 2026
tags: [guide]
seo_title: "What is JWKS? JSON Web Keys and JWT verification | pac4j"
description: "Understand JWK and JWKS meaning, JSON key format and their role in JWT signature verification, OpenID Connect and cryptographic key management."
---

When it comes to security, certificates have been used everywhere since the early days of the web.

While storing them in PEM/DER format has always been complicated, things have become much easier with the modern JWKS (J for JSON) format.

And you're probably already using JWKS without knowing it, every time you validate a JWT from Google, GitHub, or your identity provider.


## 1) A word about cryptography

We can use symmetric cryptography based on a secret.

As this secret must be shared by both parties, this is not generally a very convenient solution.

Or we can use asymmetric cryptography based on key pairs.

In that case, there are two keys: a public one and a private one.

Two mechanisms are available:
- the signature ensures that the sender is confirmed (the sender uses its private key to sign the message and the receiver can confirm that using the public key of the sender)
- the encryption protects the data itself (the sender uses the public key of the receiver to encrypt the data and only the receiver can read the data thanks to its private key).

Both mechanisms are complementary and serve different purposes.


## 2) In the past: SAML and XML

Back when SAML was the main protocol and XML very popular, you generated certificates using the `openssl` tool:

```shell
openssl req -x509 -newkey rsa:4096 -sha256 -days 365 -nodes -keyout private.key -out public.crt -subj "/CN=localhost"
```

It created two files:

- `private.key`:

```
-----BEGIN PRIVATE KEY-----
MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQDdx8R3Y1Eyh69R
O8iACpe6MWJAUgMadWPt1VW2XGkrkvSBn9hY866VBt8wkH1uFmOAvvjwx55Tvu1K
...TRUNCATED...
ySD6rqvGLLxGkZoUGyuHt9D7B/FaBAMvjjgOSMYbHxYj0ncQioaVSpcUZIpTrHRo
jA1drmXT/LHPGeQgp/CJQ3Zf7qqavA==
-----END PRIVATE KEY-----
```

- `public.crt`:

```
-----BEGIN CERTIFICATE-----
MIIFLjCCAxagAwIBAgIUOtBi9hdWAqh1sL8U7wS3ttXgg40wDQYJKoZIhvcNAQEL
BQAwITELMAkGA1UEBhMCRlIxEjAQBgNVBAMMCWxvY2FsaG9zdDAeFw0yNjA1MTgx
...TRUNCATED...
po1DwOR88q6xAws/qM1+PxigbFRh4E8zUeVVF0vED+VxeCG0AwKDYawPjw5/9qfJ
qC8ewt6SVZmmdtMg2MK8Tdmzv0W+ciiYO21CF45Pa6YZVA==
-----END CERTIFICATE-----
```

These raw contents were hard to manipulate.

You could even generate a keystore (for Java) using the `keytool` command line.


## 3) Modern ecosystem: OIDC and JSON

Today, the OIDC protocol has somehow supplanted the SAML protocol and JSON has truly replaced the XML format.

Everyone knows the JSON format:

```json
{
    "key1": "value1",
    "key2": "value2"
}
```

Most people also know that a JSON Web Token (aka JWT) is a signed and/or encrypted JSON message.

It comes as a string in three parts separated by dots, each part being base64 encoded: `part1.part2.part3`.

`part1` is the header, `part2` is the JSON itself (it can be encrypted) and `part3` is the signature (it may not be signed).

Let's take an example from `jwt.io`:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.
KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
```

We have three parts which decode to:
- a header: `{ "alg": "HS256", "typ": "JWT" }`
- a body: `{ "sub": "1234567890", "name": "John Doe", "admin": true, "iat": 1516239022 }`
- a signature.

And the encryption/signing of the JWTs is ensured by the public/private keys.


## 4) What does JWKS mean?

**JWKS stands for JSON Web Key Set**: a JSON document that lists cryptographic keys. Each key in the list is a **JWK**, a JSON Web Key, defined by [RFC 7517](https://www.rfc-editor.org/rfc/rfc7517).

The S stands for *Set*, not for the plural: a JWKS is a single JSON object with one property, `keys`, whose value is an array of JWKs.

In practice, a JWKS is how an identity provider publishes its **public keys**. Every OpenID Connect provider exposes one at a URL named `jwks_uri` in its discovery document, for example:

- Google: `https://www.googleapis.com/oauth2/v3/certs`
- Keycloak: `https://keycloak.example.com/realms/myrealm/protocol/openid-connect/certs`
- Microsoft Entra ID: `https://login.microsoftonline.com/common/discovery/v2.0/keys`

When your application receives an ID token or an access token from one of them, it downloads this JWKS, picks the right key and verifies the signature of the JWT. That is the whole point: JWKS replaces the PEM certificate you used to copy around by a URL you fetch.


## 5) The JWKS format: the fields of a JWK

Given the popularity of JSON, it was high time to find a better format than the PEM(/DER) format for certificates and what better format than JSON?

A JWK describes one key with a handful of properties:

| Property | Meaning |
|----------|---------|
| `kty` | The key type: `RSA`, `EC` (elliptic curve) or `oct` (a symmetric secret) |
| `use` | What the key is for: `sig` for signature or `enc` for encryption |
| `alg` | The algorithm the key is meant for, such as `RS256` or `ES256` (optional) |
| `kid` | The key identifier: a name to distinguish between keys, and a very cool feature |
| `n`, `e` | The modulus and exponent of an RSA public key |
| `crv`, `x`, `y` | The curve and the coordinates of an elliptic curve public key |
| `x5c`, `x5t` | The X.509 certificate chain and thumbprint, when the key comes from a certificate (optional) |
{:.striped}

For example, this is an RSA public key as a JWK:

```json
{
    "kty" : "RSA",
    "e" : "AQAB",
    "use" : "sig",
    "kid" : "keyname",
    "n" : "2moVQ...2aq7Q"
}
```

And the JWKS containing this single key:

```json
{
  "keys" : [ {
    "kty" : "RSA",
    "e" : "AQAB",
    "use" : "sig",
    "kid" : "keyname",
    "n" : "2moVQ...2aq7Q"
  } ]
}
```

This is super easy and much clearer than the PEM format given that you now have an identifier for your key, the use of your key, an algorithm, etc.

Instead of a block certificate, you have several separate pieces of information.


## 6) How JWT verification uses the JWKS

Put the two together and you get the standard JWT verification flow:

1. Decode the JWT header: it carries the `alg` used for the signature and, usually, the `kid` of the signing key.
2. Fetch the JWKS from the provider's `jwks_uri` (and cache it).
3. Find the JWK whose `kid` matches the header. If there is no `kid`, try the keys whose `kty` and `use` fit.
4. Verify the signature with that public key and the algorithm **you** configured for this provider.
5. Only then trust the claims of the body: issuer, audience, expiration...

The `kid` is what makes key rotation painless: the provider adds a new key to its JWKS, starts signing with it, and removes the old one later. Your application picks the right key by identifier without any redeployment, as long as it refreshes the JWKS when it meets an unknown `kid`.

This is exactly what the [pac4j OIDC client](/docs/clients/openid-connect.html) does for you when it validates an ID token. And the [pac4j JWT authenticator](/docs/authenticators/jwt.html#4-jwk) can load its own keys from a JWK, so you can manage them in the same format.


## 7) Easier but...

Despite the more pleasant format, there is no magic, there are pitfalls to avoid (like with regular certificates).

Plain certificates were painful and no one would take them lightly. Yet, this nicer JWKS format of the keys must not make you forget that you deal with security.

- *Trap #1*

So you still need to take care of the rotation/revocation of the keys in your JWKS: add a JWK, remove an old one, ... things don't happen by themselves (hopefully).

- *Trap #2*

While JWKS exposed on the internet contain public keys, private/internal JWKS can contain private keys.

For example, this is the JWKS of the private key for our previous public JWK:

```json
{
   "keys":[
      {
         "p":"-4uskk...sMm98",
         "kty":"RSA",
         "q":"3kg3S...FgErM",
         "d":"UT_QS...l1LYw",
         "e":"AQAB",
         "use":"sig",
         "kid":"keyname",
         "qi":"Lp-0T...lo4afg",
         "dp":"xcakA...18JHE",
         "dq":"JByJV...XmqiP8",
         "n":"2moVQ...2aq7Q"
      }
   ]
}
```

You should notice that there is more information for private keys and especially you always find the `d` property in a private key.

This is really important as you must always be able to distinguish between a public key and a private key.

> **Because the golden rule remains: you must never publicly disclose a private key.**

- *Trap #3*

There is even a new trap with the `alg` property: this is absolutely not a security constraint, it is only a recommendation.

So you must not be confused by this value and only trust what you have really configured and applied in your code.

This is exactly like the JWT header where the `alg` key is only informative: trusting it could expose you to the *algorithm confusion* attack.

You must always rely on what you actually defined and used for encryption/signature. You must never rely on what is provided to you from the outside.

<div class="text-center highlight-blog">JWKS is a modern format to store/manage keys you will really enjoy,<br/>but you must never forget the good practices regardless!</div>

See how pac4j deals with JWKS in the [OIDC private_key_jwt authentication method](/docs/clients/openid-connect-config.html#c-private_key_jwt) or in the [OpenID Federation](/docs/clients/openid-connect-federation.html#1-federation-endpoint)...
