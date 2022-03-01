---
layout: doc
title: Release notes&#58;
---

**v4.5.5**:

- Fix CVE-2021-44878

**v4.5.4**:

- Patch log4j v2 (CVE-2021-44228)

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
