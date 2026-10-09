---
layout: blog
title: What's new in pac4j v6.6?
author: Jérôme LELEU
date: November 2026
tags: [rel]
draft: true
seo_title: "pac4j 6.6: OpenID4VP, resource loading, properties and protocol logs | pac4j"
description: "Discover pac4j 6.6: the new OpenID4VP support for digital wallets, more resilient SAML/OIDC metadata loading, ResourceProperties, JWK helpers and unified protocol logging."
---

On paper, v6.6 is "just" a minor release in the v6 line, with deprecations preparing the move to v7. There are still dependency and behavior changes to check when upgrading, especially if you use the test components or JWT encryption without a signature.

In practice, it is one of the biggest releases of the v6 line: a new protocol, more resilient metadata loading, a cleaner way to define keystores and files, new key management tools and protocol logs that should be much easier to read.

Let's dive in!


## 1) OpenID4VP: pac4j meets digital wallets

This is the headline of the release: the new `pac4j-openid4vp` module lets your application authenticate users with a **digital wallet**, using [OpenID for Verifiable Presentations 1.0](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html).

Many thanks again to [NLnet](https://nlnet.nl/project/pac4j-eIDAS2.0/) for funding this work.

Instead of a username and password, the user opens their wallet, sees what your application asks for (a name, a date of birth, "over 18"...), consents, and the wallet presents signed credentials. Your application acts as a *verifier*: it validates the presentation against your query and trust configuration before creating the user profile.

With eIDAS 2.0, EU member states must offer European Digital Identity wallets (EUDI wallets) by the end of 2026. Their use remains voluntary. pac4j provides the protocol integration for applications preparing to accept them; production deployments still need the ecosystem's trust and certificate configuration. See the [European Commission's FAQ](https://ec.europa.eu/digital-building-blocks/sites/spaces/EUDIGITALIDENTITYWALLET/pages/713526976/FAQ).

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-openid4vp</artifactId>
    <version>6.6.0</version>
</dependency>
```

### a) Three clients

- `EudiWalletClient`: an EUDI wallet, with key settings from the [High Assurance Interoperability Profile (HAIP)](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) built in: signed requests (`x509_hash`) and encrypted responses (`direct_post.jwt`)
- `OpenId4VpClient`: an OpenID4VP 1.0 wallet, with configurable protocol settings
- `OpenId4VpDcApiClient`: the wallet is reached through the **Digital Credentials API** of the browser, no QR code, no redirection.

With the first two clients, the wallet is invoked by a link on the same device, or by a QR code from a desktop browser. With a QR code, the page can poll pac4j for the wallet's response and continue automatically; on the same device, the user returns to the browser once the wallet is done, with automatic redirection on the roadmap.

```java
final var config = new OpenId4VpConfiguration()
    .setKeystore(new KeystoreProperties("classpath:verifier.p12")
        .setKeyStoreType("PKCS12")
        .setKeyStoreAlias("rp")
        .setKeystorePassword("changeit")
        .setPrivateKeyPassword("changeit"))
    .setDcqlQuery(EudiPidQuery.sdJwtVc(GIVEN_NAME, FAMILY_NAME, PERSONAL_ADMINISTRATIVE_NUMBER));
config.addCredentialVerifier(new SdJwtVcVerifier()
    .addTrustedIssuer(new CertificateTrustedIssuer(new KeystoreProperties("classpath:pid-providers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"))
        .setCertificateRevocationLists(List.of(new ResourceProperties("classpath:pid-providers.crl")))));
config.setProfileIdResolver(ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER));

