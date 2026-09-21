---
layout: doc
title: OpenID4VP / Clients and configuration
---

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and credential verifiers</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">Advanced</a></p>

<hr/>

## 1) Clients

- [`OpenId4VpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpClient.java): the generic verifier, for a wallet invoked by a URL. The redirection is a `openid4vp://` URL: on the same device the browser follows it and the wallet opens; on another device, the application must display it as a QR code: it calls the protected URL via AJAX, and the wallet URL is returned in the `Location` header. The wallet then fetches the request object by HTTP (`request_uri`), or first posts what it supports and gets a request object built for it (`request_uri_method=post`), and posts its answer (`direct_post` or `direct_post.jwt`): all of it on the regular *pac4j* callback URL, without any session. The page waits for the answer and calls back when it has arrived, which turns the received presentation into a `VerifiableCredentialProfile`
- [`OpenId4VpDcApiClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpDcApiClient.java): the same verifier over the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browser, with a `OpenId4VpDcApiConfiguration`. The page never leaves: the client answers the page a JSON `{"request": "<signed request object>"}` which the page passes to `navigator.credentials.get()`, the browser hands it to the wallet with the authenticated origin of the page, and the page posts the answer back (`dc_api` or `dc_api.jwt`). Unlike the URL binding, the wallet never calls the application by itself: every request reaches the application from the browser, with its web session, as any other page request. In return, the request must be signed, and the origins the page runs from must be declared
- [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java): the `OpenId4VpClient` with the HAIP choices pinned and not configurable: the `x509_hash` prefix, so the certificate loaded from the keystore is the identity of the verifier, the `direct_post.jwt` response mode, and the `EudiPidProfile`. Use it to read the person identification data of an EUDI wallet, the generic client to talk to any other wallet or to another credential.

The following examples configure the presentation request. For persistent user identification, also request the
identifier claim required by your profile definition, as described under [The profile identifier](#4-the-profile-identifier) below.

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

## 2) The query

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

## 3) The configuration

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
| `credentialVerifiers` | `SdJwtVcVerifier` | The interchangeable `CredentialVerifier` of each credential format (`SD_JWT_VC` is `dc+sd-jwt`, `MSO_MDOC` is `mso_mdoc`), registered with `addCredentialVerifier(verifier)`. One must be registered for each requested format. The built-in SD-JWT VC verifier requires the optional EUDI dependency and explicit issuer trust configuration; see [Configuring credential verifiers](openid4vp-verifiers.html#2-configuring-credential-verifiers). |
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

## 4) The profile identifier

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

