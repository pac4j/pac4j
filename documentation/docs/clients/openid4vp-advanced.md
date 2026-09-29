---
layout: doc
title: OpenID4VP / DCQL queries, user profiles and logging
seo_title: "OpenID4VP in Java: DCQL queries and user profiles | pac4j"
description: "Build OpenID4VP DCQL queries in Java, request EUDI PID attributes, choose stable user identifiers and trace wallet authentication with diagnostic logs."
---

What should the wallet share, and how will your application identify the user afterwards? The DCQL query defines the credentials and claims you request; the profile definition turns the verified result into a user identifier. This page covers both, then shows which logs to enable when a presentation fails.

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-clients.html">Clients and configuration</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and verifiers</a></p>

<hr/>

## 1) The query

**Always configure `setDcqlQuery(...)`: it defines what to request and what pac4j will accept.**
Use a `DcqlQuery` object or a JSON string; both are checked at initialization.

- **SD-JWT VC:** use `CredentialFormat.SD_JWT_VC` and `setVctValues(...)` with the allowed credential types.
  A credential also matches when one of these types is among the `aka_vcts` its issuer signed, such as a national
  PID declaring the general PID type
  ([SD-JWT VC, section 2.2.2.2](https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.2)).
  An inheritance only declared through `extends` in the type metadata is not followed: no metadata is fetched.
- **mdoc:** use `CredentialFormat.MSO_MDOC` and `setDoctypeValue(...)` with the document type.
- **Claims:** use `addClaim(...)` for a claim path, or a `ClaimsQuery` with `withValues(...)` to restrict its values.
  An mdoc path contains the namespace followed by the attribute name.
- **More complex requests:** use `TrustedAuthority` for issuer authority constraints and `CredentialSetQuery`
  for allowed combinations of credentials.

**For EUDI PID**, `EudiPidQuery.sdJwtVc(...)` and `EudiPidQuery.mdoc(...)` set the format and type for you.
Pass the attribute names from `EudiPidProfileDefinition`, checking that the target wallet supports them.
These helpers request the **PID**, not other credentials such as a separate age attestation.

**Optional scope alias:** if the wallet supports one, `setScope(...)` sends that alias instead of the query.
You must still configure the equivalent DCQL query for response validation:

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

`com.example.pid` is an example alias, not a standard scope. Configuring `scope` alone is rejected.

**JSON alternative:**

```java
config.setDcqlQuery("""
    {
      "credentials": [{
        "id": "pid",
        "format": "dc+sd-jwt",
        "meta": {"vct_values": ["urn:eudi:pid:1"]},
        "claims": [
          {"path": ["given_name"]},
          {"path": ["age_over_18"]}
        ]
      }]
    }
    """);
```

**Every returned credential must satisfy the query**, including optional credentials that the wallet chooses to send.
pac4j enforces requested values on verified claims; wallet-side matching alone is not sufficient.
See [Response validation](openid4vp-verifiers.html#1-response-validation).

## 2) The profile identifier

**Request an identifier claim in DCQL if you need to identify a user.** A name or an age predicate alone does not
provide a persistent user identifier. Choose an identifier that is **stable, unique within its issuer and never
reassigned** ([OpenID4VP section 14.4](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4)).

The `OpenId4VpAuthenticator` builds the profile once the presentation is validated, as the other pac4j authenticators
do: the disclosed claims become attributes, and the `profileIdResolver` of the configuration gives the identifier,
as `attributeAsId` does for SAML.

**The default, `ProfileIdResolver.issuerAndClaim("sub")`** (for `OpenId4VpClient` and `OpenId4VpDcApiClient`):

- Reads a top-level claim of an SD-JWT VC, `sub` here.
- **Checks the DCQL query when the client initializes:** at least one SD-JWT VC credential query must be able to
  return that claim, by listing it in its `claims` or by listing no claims at all (the wallet then returns "only the
  claims that are mandatory to present", see
  [OpenID4VP section 6.4.1](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#selecting_claims)).
  Otherwise the initialization fails, rather than every login.
- Requires **exactly one verified credential**, a non-blank issuer and a disclosed, non-blank string claim.
- Builds the ID as `base64url(issuer) + "." + base64url(claim)`, without padding, to distinguish issuers. For a
  credential validated through its `x5c` chain, the issuer is the subject of the leaf certificate
  (see [Trusted issuers](openid4vp-verifiers.html#211-trusted-issuers)).
- **Fails the validation** if these requirements are not met, including when several credentials are returned.

To use another top-level claim, request it in DCQL and name it:

```java
config.setProfileIdResolver(ProfileIdResolver.issuerAndClaim("account_id"));
```

`account_id` is an example; use a claim that meets the identity requirements above.

**EUDI PID:** `EudiWalletClient` has **no default**: the PID defines no `sub`, and no PID attribute is assumed to be a
stable identifier. It refuses to initialize until a resolver is configured, such as
`ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER)` where the PID provider issues that claim.

**Multiple credentials, nested claims (including mdoc) or any other identifier:** implement `ProfileIdResolver`, a
single method receiving the verified credentials, indexed by DCQL query identifier:

```java
// an illustrative nested claim: {"account": {"id": "..."}} in the credential answering the "badge" query
config.setProfileIdResolver(credentials -> {
    VerifiedCredential badge = credentials.getVerifiedCredentials().get("badge").get(0);
    return badge.getIssuer() + "|" + ((Map<?, ?>) badge.getClaims().get("account")).get("id");
});
```

It checks nothing at initialization by default; override its `check(DcqlQuery)` to verify at startup that the query
returns what it needs. To enrich the profile afterwards, set a `ProfileCreator` on the client: it receives the
credentials, whose `getUserProfile()` is the profile built by the authenticator.

## 3) Diagnostic logging

DEBUG logs follow transaction creation, request construction and capability negotiation, wallet response reception,
transaction consumption, decryption, credential verification, DCQL checks and profile creation.
Transaction identifiers link these stages; credential query identifiers identify the presentations being checked.
Rejections report the failing stage and, for common protocol checks, the reason. Custom verifier failures are logged
by exception type without copying their potentially sensitive messages.
Wallet error responses are logged at WARN on receipt, including their error code and optional description,
without waiting for the browser callback.

For example, enable these categories in Logback:

```xml
<logger name="org.pac4j.openid4vp.config" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.redirect" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.request" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.credentials" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.dcql" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.verifier" level="DEBUG"/>
<logger name="org.pac4j.openid4vp.profile" level="DEBUG"/>
```

These logs describe stages and outcomes without dumping wallet URLs, request JWTs, presentations, keys, nonces,
state values or disclosed claim values. Client and profile-definition classes also inherit DEBUG logs from pac4j-core
which can include credentials, profiles or converted attribute values. Enabling DEBUG for the entire module or
framework also enables those inherited logs; the categories above allow tracing the protocol without enabling them.
