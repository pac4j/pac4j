---
layout: doc
title: Release notes (dead versions)&#58;
---

**v2.3.1**:

- Saving the profile in session can be disabled on the callback endpoint

**v2.2.1**:

- Improve SAML support: fix generated binding, handle AttributeConsumingServiceIndex in authentication request, add capability to add authentication-related attributes to the user profile with specific attributes added to the profile...

**v2.1.0**:

- Added Kerberos support
- Removed Stormpath support
- The password encoders and LDAP/SQL authenticators can be defined via properties through the `PropertiesConfigFactory`
- Supports CouchDB for authentication and user management
- REST API `Authenticator`
- In case of an unauthorized AJAX request, the redirection URL to the identity server is added as the `Location` header to the 401 error
- Allow passive authentication for SAML

**v2.0.0**:

- All clients are built using sub-components (`RedirectActionBuilder`, `CredentialsExtractor` , `Authenticator`, `ProfileCreator` and `LogoutActionBuilder`): the `IndirectClientV2` and `DirectClientV2` are renamed as `IndirectClient` and `DirectClient` (and the existing `IndirectClient` and `DirectClient` components are removed)
- The  `LdapProfileService`, `DbProfileService` and `MongoProfileService` replace the deprecated `LDapAuthenticator`, `DbAuthenticator` and `MongoAuthenticator` to validate username/password and create, update or delete users in a LDAP, in a relational database and in a MongoDB database
- A user profile can be linked to another user profile
- The `LogoutLogic` (formerly `ApplicationLogoutLogic`) handles the application and identity provider logout
- The `WebContext` directly relies on the `SessionStore` whose capabilities are upgraded to handle back-channel logout
- The `AuthorizationGenerator` takes the `WebContext` as input and can return a new built profile
- Using Spring framework `Resource` components for SAML files/URLs
- The session renewal is properly handled by clients (and especially CAS)
- Caches are backed via a `Store` component
- Upgrade the OAuth support with Scribe v3.3 and rebuild all clients on the generic `OAuth10Client` and `OAuth20Client`
- User profiles are simple POJOs, the `AttributesDefinition` is replaced by the `ProfileDefinition`
- CAS specificities (Kryo serialization, `toString` service ticket validation) are handled via the `InternalAttributeHandler`
- Authenticators may throw the checked `CredentialsException`
- Only two `PasswordEncoder` wrappers are available: one for Spring Security Crypto, the other one for Shiro
- Added new matcher `PathMatcher` and deprecated `ExcludedPathMatcher`

**v1.9.7**:

- Security fix on `JwtAuthenticator`

**v1.9.6**:

- Added LinkedIn support in `PropertiesConfigFactory`
- `CallbackLogic` and `ApplicationLogoutLogic` can be set at the `Config` level

**v1.9.5**:

- Various bug fixes

**v1.9.4**:

- Critical security issue since the version 1.9.2 on the `NopPasswordEncoder` regarding the `MongoAuthenticator` and the `DbAuthenticator`: upgrading is mandatory

**v1.9.3**:

- Bug fixes (`Authenticator` initialization, `resource:`/`classpath:` prefixes in the SAML support...)
- New `HeaderMatcher` and `HttpMethodMatcher`
- The `Config` holds a `SecurityLogic`
- The OpenID Connect configuration can be done without a discovery URL
- The Dropbox support uses the OAuth protocol v2.0
- The expiration time is checked on JWT, as well as the existence of the subject
- The `IpExtractor` can work on an alternative header name
- A specific profile can be built by the `AuthenticatorProfileCreator`

**v1.9.2**:

- the CAS support has been upgraded: the CAS configuration is defined via the `CasConfiguration`, the new `DirectCasProxyClient` must be used to validate proxy tickets, the front channel logout is supported by the `CasSingleSignOutHandler`, the OAuth support is compatible with CAS v5
- the JWT support has been upgraded: `SignatureConfiguration` classes allow to define HMac, RSA or Elliptic Curve signatures
- the OpenID Connect support has been upgraded: the OIDC configuration is defined via the `OidcConfiguration`, all standard claims are supported in the `OidcProfile`, most flows are supported
- CORS (AJAX) requests can be controlled via the `CorsAuthorizer` and its default pre-defined `allowAjaxRequests` name
- Profile attribute can be checked via the `RequireAnyAttributeAuthorizer`
- the `AjaxRequestResolver`,  `CallbackUrlResolver` and `AuthorizationGenerator` can be defined at the `Clients` level for all defined clients
- new implementations for the `PasswordEncoder` are available for Spring Security, Shiro or JBCrypt.

**v1.9.1**:

- the `Authenticator` and `ProfileCreator` have access to the web context
- the signature of the SAML authentication requests can be disabled

**v1.9.0**:

- Upgraded to Java 8 as well as all most dependency versions
- Removed useless concepts: client type, client cloning capabilities, raw data, direct/indirect redirection, proxy configuration for OAuth clients (to be set at the JVM level or by overriding the `OAuthRequest` class)
- All security logics are now available in the core via the `SecurityLogic`, `CallbackLogic` and `ApplicationLogoutLogic` components
- Any client can be built using the `RedirectActionBuilder`, `CredentialsExtractor`, `Authenticator` and `ProfileCreator` concepts (`DirectClientV2` and `IndirectClientV2`): to be re-used to build asynchronous clients
- `CredentialsExtractor`, `Authenticator`, `ProfileCreator` and `Authorizer` can throw `HttpAction` (previously named `RequiresHttpAction`) to break the flow and handle custom use cases
- Typed id are now defined using the full class name (with package): "org.pac4j.oauth.profile.facebook.FacebookProfile#id" instead of "FacebookProfile#id" (use the `getOldTypedId()` method to get the old value)
- Comparisons for clients/authorizers names are case insensitive and trimmed
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
- Roles/permissions are kept through JWT

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

**pac4j 1.7.1**:

- the SAML support is improved, but unfortunately, it only works in J2E environment (j2e-pac4j and spring-security-pac4j libraries)

**pac4j 1.7.0**:

- Improve roles management
- Remove Google OpenID support
- Add Strava (OAuth 2) support
- Add OpenID Connect support

**pac4j 1.6.0**:

- Update to scribe 1.3.6
- SAML improvments
- CAS client update (v3.3.3 for a security fix)
- New Google App Engine module
- Support for Yahoo with OpenID
- Upgrade to Java 6
- Support for ORCiD (OAuth)

**pac4j 1.5.1**:

- add Bitbucket support

**pac4j 1.5.0**:

- callback urls can be dynamically computed according to the current host and port
- added PayPal support (OAuth 2.0)
- AJAX requests can be handled properly instead of performing a redirection to the provider for authentication
- infinite loop when accessing a protected page and authentication fails are now automatically handled by returning a forbidden response (HTTP 403)
- remove myopenid.com support
- add Vk.com support
- add Foursquare support
- add SAML support
- authorizations support: roles, permissions and a "remembered" status are now available in all user profiles

**pac4j 1.4.1**:

- Add LinkedIn OAuth 2.0 protocol support
- Add Google OpenID support

**pac4j 1.4.0**:

- Rebuilding of the project to support also CAS, HTTP (form & basic auth), OpenID (myopenid.com)... protocols
- "Cancel" actions are/can be now properly handled (Facebook, Twitter)
- Indirect redirection urls (OAuth 1.0, myopenid.com) are integrated within the library
- handle Kryo serialization
- remove Google OAuth 1.0 support

---

**scribe-up 1.3.1**:

- Bug fix the CAS OAuth wrapping

**scribe-up 1.3.0**:

- Create common profile for all profiles
- Add providers definition with specific mechanism to handle redirection to and from OAuth provider when having multiples providers with only one callback url
- Add the ability to handle HTTP exceptions when requesting a user profile (use <i>BaseOAuthProvider.retrieveUserProfile</i> method instead of <i>OAuthProvider.getUserProfile</i> method)
- Add the ability to handle credential exceptions when retriving credentials (use <i>BaseOAuthProvider.retrieveCredential</i> method instead of <i>OAuthProvider.getCredential</i> method)
- Add support for CAS OAuth wrapper

**scribe-up 1.2.0**:

- Add 2 providers: DropBox and Google (OAuth 2.0)
- Make provider type settable
- Make providers cloneable
- Replace java.awt.Color by a specific Color object to be compatible with Google App Engine
- Optimize Facebook calls and add more data
- Hide completely the dependency on scribe
- Add proxy capabilities for OAuth requests
- Add state parameter (security) for Facebook
- Optimize profile creations for none CAS usage
- Upgrade to scribe 1.3.2, Jackson 2.0.6 and slf4j-api 1.7.0
- Add dependency on commons-lang3

**scribe-up 1.1.0**:

- Simplify OAuth provider interface : 4 methods instead of 6, remove explicit dependency on scribe
- Create specific and more complete profiles for all providers
- Add 2 more providers : Windows Live and WordPress
- Refactor profiles to be compatible with "CAS serialization" on client side
- Add typedId concept to differentiate profiles
- Make init() calls implicit
- Add access_token as default attribute of profile
- Add connect and read timeouts
- Switch GitHub provider from API v2 to API v3
- Make profiles serializable
- Upgrade to scribe 1.3.1 and Jackson 1.9.7

**scribe-up 1.0.0**:

- This is the first version of the project
- 6 OAuth providers are available : Google (OAuth 1.0), GitHub, LinkedIn, Twitter, Yahoo and Facebook
- Each provider returns a generic profile (UserProfile class), except the Facebook one which returns a minimal FacebookProfile
- Based on scribe 1.3.0, Jackson 1.9.4 and slf4j-api 1.6.4