final var client = new EudiWalletClient(config);
```

Use `PERSONAL_ADMINISTRATIVE_NUMBER` as an identifier only if your PID provider issues it and guarantees that it is stable, unique and never reassigned.

### b) Ask exactly what you need

What you ask for is a [DCQL](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#name-digital-credentials-query-l) query: you can [write it in JSON or build it in Java](/docs/clients/openid4vp-advanced.html#1-the-query). `EudiPidQuery` gives you the person identification data (PID) of the EUDI wallet in one line, and the full language is available for the rest: claim paths, expected values, alternatives (`claim_sets`, `credential_sets`), multiple credentials, trusted authorities...

For example, to accept either of the two supported PID formats:

```java
new DcqlQuery()
    .addCredential(EudiPidQuery.sdJwtVcCredential(GIVEN_NAME, FAMILY_NAME).setId("pid_sdjwt"))
    .addCredential(EudiPidQuery.mdocCredential(GIVEN_NAME, FAMILY_NAME).setId("pid_mdoc"))
    .addCredentialSet(new CredentialSetQuery().addOption("pid_sdjwt").addOption("pid_mdoc"));
```

For this query, configure a profile identifier resolver that handles both formats: the top-level claims of SD-JWT VC and the namespace-qualified claims of mdoc.

The DCQL constraints are not left to the wallet: pac4j checks them again on what was actually presented.

### c) Trust nothing by default

Two credential formats are supported, each one through an optional library you add to your application:

- **SD-JWT VC**, credentials based on selective disclosure JWTs, verified with the EUDI SD-JWT library: issuer signature, disclosures, validity and key binding (nonce, audience, time window)
- **mdoc** (ISO/IEC 18013-5), the format used for mobile driving licences, verified with the walt.id library: issuer authentication, digests, validity and device signature.

No issuer is trusted until you configure it: by a certificate chain (`CertificateTrustedIssuer`, with your truststore and its revocation lists) or by keys (`KeysTrustedIssuer`, an issuer and its JWKS). A credential carrying status information is rejected unless you configure a status checker.

On the verifier side, four client identifier prefixes are supported (`x509_hash`, `x509_san_dns`, `decentralized_identifier` and `redirect_uri` for unsigned requests), as well as the negotiation with the wallet (`request_uri_method=post`) and the verifier attestations (`verifier_info`).

### d) Not complete yet, but already solid

Some parts of the specification are still on the roadmap: transaction data, redirection back to the browser at the end of a same-device flow, the `verifier_attestation` and `openid_federation` prefixes, and other credential formats. The module does not provide complete EUDI trust-list or HAIP conformance validation.

We are currently testing the module against real wallets: please do the same and send us your feedback!

The full documentation starts here: [OpenID for Verifiable Presentations](/docs/clients/openid4vp.html).


## 2) More resilient metadata loading

SAML identity provider metadata and OpenID Connect discovery documents are loaded through `SpringResourceLoader`. Its behavior has been reworked to handle identity provider outages:

- **before anything has been loaded**, calls to `load()` use a 2-second interval between change checks
- **after the first successful load**, that interval becomes 60 seconds
- **if a reload fails**, the error is logged and the last successfully loaded metadata remain available. Monitor prolonged outages, since these metadata may become stale.

These checks happen when the loader is called, rather than on a background timer. A first-load failure still propagates: this does not guarantee that an application eagerly initializing its clients will start while the identity provider is unavailable.

The delay between two checks is no longer configurable: the `minimumDelayBetweenChangeDetectionInMilliseconds` property is deprecated and ignored, with a warning in the logs.


## 3) `ResourceProperties` and the rise of the `*Properties`

With v6.4, `KeystoreProperties` and `JwksProperties` appeared. v6.6 goes one step further with `ResourceProperties`, which defines any file by its path: `classpath:`, `file:`, `http(s)://` or a plain file path.

```java
new ResourceProperties("classpath:metadata-okta.xml");
```

`KeystoreProperties` and `JwksProperties` now extend it, so they all share the same `getResource()`, `setResource(...)` and `setPath(...)` methods, and the same constructor:

```java
new KeystoreProperties("classpath:samlKeystore.jks");
new JwksProperties("file:./metadata/rpjwks.jwks");
```

The OpenID4VP support is built on them from the ground up, and the SAML support now uses them too. Remember the SAML configuration of the 6.4 version? It now reads:

