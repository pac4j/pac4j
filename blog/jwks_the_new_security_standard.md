---
layout: blog
title: JWKS&colon; the new security standard
author: Jérôme LELEU
date: June 2026
---

When it comes to security, certificates are used everywhere since almost forever.

While storing them in PEM/DER format has always been complicated, things have become much easier with the new JWKS (J for JSON) format.


## 1) A word about cryptography

We can use symetric cryptography based on a secret.

As this secret must be shared by both parties, this is not generally a very convenient solution.

Or we can use asymetric cryptography based on certificates.

In that case, there are two certificates: a public one and a private one.

Two mechanisms are available:
- the signature ensures that the sender is confirmed (the sender uses its private key to sign the message and anyone can confirm that using the public key of the sender)
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

Today, the OIDC protocol has somehow supplanted the SAML protocol and JSON has truely replaced the XML format.

Everyone knows the JSON format:

```json
{
    "key1": "value1",
    "key2": "value2"
}
```

Most people also knows that a JSON Web Token (aka JWT) is a signed and/or encrypted JSON message.

It comes as as string in three parts separated by dots, each part being base64 encoded: `part1.part2.part3`.

`part1` is the header, `part2` is the the JSON itself (it can be encrypted) and `part3` is the signature (it may not be signed).

Let's take an example from `jwt.io`:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.
KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
```

We indeed find three parts:
- a header: `{ "alg": "HS256", "typ": "JWT" }`
- a body: `{ "sub": "1234567890", "name": "John Doe", "admin": true, "iat": 1516239022 }`
- a signature.

And the encryption/signing of the JWTs is ensured by the public/private certificates.


## 4) JWK(S)

Given the popularity of JSON, it was high time to find a better format than the PEM(/DER) format for certificates and what better format than JSON?

Thus, JWK (for JSON Web Key) is the format to define certificates:
- the `kty` property defines the type `RSA`, `EC`, ...
- the `use` property indicates if the certificate is used for signature ("sig") or encryption ("enc)
- the `alg` property defines the algorithm (can be ommitted)
- the `kid` property defines the name for the certificate and this is a very cool feature to sort out certificates.
...
