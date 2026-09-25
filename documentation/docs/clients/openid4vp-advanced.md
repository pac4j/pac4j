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
provide a persistent user identifier.

The default `OpenId4VpProfileDefinition`:

- Requires **exactly one verified credential**, a non-blank issuer and a disclosed, non-blank string `sub`.
- Builds the ID as `base64url(issuer) + "." + base64url(sub)`, without padding, to distinguish issuers.
- **Fails profile creation** if these requirements are not met, including when several credentials are returned.

Choose an identifier that is **stable, unique within its issuer and never reassigned**
([OpenID4VP section 14.4](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4)).
To use another top-level claim, request it in DCQL and configure its name:

```java
OpenId4VpProfileDefinition definition = new OpenId4VpProfileDefinition();
definition.setProfileId("account_id");
client.setProfileCreator(new OpenId4VpProfileCreator(client, definition));
```

`account_id` is an example; use a claim that meets the identity requirements above.

- **Multiple credentials or nested claims (including mdoc):** override `computeProfileId(VerifiablePresentationCredentials)`.
  You can select a verified credential by its DCQL query identifier and issuer before attributes are merged.
- **Application-specific mapping:** a custom `ProfileDefinition` or `ProfileFactory` can assign the ID in `newProfile(...)`.
- **EUDI PID:** `EudiPidProfileDefinition` uses the same default mapping. Configure or override it for your wallet;
  a PID does not necessarily contain `sub`.

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
<logger name="org.pac4j.openid4vp.profile.creator" level="DEBUG"/>
```

These logs describe stages and outcomes without dumping wallet URLs, request JWTs, presentations, keys, nonces,
state values or disclosed claim values. Client and profile-definition classes also inherit DEBUG logs from pac4j-core
which can include credentials, profiles or converted attribute values. Enabling DEBUG for the entire module or
framework also enables those inherited logs; the categories above allow tracing the protocol without enabling them.
