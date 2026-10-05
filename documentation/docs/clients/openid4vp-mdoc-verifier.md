---
layout: doc
title: OpenID4VP / mdoc verifier
seo_title: "OpenID4VP in Java: mdoc (ISO 18013-5) verifier | pac4j"
description: "Validate mdoc presentations in Java with pac4j: walt.id dependency, device signature and session transcript, claims, profile identifier and status."
---

`MdocVerifier` validates the `mso_mdoc` presentations (ISO/IEC 18013-5 mobile documents, such as the EUDI PID or a
mobile driving licence): the issuer signature and trust, the attribute digests and the device signature bound to the
transaction. It is registered by default, but **trusts no issuer until you configure [trusted issuers](openid4vp-verifiers.html#3-trusted-issuers)**.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and verifiers</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-sd-jwt-vc-verifier.html">SD-JWT VC verifier</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">DCQL queries, user profiles and logging</a></p>

<hr/>

## 1) Dependencies

### 1.1) walt.id

The verifier relies on **walt.id 0.11.0**, which is optional and published in the **walt.id Maven repository**,
outside Maven Central:

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

- Keep its transitive dependencies, including COSE and `kotlinx-datetime`.
- Missing or incompatible dependencies produce an `OpenId4VpException` naming the artifact and the repository.
- Applications replacing the built-in verifier with their own `CredentialVerifier` do not need walt.id.

### 1.2) Kotlin alignment

**Align the Kotlin dependencies in your application's `dependencyManagement`, especially when you also use the
[SD-JWT VC verifier](openid4vp-sd-jwt-vc-verifier.html).** This combination is tested with Java 17, EUDI SD-JWT 0.20.1
and walt.id 0.11.0:

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

- **Without alignment**, dependency order can select walt.id's older Kotlin runtime and make EUDI fail with
  `NoClassDefFoundError: kotlin/time/Clock$System`. pac4j manages these versions for its own build, but your
  application's other direct dependencies can override the versions selected at runtime.
- **Do not upgrade `kotlinx-datetime` to 0.7.x**: it removes the `kotlinx.datetime.Instant` API used by walt.id 0.11.0.

## 2) Quick start

```java
// trustedIssuers: see "Trusted issuers" in Response validation and verifiers
config.addCredentialVerifier(new MdocVerifier().setTrustedIssuers(trustedIssuers));
config.setDcqlQuery(EudiPidQuery.mdoc(GIVEN_NAME, PERSONAL_ADMINISTRATIVE_NUMBER));
config.setProfileIdResolver(...); // required, see "Claims and profile identifier" below
```

## 3) What is verified

- **Issuer signature and trust:** the signature of the MSO (Mobile Security Object), whose `x5chain` is checked by
  the [trusted issuers](openid4vp-verifiers.html#3-trusted-issuers). An mdoc has no issuer identifier: only
  certificate-based trusted issuers (IACA roots) apply, with Java PKIX rather than walt.id's default trust policy.
- **Document type:** the doctype of the document; the common validator then checks it against the `doctype_value`
  of the DCQL query.
- **MSO validity** dates.
- **Every disclosed attribute digest** against the MSO.
- **Device signature**, with the device public key authenticated by the MSO: the [holder binding](#4-holder-binding).

## 4) Holder binding

The device signature covers a `SessionTranscript` rebuilt from the transaction's saved request, as defined by
[OpenID4VP 1.0, appendix B.2.6](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#appendix-B.2.6),
for both URL/QR code and Digital Credentials API clients:

| Element | URL / QR code flow (`OpenID4VPHandover`) | Digital Credentials API (`OpenID4VPDCAPIHandover`) |
|---|---|---|
| Verifier identity | the full `client_id` | one of the saved `expected_origins` |
| `nonce` | the request `nonce` | the request `nonce` |
| Encryption key | JWK thumbprint of the response encryption key if the response is encrypted, otherwise `null` | same |
| Response URI | the `response_uri` | — |

A presentation signed for another request, another verifier or another encryption key is rejected.
Unlike SD-JWT VC, an mdoc presentation always carries a device signature: there is no presentation without holder binding.

## 5) Supported presentations

| Supported | Not supported (rejected) |
|---|---|
| `DeviceSignature` | `DeviceMAC` |
| ES256 (COSE algorithm `-7`) with P-256 keys, for issuer and device | other algorithms and curves |
| issuer-signed claims | device-signed claims |
| | transaction data |

- **Capabilities:** the supported algorithms are advertised in `vp_formats_supported`, as `issuerauth_alg_values` and
  `deviceauth_alg_values`.
- **One document per `DeviceResponse`:** each base64url-encoded `DeviceResponse` must contain one document. Several
  presentations may still be returned in a `vp_token` array when the DCQL query allows them.
- **Size:** a decoded presentation is limited to 2 MiB by default; adjust `setMaxPresentationSize(...)` for larger
  portraits.

## 6) Claims and profile identifier

**The claims are grouped by namespace:** `VerifiedCredential.getClaims()` maps each namespace to a map of its
issuer-signed elements. Dates are ISO text, byte strings unpadded base64url, and maps and lists keep their nesting
(map keys must be strings). For the EUDI PID, the namespace is the doctype:

```json
{
  "eu.europa.ec.eudi.pid.1": {
    "given_name": "Erika",
    "birth_date": "1964-08-12",
    "personal_administrative_number": "..."
  }
}
```

In the DCQL query, an mdoc claim path is the namespace followed by the element name.

**You must configure your own [profile identifier resolver](openid4vp-advanced.html#2-the-profile-identifier):**
`ProfileIdResolver.issuerAndClaim(...)` only reads top-level SD-JWT VC claims and refuses, at initialization, a query
without SD-JWT VC credential. Request a namespace-qualified claim which identifies the user (a name alone does not)
and read it, for example:

```java
config.setProfileIdResolver(credentials -> {
    VerifiedCredential pid = credentials.getVerifiedCredentials().get(EudiPidQuery.PID).get(0);
    var namespace = (Map<?, ?>) pid.getClaims().get(EudiPidProfileDefinition.PID_DOCTYPE);
    return pid.getIssuer() + "|" + namespace.get(EudiPidProfileDefinition.PERSONAL_ADMINISTRATIVE_NUMBER);
});
```

Check that the PID provider issues that claim, and override `check(DcqlQuery)` to verify the query at startup.

## 7) Credential status

**Credential status is not checked automatically, and no status list is fetched.** When an authenticated MSO contains a
`status` object, configure `setStatusChecker((credential, status) -> { ... })`:

- the callback receives the verified credential and the MSO status map;
- it must throw if the status cannot be validated or is revoked.

Without a checker, credentials containing a status are rejected.

## 8) Scope and limits

This is format verification, not a complete EUDI trust-list, certificate-profile or HAIP conformance implementation.
walt.id provides the parsing, the COSE verification and the MSO digest checks; pac4j supplies the transaction binding,
the issuer trust and the profile mapping. The digest check is invoked separately for every item, because walt.id
0.11.0's bulk method returns after the first item.
