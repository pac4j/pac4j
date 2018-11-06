---
layout: doc
title: Release notes&#58;
---

**v3.4.0**:

- Added ability to create a composition of authorizers (conjunction or disjunction)
- SAML SLO support (POST and HTTP-Redirect bindings)

**v3.3.0**:

- Improve SAML support: always return a default key which is a private one, add a SingleLogoutService URL in the SP metadata, make local and central logouts work together, allow attributes to be mapped to new names
- Default state generation can be overriden by your own `StateGenerator` for the OAuth, OpenID Connect and SAML protocols
- Custom OpenSAML bootstrapping in SAML authentication module
- X509 certificate support

**v3.2.0**:

- Allow to set the `profileId` for the `GenericOAuth20Client`
- Fixed the `setConfiguration` method name in the OAuth v2.0 support
- Optionally sign or specificy requested attributes in the SAML SP metadata
- Update to Scribejava v5.6.0
- Added support for HiOrg-Server (OAuth)
- Revised OAuth error handling for extracting user profiles. Now, an exception is thrown instead of returning an empty profile
- Fix the `Access-Control-Expose-Headers` name and the `Access-Control-Allow-Credentials` header verification

**v3.1.0**:

- Added attribute merging capabilities for the user profile: multiple attributes with the same name and value of the collection type can be merged into a single attribute
- Added Weibo, QQ and Wechat (OAuth) supports

**v3.0.3:**

- `AzureAdClient` uses the `PathParameterCallbackUrlResolver` by default

**v3.0.2**:

- Properly handles all the HTTP codes in the `setResponseStatus` method of the `J2EContext`
- Added the `setExcludedPath` and `setExcludedPattern` methods to the `PathMatcher` class (for Shiro)

**v3.0.1**:

- The `ProfileHelper.flatIntoOneProfile` method returns an `AnonymousProfile` (instead of empty) if it's the only profile

**v3.0.0**:

- Handle AJAX requests in the OpenID Connect logout
- All session interactions are done via the `SessionStore` (retrieved from the `WebContext`)
- All exceptions (especially `HttpAction`) are unchecked
- Upgraded dependencies
- Added "multi-tenancy" capabilities: you can dynamically define multiple callback URLs for the same client, except for SAML for which you need as many `SAML2Client` as the number of different callback URLs you want
- The `CallbackUrlResolver` computes the callback URL (using a query parameter or a path parameter to define the client), based on the `UrlResolver` which computes a URL
- You can define an error (page) URL at the logics' level to handle unexpected exceptions
- The SAML Keystore alias can be defined via a property; SAML date comparisons are now UTC-based
- The client name is not set at the credential's level
- The username of the `AzureAdProfile` is the UPN
- The issue time is generated and an expiration date can be used for a JWT
- The OpenID Connect user profile can be expired
- In the `J2EContext`, header names are checked in a case-insensitive way
- Supports the `javax.faces.partial.ajax` parameter for AJAX requests
- If only one client is defined in the configuration, it is used as a fallback on the security and callback endpoints

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

[&#9656; Older versions...](release-notes-older.html)
