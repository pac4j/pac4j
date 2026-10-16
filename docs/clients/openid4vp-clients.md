---
layout: doc
title: OpenID4VP / Clients and configuration
seo_title: "OpenID4VP Java clients: EUDI wallet and Digital Credentials API | pac4j"
description: "Configure OpenID4VP clients in Java for wallet URLs, QR codes or the Digital Credentials API. Set signing keys, response modes and EUDI wallet options."
---

Choose how your Java application opens the wallet: a URL or QR code with `OpenId4VpClient`, or the browser's Digital Credentials API with `OpenId4VpDcApiClient`. `EudiWalletClient` fixes the settings for the EUDI PID profile; the configuration below explains what each choice requires.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and verifiers</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-advanced.html">DCQL queries, user profiles and logging</a></p>

<hr/>

## 1) Clients

- [`OpenId4VpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpClient.java): the generic verifier, for a wallet invoked by a URL. The redirection is a `openid4vp://` URL: on the same device the browser follows it and the wallet opens; on another device, the application must display it as a QR code: it calls the protected URL via AJAX, and the wallet URL is returned in the `Location` header. The wallet then fetches the request object by HTTP (`request_uri`), or first posts what it supports and gets a request object built for it (`request_uri_method=post`), and posts its answer (`direct_post` or `direct_post.jwt`): all of it on the regular *pac4j* callback URL, without any session. On the same device, `setUseRedirectUri(true)` makes the wallet return the original browser to the pac4j callback, which completes authentication and restores the saved request. For a QR code on another device, leave the option at its default `false`: the computer page can poll `client.getPresentationStatus(ctx)` (`PENDING`, `RECEIVED` or `EXPIRED`) and continue to the callback when the wallet has answered. See [Returning to the application](#3-returning-to-the-application)
- [`OpenId4VpDcApiClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/OpenId4VpDcApiClient.java): the same verifier over the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browser, with a `OpenId4VpDcApiConfiguration`. The page never leaves: the client answers the page a JSON `{"request": "<signed request object>"}` which the page passes to `navigator.credentials.get()`, the browser hands it to the wallet with the authenticated origin of the page, and the page posts the answer back (`dc_api` or `dc_api.jwt`). Unlike the URL binding, the wallet never calls the application by itself: every request reaches the application from the browser, with its web session, as any other page request. In return, the request must be signed, and the origins the page runs from must be declared
- [`EudiWalletClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid4vp/src/main/java/org/pac4j/openid4vp/client/EudiWalletClient.java): the `OpenId4VpClient` with the HAIP choices pinned and not configurable: the `x509_hash` prefix, so the certificate loaded from the keystore is the identity of the verifier, the `direct_post.jwt` response mode, and the `EudiPidProfile`, whose identifier claim must be chosen by the application. Use it to read the person identification data of an EUDI wallet, the generic client to talk to any other wallet or to another credential.

The following examples configure the presentation request, including the claim identifying the user: a client whose
DCQL query cannot return that claim refuses to initialize. See [The profile identifier](openid4vp-advanced.html#2-the-profile-identifier).

**Example (EUDI wallet, with the relying party access certificate in a keystore):**

```java
OpenId4VpConfiguration config = new OpenId4VpConfiguration()
    .setKeystore(new KeystoreProperties("/path/to/access-certificate.p12")
        .setKeystorePassword("...")
        .setKeyStoreAlias("rp"))
    .setDcqlQuery(EudiPidQuery.sdJwtVc(PERSONAL_ADMINISTRATIVE_NUMBER, GIVEN_NAME, AGE_OVER_18))
    // the PID defines no sub: name the claim identifying the user, here one that the targeted PID provider issues
    .setProfileIdResolver(ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER));
EudiWalletClient client = new EudiWalletClient(config);
```

`personal_administrative_number` is optional in the PID and not issued by every provider: check it against the target
ecosystem, or set a `ProfileIdResolver` (see [The profile identifier](openid4vp-advanced.html#2-the-profile-identifier)).

**Example (any wallet, over the Digital Credentials API, with a DID):**

```java
OpenId4VpDcApiConfiguration config = new OpenId4VpDcApiConfiguration();
config.setExpectedOrigins(List.of("https://verifier.example.org"))
    .setClientId("did:web:verifier.example.org")
    .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
    .setJwks(new JwksProperties("/path/to/verifier.jwks").setKid("verifier-key"))
    .setDcqlQuery(new DcqlQuery()
        .addCredential(new CredentialQuery("badge", CredentialFormat.SD_JWT_VC)
            .setVctValues("https://credentials.example.org/employee-badge")
            // sub: the claim the default profile definition identifies the user with
            .addClaim("sub")
            .addClaim("family_name")
            .addClaim(new ClaimsQuery("role").withValues("manager"))));
OpenId4VpDcApiClient client = new OpenId4VpDcApiClient(config);
```

## 2) The configuration

The `OpenId4VpConfiguration` has the following properties, with the HAIP choices as defaults:

| Property | Default | Purpose |
|---|---|---|
| `clientIdPrefix` | `X509_HASH` | How the wallet authenticates the verifier: `X509_SAN_DNS` and `X509_HASH` by a X.509 certificate chain published in the request object (the trust anchor being removed from it), `DECENTRALIZED_IDENTIFIER` by a key looked up in the DID document, `REDIRECT_URI` by nothing at all, for the wallets which accept an unsigned request. The `verifier_attestation` and `openid_federation` prefixes are not supported |
| `clientId` | | The identifier of the verifier, without its prefix: a DNS name among the subject alternative names of the certificate for `X509_SAN_DNS`, the DID for `DECENTRALIZED_IDENTIFIER`. Computed and not settable for `X509_HASH` (the hash of the certificate) and `REDIRECT_URI` (the response URI of each request) |
| `jwks` | | A `JwksProperties`: the JWKS holding the signing key of the request object, with an optional `kid` to pick it. The key must carry a `x5c` member for the X.509 prefixes and a `kid` for the DID |
| `keystore` | | A `KeystoreProperties`: the keystore holding the signing key and its certificate chain, used when no JWKS is defined. The natural source for the X.509 prefixes |
| `requestObjectSigningKey` | | Read-only: the key resolved from the two sources above at initialization. Its algorithm is derived from the key itself, so a P-256 key signs with the ES256 that HAIP mandates |
| `responseMode` | `DIRECT_POST_JWT` | How the wallet returns the presentation: `DIRECT_POST` posted in clear or `DIRECT_POST_JWT` posted encrypted to the response URI, `DC_API` or `DC_API_JWT` through the browser. The encrypted modes generate an ephemeral ECDH-ES P-256 key for each request and accept A128GCM and A256GCM; a clear mode is announced once with a warning as it does not fit HAIP. A wallet answering in clear a request which asked for encryption is refused; a wallet answering with an error (`error`, `error_description`) makes the callback fail with that error |
| `dcqlQuery` | | Required, including when `scope` is configured. Always used for response validation, and sent to the wallet only when `scope` is absent or blank. The [DCQL query](openid4vp-advanced.html#1-the-query), the only query language of OpenID4VP 1.0: which credentials, of which format, with which claims. A `DcqlQuery` built programmatically (`CredentialQuery`, `ClaimsQuery`, `TrustedAuthority`, `CredentialSetQuery`, with `EudiPidQuery` for the person identification data), or its JSON given as plain text to `setDcqlQuery(String)`. Checked at initialization against the rules of the specification: unique identifiers, sets referencing existing credentials, claim identifiers where claim sets need them |
| `scope` | | An alias for a DCQL query, sent instead of it: which aliases exist, and which query each stands for, is defined by an ecosystem, not by the specification, and a wallet may support none. Optional: when non-blank, only this alias is sent to the wallet. `dcqlQuery` must still be configured with the equivalent query to validate the response |
| `verifierInfo` | empty | Attestations about the verifier (`VerifierAttestation`: a `format`, the `data`, optional `credentialIds`), sent as the `verifier_info` parameter: what a third party says this verifier is entitled to ask, such as the registration certificate of an EUDI relying party, which the wallet may show to the End-User or check the request against. The formats belong to the ecosystem; nothing comes back |
| `requestObjectIssuer` | none | The `iss` claim of the signed request object, sent as is. Redundant with the `client_id` claim and ignored by a conforming wallet ("if it is present, the Wallet MUST ignore it"), so none is sent by default; set it for a wallet which still reads it, with the value that wallet expects, the client identifier or, with the `decentralized_identifier` prefix, the bare DID |
| `credentialVerifiers` | `SdJwtVcVerifier`, `MdocVerifier` | The interchangeable `CredentialVerifier` of each credential format (`SD_JWT_VC` is `dc+sd-jwt`, `MSO_MDOC` is `mso_mdoc`), registered with `addCredentialVerifier(verifier)`. One must be registered for each requested format. The built-in verifiers require an optional library and an explicit issuer trust configuration; see [Configuring credential verifiers](openid4vp-verifiers.html#2-configuring-credential-verifiers). |
| `transactionLifetimeSeconds` | `300` | How long a request stays valid: stamped as the `exp` of the request object, and the date at which the pending transaction is dropped from the store |
| `transactionStore` | `VpTransactionStore` | The `Store` of the pending transactions, keyed by their identifier: the wallet legs carry no session and find the request there. The answer of the wallet is kept apart, under the key `<identifier>#response`, with the same expiration, so that no other leg can overwrite it. In memory by default; use a shared store (Redis, Hazelcast...) behind several instances |
| `nonceGenerator` | 32 random characters | Generates the `nonce` sent to the wallet, which the presentation must be bound to |
| `transactionIdGenerator` | 32 random characters | Generates the transaction identifier, visible in the `request_uri` and in the response URI |
| `profileIdResolver` | `issuerAndClaim("sub")`, none for `EudiWalletClient` | How the user is identified from the verified credentials, see [The profile identifier](openid4vp-advanced.html#2-the-profile-identifier). Checked against `dcqlQuery` at initialization |
| `stateGenerator` | 64 random characters | Generates the `state` sent with every request invoking a wallet by URL (not over the Digital Credentials API) and checked on the response: it is what binds the response to the request when a presentation comes without holder binding |
| `requestUriMethod` | `POST` | How the wallet fetches the request object: `GET` as RFC 9101 defines, or `POST` to let it first post its metadata (`wallet_metadata`, what it supports) and a nonce (`wallet_nonce`). The request object then carries the nonce back, and publishes only the credential formats and the response encryption algorithms the wallet declared, the request being refused when no common encryption algorithm or credential format remains, or when the common formats cannot satisfy the required DCQL credential selections (including alternatives and optional sets). This format check also applies when a `scope` represents the query; it cannot determine whether the wallet actually holds the requested credentials. Announced in the wallet URL as `request_uri_method=post`; a wallet which does not support it falls back to a GET, so nothing is lost. Only meaningful for a signed request over the URL binding |
| `useRedirectUri` | `false` | Set `setUseRedirectUri(true)` for an automatic same-device browser return. Before invoking the wallet, the pac4j callback URL is captured in the transaction. After the wallet posts its answer, the JSON response contains this callback URL as `redirect_uri`, with a fresh `response_code` parameter. The wallet opens it in the browser, and pac4j checks the code, completes authentication and redirects to the saved request URL (or the callback default URL if no request was saved). The application does not need to relay the code. Does not apply to the Digital Credentials API |
| `walletScheme` | `openid4vp://` | The custom scheme of the URL invoking a wallet on the same device; a wallet may register another one (`eudi-openid4vp://` for the EUDI reference wallet, `haip://`...) |

The `OpenId4VpDcApiConfiguration` adds one property and closes two: its default response mode is `DC_API_JWT`, only `DC_API` and `DC_API_JWT` are accepted, and the `REDIRECT_URI` prefix is refused since this binding hands the browser a signed request.

| Property | Default | Purpose |
|---|---|---|
| `expectedOrigins` | | The origins (scheme, host and optional port, nothing more) the page calling the API runs from. The browser gives the wallet the actual origin of the page, which checks it is one of them: this is what ties a signed request to your site and defeats its replay from another one |

The clients themselves expose a `requestObjectBuilder` (`OpenId4VpRequestObjectBuilder`, or `DcApiRequestObjectBuilder` over the Digital Credentials API), to be overridden to add parameters to the request object, and the usual `redirectionActionBuilder`, `credentialsExtractor`, `authenticator` and `profileCreator` of an indirect client. The `OpenId4VpAuthenticator` builds the user profile, with its `profileDefinition` (`OpenId4VpProfileDefinition`, or `EudiPidProfileDefinition` for the `EudiWalletClient`); the default profile creator returns it, and a custom one may enrich it.


## 3) Returning to the application

For a wallet opened on the **same device**, enable the return on your existing configuration:

```java
configuration.setUseRedirectUri(true);
```

This option is available to both `OpenId4VpClient` and `EudiWalletClient`, and defaults to `false`.

1. Before invoking the wallet, pac4j captures its final callback URL in the transaction, including the client name
   parameter or path chosen by the callback URL resolver.
2. After the wallet posts a valid presentation (or a wallet error), pac4j answers with HTTP 200 and a JSON object
   containing `redirect_uri`: that callback URL plus a fresh random `response_code`.
3. The wallet opens the URL in the browser. The callback finds the pending transaction through the browser session,
   checks the code and, for a successful presentation, saves the already validated profile in the session.
4. The callback consumes the transaction and restores the saved request. If no request was saved, it uses its
   configured default URL. The final application URL does not receive the `response_code`.

The application does not need a manual return link or code to relay the `response_code`. The wallet must return to
**the original browser with its session cookie**; another browser or an isolated webview cannot claim that session's
presentation using the code alone. Start the flow in the device's **default browser, using its regular profile**
(avoid private browsing and embedded browsers): the wallet may open the return URL in the default browser even
when authentication started in a different one. The return must use the same browser and profile throughout.

The `redirect_uri` is a member of the **JSON response to the wallet's POST**, as defined in
[OpenID4VP section 8.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.2).
It is not an authorization request parameter: the request still carries `response_uri`, where the wallet posts the
presentation. The browser return URL carries no `vp_tx`; that identifier is read from the session.

For a **QR code on another device**, keep `useRedirectUri` at `false`: the original browser is on the computer,
while a wallet redirect would open a browser on the phone. The computer page can poll for completion and continue to
the callback itself.
The Digital Credentials API keeps the browser in the exchange and does not use this option.
