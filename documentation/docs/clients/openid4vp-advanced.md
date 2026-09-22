---
layout: doc
title: OpenID4VP / Advanced
---

See also:

<p> &nbsp; &#9656; <a href="openid4vp.html">OpenID4VP overview</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-clients.html">Clients and configuration</a></p>
<p> &nbsp; &#9656; <a href="openid4vp-verifiers.html">Response validation and credential verifiers</a></p>

<hr/>

## 1) The query

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

## 2) The profile identifier

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

## 3) Diagnostic logging

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
