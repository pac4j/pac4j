---
layout: doc
title: OpenID4VP / Response validation and verifiers
seo_title: "OpenID4VP in Java: response validation and verifiers | pac4j"
description: "Validate OpenID4VP responses in Java with pac4j. Register the SD-JWT VC and mdoc verifiers and their trusted issuers, or your own."
---

Receiving a wallet response is only the first step. Before creating a user profile, pac4j checks the exchange, asks the credential verifiers to validate the presentations, and checks that the results satisfy the original DCQL query. Here is how to configure that validation in your Java application.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-clients.html">Clients and configuration</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-sd-jwt-vc-verifier.html">SD-JWT VC verifier</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-mdoc-verifier.html">mdoc verifier</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">DCQL queries, user profiles and logging</a></p>

<hr/>

## 1) Response validation

**A profile is created only after the whole response passes three levels of checks**, always against the request
saved with the transaction, even if the configuration has changed since:

| Level | Done by | Checks |
|---|---|---|
| **Exchange** | pac4j | transaction expiry, response mode, response encryption, `state`, well-formed and consistent parameters |
| **Presentations** | the [credential verifier](#2-configuring-credential-verifiers) of each format | signatures, [issuer trust](#3-trusted-issuers), holder binding, credential status |
| **DCQL query** | pac4j | credential types, requested claims and values, claim and credential sets, multiplicity, trusted authorities, and holder binding unless the query disables it |

The verified credentials are available through `VerifiablePresentationCredentials.getVerifiedCredentials()`: each
query identifier maps to a **list of credentials**.

**The `state` binds the response to the request** when a wallet is invoked by URL: a fresh one is sent with every
request and must come back unchanged, since without holder binding no nonce is returned
([OpenID4VP section 5.3](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-5.3)).
The Digital Credentials API maintains that binding itself and carries no `state`.

**With a wallet invoked by URL** (`OpenId4VpClient`, `EudiWalletClient`), the response URI is not authenticated and
its transaction identifier is visible in the wallet URL, thus in the QR code. The response is therefore validated
**twice**:

1. **when the wallet posts it** to the response URI, without any session: a post which does not validate is refused
   and leaves the transaction open for the wallet's own answer (beyond what
   [section 14.3.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.3.2) asks);
2. **when the user's browser calls the callback URL** to claim it, once the page knows the response has arrived: its
   web session identifies the transaction, and the stored response is validated again before the profile is built.

Two limits remain, which the Digital Credentials API avoids:

- **Wallet errors:** an `error` cannot be authenticated, so whoever sees the QR code can end the transaction with one.
- **Session fixation:** nothing ties the wallet which answers to the browser which started the flow, so a valid
  presentation made by another wallet for this very request is accepted
  ([section 14.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.2)).

## 2) Configuring credential verifiers

Register a `CredentialVerifier` for each credential format requested by your DCQL query using
`config.addCredentialVerifier(verifier)`. Registration replaces the verifier for that format, and a query whose
format has no verifier is refused at initialization. The same registration mechanism applies to `OpenId4VpClient`,
`OpenId4VpDcApiClient` and `EudiWalletClient`.

The configuration provides one built-in verifier per format. **Neither accepts an issuer until trust is configured**,
and each relies on an optional library to add to your application:

| Format | DCQL `format` | Built-in verifier | Optional library | Documentation |
|---|---|---|---|---|
| SD-JWT VC | `dc+sd-jwt` | `SdJwtVcVerifier` | EUDI `eudi-lib-jvm-sdjwt-kt` | [SD-JWT VC verifier](openid4vp-sd-jwt-vc-verifier.html) |
| mdoc (ISO/IEC 18013-5) | `mso_mdoc` | `MdocVerifier` | walt.id `waltid-mdoc-credentials-jvm` | [mdoc verifier](openid4vp-mdoc-verifier.html) |

