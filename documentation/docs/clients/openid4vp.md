---
layout: doc
title: OpenID for Verifiable Presentations (EUDI wallet, eIDAS 2.0)
---

*pac4j* allows you to authenticate users with a digital wallet, using the OpenID for Verifiable Presentations protocol (OpenID4VP).
Your application acts as a *verifier*: it asks the wallet to present credentials, such as the person identification data of the European Digital Identity wallet (EUDI wallet).

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

The [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java) pins those choices, so that a configuration only has to provide the access certificate, its key and the query, and maps the person identification data onto an `EudiPidProfile` with a typed accessor for each attribute (`getGivenName()`, `isAgeOver18()`...). Beware that the attribute identifiers of the PID have changed across versions of the ARF: check them against the version you target.

An EUDI wallet only talks to a registered relying party: the `EudiWalletClient` needs that access certificate, with its private key and its chain, loaded through the `keystore` property of the configuration, or through its `jwks` property when the key carries the chain in a `x5c` member. It cannot be tried against a real wallet without it.

## 4) Clients and configuration

### The clients

- [`OpenId4VpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpClient.java): the generic verifier, for a wallet invoked by a URL. The redirection is a `openid4vp://` URL: on the same device the browser follows it and the wallet opens; on another device, the application must display it as a QR code: it calls the protected URL via AJAX, and the wallet URL is returned in the `Location` header. The wallet then fetches the request object by HTTP (`request_uri`), or first posts what it supports and gets a request object built for it (`request_uri_method=post`), and posts its answer (`direct_post` or `direct_post.jwt`): all of it on the regular *pac4j* callback URL, without any session. The page waits for the answer and calls back when it has arrived, which turns the received presentation into a `VerifiableCredentialProfile`
- [`OpenId4VpDcApiClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpDcApiClient.java): the same verifier over the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browser, with a `OpenId4VpDcApiConfiguration`. The page never leaves: the client answers the page a JSON `{"request": "<signed request object>"}` which the page passes to `navigator.credentials.get()`, the browser hands it to the wallet with the authenticated origin of the page, and the page posts the answer back (`dc_api` or `dc_api.jwt`). Unlike the URL binding, the wallet never calls the application by itself: every request reaches the application from the browser, with its web session, as any other page request. In return, the request must be signed, and the origins the page runs from must be declared
- [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java): the `OpenId4VpClient` with the HAIP choices pinned and not configurable: the `x509_hash` prefix, so the certificate loaded from the keystore is the identity of the verifier, the `direct_post.jwt` response mode, a `SdJwtVcVerifier` when none is registered, and the `EudiPidProfile`. Use it to read the person identification data of an EUDI wallet, the generic client to talk to any other wallet or to another credential.

**Example (EUDI wallet, with the relying party access certificate in a keystore):**

```java
OpenId4VpConfiguration config = new OpenId4VpConfiguration()
    .setKeystore(new KeystoreProperties()
        .setKeystorePath("/path/to/access-certificate.p12")
        .setKeystorePassword("...")
        .setKeyStoreAlias("rp"))
    .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]},\"claims\":[{\"path\":[\"given_name\"]},{\"path\":[\"age_over_18\"]}]}]}");
EudiWalletClient client = new EudiWalletClient(config);
```

**Example (any wallet, over the Digital Credentials API, with a DID):**

```java
OpenId4VpDcApiConfiguration config = new OpenId4VpDcApiConfiguration();
config.setExpectedOrigins(List.of("https://verifier.example.org"))
    .setClientId("did:web:verifier.example.org")
    .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
    .setJwks(new JwksProperties().setJwksPath("/path/to/verifier.jwks").setKid("verifier-key"))
    .setDcqlQuery("...")
    .addCredentialVerifier(new SdJwtVcVerifier());
