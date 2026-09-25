---
layout: doc
title: OpenID4VP / Response validation and verifiers
seo_title: "OpenID4VP in Java: response validation and verifiers | pac4j"
description: "Validate OpenID4VP responses in Java with pac4j. Configure SD-JWT VC issuer trust, holder binding, credential status checks and custom verifiers."
---

Receiving a wallet response is only the first step. Before creating a user profile, pac4j checks the exchange, asks the credential verifiers to validate the presentations, and checks that the results satisfy the original DCQL query. Here is how to configure that validation in your Java application.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-clients.html">Clients and configuration</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">DCQL queries, user profiles and logging</a></p>

<hr/>

## 1) Response validation

**A profile is created only after the complete response passes validation.**

- **pac4j validates the exchange:** transaction expiry, expected response mode, response encryption and `state` when sent.
  Malformed or conflicting response parameters are rejected.
- **Credential verifiers validate the presentations:** signatures, issuer trust, holder proofs and credential status.
  Register and configure a verifier for every requested format (see below).
- **pac4j enforces the original DCQL query:** credential types, requested claims and values, claim and credential sets,
  multiplicity and trusted authorities. Holder binding is required unless explicitly disabled in the query.

Validation uses the saved request, even if the configuration changes afterwards. Verified results are available through
`VerifiablePresentationCredentials.getVerifiedCredentials()`: each query identifier maps to a **list of credentials**.

## 2) Configuring credential verifiers

