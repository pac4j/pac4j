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

It only depends on `pac4j-core`, on `nimbus-jose-jwt` and on `spring-core` (for the resources holding the keys), plus BouncyCastle to read a key and its certificate chain from a keystore.

The module does not depend on `pac4j-oidc`: OpenID4VP shares the OAuth vocabulary (`client_id`, `nonce`, a signed request object) but none of the OpenID Connect mechanisms. There is no identity provider, no token endpoint and no user info: the wallet presents the credentials directly and the whole validation happens in the application.

## 2) The protocol, briefly

A presentation runs in three steps:

1. the application, as the verifier, hands the wallet a **request**: which credentials it wants, expressed in the Digital Credentials Query Language (DCQL), a `nonce` to bind the answer to, and the key to encrypt that answer to. The request is a JWT signed by the verifier, the *request object*
2. the wallet authenticates the verifier, shows the End-User what is asked, and, on consent, builds a **presentation** from its credentials: for a SD-JWT VC, the issuer-signed credential with only the disclosed claims, and a key binding JWT proving the holder holds the key and answers this very `nonce`
3. the wallet returns the presentation, encrypted to the verifier, which validates the issuer signature, the trust in that issuer, the revocation status and the key binding, and finally reads the claims.

How the request reaches the wallet depends on where the wallet is: invoked by a URL, on the same device or through a QR code, or through the Digital Credentials API of the browser. *pac4j* provides one client for each way (see below).