OpenId4VpDcApiClient client = new OpenId4VpDcApiClient(config);
```

### The configuration

The `OpenId4VpConfiguration` has the following properties, with the HAIP choices as defaults:

| Property | Default | Purpose |
|---|---|---|
| `clientIdPrefix` | `X509_SAN_DNS` | How the wallet authenticates the verifier: `X509_SAN_DNS` and `X509_HASH` by a X.509 certificate chain published in the request object (the trust anchor being removed from it), `DECENTRALIZED_IDENTIFIER` by a key looked up in the DID document, `REDIRECT_URI` by nothing at all, for the wallets which accept an unsigned request. The `verifier_attestation` and `openid_federation` prefixes are not supported |
| `clientId` | | The identifier of the verifier, without its prefix: a DNS name among the subject alternative names of the certificate for `X509_SAN_DNS`, the DID for `DECENTRALIZED_IDENTIFIER`. Computed and not settable for `X509_HASH` (the hash of the certificate) and `REDIRECT_URI` (the response URI of each request) |
| `jwks` | | A `JwksProperties`: the JWKS holding the signing key of the request object, with an optional `kid` to pick it. The key must carry a `x5c` member for the X.509 prefixes and a `kid` for the DID |
| `keystore` | | A `KeystoreProperties`: the keystore holding the signing key and its certificate chain, used when no JWKS is defined. The natural source for the X.509 prefixes |
| `requestObjectSigningKey` | | Read-only: the key resolved from the two sources above at initialization. Its algorithm is derived from the key itself, so a P-256 key signs with the ES256 that HAIP mandates |
| `responseMode` | `DIRECT_POST_JWT` | How the wallet returns the presentation: `DIRECT_POST` posted in clear or `DIRECT_POST_JWT` posted encrypted to the response URI, `DC_API` or `DC_API_JWT` through the browser. The encrypted modes generate an ephemeral ECDH-ES P-256 key for each request and accept A128GCM and A256GCM; a clear mode is announced once with a warning as it does not fit HAIP |
| `dcqlQuery` | | The DCQL query, as a JSON object in a string: which credentials, of which format, with which claims. The only query language of OpenID4VP 1.0 |
| `supportedFormats` | `[SD_JWT_VC]` | The credential formats requested from the wallet (`SD_JWT_VC` is `dc+sd-jwt`, `MSO_MDOC` is `mso_mdoc`), published in the `client_metadata` of the request. A verifier must be registered for each of them |
| `credentialVerifiers` | | The `CredentialVerifier` for each format, registered with `addCredentialVerifier(verifier)`: it validates a presentation (issuer signature, trust in the issuer, revocation, key binding) and returns the disclosed claims. `SdJwtVcVerifier` is provided, not yet implemented |
| `transactionLifetimeSeconds` | `300` | How long a request stays valid: stamped as the `exp` of the request object, and the date at which the pending transaction is dropped from the store |
| `transactionStore` | `VpTransactionStore` | The `Store` of the pending transactions, keyed by their identifier: the wallet legs carry no session and find the request there. In memory by default; use a shared store (Redis, Hazelcast...) behind several instances |
| `nonceGenerator` | 32 random characters | Generates the `nonce` sent to the wallet, which the presentation must be bound to |
| `transactionIdGenerator` | 32 random characters | Generates the transaction identifier, visible in the `request_uri` and in the response URI |
| `requestUriMethod` | `POST` | How the wallet fetches the request object: `GET` as RFC 9101 defines, or `POST` to let it first post its metadata (`wallet_metadata`, what it supports) and a nonce (`wallet_nonce`) that the request object carries back. Announced in the wallet URL as `request_uri_method=post`; a wallet which does not support it falls back to a GET, so nothing is lost. Only meaningful for a signed request over the URL binding |
| `walletScheme` | `openid4vp://` | The custom scheme of the URL invoking a wallet on the same device; a wallet may register another one (`eudi-openid4vp://` for the EUDI reference wallet, `haip://`...) |

The `OpenId4VpDcApiConfiguration` adds one property and closes two: its default response mode is `DC_API_JWT`, only `DC_API` and `DC_API_JWT` are accepted, and the `REDIRECT_URI` prefix is refused since this binding hands the browser a signed request.

| Property | Default | Purpose |
|---|---|---|
| `expectedOrigins` | | The origins (scheme, host and optional port, nothing more) the page calling the API runs from. The browser gives the wallet the actual origin of the page, which checks it is one of them: this is what ties a signed request to your site and defeats its replay from another one |

The clients themselves expose a `requestObjectBuilder` (`OpenId4VpRequestObjectBuilder`, or `DcApiRequestObjectBuilder` over the Digital Credentials API), to be overridden to add parameters to the request object, and the usual `redirectionActionBuilder`, `credentialsExtractor`, `authenticator` and `profileCreator` of an indirect client.
