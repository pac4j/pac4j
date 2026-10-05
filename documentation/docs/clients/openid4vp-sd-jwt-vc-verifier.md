---
layout: doc
title: OpenID4VP / SD-JWT VC verifier
seo_title: "OpenID4VP in Java: SD-JWT VC verifier | pac4j"
description: "Validate SD-JWT VC presentations in Java with pac4j: EUDI dependency, disclosures, holder binding and credential status."
---

`SdJwtVcVerifier` validates the `dc+sd-jwt` presentations: the issuer signature and trust, the selective
disclosures, the holder binding and the credential status. It is registered by default, but **trusts no issuer until
you configure [trusted issuers](openid4vp-verifiers.html#3-trusted-issuers)**.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and verifiers</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-mdoc-verifier.html">mdoc verifier</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">DCQL queries, user profiles and logging</a></p>

<hr/>

## 1) Dependencies

The verifier relies on the EUDI SD-JWT library, which is **not included by default**:

```xml
<dependency>
    <groupId>eu.europa.ec.eudi</groupId>
    <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
    <version>0.20.1</version>
</dependency>
```

Without it, `SdJwtVcVerifier` throws an `OpenId4VpException` when validating a credential, with a message identifying
the dependency to add. Applications replacing it with their own `CredentialVerifier` do not need EUDI.

If you also use the [mdoc verifier](openid4vp-mdoc-verifier.html), align the Kotlin dependencies as explained in
[Kotlin alignment](openid4vp-mdoc-verifier.html#12-kotlin-alignment).

## 2) Quick start

```java
// trustedIssuers: see "Trusted issuers" in Response validation and verifiers
config.addCredentialVerifier(new SdJwtVcVerifier().setTrustedIssuers(trustedIssuers));
config.setDcqlQuery(EudiPidQuery.sdJwtVc(GIVEN_NAME, AGE_OVER_18));
```

## 3) What is verified

- **Issuer signature and trust:** `typ=dc+sd-jwt` and `vct` are required, and an `iss` claim or an `x5c` chain, checked
  by the [trusted issuers](openid4vp-verifiers.html#3-trusted-issuers).
- **Disclosures:** EUDI verifies the disclosure hashes. `iss`, `vct` and `aka_vcts` must not be selectively disclosed.
- **Validity:** `exp` and `nbf` are checked, without tolerance.
- **Key-binding JWT**, when present: its signature, `typ=kb+jwt` and `sd_hash`, then the [holder binding](#4-holder-binding).

## 4) Holder binding

The key-binding JWT must match the transaction's saved request:

| Check | URL / QR code flow | Digital Credentials API |
|---|---|---|
| `nonce` | the request `nonce` | the request `nonce` |
| `aud` | the full `client_id` | `origin:` followed by one of the saved `expected_origins` |
| `iat` | between the transaction creation and now | same |

- **Clock skew:** the `iat` window has a 30-second tolerance, configurable through `clockSkewSeconds`.
- **Missing proof:** a presentation without key binding is reported as such and rejected by the common DCQL validator,
  unless the query explicitly sets `require_cryptographic_holder_binding` to false.
- **Invalid proof:** a present but invalid proof is always rejected.

## 5) Supported presentations

| Supported | Not supported (rejected) |
|---|---|
| SD-JWT compact serialization, with or without key-binding JWT | other serializations |
| asymmetric signature algorithms: ES256 by default, configurable with `issuerAlgorithms` and `holderAlgorithms` | symmetric algorithms |
| issuer keys from the [trusted issuers](openid4vp-verifiers.html#3-trusted-issuers): configured keys or `x5c` chain | issuer metadata, DID |
| nested objects and array elements disclosures | |

- **Capabilities:** the accepted algorithms are advertised in `vp_formats_supported`, as `sd-jwt_alg_values` and
  `kb-jwt_alg_values`.
- **Types:** besides `vct`, the other types the issuer signed in `aka_vcts` are reported in
  `VerifiedCredential.getAdditionalTypes()` and matched against the `vct_values` of the DCQL query. The type metadata
  is not used.

## 6) Claims and profile identifier

**The claims are the reconstructed JSON payload:** nested objects and arrays keep their structure, digests are removed,
and an undisclosed claim is absent, which is not an error:

```json
{
  "iss": "https://issuer.example",
  "vct": "urn:eudi:pid:1",
  "given_name": "Erika",
  "address": {"locality": "Berlin"}
}
```

**The issuer** is the configured `iss` or, with an `x5c` chain, the leaf certificate subject (RFC 2253): the `iss`
claim is then kept as a mere claim.

**The default [profile identifier](openid4vp-advanced.html#2-the-profile-identifier)**,
`ProfileIdResolver.issuerAndClaim("sub")`, reads a top-level claim and combines it with the issuer; it checks at
initialization that the DCQL query can return that claim. `EudiWalletClient` has no default: choose a stable claim, such
as `ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER)` where the PID provider issues it.

## 7) Credential status

**Credential status is not checked automatically, and no status list is fetched.** A credential containing a `status`
claim is rejected unless `setStatusChecker(...)` is configured:

- the checker receives the cryptographically verified credential, including its reconstructed claims;
- it must throw if the status is invalid, unsupported or cannot be checked;
- if it uses a status list, it must retrieve it if needed, authenticate it and check its validity and the
  credential's status entry.

A credential without a `status` claim does not invoke the checker.

## 8) Scope and limits

This is a minimal SD-JWT VC integration, not a complete EUDI trust-list or HAIP validation implementation. EUDI
provides the parsing, the disclosure verification and the key-binding JWT signature checks; pac4j supplies the
transaction binding, the issuer trust and the profile mapping.
