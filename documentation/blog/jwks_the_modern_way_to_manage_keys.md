---
layout: blog
title: JWKS&colon; the modern way to manage keys
author: Jérôme LELEU
date: June 2026
---

When it comes to security, certificates are used everywhere since the early days of the web.

While storing them in PEM/DER format has always been complicated, things have become much easier with the modern JWKS (J for JSON) format.


## 1) A word about cryptography

We can use symmetric cryptography based on a secret.

As this secret must be shared by both parties, this is not generally a very convenient solution.

Or we can use asymmetric cryptography based on key pairs.

In that case, there are two certificates: a public one and a private one.

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
1lJeHcyf/zxL8yhUAmMirs2KjmsRG9ObCRmaZWm2qn047+5yMYBAgJw6R3dRNCuy
...TRUNCATED...
DxI7zekvoIySQJEfPsB/rNDxThgHfdjehnff4dp0MFfJC6SmjuCDyhymqgRO+zUP
ySD6rqvGLLxGkZoUGyuHt9D7B/FaBAMvjjgOSMYbHxYj0ncQioaVSpcUZIpTrHRo
jA1drmXT/LHPGeQgp/CJQ3Zf7qqavA==
-----END PRIVATE KEY-----
```

- `public.crt`:

```
-----BEGIN CERTIFICATE-----
MIIFLjCCAxagAwIBAgIUOtBi9hdWAqh1sL8U7wS3ttXgg40wDQYJKoZIhvcNAQEL
BQAwITELMAkGA1UEBhMCRlIxEjAQBgNVBAMMCWxvY2FsaG9zdDAeFw0yNjA1MTgx
MjUwNDNaFw0yNzA1MTgxMjUwNDNaMCExCzAJBgNVBAYTAkZSMRIwEAYDVQQDDAls
...TRUNCATED...
fnS3DzBiMj+hdy5cuMQoMKvNo8K8HTozr60mK3FLkr5iZI06HivkZL1S14qWhcBe
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


## 4) JWK(S)

Given the popularity of JSON, it was high time to find a better format than the PEM(/DER) format for certificates and what better format than JSON?

Thus, JWK (for JSON Web Key) is the format to define keys:
- the `kty` property defines the type `RSA`, `EC`, ...
- the `use` property indicates if the key is used for signature ("sig") or encryption ("enc")
- the `alg` property defines the algorithm (it can be omitted)
- the `kid` property defines the name for the key and this is a very cool feature to distinguish between keys
- the specific `n` and `e` properties for RSA, the specific `x` and `y` properties for Elliptic Curve.

For example, you can have this JWK:

```json
{
    "kty" : "RSA",
    "e" : "AQAB",
    "use" : "sig",
    "kid" : "keyname",
    "n" : "2moVQ...2aq7Q"
}
```

And a JWKS, the S stands for Set (not for the plural), is a set of JWKs listed in an array defined by the `keys` property.

For example, the JWKS of our previous JWK is:

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


## 5) Easier but...

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

You should notice that there are more information for private keys and especially you always find the `d` property in a private key.

This is really important as you must always be able to distinguish between a public key and a private key.

> **Because the golden rule remains: you must never publicly disclose a private key.**

- *Trap #3*

There is even a new trap with the `alg` property: this is absolutely not a security constraint, it is only a recommendation.

So you must not be confused by this value and only trust what you have really configured and applied in your code.

This is exactly like for the JWT header where the `alg` key is only informative: trusting it could expose you to the *algorithm confusion* attack.

You must always rely on what you actually defined and used for encryption/signature. You must never rely on what is provided to you from the outside.

<div class="text-center highlight-blog">JWKS is a modern format to store/manage keys, but you must never forget the good practices regardless.</div>
