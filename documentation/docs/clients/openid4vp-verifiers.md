---
layout: doc
title: OpenID4VP / Response validation and verifiers
---

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-clients.html">Clients and configuration</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">Advanced</a></p>

<hr/>

## 1) Response validation

The authorization parameters and effective DCQL query are saved with the transaction before the request is returned.
Response validation uses this snapshot, including the negotiated encryption methods, even if the client configuration changes afterwards.
The authenticator supports ECDH-ES/P-256 responses with the advertised A128GCM or A256GCM method, verifies the JWE authentication tag
and key identifier, and checks `state` when it was sent over the URL binding. An encrypted response must contain a `vp_token` JSON object.
Mixed success/error parameters, an unexpected response mode, malformed presentations and expired transactions are rejected.

Every returned presentation is passed to its format's `CredentialVerifier`; a null result is rejected.
`VerifiablePresentationCredentials.getVerifiedCredentials()` maps each DCQL query identifier to a **list** of verified credentials,
so `multiple: true` preserves every presentation. Results are published only after the complete response passes validation.

A format verifier must set `VerifiedCredential.cryptographicHolderBinding` to true only after verifying the holder proof against the
transaction nonce and the appropriate audience, using the saved request parameters. It must populate `trustedAuthorities` only with
identifiers established through its trust validation, indexed by DCQL authority type (`aki`, `etsi_tl`, `openid_federation`).
The common validator requires holder binding unless the query explicitly disables it, and requires at least one matching authority
when the query supplies `trusted_authorities`. Disclosed claims retain their JSON nesting; mdoc claims use namespace maps whose
values are JSON-compatible, so the same DCQL path and value checks can be applied.

## 2) Configuring credential verifiers

Register a `CredentialVerifier` for each credential format requested by your DCQL query using
`config.addCredentialVerifier(verifier)`. Registration replaces the verifier for that format. The configuration
provides a `SdJwtVcVerifier` by default, but it accepts no issuer until trusted issuer keys are configured.
The same registration mechanism applies to `OpenId4VpClient`, `OpenId4VpDcApiClient` and `EudiWalletClient`.
A custom implementation owns the format-specific cryptographic, issuer trust and status checks described above;
the common response validator then checks its result against the saved DCQL query.

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

The initial implementation accepts trusted issuer keys configured explicitly. It does not discover issuer metadata,
download JWKS, validate an `x5c` chain against a trust list, or fetch credential type metadata. It never treats keys
or certificates embedded in a wallet presentation as trusted merely because they are present.

```java
// Load public verification keys from your trusted configuration, not from the wallet's presentation.
JWKSet issuerKeys = JWKSet.parse(trustedIssuerJwksJson);
SdJwtVcVerifier verifier = new SdJwtVcVerifier()
    .setTrustedIssuers(Map.of("https://issuer.example", new SdJwtVcTrustedIssuer(issuerKeys)));
config.addCredentialVerifier(verifier);
```

`SdJwtVcTrustedIssuer` describes an issuer your application has decided to trust; it does not issue credentials.
Its `keys` field contains that issuer's public verification keys as a Nimbus `JWKSet`. Its optional
`trustedAuthorities` field records established authority affiliations for DCQL checks and defaults to an empty map.
The map key passed to `setTrustedIssuers` is the issuer identifier.

The issuer identifier must match `iss` exactly. No issuer is accepted by default. The configured keys verify the
issuer signature, with `kid` used to select a key when present. The verifier requires `typ=dc+sd-jwt`, `iss` and `vct`.
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

#### 2.1.4) Credential status

**Credential status is not checked automatically in this initial implementation.** A credential containing a
`status` claim is rejected unless `SdJwtVcVerifier.setStatusChecker(...)` is configured. This checker receives the
cryptographically verified credential, including its reconstructed claims, and must throw if status is invalid,
unsupported or cannot be checked. If it uses a status list, it must authenticate that list and check its validity
and the credential's status entry. A credential without a status claim does not invoke the checker.

### 2.2) Remote resources and application responsibilities

The built-in verifier makes no automatic HTTP requests to obtain verification resources:

- **Issuer keys:** it uses only the configured `JWKSet`. It does not download a remote JWKS or refresh keys
  when an issuer rotates them. The application must supply updated trusted keys.
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

