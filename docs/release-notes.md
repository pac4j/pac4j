---
layout: doc
title: Release notes&#58;
---

**v5.1.5**:

- Fixed a NPE on the `SAML2AuthnResponseValidator`
- Added the `setExpiration(Date)` method on the `OidcProfile`
- Fixed the `expireSessionWithToken` behavior for the `OidcProfile`
- Set the `secure` and `httpOnly` flag to `true` for the CSRF cookie
- Fix multi-values attribute duplication in `SAML2Profile`

**v5.1.4**:

- SAML2 configuration can now accept custom socket factories and hostname verification options
- Ability to ignore an existing authentication via the `loadProfilesFromSession` flag at the security filter level.

**v5.1.3**:

- Treat SAML partial logout responses as success

**v5.1.2**:

- Support SAML2 `Scoping` in authentication requests
- `WebContext` is now able to provide the request url directly
- Fix SAML2 response attributes resolution when using ADFS as IdP
- Add claims mapping for the OIDC support.

**v5.1.1**:

- Removed the ORCID OAuth client which no longer works. Use the `OidcClient` instead
- Fixed PKCE OIDC flow support
- Properly parse SAML complex attributes
- For the CAS server OIDC support: allow to disable the checking of authentication attempts

**v5.1.0**:

- SAML2 identity provider metadata resolver can optionally be forced to download the metadata again.
- SAML2 identity provider metadata resolver is given the ability to support `last-modified` attributes for URLs.
- SAML2 response validation can now disable the validation of `authnInstant` by assigning a zero/negative value to
  `SAML2Configuration#configuration.setMaximumAuthenticationLifetime()`. This setting should not be using sparingly.
- Clients can be changed at any time in the `Clients` component

**v5.0.1**:

- Hazelcast-based implementation for SAMLMessageStore
- Added an option to tolerate missing SAML response `Destination` attribute
- SAML support: don't add the friendly name if it is the same as the (mapped) name (avoid duplication of values)
- Improve JWT parsing for nested attributes