See the [OpenID for Verifiable Presentations 1.0](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html) specification, and in particular its [client identifier prefixes](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes), [response encryption](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_encryption) and [Digital Credentials API](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#dc_api) sections.

## 3) The EUDI wallet: ARF and HAIP

The European Digital Identity wallet is established by the eIDAS 2.0 regulation ([Regulation (EU) 2024/1183](https://eur-lex.europa.eu/eli/reg/2024/1183/oj)), which has every member state provide one to its citizens and has the relying parties register to use it: the access certificate of a verifier comes from that registration. Its technical architecture is the [Architecture and Reference Framework](https://eudi.dev/2.7.3/architecture-and-reference-framework-main/) (ARF) of the European Commission. For remote presentations, the ARF relies on OpenID4VP, and since that specification leaves many options open, it mandates a profile which closes them: the [OpenID4VC High Assurance Interoperability Profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) (HAIP). In the words of the ARF (section 5.7.4): *"the use of the profile for 'OpenID for Verifiable Presentations for IETF SD-JWT VC' specified in [HAIP] is necessary to ensure interoperability between Wallet Units and Relying Parties"*, and its annex 2 turns it into requirements: relying parties and wallet units *shall* comply with HAIP.

HAIP decides, among other things, that:

- the request object is signed, and the verifier authenticates with the `x509_hash` client identifier prefix: its identifier is the hash of its *relying party access certificate*, obtained through the registration process of a member state
- the answer is encrypted (`direct_post.jwt` or `dc_api.jwt`), to a key generated for each request, with ECDH-ES on P-256 and A128GCM or A256GCM
- the credentials are SD-JWT VCs or ISO mobile documents (mdoc), the person identification data being issued in both formats.

The [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java) pins those choices and maps the person identification data onto an `EudiPidProfile` with a typed accessor for each attribute (`getGivenName()`, `isAgeOver18()`...). Provide the access certificate, its key, the query and an appropriate profile identifier mapping (see below). Beware that the attribute identifiers of the PID have changed across versions of the ARF: check them against the version you target.

An EUDI wallet only talks to a registered relying party: the `EudiWalletClient` needs that access certificate, with its private key and its chain, loaded through the `keystore` property of the configuration, or through its `jwks` property when the key carries the chain in a `x5c` member. It cannot be tried against a real wallet without it.

## 4) Clients and configuration

### The clients

- [`OpenId4VpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpClient.java): the generic verifier, for a wallet invoked by a URL. The redirection is a `openid4vp://` URL: on the same device the browser follows it and the wallet opens; on another device, the application must display it as a QR code: it calls the protected URL via AJAX, and the wallet URL is returned in the `Location` header. The wallet then fetches the request object by HTTP (`request_uri`), or first posts what it supports and gets a request object built for it (`request_uri_method=post`), and posts its answer (`direct_post` or `direct_post.jwt`): all of it on the regular *pac4j* callback URL, without any session. The page waits for the answer and calls back when it has arrived, which turns the received presentation into a `VerifiableCredentialProfile`
- [`OpenId4VpDcApiClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpDcApiClient.java): the same verifier over the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browser, with a `OpenId4VpDcApiConfiguration`. The page never leaves: the client answers the page a JSON `{"request": "<signed request object>"}` which the page passes to `navigator.credentials.get()`, the browser hands it to the wallet with the authenticated origin of the page, and the page posts the answer back (`dc_api` or `dc_api.jwt`). Unlike the URL binding, the wallet never calls the application by itself: every request reaches the application from the browser, with its web session, as any other page request. In return, the request must be signed, and the origins the page runs from must be declared
- [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java): the `OpenId4VpClient` with the HAIP choices pinned and not configurable: the `x509_hash` prefix, so the certificate loaded from the keystore is the identity of the verifier, the `direct_post.jwt` response mode, and the `EudiPidProfile`. Use it to read the person identification data of an EUDI wallet, the generic client to talk to any other wallet or to another credential.

The following examples configure the presentation request. For persistent user identification, also request the
identifier claim required by your profile definition, as described under **The profile identifier** below.

**Example (EUDI wallet, with the relying party access certificate in a keystore):**

```java
OpenId4VpConfiguration config = new OpenId4VpConfiguration()
    .setKeystore(new KeystoreProperties()
        .setKeystorePath("/path/to/access-certificate.p12")
        .setKeystorePassword("...")
        .setKeyStoreAlias("rp"))
    .setDcqlQuery(EudiPidQuery.sdJwtVc(GIVEN_NAME, AGE_OVER_18));
EudiWalletClient client = new EudiWalletClient(config);
```

**Example (any wallet, over the Digital Credentials API, with a DID):**

```java
OpenId4VpDcApiConfiguration config = new OpenId4VpDcApiConfiguration();
config.setExpectedOrigins(List.of("https://verifier.example.org"))
    .setClientId("did:web:verifier.example.org")
    .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
    .setJwks(new JwksProperties().setJwksPath("/path/to/verifier.jwks").setKid("verifier-key"))
    .setDcqlQuery(new DcqlQuery()
        .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
            .setVctValues("urn:eudi:pid:1")
            .addClaim("family_name")
            .addClaim(new ClaimsQuery("age_over_18").withValues(true))));
OpenId4VpDcApiClient client = new OpenId4VpDcApiClient(config);
```

### The query

The credentials are asked for with a DCQL query, which the `dcql` package models: a `DcqlQuery` holds `CredentialQuery`
objects, one per credential, each with its format, its format-specific `meta` (`setVctValues` for a SD-JWT VC,
`setDoctypeValue` for a mobile document), the `ClaimsQuery` objects naming the claims by their path, and optional
`TrustedAuthority` constraints on the issuers; `CredentialSetQuery` objects say which combinations of credentials
satisfy the verifier.

Each credential query selects its format with `CredentialFormat.SD_JWT_VC` or `CredentialFormat.MSO_MDOC`.
Its `meta` must also specify the credential type: `setVctValues(...)` supplies a non-empty list of allowed SD-JWT VC
types, while `setDoctypeValue(...)` supplies the mdoc document type. Missing, empty or malformed type constraints
are rejected at initialization, whether the query is built programmatically or parsed from JSON.
The `EudiPidQuery` helpers below set both the format and the required type constraint.

The query is always configured with `setDcqlQuery(...)` and checked at initialization.
It is sent to the wallet unless a non-blank `scope` is configured; in that case, only the alias is sent,
and `dcqlQuery` must describe its equivalent request. Both cases use the same query to validate the response.
A query can be read back from JSON with `DcqlQuery.parse`.

For the person identification data of the EUDI wallet, `EudiPidQuery.sdJwtVc(...)` and `EudiPidQuery.mdoc(...)` build
the query from the attribute names of `EudiPidProfileDefinition`.

The two alternatives below configure the query part of the client:

```java
DcqlQuery query = EudiPidQuery.sdJwtVc(GIVEN_NAME, AGE_OVER_18);

// Send dcql_query to the wallet and validate the response against it.
OpenId4VpConfiguration dcqlConfig = new OpenId4VpConfiguration()
    .setDcqlQuery(query);

// Send scope to the wallet and validate the response against the equivalent query.
OpenId4VpConfiguration scopeConfig = new OpenId4VpConfiguration()
    .setDcqlQuery(query)
    .setScope("com.example.pid");
```

`com.example.pid` is an illustrative alias: the wallet must support it, and its definition must match `query`.
Configuring `scope` alone is rejected at initialization because `dcqlQuery` is required for response validation.

The authenticator also checks the verified credentials against the saved query: format and type, required claims and
their paths, claim values, claim sets, credential sets, multiplicity, holder binding and trusted-authority evidence.
Wallet-side value matching is best effort; pac4j enforces configured values on the verified claims. A mismatch rejects
the response, including when it concerns an optional credential that was returned.

The query can also be given as plain JSON text, which `setDcqlQuery(String)` parses into the same model, checked the same way at initialization:

```java
config.setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]},\"claims\":[{\"path\":[\"given_name\"]},{\"path\":[\"age_over_18\"]}]}]}");
```

### The configuration

The `OpenId4VpConfiguration` has the following properties, with the HAIP choices as defaults:

| Property | Default | Purpose |
|---|---|---|
| `clientIdPrefix` | `X509_HASH` | How the wallet authenticates the verifier: `X509_SAN_DNS` and `X509_HASH` by a X.509 certificate chain published in the request object (the trust anchor being removed from it), `DECENTRALIZED_IDENTIFIER` by a key looked up in the DID document, `REDIRECT_URI` by nothing at all, for the wallets which accept an unsigned request. The `verifier_attestation` and `openid_federation` prefixes are not supported |
| `clientId` | | The identifier of the verifier, without its prefix: a DNS name among the subject alternative names of the certificate for `X509_SAN_DNS`, the DID for `DECENTRALIZED_IDENTIFIER`. Computed and not settable for `X509_HASH` (the hash of the certificate) and `REDIRECT_URI` (the response URI of each request) |
| `jwks` | | A `JwksProperties`: the JWKS holding the signing key of the request object, with an optional `kid` to pick it. The key must carry a `x5c` member for the X.509 prefixes and a `kid` for the DID |
| `keystore` | | A `KeystoreProperties`: the keystore holding the signing key and its certificate chain, used when no JWKS is defined. The natural source for the X.509 prefixes |
| `requestObjectSigningKey` | | Read-only: the key resolved from the two sources above at initialization. Its algorithm is derived from the key itself, so a P-256 key signs with the ES256 that HAIP mandates |
| `responseMode` | `DIRECT_POST_JWT` | How the wallet returns the presentation: `DIRECT_POST` posted in clear or `DIRECT_POST_JWT` posted encrypted to the response URI, `DC_API` or `DC_API_JWT` through the browser. The encrypted modes generate an ephemeral ECDH-ES P-256 key for each request and accept A128GCM and A256GCM; a clear mode is announced once with a warning as it does not fit HAIP. A wallet answering in clear a request which asked for encryption is refused; a wallet answering with an error (`error`, `error_description`) makes the callback fail with that error |
| `dcqlQuery` | | Required, including when `scope` is configured. Always used for response validation, and sent to the wallet only when `scope` is absent or blank. The DCQL query, the only query language of OpenID4VP 1.0: which credentials, of which format, with which claims. A `DcqlQuery` built programmatically (`CredentialQuery`, `ClaimsQuery`, `TrustedAuthority`, `CredentialSetQuery`, with `EudiPidQuery` for the person identification data), or its JSON given as plain text to `setDcqlQuery(String)`. Checked at initialization against the rules of the specification: unique identifiers, sets referencing existing credentials, claim identifiers where claim sets need them |
| `scope` | | An alias for a DCQL query, sent instead of it: which aliases exist, and which query each stands for, is defined by an ecosystem, not by the specification, and a wallet may support none. Optional: when non-blank, only this alias is sent to the wallet. `dcqlQuery` must still be configured with the equivalent query to validate the response |
| `verifierInfo` | empty | Attestations about the verifier (`VerifierAttestation`: a `format`, the `data`, optional `credentialIds`), sent as the `verifier_info` parameter: what a third party says this verifier is entitled to ask, such as the registration certificate of an EUDI relying party, which the wallet may show to the End-User or check the request against. The formats belong to the ecosystem; nothing comes back |
| `credentialVerifiers` | `SdJwtVcVerifier` | The `CredentialVerifier` of each credential format (`SD_JWT_VC` is `dc+sd-jwt`, `MSO_MDOC` is `mso_mdoc`), registered with `addCredentialVerifier(verifier)`: it validates a presentation (issuer signature, trust in the issuer, revocation, key binding) and returns the disclosed claims. It must also report whether transaction-bound cryptographic holder binding was verified and which trusted authorities were established. One must be registered for each format the effective DCQL query asks for, and their formats are published as `vp_formats_supported` in the `client_metadata` of the request. `SdJwtVcVerifier` is provided, not yet implemented |
| `transactionLifetimeSeconds` | `300` | How long a request stays valid: stamped as the `exp` of the request object, and the date at which the pending transaction is dropped from the store |
| `transactionStore` | `VpTransactionStore` | The `Store` of the pending transactions, keyed by their identifier: the wallet legs carry no session and find the request there. In memory by default; use a shared store (Redis, Hazelcast...) behind several instances |
| `nonceGenerator` | 32 random characters | Generates the `nonce` sent to the wallet, which the presentation must be bound to |
| `transactionIdGenerator` | 32 random characters | Generates the transaction identifier, visible in the `request_uri` and in the response URI |
| `requestUriMethod` | `POST` | How the wallet fetches the request object: `GET` as RFC 9101 defines, or `POST` to let it first post its metadata (`wallet_metadata`, what it supports) and a nonce (`wallet_nonce`). The request object then carries the nonce back, and publishes only the credential formats and the response encryption algorithms the wallet declared, the request being refused when it declares none of them. Announced in the wallet URL as `request_uri_method=post`; a wallet which does not support it falls back to a GET, so nothing is lost. Only meaningful for a signed request over the URL binding |
| `walletScheme` | `openid4vp://` | The custom scheme of the URL invoking a wallet on the same device; a wallet may register another one (`eudi-openid4vp://` for the EUDI reference wallet, `haip://`...) |

The `OpenId4VpDcApiConfiguration` adds one property and closes two: its default response mode is `DC_API_JWT`, only `DC_API` and `DC_API_JWT` are accepted, and the `REDIRECT_URI` prefix is refused since this binding hands the browser a signed request.

| Property | Default | Purpose |
|---|---|---|
| `expectedOrigins` | | The origins (scheme, host and optional port, nothing more) the page calling the API runs from. The browser gives the wallet the actual origin of the page, which checks it is one of them: this is what ties a signed request to your site and defeats its replay from another one |

The clients themselves expose a `requestObjectBuilder` (`OpenId4VpRequestObjectBuilder`, or `DcApiRequestObjectBuilder` over the Digital Credentials API), to be overridden to add parameters to the request object, and the usual `redirectionActionBuilder`, `credentialsExtractor`, `authenticator` and `profileCreator` of an indirect client.

### The profile identifier

`OpenId4VpProfileCreator` passes the validated `VerifiablePresentationCredentials` to `ProfileDefinition.newProfile(...)`.
The definition creates the profile and assigns its identifier; the creator then adds the disclosed attributes.
The credentials retain their issuer and their DCQL query identifier, so a custom definition can select the credential
used for identification before attributes from different credentials are merged.

The generic clients use `OpenId4VpProfileDefinition`. It requires exactly one verified credential, a non-blank
issuer and a disclosed, non-blank string claim named `sub`. The profile identifier is
`base64url(issuer) + "." + base64url(subject)`, without padding, so different issuer/subject pairs remain distinct.
Missing identifiers or multiple credentials cause profile creation to fail.

The claim must be requested in DCQL. The application must ensure that it is stable, unique within its issuer and never
reassigned, as required by [OpenID4VP section 14.4](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4).
The claim name can be changed for an ecosystem that provides another top-level identifier:

```java
OpenId4VpProfileDefinition definition = new OpenId4VpProfileDefinition();
definition.setProfileId("account_id");
client.setProfileCreator(new OpenId4VpProfileCreator(client, definition));
```

Here, `account_id` is an illustrative claim that must be included in the configured query and satisfy those identity
guarantees. Override `computeProfileId(VerifiablePresentationCredentials)` to select among several credentials or read
a nested claim, such as an mdoc namespace member. A custom `ProfileDefinition` or `ProfileFactory` can also use the
credentials passed to `newProfile(...)` to assign an application-specific identifier.

`EudiPidProfileDefinition` inherits this mapping while creating an `EudiPidProfile`. Configure or specialize it for the
targeted PID ecosystem: neither the presence of `sub` nor the suitability of another PID attribute is assumed.
A presentation disclosing only a name or an age predicate does not supply a persistent user identifier.

## Response validation

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

The built-in SD-JWT VC cryptographic verifier remains unimplemented, and no built-in mdoc verifier is provided yet.
The common response checks do not replace issuer-signature, trust, status or holder-proof validation by a format verifier.

## Diagnostic logging

DEBUG logs follow transaction creation, request construction and capability negotiation, wallet response reception,
transaction consumption, decryption, credential verification, DCQL checks and profile creation.
Transaction identifiers link these stages; credential query identifiers identify the presentations being checked.
Rejections report the failing stage and, for common protocol checks, the reason. Custom verifier failures are logged
by exception type without copying their potentially sensitive messages.

For example, enable these categories in Logback:

```xml
<logger name="org.pac4j.openid4vp.config" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.redirect" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.request" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.credentials" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.dcql" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.verifier" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.profile.creator" level="DEBUG"/>
```

These logs describe stages and outcomes without dumping wallet URLs, request JWTs, presentations, keys, nonces,
state values or disclosed claim values. Client and profile-definition classes also inherit DEBUG logs from pac4j-core
which can include credentials, profiles or converted attribute values. Enabling DEBUG for the entire module or
framework also enables those inherited logs; the categories above allow tracing the protocol without enabling them.
