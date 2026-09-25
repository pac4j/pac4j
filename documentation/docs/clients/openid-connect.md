---
layout: doc
title: OpenID Connect (OIDC) client for Java
seo_title: "OpenID Connect (OIDC) client for Java | pac4j"
description: "Add OpenID Connect login to Java applications with pac4j-oidc. Configure OIDC clients, identity providers, token handling and OpenID Federation."
---

`pac4j-oidc` provides an **OpenID Connect (OIDC) client for Java**. Your application sends the user to an identity provider to sign in; pac4j handles the response and creates an `OidcProfile` for your application.

It has been tested with providers such as CAS server, Google, Microsoft Entra ID (Azure AD), Okta, IdentityServer, MitreID and Keycloak. Use the generic `OidcClient`, or a provider-specific client when one is available.


## 1) Dependency

You need to use the following module: `pac4j-oidc`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) Usage

Start with an `OidcConfiguration`: it holds the client ID, secret and provider settings. Pass it to `OidcClient`, then connect the client to your application through a [pac4j framework integration](/implementations.html).

- [Basic configuration and OIDC clients](openid-connect-clients.html)
- [Advanced configuration settings](openid-connect-config.html)
- [OIDC federation support](openid-connect-federation.html)

For authentication with credentials presented by a digital wallet, see the [OpenID4VP client for Java](openid4vp.html). That flow uses a separate module and does not call an OIDC token endpoint.