```java
final var cfg = new SAML2Configuration(
    new KeystoreProperties("classpath:samlKeystore.jks")
        .setKeystorePassword("pac4j-demo-passwd")
        .setPrivateKeyPassword("pac4j-demo-passwd"),
    new ResourceProperties("classpath:metadata-okta.xml"));
```

The identity provider and service provider metadata are available through `getIdentityProviderMetadata()` and `getServiceProviderMetadata()`; the JWKS of an OpenID federation trust anchor are available through `getJwks()`.

Many constructors and setters are therefore deprecated: `setKeystorePath`, `setJwksPath`, `setIdentityProviderMetadataPath`, `setServiceProviderMetadataResource`, the `SAML2Configuration` constructors based on a `Resource` or a path... They still work in v6 and will be removed in v7.


## 4) JWK everywhere

JWKs became first-class citizens with v6.4. In v6.6, the `JwkHelper` grows to serve the protocols binding the identity of a signer to a certificate, such as OpenID4VP:

- `resolveSigningKey`: get the signing key from a JWKS if it is defined, or from a keystore otherwise, using the logic previously held by the OpenID federation support
- `generateKey`: create a signature key for a given algorithm (an EC P-256 key for ES256, for example)
- `loadJwkFromOrCreateJwks` now takes the algorithm of the key it creates when none exists yet, the default staying RSA-2048
- `buildSignedJwt` can publish the certificate chain of the key in the `x5c` header.

```java
final var key = JwkHelper.resolveSigningKey(config.getJwks(), config.getKeystore(), JWSAlgorithm.ES256);
final var requestObject = JwkHelper.buildSignedJwt(claims, key, JWSAlgorithm.ES256, "oauth-authz-req+jwt", true);
```


## 5) Protocol logs you can actually read

Ever struggled to understand why authentication fails while digging through hundreds of debug lines?

In v6.6, messages exchanged through SAML, CAS, OAuth, OpenID Connect and OpenID4VP are all logged the same way: raw, one line per message, with the direction (`>>>` sent, `<<<` received) and the other party, on a dedicated logger per protocol:

- `PROTOCOL_MESSAGE.SAML`
- `PROTOCOL_MESSAGE.CAS`
- `PROTOCOL_MESSAGE.OAUTH`
- `PROTOCOL_MESSAGE.OIDC`
- `PROTOCOL_MESSAGE.OPENID4VP`.

```
>>> browser authentication request URL: https://op.example.org/authorize?response_type=code&client_id=…
<<< browser GET client_name=OidcClient&code=…&state=…
>>> OpenID provider POST https://op.example.org/token grant_type=authorization_code&code=…
<<< OpenID provider 200 {"access_token":"…","id_token":"…","token_type":"Bearer","expires_in":3600}
```

Enable one protocol, or all of them at once through their parent logger:

```properties
logging.level.PROTOCOL_MESSAGE=DEBUG
```

The secrets of your application (client secret, client assertion, CAS REST password) are masked, but the messages are logged as they travel, tokens included: enable these logs to diagnose, not permanently in production.


## 6) And a few more things

- most of the test components have moved to the new `pac4j-test` module
- pac4j is built against Spring v7, but you may still use older versions of Spring
- `pac4j-core` no longer needs Guava, unless you explicitly use a `GuavaStore`
- the OIDC login hints can be provided dynamically as a `login_hint` request attribute
- the `DefaultSessionLogoutHandler` now relies on the new `ConcurrentMapStore` (30-minute expiration, no maximum size) instead of a `GuavaStore`
- only empty credentials or a `CredentialsException` now block further authentication attempts of an indirect client: any other exception allows a retry
- the `JwtAuthenticator` now rejects an RSA or EC encryption without any signature, and warns for the other encryption-only configurations: check your configuration if you rely on encryption alone.

Check the [release notes](/docs/release-notes.html) for the full list, and enjoy pac4j v6.6!
