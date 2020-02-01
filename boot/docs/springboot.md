---
layout: doc
title: Spring Boot Integration
---

## 0) Dependency

You need to use the following module: `pac4j-springboot`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-springboot</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 1) Auto-Configuration

The following auto-configuration classes are provided:

### Facebook

- `FacebookClientAutoConfiguration`

| Property                                              |
|-------------------------------------------------------|
| `pac4j.facebook.id`                                   |
| `pac4j.facebook.secret`                               |
| `pac4j.facebook.fields`                               |
| `pac4j.facebook.scope`                                |

### Twitter

- `TwitterClientAutoConfiguration`

| Property                                             |
|------------------------------------------------------|
| `pac4j.twitter.id`                                   |
| `pac4j.twitter.secret`                               |

### GitHub

- `GitHubClientAutoConfiguration`

| Property                                             |
|------------------------------------------------------|
| `pac4j.github.id`                                   |
| `pac4j.github.secret`                               |

### Google

- `GoogleClientAutoConfiguration`

| Property                                             |
|------------------------------------------------------|
| `pac4j.google.id`                                   |
| `pac4j.google.secret`                               |

### OAuth2

- `OAuth20ClientAutoConfiguration`

| Property                                             |
|------------------------------------------------------|
| `pac4j.oauth2.id`                                   |
| `pac4j.oauth2.secret`                               |
| `pac4j.oauth2.authUrl`                               |
| `pac4j.oauth2.tokenUrl`                               |
| `pac4j.oauth2.profileUrl`                             |
| `pac4j.oauth2.profilePath`                            |
| `pac4j.oauth2.profileId`                               |
| `pac4j.oauth2.scope`                                   |
| `pac4j.oauth2.withState`                               |
| `pac4j.oauth2.clientAuthenticationMethod`              |

### OIDC

- `OidcClientAutoConfiguration`

| Property                                              |
|-------------------------------------------------------|
| `pac4j.oidc.type`                                     |
| `pac4j.oidc.id`                                       |
| `pac4j.oidc.secret`                                   |
| `pac4j.oidc.scope`                                    |
| `pac4j.oidc.discoveryUri`                             |
| `pac4j.oidc.useNonce`                                 |
| `pac4j.oidc.preferredJwsAlgorithm`                    |
| `pac4j.oidc.maxClockSkew`                             |
| `pac4j.oidc.clientAuthenticationMethod`               |
| `pac4j.oidc.customParamKey`                           |
| `pac4j.oidc.customParamValue`                         |
| `pac4j.oidc.azureTenant`                              |
    
### CAS

- `CasClientAutoConfiguration`

| Property                                           |
|----------------------------------------------------|
| `pac4j.cas.loginUrl`                               |
| `pac4j.cas.protocol`                               |

### SAML2

- `Saml2ClientAutoConfiguration`

| Property                                           |
|----------------------------------------------------|
| `pac4j.saml2.keystorePassword`                     |
| `pac4j.saml2.privateKeyPassword`                   |
| `pac4j.saml2.keystorePath`                         |
| `pac4j.saml2.identityProviderMetadataPath`         |
| `pac4j.saml2.maximumAuthenticationLifetime`        |
| `pac4j.saml2.serviceProviderEntityId`              |
| `pac4j.saml2.serviceProviderMetadataPath`          |
| `pac4j.saml2.authnRequestBindingType`              |
| `pac4j.saml2.keystoreAlias`                        |