Add only the libraries of the formats your application requests; when using both, align the Kotlin dependencies
(see [Kotlin alignment](openid4vp-mdoc-verifier.html#12-kotlin-alignment)).

## 3) Trusted issuers

**No issuer is trusted by default.** Give each verifier its `TrustedIssuers`, built with trusted issuer
definitions, implementations of `TrustedIssuer`. Two are provided, which **do not trust the same thing**:

| | `KeysTrustedIssuer` | `CertificateTrustedIssuer` |
|---|---|---|
| **Created with** | `new KeysTrustedIssuer(iss, jwks)` | `new CertificateTrustedIssuer(trustStore)` |
| **What you configure** | the issuer's own **signing keys** | **CA certificates**, never the issuers' keys |
| **Where the verifying key comes from** | the configuration | the **credential**: the leaf of its `x5c` / `x5chain`, accepted because its chain leads to a trust anchor |
| **Trust** | direct: the key is pinned | delegated: the CA vouches for issuers not known in advance |
| **How many issuers** | one, named | all those of the CA, including future ones |
| **Issuer reported** | the configured `iss` | the leaf certificate subject (RFC 2253) |
| **Checks** | the signature only | validity, key usage, basic constraints, revocation |
| **Trusted authorities** | configured with `setTrustedAuthorities(...)` | `aki` of the validated chain |
| **Lookup** | by identifier | by certificate chain |
| **Formats** | SD-JWT VC | SD-JWT VC, mdoc |

A truststore thus holds **trust anchors**: one of its keys never verifies a credential directly, it verifies the
certificate of an issuer, which carries the key. To pin the key of one issuer, use a `KeysTrustedIssuer`. Keeping both kinds apart is also what makes the
[resolution order](#32-resolution-order) safe: for a given `iss`, the verification process never depends on what the credential carries.

Add a definition to a verifier with `addTrustedIssuer(...)`, or share the same `TrustedIssuers` between several
verifiers with `setTrustedIssuers(...)`: each one ignores the definitions which do not support its format.

```java
var trustedIssuers = new TrustedIssuers(
    new CertificateTrustedIssuer(new KeystoreProperties("classpath:pid-providers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"))
        .setCertificateRevocationLists(List.of(new ResourceProperties("classpath:pid-providers.crl"))),
    new KeysTrustedIssuer("https://issuer.example", new JwksProperties("classpath:partner.jwks")));

config.addCredentialVerifier(new SdJwtVcVerifier().setTrustedIssuers(trustedIssuers));
config.addCredentialVerifier(new MdocVerifier().setTrustedIssuers(trustedIssuers));
```

### 3.1) Configuration

**`KeysTrustedIssuer`:** the static counterpart of the issuer metadata, which pac4j does not download.

- **Keys:** a `JWKSet`, or a `JwksProperties` read once at construction. The resource must exist, nothing is generated,
  and a `kid` set on the properties keeps only that key. Only the public part of the keys is kept.
- **Trusted authorities:** configure them only once the corresponding trust relationship is established.

**`CertificateTrustedIssuer`:**

- **Truststore:** JKS (default) or PKCS12, with **trusted certificate entries** only, optionally restricted to one
  `keyStoreAlias`. System roots and certificates supplied by the wallet are never trusted. Intermediates must be in the
  credential's chain.
- **Reloading:** the trust anchors are read once, then again only when the truststore settings change or when
  `setTrustStore(...)` is called, which picks up a file replaced on disk.
- **Revocation:** enabled by default, so **a chain whose revocation status is unavailable is rejected**. Supply local
  CRLs as `ResourceProperties` with `setCertificateRevocationLists(...)`: they are read once, then again when this
  setter is called. CRL/OCSP network access depends on your Java PKIX provider. For a test PKI without revocation
  data, call `setCertificateRevocationEnabled(false)`.
- **`aki`:** a DCQL query asking for `{"type": "aki", "values": ["<root key identifier>"]}` accepts the credentials
  issued under that CA ([OpenID4VP section 6.1.1.1](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.1.1.1)).

### 3.2) Resolution order

The definitions supporting the credential format are consulted **by identifier first**, then the others in the order
they were added. **The first one which knows the issuer decides, and its decision is final:**

- **An `iss` configured with keys** is verified with these keys only, whatever chain the credential carries: "A
  Verifier MUST ensure that for any given iss value, an attacker cannot influence the type of verification process
  used" ([SD-JWT VC, section 7.3](https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-7.3)).
- **A truststore knows a chain** issued by one of its trust anchors: an unknown chain goes to the next truststore, a
  known but invalid one (expired, revoked...) is rejected.
- **A credential no definition knows** is rejected.

### 3.3) Other sources of trust

Implement `TrustedIssuer` for another ecosystem (trusted list, federation, issuer metadata): from the
`IssuerEvidence` of a credential (identifier, `kid`, certificate chain), return the `ResolvedIssuer` (name,
keys, trusted authorities), empty if the issuer is unknown, or throw to reject it. Override `isIdentifierBased()` and
`supports(format)` as needed.

## 4) Application responsibilities

The built-in verifiers perform format verification, not a complete EUDI trust-list or HAIP validation.
**pac4j does not download issuer metadata, keys, trust lists or credential status lists:**

- **Issuer trust:** configure the [trusted issuers](#3-trusted-issuers) yourself, and keep them current. Obtaining a key
  from an issuer's endpoint does not by itself establish that the application should trust that issuer.
- **Certificate revocation:** checks use the supplied CRLs and the Java PKIX provider; CRL/OCSP network access depends
  on its configuration. This is separate from the credential status.
- **Credential status:** a credential containing a status is rejected unless a status checker is configured, which must
  perform the required checks, including retrieval if needed, authentication and validity of the status data.

An application may obtain these resources separately, or register a [custom verifier](#5-custom-verifiers) which
manages them.

## 5) Custom verifiers

Implement `CredentialVerifier` and register it with `config.addCredentialVerifier(...)`. Extend
`AbstractCredentialVerifier` to get the [trusted issuers](#3-trusted-issuers) of the built-in verifiers: its
`resolveIssuer(...)` applies the [resolution order](#32-resolution-order) to your format.

- **Reject invalid presentations:** perform signature, issuer trust and status checks; never return an unverified result or `null`.
- **Holder binding:** set `VerifiedCredential.cryptographicHolderBinding` to true only after verifying the proof
  against the saved request's nonce and audience.
- **Trusted authorities:** populate `trustedAuthorities` only from established trust evidence, indexed by DCQL
  authority type (`aki`, `etsi_tl`, `openid_federation`), for instance the `aki` values of a certificate chain once
  validated. A query requiring authorities needs at least one match.
- **Claims:** preserve JSON nesting. For mdoc, use namespace maps containing JSON-compatible values.