Register a `CredentialVerifier` for each credential format requested by your DCQL query using
`config.addCredentialVerifier(verifier)`. Registration replaces the verifier for that format. The configuration
provides a `SdJwtVcVerifier` by default, but it accepts no issuer until trusted keys or certificate trust anchors are configured.
The same registration mechanism applies to `OpenId4VpClient`, `OpenId4VpDcApiClient` and `EudiWalletClient`.
For implementation requirements, see [Custom verifiers](#23-custom-verifiers).

To use `SdJwtVcVerifier`, explicitly add the following EUDI dependency, which is not included by default:

```xml
<dependency>
    <groupId>eu.europa.ec.eudi</groupId>
    <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
    <version>0.20.1</version>
</dependency>
```

Without this dependency, `SdJwtVcVerifier` throws an `OpenId4VpException` when validating a credential, with a message identifying the dependency to add.
Applications replacing it with their own implementation do not need EUDI.

### 2.1) Built-in SD-JWT VC verifier

#### 2.1.1) Trusted issuers

Choose how to trust the credential issuer:

- **With `iss`:** configure `trustedIssuers` with its exact identifier and public keys.
- **Without `iss`, with `x5c`:** configure a `trustStore` containing trusted CA certificates.

```java
// For credentials with iss:
var verifier = new SdJwtVcVerifier().setTrustedIssuers(Map.of(
    "https://issuer.example", new SdJwtVcTrustedIssuer(JWKSet.parse(trustedIssuerJwksJson))));

// For credentials with x5c and no iss; both settings can coexist:
verifier.setTrustStore(new KeystoreProperties()
    .setKeystorePath("classpath:trusted-issuers.p12")
    .setKeyStoreType("PKCS12")
    .setKeystorePassword("changeit"));

config.addCredentialVerifier(verifier);
```

Use a JKS (default) or PKCS12 truststore with **trusted certificate entries**. An optional `keyStoreAlias`
restricts trust to one entry. No private key, private-key password or automatic keystore generation is needed.
Certificates supplied by the wallet are never automatically trusted.

With `iss`, configured keys verify the signature; `kid` selects a key when present. This mode takes precedence
over `x5c`. Without `iss`, the verifier validates the leaf-first certificate chain against the truststore,
checks validity and signing usage, then verifies the JWT with the leaf key. Necessary intermediates must be
in `x5c`. `VerifiedCredential.issuer` becomes the certificate subject (RFC 2253); no `iss` claim is invented.

Certificate revocation checks are enabled by default. Supply local CRLs with
`setCertificateRevocationLists(List<X509CRL>)`; unavailable or revoked status causes rejection.
For a test PKI without revocation data, explicitly use `setCertificateRevocationEnabled(false)`.
Credential status checking remains separate (see below).

Both modes require `typ=dc+sd-jwt` and `vct`. No issuer is trusted by default.
EUDI verifies disclosure hashes and reconstructs nested objects and arrays, checks `exp`/`nbf`, and verifies a
present key-binding JWT, including its signature, `typ=kb+jwt` and `sd_hash`.

#### 2.1.2) Signature algorithms and holder binding

The holder proof must match the nonce and audience from the transaction's saved request. For URL flows the audience
is the full `client_id`; for the Digital Credentials API it is `origin:` followed by one of the saved
`expected_origins`. The proof's `iat` must fall between transaction creation and the current time, with a
30-second tolerance configurable through `clockSkewSeconds`. Credential expiration is checked without that tolerance.
Issuer and holder signature algorithms default to ES256 and can be configured with `issuerAlgorithms` and
`holderAlgorithms`; symmetric algorithms are rejected.

A presentation without key binding is reported as such and rejected by the common DCQL validator unless the query
explicitly sets `require_cryptographic_holder_binding` to false. A present but invalid proof is always rejected.

#### 2.1.3) Trusted authorities

Authority evidence may be configured on `SdJwtVcTrustedIssuer.setTrustedAuthorities(...)` only after establishing the
corresponding trust relationship; otherwise it remains empty and a DCQL authority requirement cannot be satisfied.
The certificate mode returns no authority evidence automatically; use a custom verifier if DCQL requires it.

#### 2.1.4) Credential status

**Credential status is not checked automatically in this initial implementation.** A credential containing a
`status` claim is rejected unless `SdJwtVcVerifier.setStatusChecker(...)` is configured. This checker receives the
cryptographically verified credential, including its reconstructed claims, and must throw if status is invalid,
unsupported or cannot be checked. If it uses a status list, it must authenticate that list and check its validity
and the credential's status entry. A credential without a status claim does not invoke the checker.

### 2.2) Remote resources and application responsibilities

pac4j does not automatically download issuer metadata, keys or credential status lists:

- **Issuer keys:** it uses a configured `JWKSet`, or the leaf key of an `x5c` chain validated against configured
  truststore certificates when `iss` is absent. It does not download JWKS or trust lists. Keep trust configuration current.
- **Certificate revocation:** checks use supplied CRLs and the Java provider; CRL/OCSP network access depends on
  its configuration. This is separate from the credential's `status` claim.
- **Metadata:** it does not discover the issuer's metadata to locate its keys, or retrieve credential type
  metadata describing the credential's schema and display information. It still checks `vct` and the common
  validator checks the type and claims against the DCQL query.
- **Status lists:** it does not download a list to determine whether a credential has been revoked or suspended.
  When a credential contains `status`, a configured `statusChecker` must perform the required checks, including
  retrieval if needed, authentication and validity of the status data. Without that checker, the credential is rejected.

An application may obtain these resources separately and provide trusted keys and a status checker, or register
a different `CredentialVerifier` that manages retrieval. Obtaining a key from an issuer's endpoint does not by
itself establish that the application should trust that issuer.

This is a minimal SD-JWT integration, not a complete EUDI trust-list or HAIP validation implementation.
No built-in mdoc verifier is provided yet. All format verifiers remain replaceable through `addCredentialVerifier`.

### 2.3) Custom verifiers

Implement `CredentialVerifier` and register it with `config.addCredentialVerifier(...)`:

- **Reject invalid presentations:** perform signature, issuer trust and status checks; never return an unverified result or `null`.
- **Holder binding:** set `VerifiedCredential.cryptographicHolderBinding` to true only after verifying the proof
  against the saved request's nonce and audience.
- **Trusted authorities:** populate `trustedAuthorities` only from established trust evidence, indexed by DCQL
  authority type (`aki`, `etsi_tl`, `openid_federation`). A query requiring authorities needs at least one match.
- **Claims:** preserve JSON nesting. For mdoc, use namespace maps containing JSON-compatible values.
