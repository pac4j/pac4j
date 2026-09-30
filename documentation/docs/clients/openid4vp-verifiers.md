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

With `OpenId4VpClient` and `EudiWalletClient`, the response is validated twice: when the wallet posts it, and again
when the browser comes back. The response URI is not authenticated, and its transaction identifier is visible in the
wallet URL, thus in the QR code: a post which does not validate is refused on arrival and leaves the transaction open
for the wallet's own answer. This goes beyond what
[OpenID4VP section 14.3.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.3.2) asks.
Two limits remain with `direct_post`: a wallet `error` cannot be authenticated, so whoever sees the QR code can still
end the transaction with one; and nothing ties the wallet which answers to the browser which started the flow, so a
valid presentation made by another wallet for this very request is accepted. This is the session fixation described in
[OpenID4VP section 14.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.2), which the
Digital Credentials API avoids.

- **pac4j validates the exchange:** transaction expiry, expected response mode, response encryption and `state`.
  A fresh `state` is sent with every request invoking a wallet by URL and must come back unchanged: without holder
  binding, no nonce is returned and the `state` is what binds the response to the request
  ([OpenID4VP section 5.3](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-5.3)).
  The Digital Credentials API maintains that binding itself and carries no `state`.
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
provides `SdJwtVcVerifier` and `MdocVerifier` by default; neither accepts an issuer until trust is configured.
The same registration mechanism applies to `OpenId4VpClient`, `OpenId4VpDcApiClient` and `EudiWalletClient`.
For implementation requirements, see [Custom verifiers](#24-custom-verifiers).

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

- **By its `iss`:** configure `trustedIssuers` with its exact identifier and public keys.
- **By its `x5c` chain, with or without `iss`:** configure a `trustStore` containing trusted CA certificates. This is
  the method the [high assurance profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html)
  mandates: "The SD-JWT VC MUST contain the credential issuer's signing certificate along with a trust chain in the x5c
  JOSE header parameter".

```java
// For credentials with iss:
var verifier = new SdJwtVcVerifier().setTrustedIssuers(Map.of(
    "https://issuer.example", new SdJwtVcTrustedIssuer(JWKSet.parse(trustedIssuerJwksJson))));

// For credentials with x5c whose iss, if any, is not configured above; both settings can coexist:
verifier.setTrustStore(new KeystoreProperties()
    .setKeystorePath("classpath:trusted-issuers.p12")
    .setKeyStoreType("PKCS12")
    .setKeystorePassword("changeit"));

config.addCredentialVerifier(verifier);
```

Use a JKS (default) or PKCS12 truststore with **trusted certificate entries**. An optional `keyStoreAlias`
restricts trust to one entry. No private key, private-key password or automatic keystore generation is needed.
Certificates supplied by the wallet are never automatically trusted.
The trust anchors are read at the first certificate validation and kept: they are read again only when the
truststore's resource, password, type or alias change, or when `setTrustStore(...)` is called again, which is the
way to pick up a truststore file replaced on disk.

When the `iss` value is configured in `trustedIssuers`, its keys verify the signature; `kid` selects a key when
present. This mode always takes precedence over `x5c` for that `iss`: "A Verifier MUST ensure that for any given iss
value, an attacker cannot influence the type of verification process used"
([SD-JWT VC, section 7.3](https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-7.3)).
Otherwise, the verifier validates the leaf-first certificate chain against the truststore, checks validity and
signing usage, then verifies the JWT with the leaf key. Necessary intermediates must be in `x5c`.
`VerifiedCredential.issuer` becomes the certificate subject (RFC 2253), even when an `iss` claim is present: "the
Issuer of the Verifiable Digital Credential is the subject of the end-entity certificate"
([SD-JWT VC, section 2.5](https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.5)).
The `iss` claim is then kept as a claim; no `iss` claim is invented when absent.

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
In certificate mode, once the `x5c` chain is validated against the truststore, the verifier reports the `aki`
evidence itself: the key identifiers of the `AuthorityKeyIdentifier` extensions of the chain's certificates, in
base64url, as [OpenID4VP 1.0, section 6.1.1.1](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.1.1.1)
defines them. A query asking for `{"type": "aki", "values": ["<root key identifier>"]}` thus accepts the credentials
issued under that CA. Other authority types (`etsi_tl`, `openid_federation`) are never inferred from a certificate.

#### 2.1.4) Credential status

**Credential status is not checked automatically in this initial implementation.** A credential containing a
`status` claim is rejected unless `SdJwtVcVerifier.setStatusChecker(...)` is configured. This checker receives the
cryptographically verified credential, including its reconstructed claims, and must throw if status is invalid,
unsupported or cannot be checked. If it uses a status list, it must authenticate that list and check its validity
and the credential's status entry. A credential without a status claim does not invoke the checker.

### 2.2) Built-in mdoc verifier

`MdocVerifier` validates `mso_mdoc` presentations using **walt.id 0.11.0**. The library is optional and is
published in the **walt.id Maven repository**, outside Maven Central. Add the repository and dependency to your application:

```xml
<repositories>
    <repository>
        <id>waltid-releases</id>
        <url>https://maven.waltid.dev/releases</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>id.walt.mdoc-credentials</groupId>
        <artifactId>waltid-mdoc-credentials-jvm</artifactId>
        <version>0.11.0</version>
    </dependency>
</dependencies>
```

Keep its transitive dependencies, including COSE and `kotlinx-datetime`. Applications replacing the built-in
verifier do not need walt.id. Missing or incompatible dependencies produce an `OpenId4VpException` naming the
artifact and repository. EUDI SD-JWT is needed only when using `SdJwtVcVerifier`.

**Align Kotlin dependencies in your application's `dependencyManagement`, particularly when using both formats.**
The following combination is tested with Java 17, EUDI SD-JWT 0.20.1 and walt.id 0.11.0:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-bom</artifactId>
            <version>2.2.21</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-coroutines-bom</artifactId>
            <version>1.10.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-serialization-bom</artifactId>
            <version>1.9.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-datetime-jvm</artifactId>
            <version>0.6.1</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Without alignment, dependency order can select walt.id's older Kotlin runtime and cause EUDI to fail with
`NoClassDefFoundError: kotlin/time/Clock$System`. pac4j manages these versions for its own build; an application's
other direct dependencies can override the versions selected at runtime. Do not upgrade `kotlinx-datetime` to
0.7.x without addressing its removal of the `kotlinx.datetime.Instant` API used by walt.id 0.11.0.

#### 2.2.1) Issuer trust and configuration

Configure **issuer CA certificates** in an explicit truststore:

```java
var verifier = new MdocVerifier().setTrustStore(new KeystoreProperties()
    .setKeystorePath("classpath:mdoc-issuers.p12")
    .setKeyStoreType("PKCS12")
    .setKeystorePassword("changeit"));
config.addCredentialVerifier(verifier);
config.setDcqlQuery(EudiPidQuery.mdoc("given_name", "family_name"));
```

Only trusted certificate entries are used; `keyStoreAlias` optionally selects one. The store is read for each
validation, so replacing its contents is picked up on the next presentation. The issuer's `x5chain` must validate
against these anchors. System trust roots are not used. `certificateRevocationEnabled` defaults to `true`;
`setCertificateRevocationLists(...)` supplies local CRLs, while network CRL/OCSP behavior depends on your Java
PKIX provider. Disabling revocation is appropriate for synthetic test certificates without revocation information.
The certificate validation is shared with the SD-JWT verifier and uses Java PKIX rather than walt.id's default trust policy.

The profile's issuer is the validated signing certificate's subject DN. Validated chain AKIs are returned as DCQL
`aki` authority evidence; no ETSI trust-list membership is inferred. Configure a
[profile identifier resolver](openid4vp-advanced.html#2-the-profile-identifier) for the namespace-qualified claim
identifying your user. The name-only query above does not itself provide a suitable stable identifier.

#### 2.2.2) Validation and supported presentations

The verifier checks the issuer signature, document type, MSO validity, **every disclosed attribute digest** and
the device signature using the public key authenticated by the MSO. It reconstructs the OpenID4VP 1.0 handover
from the saved request for both URL/QR and DC API clients, including the nonce, full client ID and response URI
or expected browser origin, and the response encryption key thumbprint when applicable.

- Supports `DeviceSignature` with ES256: COSE algorithm `-7` and P-256 keys for both issuer and device.
  These capabilities are advertised in `vp_formats_supported`.
- Requires one document per base64url-encoded `DeviceResponse`. Multiple presentations may still be returned
  in a `vp_token` array when the DCQL query allows them.
- Returns issuer-signed claims in namespace maps. Dates use ISO text, byte strings use unpadded base64url,
  and maps/lists retain their nesting. Map keys must be strings.
- Rejects `DeviceMAC`, device-signed claims, transaction data and unsupported algorithms.
- Limits a decoded presentation to 2 MiB by default; adjust `setMaxPresentationSize(...)` for larger portraits.

When an authenticated MSO contains a `status` object, configure
`setStatusChecker((credential, status) -> { ... })`. The callback receives the verified credential and its
MSO status map, and must throw if the status cannot be validated or is revoked. Without it, credentials
containing status are rejected. No status list is fetched automatically.

This is format verification, not a complete EUDI trust-list, certificate-profile or HAIP conformance implementation.
The integration reuses walt.id for parsing, COSE verification and MSO digest checks; pac4j supplies the transaction
binding, explicit issuer trust and profile mapping. It invokes the digest check separately for every item because
walt.id 0.11.0's bulk method returns after the first item.

### 2.3) Remote resources and application responsibilities

pac4j does not automatically download issuer metadata, keys or credential status lists:

- **Issuer keys:** it uses a configured `JWKSet`, or the leaf key of an `x5c` chain validated against configured
  truststore certificates when `iss` is absent or not configured. It does not download JWKS or trust lists. Keep trust configuration current.
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
Both built-in format verifiers remain replaceable through `addCredentialVerifier`.

### 2.4) Custom verifiers

Implement `CredentialVerifier` and register it with `config.addCredentialVerifier(...)`:

- **Reject invalid presentations:** perform signature, issuer trust and status checks; never return an unverified result or `null`.
- **Holder binding:** set `VerifiedCredential.cryptographicHolderBinding` to true only after verifying the proof
  against the saved request's nonce and audience.
- **Trusted authorities:** populate `trustedAuthorities` only from established trust evidence, indexed by DCQL
  authority type (`aki`, `etsi_tl`, `openid_federation`), for instance the `aki` values of a certificate chain once
  validated. A query requiring authorities needs at least one match.
- **Claims:** preserve JSON nesting. For mdoc, use namespace maps containing JSON-compatible values.
