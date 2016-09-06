---
layout: doc
title: Release notes&#58;
---

**pac4j 1.9.2**:

- the CAS support has been upgraded: the CAS configuration is defined via the `CasConfiguration`, the new `DirectCasProxyClient` must be used to validate proxy tickets, the front channel logout is supported by the `CasSingleSignOutHandler`, the OAuth support is compatible with CAS v5
- the JWT support has been upgraded: `SignatureConfiguration` classes allow to define HMac, RSA or Elliptic Curve signatures
- the OpenID Connect support has been upgraded: the OIDC configuration is defined via the `OidcConfiguration`, all standard claims are supported in the `OidcProfile`, most flows are supported
- CORS (AJAX) requests can be controlled via the `CorsAuthorizer` and its default pre-defined `allowAjaxRequests` name
- Profile attribute can be checked via the `RequireAnyAttributeAuthorizer`
- the `AjaxRequestResolver`,  `CallbackUrlResolver` and `AuthorizationGenerator` can be defined at the `Clients` level for all defined clients

**pac4j 1.9.1**:

- the `Authenticator` and `ProfileCreator` have access to the web context
- the signature of the SAML authentication requests can be disabled

**pac4j 1.9.0**:

- Upgraded to Java 8 as well as all most dependency versions
- Removed useless concepts: client type, client cloning capabilities, raw data, direct/indirect redirection, proxy configuration for OAuth clients (to be set at the JVM level or by overriding the `OAuthRequest` class)
- All security logics are now available in the core via the `SecurityLogic`, `CallbackLogic` and `ApplicationLogoutLogic` components
- Any client can be built using the `RedirectActionBuilder`, `CredentialsExtractor`, `Authenticator` and `ProfileCreator` concepts (`DirectClientV2` and `IndirectClientV2`): to be re-used to build asynchronous clients
- `CredentialsExtractor`, `Authenticator`, `ProfileCreator` and `Authorizer` can throw `HttpAction` (previously named `RequiresHttpAction`) to break the flow and handle custom use cases
- Typed id are now defined using the full class name (with package): "org.pac4j.oauth.profile.facebook.FacebookProfile#id" instead of "FacebookProfile#id" (use the `getOldTypedId()` method to get the old value)
- Comparisons for clients / authorizers names are case insensitive and trimmed
- Most integration tests have been replaced by manual tests (RunXXX classes)
- Updated OpenID Connect support (`GoogleOidClient` and `AzureAdClient`)

**pac4j 1.8.8**:

- Support default client in `Clients`
- Properly handle Javascript calls on `FormClient`
- Add `Resource` concept from Spring (in SAML support)

**pac4j 1.8.7**:

- Ability to define the ticket validator for the CAS REST authenticator
- Option to disable SAML requests signing

**pac4j 1.8.6**:

- New DirectFormClient
- Improved CAS support: callbackUrlResolver applies on CAS prefix url + the LocalCachingAuthenticator can be used with the CasRest*Client
- The `RelativeCallbackUrlResolver` properly handles HTTPS requests
- Roles / permissions are kept through JWT

**pac4j 1.8.5**:

- Remove the `setResponseCharacterEncoding` from the `WebContext`

**pac4j 1.8.4**:

- Improved SAML support security configuration

**pac4j 1.8.2 & 1.8.3**:

- Improved JWT support
- Added Microsft Azure AD (OpenID Connect) support

**pac4j 1.8.1**:

- More authorizers: IP check, HTTP method check, profile type verification, Spring Security like security filters (cache control, Xframe...)
- Updated CSRF protection support
- Path exclusions support
- new AnonymousClient for advanced use cases
- Updated OAuth, CAS, SAML and OpenID Connect supports
- new session store mechanism
- new configuration module (build clients via properties only)
- Customizable callback urls

**pac4j 1.8.0**:

- Support REST authentication (basic auth, request parameter, request header, IP, cookie)
- New authentication mechanisms (JWT, LDAP, RDBMS, MongoDB, Stormpath)
- AJAX requests are automatically detected
- Arbitrary attributes are allowed on profiles (even with a definition)
- Upgrade SAML support
- Upgrade CAS support (protocol v3, REST API)
- Handle authorizations (on roles, permissions, CSRF protection...)
- Bring default guidelines (DefaultClientFinder, DefaulutAuthorizationChecker)
- Add ok.ru support
- Remove the LinkedIn OAuth v1 support (use the OAuth version 2 support)

[&#9656; Older versions...](/docs/release-notes-older.html)
