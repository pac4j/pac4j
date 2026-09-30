---
layout: doc
title: OpenID for Verifiable Presentations (EUDI wallet, eIDAS 2.0)
seo_title: "OpenID4VP for Java: EUDI Wallet authentication | pac4j"
description: "Integrate OpenID for Verifiable Presentations with pac4j in Java. Explore wallet authentication, EUDI Wallet profiles, queries and client configuration."
---

`pac4j-openid4vp` provides an **OpenID4VP client for Java**: it allows you to authenticate users with a digital wallet, using the OpenID for Verifiable Presentations protocol.
Your application acts as a *verifier*: it asks the wallet to present credentials, such as the person identification data of the European Digital Identity wallet (EUDI wallet).

**Requires a wallet compatible with [OpenID4VP 1.0 final](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html).** Earlier drafts using `client_id_scheme` or
`presentation_definition` are not supported.

## 1) Dependency

You need to use the following module: `pac4j-openid4vp`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-openid4vp</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

OpenID4VP shares the OAuth vocabulary with [OpenID Connect](openid-connect.html) (`client_id`, `nonce`, signed request objects), but the wallet presents credentials directly to the application: there is no token endpoint or user info endpoint, and the module does not depend on `pac4j-oidc`.

To validate SD-JWT VCs with `SdJwtVcVerifier`, explicitly add the following dependency, which is not included by default:

```xml
<dependency>
    <groupId>eu.europa.ec.eudi</groupId>
    <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
    <version>0.20.1</version>
</dependency>
```

Without this dependency, `SdJwtVcVerifier` throws an `OpenId4VpException` when validating a credential, with a message identifying the dependency to add.

To validate mdocs with `MdocVerifier`, add **walt.id 0.11.0** from its Maven repository,
[https://maven.waltid.dev/releases](https://maven.waltid.dev/releases). See the
[mdoc dependency, Kotlin alignment and trust configuration](openid4vp-verifiers.html#22-built-in-mdoc-verifier).
Both verifiers use optional libraries; add the libraries for the formats your application needs.

## 2) The protocol, briefly

- **Request:** configure a DCQL query describing the credentials and attributes your application needs.
- **Consent:** the wallet asks the user to approve sharing them.
- **Validation:** pac4j and the configured credential verifiers validate the response before creating a user profile.

Choose a [client](openid4vp-clients.html) for a wallet opened by **URL or QR code**, or through the browser's
**Digital Credentials API**. Requests are signed and responses encrypted by default.
Configure [trusted issuers and credential validation](openid4vp-verifiers.html) before accepting presentations.

## 3) The EUDI wallet: ARF and HAIP

The European Digital Identity wallet (EUDI wallet) is part of the eIDAS 2.0 framework. Its [Architecture and Reference Framework (ARF)](https://eudi.dev/2.7.3/architecture-and-reference-framework-main/)
uses the [High Assurance Interoperability Profile (HAIP) 1.0](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html)
to define its OpenID4VP requirements.

**Use `EudiWalletClient` for the EUDI PID profile:**

- **Fixed settings:** signed requests with `x509_hash` and encrypted responses with `direct_post.jwt`.
- **Signing material:** supply the relying party access certificate, its private key and certificate chain through
  `keystore`, or through `jwks` with an `x5c` chain. Follow the target wallet's registration and trust requirements.
- **Requested data:** configure a DCQL query matching the wallet's credential format and attribute names.
- **User profile:** returns an `EudiPidProfile`. **Choose the claim identifying the user**: the PID defines no `sub` and
  none of its attributes is assumed to be a stable identifier, so the client refuses to initialize until a
  [profile identifier](openid4vp-advanced.html#2-the-profile-identifier) resolver is configured, such as
  `ProfileIdResolver.issuerAndClaim(...)` with a claim requested in the DCQL query.

**Use `OpenId4VpClient` if the wallet requires different settings**, such as `x509_san_dns`, or for credentials other
than the PID. See [Clients and configuration](openid4vp-clients.html).

**SD-JWT VC and mdoc validation are provided through optional libraries.** Selecting `EudiWalletClient` does not provide
a complete EUDI trust or status validation setup: see [Configuring credential verifiers](openid4vp-verifiers.html#2-configuring-credential-verifiers).

## 4) Usage

- [OpenID4VP clients and wallet configuration](openid4vp-clients.html)
- [Response validation and verifiers](openid4vp-verifiers.html)
- [DCQL queries, user profiles and diagnostic logging](openid4vp-advanced.html)
