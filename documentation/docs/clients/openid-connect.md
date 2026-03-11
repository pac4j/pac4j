---
layout: doc
title: OpenID Connect
---

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
