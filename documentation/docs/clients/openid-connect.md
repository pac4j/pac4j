---
layout: doc
title: OpenID Connect (OIDC) client for Java
seo_title: "OpenID Connect (OIDC) client for Java | pac4j"
description: "Add OpenID Connect authentication to Java applications with pac4j-oidc. Find an OIDC tutorial, provider configuration and advanced client options."
---

OpenID Connect (OIDC) adds an identity layer to OAuth 2.0. Your application redirects the user to an identity provider and uses the returned ID token to establish who signed in.

**[How to secure a Java application with OIDC (using Spring Boot)](/how-to-secure-a-java-application-with-oidc.html)** — follow a complete Spring Boot example, then use the reference below for the full configuration.

*pac4j* allows you to login using the OpenID Connect protocol v1.0.

It has been tested with various OpenID Connect providers: CAS server, Google, AzureAD, Okta, IdentityServer, MitreID, Keycloak...


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

To define any OIDC client (`OidcClient`), you will need to define the OIDC configuration first (`OidcConfiguration`):

- [Basic configuration and OIDC clients](openid-connect-clients.html)
- [Advanced configuration settings](openid-connect-config.html)
- [OIDC federation support](openid-connect-federation.html)
