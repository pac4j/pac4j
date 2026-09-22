---
layout: doc
title: OpenID for Verifiable Presentations (EUDI wallet, eIDAS 2.0)
---

*pac4j* allows you to authenticate users with a digital wallet, using the OpenID for Verifiable Presentations protocol (OpenID4VP).
Your application acts as a *verifier*: it asks the wallet to present credentials, such as the person identification data of the European Digital Identity wallet (EUDI wallet).

The module implements the **final version 1.0** of [OpenID4VP](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html), not its earlier drafts, which differ on the wire: the drafts had a separate `client_id_scheme` parameter where 1.0 prefixes the `client_id` itself, and a `presentation_definition` query language which 1.0 replaced with DCQL. A wallet built on a draft will not understand these requests. The EUDI profile it follows is the [OpenID4VC High Assurance Interoperability Profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) 1.0.

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

OpenID4VP shares the OAuth vocabulary with OpenID Connect (`client_id`, `nonce`, signed request objects), but the wallet presents credentials directly to the application: there is no token endpoint or user info endpoint, and the module does not depend on `pac4j-oidc`.

To validate SD-JWT VCs with `SdJwtVcVerifier`, explicitly add the following dependency, which is not included by default:

```xml
<dependency>
    <groupId>eu.europa.ec.eudi</groupId>
    <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
    <version>0.20.1</version>
</dependency>
```

Without this dependency, `SdJwtVcVerifier` throws an `OpenId4VpException` when validating a credential, with a message identifying the dependency to add.

## 2) The protocol, briefly

A presentation runs in three steps:

1. the application, as the verifier, hands the wallet a **request**: which credentials it wants, expressed in the Digital Credentials Query Language (DCQL), a `nonce` to bind the answer to, and the key to encrypt that answer to. The request is a JWT signed by the verifier, the *request object*
2. the wallet authenticates the verifier, shows the End-User what is asked, and, on consent, builds a **presentation** from its credentials: for a SD-JWT VC, the issuer-signed credential with only the disclosed claims, and a key binding JWT proving the holder holds the key and answers this very `nonce`
3. the wallet returns the presentation, encrypted to the verifier, which validates the issuer signature, the trust in that issuer, the revocation status and the key binding, and finally reads the claims.

How the request reaches the wallet depends on where the wallet is: invoked by a URL, on the same device or through a QR code, or through the Digital Credentials API of the browser. *pac4j* provides one client for each way (see [Clients and configuration](openid4vp-clients.html)).

See the [OpenID for Verifiable Presentations 1.0](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html) specification, and in particular its [client identifier prefixes](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes), [response encryption](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_encryption) and [Digital Credentials API](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#dc_api) sections.

## 3) The EUDI wallet: ARF and HAIP

The European Digital Identity wallet is established by the eIDAS 2.0 regulation ([Regulation (EU) 2024/1183](https://eur-lex.europa.eu/eli/reg/2024/1183/oj)), which has every member state provide one to its citizens and has the relying parties register to use it: the access certificate of a verifier comes from that registration. Its technical architecture is the [Architecture and Reference Framework](https://eudi.dev/2.7.3/architecture-and-reference-framework-main/) (ARF) of the European Commission. For remote presentations, the ARF relies on OpenID4VP, and since that specification leaves many options open, it mandates a profile which closes them: the [OpenID4VC High Assurance Interoperability Profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) (HAIP). In the words of the ARF (section 5.7.4): *"the use of the profile for 'OpenID for Verifiable Presentations for IETF SD-JWT VC' specified in [HAIP] is necessary to ensure interoperability between Wallet Units and Relying Parties"*, and its annex 2 turns it into requirements: relying parties and wallet units *shall* comply with HAIP.

HAIP decides, among other things, that:

- the request object is signed, and the verifier authenticates with the `x509_hash` client identifier prefix: its identifier is the hash of its *relying party access certificate*, obtained through the registration process of a member state
- the answer is encrypted (`direct_post.jwt` or `dc_api.jwt`), to a key generated for each request, with ECDH-ES on P-256 and A128GCM or A256GCM
- the credentials are SD-JWT VCs or ISO mobile documents (mdoc), the person identification data being issued in both formats.

The [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java) pins those choices and maps the person identification data onto an `EudiPidProfile` with a typed accessor for each attribute (`getGivenName()`, `isAgeOver18()`...). Provide the access certificate, its key, the query and an appropriate [profile identifier mapping](openid4vp-advanced.html#2-the-profile-identifier). Beware that the attribute identifiers of the PID have changed across versions of the ARF: check them against the version you target.

An EUDI wallet only talks to a registered relying party: the `EudiWalletClient` needs that access certificate, with its private key and its chain, loaded through the `keystore` property of the configuration, or through its `jwks` property when the key carries the chain in a `x5c` member. It cannot be tried against a real wallet without it.

## 4) Usage

- [Clients and configuration](openid4vp-clients.html)
- [Response validation and credential verifiers](openid4vp-verifiers.html)
- [Advanced](openid4vp-advanced.html)