**v5.0.0** (see: [what's new in pac4j v5?](/blog/what_s_new_in_pac4j_v5.html)):

- Upgraded to JDK 11
- Removed the `pac4j-saml-opensamlv3` and `pac4j-openid` modules
- Removed deprecated methods and classes
- Removed most generics
- Slightly refactored the auto-initialization
- Refactored the session management (mainly `ProfileManager` and `SessionStore`): reading in the web session does not create it while writing a none-null value in the session always create it. The multi-profile and save-profile-in-session-or-not options can now be defined at the `Client` level, and no longer in the "security filter" and "callback endpoint". The `get(readFromSession)` and `getAll(readFromSession)` methods of the `ProfileManager` are replaced by the `getProfile()` and `getProfiles()` methods
- The SAML central logout does not perform any local logout
- When no authorizers is defined, one of the default authorizers is `isAuthenticated` if the `AnonymousClient` is not used
- Serialize profiles in JSON (instead of using the Java serialization) for the MongoDB, SQL, LDAP and CouchDB `ProfileService` supports; Added a `JsonSerializer` and turned the `JavaSerializationHelper` into a `JavaSerializer`; Removed the `ProfileServiceSerializer`
- Removed the 307 HTTP code for a new POST request after a POST request (use 200 instead)
- Turned the `UserProfile` component into a pure interface and use it as much as possible (especially in the `JwtGenerator` and `JwtAuthenticator`)
- The `ProfileHelper.restoreOrBuildProfile` method has been removed and the behavior is controlled by the `ProfileDefinition` and its `setRestoreProfileFromTypedId` method (enabled for JWT, disabled for others)
- Authorizers and matchers can be defined additionaly with "+"
- CSRF security improvements proposed by Xhelal Likaj (https://github.com/xhlika): longer CSRF token values (32 bytes), CSRF tokens generated per HTTP request and with an internal expiration date (4 hours), CSRF token verification protected against time-based attacks
- Improved responses for unauthenticated users: 401 with "WWW-Authenticate" header or 403 to be compliant with the HTTP spec
- Default authorizers and matchers can be re-defined by users
- Separate the `SessionStore` from the `WebContext`
- Signing operations for SAML2 metadata can now be done using the existing default method or via XMLSec. The choice for the signer component can be decided via `SAML2Configuration`.
- Ability to specify the SAML2 SLO url in the `SAML2Configuration` and metadata.
- Options in `SAML2Configuration` to determine how to URLs should be compared when doing endpoint verifications with SAML2 responses.
- SAML2 logout validation can be given an expected destination so as to not just rely on the SLO endpoint defined in the SAML2 metadata.
- SAML2 requested authentication context class refs are now checked and enforced again in SAML responses.
- The presence of `NameID` elements in SAML2 responses is now made optional, if the `SAML2Configuration` is configured to build the final credential using a SAML2 attribute found in the assertion. If the attribute is not found or is undefined, `NameID` is expected as the default.
- Handle the "same site policy" in cookies (default: `lax`). Renamed `ContextHelper` as `WebContextHelper`
- Authentication requests for protocols that support forced/passive authentication can now be modified on a per-request basis using pre-defined HTTP attributes to control the type of authentication request sent to the provider.

**v4.5.1**:

- Removed the ORCID OAuth client which no longer works. Use the `OidcClient` instead
- Fixed PKCE OIDC flow support

**v4.5.0**:

- SAML2 identity provider metadata resolver can optionally be forced to download the metadata again.
- SAML2 identity provider metadata resolver is given the ability to support `last-modified` attributes for URLs.
- Improve JWT parsing for nested attributes
- Avoid NPE with `null` domain for cookies on `JEEContext`

**v4.4.0**:
- For SAML IdP metadata defined as files, the metadata are reloaded if the file is changed

**v4.3.0**:

- Added a `ChainingConverter` for attributes
- Fix expired access tokens for the OIDC protocol

**v4.2.0**:

- Apple SignIn support (OIDC protocol)
- Serialize profiles in JSON (instead of using the Java serialization) for the MongoDB, SQL, LDAP and CouchDB `ProfileService` supports

**v4.1.0** (see: [what's new in pac4j v4.1?](/blog/what_s_new_in_pac4j_v4_1.html)):

- The `RememberMeAuthorizationGenerator` is deprecated and will be removed in the next version (v5)
- The OpenID support (`YahooOpenIdClient`) is deprecated and will be removed in the next version (v5)
- The `ProfileManagerFactory2` is deprecated and will be removed in the next version (v5)
- Removed the `InternalAttributeHandler`
- The default matchers/authorizers are `securityHeaders`/`none` for web services instead of `csrfToken,securityHeaders`/`csrfCheck` for web applications

**v4.0.3**:

- Fix the expiration date for the SAML generated certificate
- Added a new `ValueRetriever` interface and its implementation `SessionStoreValueRetriever` for the OpenID Connect protocol
- Added support for PKCE to the OpenID Connect protocol [RFC-7636](https://tools.ietf.org/html/rfc7636)
- Improved handling of expired tokens in `OidcProfile`

**v4.0.2**:

- Fix the `ClassCastException` when receiving a SAML logout response
- Send the access token as a header for the `GithubClient`
- CAS front channel logout: fix the 401 error after the logout
- Fix default `CallbackUrlResolver` in `CasClient`

**v4.0.1**:

- Type parameters: add the `? extends Credentials` type parameter for the `Client` in the return type of the method `find` of the `ClientFinder` and add the `UserProfile` type parameter for the `ProfileManager` in the return type of the `getProfileManager` method of the `ProfileManagerFactoryAware*`
- Add setters on `Color`
- Pull the `pac4j-saml-opensamlv3` dependency instead of the `pac4j-saml` dependency
- Remove deprecated behaviors: the retrieval of one `CommonProfile` in request or session via the `ProfileManager` and the retrieval of a `String` as the requested URL
- The default client name parameter used for security has a new value (`force_client`) to avoid conflicting with the default client name parameter (`client_name`) used on the callback (the old value is still used as a fallback, but will be removed)
- Allow `pac4j-saml` to store and generate SAML metadata and keystores using a REST API and provide options for extensibility so custom components can be designed and injected to manage metadata artifacts externally. Resolution of SAML2 identity provider metadata can be controlled/overridden.
- Handle a strange use case for the `JEEContext` where the `request.getRequestURI()` returns a path starting by a double slash
- Can return a custom profile when the authentication fails or is cancelled ("silent login")
- Fix the CAS logout URL computation (for central logout without prefix)
- Introduce the `WebContextFactory` concept and the `JEEContextFactory` implementation

**v4.0.0**:

- Improved the profile manager configuration
- Renamed `J2E` components as `JEE`
- Started updating dependencies via Renovate
- A client can return any kind of profile (using a custom `AuthorizationGenerator` or `ProfileCreator`) and even a minimal user profile (`UserProfile`)
- HTTP actions are no longer applied automatically to the web context (the `setResponseStatus` and `writeResponseContent` methods have been removed from the `WebContext` interface), an `HttpActionAdapter` must be used for that. Multiple HTTP actions (inheriting from `HttpAction`) are created to handle the necessary HTTP actions. The `RedirectAction` is replaced by the new HTTP actions inheriting from `RedirectionAction`. The `redirect` method is renamed as `getRedirectionAction`
- By default, the CSRF check applies on the PUT, PATCH and DELETE requests in addition to the POST requests
- Renamed the `SAMLMessageStorage*` classes as `SAMLMessageStore*` (based on `Store`)
- For `Google2Client`, change profile URL from `https://www.googleapis.com/plus/v1/people/me` to `https://www.googleapis.com/oauth2/v3/userinfo`. This change is to prepare for the shutdown of Google plus API. This change will remove the `birthday` and `emails` attribute for `Google2Client`.
- For an AJAX request, only generates the redirection URL when requested (`addRedirectionUrlAsHeader` property of the `DefaultAjaxRequestResolver`)
- Updated the APIs to use `Optional` instead of returning `null`
- Use the 303 "See Other" and 307 "Temporary Redirect" HTTP actions after a POST request (`RedirectionActionHelper`)
- Handles originally requested URLs with POST method
- Add HTTP POST Simple-Sign protocol implementation
- Properly handle states and nonces for multiple OIDC clients
- A profile can be renewed by its client when it's expired
- Most web authorizers are now matchers. The default matchers are "securityHeaders,csrfToken" and the default authorizer is "csrfCheck". Use "none" for no matcher or authorizer
- Use the `FindBest` utility class to find the best adapter, logic...
- Support for the OIDC back-channel and front-channel logouts
- Load the profiles in the `ProfileManager` (from the session or not) like in the `DefaultSecurityLogic` via the `getLikeDefaultSecurityLogic` and `getAllLikeDefaultSecurityLogic` methods
- REVERT: remove the ID token in the `removeLoginData`  method (previously `clearSensitiveData`)
- The `pac4j-saml` module is saved as the legacy `pac4j-saml-opensamlv3` module and upgraded to JDK 11 and OpenSAML v4

---

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> The 3.x stream is no longer maintained except via the <a href="/commercial-support.html">LTS program</a>.</div>

**v3.9.0**:

- Serialize profiles in JSON (instead of using the Java serialization) for the MongoDB, SQL, LDAP and CouchDB `ProfileService` supports

**v3.8.3**:

- Upgrade the nimbus-jose-jwt library to version 7.9 because of [CVE-2019-17195](https://connect2id.com/blog/nimbus-jose-jwt-7-9)

**v3.8.2**:

- Add customizable SAML post Logout URL
- QualifiedName must not be included by default in SAML authentication requests
- Added replay protectection to the SAML client.
- Fix SAML signature validation w.r.t. WantAssertionsSigned handling. Signing is now always required, even when WantAssertionsSigned is disabled. WantAssertionsSigned now requires explicit signing of the assertions, not the response.
- Added support for the SAML artifact binding for the authentication response.
- Sign metadata when configured to do so and open up the metadata generation API for customization.
- Never sign AuthnRequests with XMLSig when using REDIRECT binding, signing is done via the Signature query parameter.
- Added support for LinkedIn v2 API
- Added support for FigShare

**v3.7.0**:

- Fix SAML SP metadata signature
- CAS improvements: better service requests detection, support of the CAS server `method` parameter
- Fix the `CasRestProfile` with JWT
- Add HTTP POST Simple-Sign protocol implementation
- Add the `get`, `post`, `put` and `delete` matchers based on the `HttpMethodMatcher` when not defined

**v3.6.1**:

- Fix Google OAuth support

**v3.6.0**:

- Multiple authn context class refs can be set in the SAML protocol support
- For `Google2Client`, change profile url from `https://www.googleapis.com/plus/v1/people/me` to `https://www.googleapis.com/oauth2/v3/userinfo`. This change is to prepare for the shutdown of Google plus API. This change will make the `birthday` attribute return `null` and `emails` attribute resolve a single email from `email` attribute for `Google2Client`.
- Clean shutdown of the `SAML2Client` via the `destroy` method
- Do not clear the ID Token as a sensitive data in the `OidcProfile`
- Improve signature and digest methods for SAML metadata
- Enhance OAuth 2 generic support
- Use the NameID as the fallback of the SessionIndex for the SAML SLO support

**v3.5.0**:

- Added `UserInfoOidcAuthenticator` to authenticate a user based on an access token received from an OpenID Connect login process
- Updated the OpenID Connect/JWT dependencies (v6)
- Added `DirectBearerAuthClient`
- Handled the inResponseTo and the RelayState in the logout response (SAML)
- Added `trustedClasses` to the `JavaSerializationHelper`

**v3.4.0**:

- Added ability to create a composition of authorizers (conjunction or disjunction)
- SAML SLO support with SOAP (ingoing only), HTTP-POST and HTTP-Redirect bindings
- OpenID Connect improvements: supports multiple JWS algorithms from the identity server, retrieves the Keycloak roles

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

[&#9656; Older versions...](release-notes-older.html)
