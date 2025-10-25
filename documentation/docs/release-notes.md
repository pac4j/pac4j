---
layout: doc
title: Release notes&#58;
---

### JDK17:

**v6.2.3**:
- Use an empty string instead of `null` if need be for the conditional `Authorization` header (issue with Play)
- Fix uninitialized components required for handling http artifact (SAML protocol)
- Add `<meta charset>` tag to request saved during authentication
- Fix rejected logout token (backchannel logout, OIDC)

**v6.2.2**:
- Fix the X509 `SubjectDN` parsing
- OIDC: Allow to ignore nonce for idToken on refresh
- Properly handle spaces in keytab path

**v6.2.1**:
- Fix the SAML `IssueInstant` check
- Fix the conditional `Authorization` header

**v6.2.0**:
- Only return an `Authorization` header when there is no credentials or when credentials are invalid (for `DirectBasicAuthClient`, `DirectBearerAuthClient` and `DirectDigestAuthClient`)

**v6.1.4**:
- Downloading SAML2 metadata over a URL is able to support SSL context and hostname verification options when checking for metadata updates.
- Fix the `SAMLSOAPDecoderBodyHandler` initialization (in the `Pac4jHTTPPostDecoder`)

**v6.1.3**:
- SAML2 operations that use `FilesystemMetadataResolver` are replaced with a DOM parser instead.
- OIDC: prevent creating a profile from an unvalidated access token

**v6.1.2**:
- Use the configured scope in OpenID Connect authenticator
- Fix the `getFullRequestURL` method
- Fixes setting proper implementation of `OidcOpMetadataResolver` in `OidcConfiguration` and its descendants when `internalInit` is called with `forceReinit` set to true
- SAML2 metadata URLs can be downloaded and resolved concurrently if the URL resource supports the `Accept-Ranges` header as `bytes`.
- SAML2 clients that point to the same SP metadata resource can be merged together so the final SP metadata may reference all clients.
- SAML2 `BasicParserPool` is adjusted to have a larger pool size.
- SAML2 client fields and reference are only initialized if they are undefined.
- There is `SAML2DelegatingMetadataResolver` that delegates resolution tasks to an existing metadata resolver.
- There is `SAML2InMemoryMetadataGenerator` that keeps data in memory and acts as a virtual generator.
- Removal of various code constructs in favor of Lombok.

**v6.1.1**:
- Protect the `getRequestAttribute` method for Jetty 12.0.8+
- Fix bug for HTML values in POST forms

**v6.1.0**:
- Deprecate basic `CommonHelper` methods in favor of `commons-lang3`
- Allow serializing SAML2Profiles using the JsonSerializer with default typing

**v6.0.7**:
- SAML2: `maximumAuthenticationLifetime` is set to `0` by default to disable the validation of `authnInstant` in SAML2 assertions.

**v6.0.6**:
- Security fix: cannot accept empty OIDC credentials

**v6.0.5**:
- Allow to force the reloading of the SAML metadata
- Reinforce security by checking OIDC logout requests (can be disabled via `OidConfiguration.setLogoutValidation(false)`)
- Retrieving OIDC resources such as keys from a remote IDP now recognizes the OIDC configuration for remote hostname verification
- OAuth2 credentials can now be serialized from/to JSON correctly using an intermediate object to carry the access token
- Properly handle the common tenant for Microsoft Azure OIDC

**v6.0.4**:
- OIDC support: set the profile identifier from the subject of the userinfo endpoint if need be
- Fix: `StaticOidcOpMetadataResolver` should not enforce a discovery URI
- `OidcOpMetadataResolver`: secret is not mandatory for `private_key_jwt` client authentication method
- SAML2 support: service provider metadata can now be stored in AWS S3 buckets.
- Reinforce security on `JBCryptPasswordEncoder`

**v6.0.3**:
- Only 'SAML version 2' in metadata
- Fix `ConcurrentModificationException` in `ProfileManager.removeOrRenewExpiredProfiles`
- Add the `oidc.withState` config property
- De-duplicate user profile attribute values (avoid memory overconsumption)

**v6.0.2**:
- Fix NPE on `SAML2FileSystemMetadataGenerator`

**v6.0.1**:
- Fix the flatten OAuth profile attributes parsing (`CasOAuthWrapperClient`)
- Added the `setAccessTokenObject` and `setRefreshTokenObject` methods to the `OidcCredentials`
- Fix the `forceReinit` behavior on `OidcConfiguration`

**v6.0.0**:
- Based on JDK17
- Removed the deprecated `pac4j-saml`, `pac4j-cas` and `pac4j-springboot` modules
- Renamed the `pac4j-cas-clientv4` module as `pac4j-cas`
- Renamed the `pac4j-saml-opensamlv5` module as `pac4j-saml`
- Renamed the `pac4j-springbootv3` module as `pac4j-springboot`
- Removed the `JEESessionStore.INSTANCE`
- Removed deprecated concepts and components
- `Authenticator` now returns an `Optional<Credentials>`
- Usage of Lombok
- Removed `ProfileManagerFactoryAware`
- Removed `FindBest`
- Customisations for the endpoints/interceptors can only be done via the `Config` component and thanks to the `FrameworkParameters`
- Framework specificities (to set up by default) are specified in `org.pac4j.framework.adapter.FrameworkAdapterImpl` or `org.pac4j.jee.adapter.JEEFramworkAdapter` or `DefaultFrameworkAdapter`
- Renamed `defaultXXX` methods as `setXXXIfUndefined`
- Gather the web context, the session store and the profile manager factory in a `CallContext`
- `Client` interface:
  - Split the `getCredentials` method into the `getCredentials` and `validateCredentials` methods
  - Add a new `processLogout` method based on the `LogoutProcessor` component
- Renamed the `LogoutHandler` as `SessionLogoutHandler`
- Created the `SpringResourceLoader` for OIDC/SAML metadata loading: for the OIDC support, the `discoveryURI` can use the "file:", "classpath:" or "resource:" prefix in addition to HTTP/HTTPS URLs
- The `DefaultSessionLogoutHandler` smartly tries a front channel logout and then a back channel logout
- The `OidcProfile` will internally encode/decode codes, access and refresh tokens. Asking the profile to return back the actual object will effectively reconstruct it, to avoid  issues with JSON serialization.
- Added `getQueryString` on the `WebContext`
- `X509CredentialsExtractor` is now given the ability to specify a custom header for certificate extraction.
- `Credentials` are now able to specify and carry their source, typically set by the credential extraction process.
- It is now possible to specify extraction modes for `FormExtractor`
- An `AutomaticFormPostAction` is inferred for SAML requests/responses
- The `SessionLogoutHandler` is now part of the `Config` (and may be set to `null`)

---

### JDK11:

**v5.7.8**:
- Fix the X509 `SubjectDN` parsing
- Fix bug for HTML values in POST forms
- Fix the `getFullRequestURL` method
- OIDC: prevent creating a profile from an unvalidated access token

**v5.7.7**:
- Security fix: cannot accept empty OIDC credentials

**v5.7.6**:
- De-duplicate user profile attribute values (avoid memory overconsumption)
- OIDC support: set the profile identifier from the subject of the userinfo endpoint if need be
- `OidcAuthenticator`: secret is not mandatory for `private_key_jwt` client authentication method
- Reinforce security on `JBCryptPasswordEncoder`
- Reinforce security by checking OIDC logout requests (can be disabled via `OidConfiguration.setLogoutValidation(false)`)

**v5.7.5**:
- Add the `oidc.withState` config property

**v5.7.4**:
- Only 'SAML version 2' in metadata
- Fix `ConcurrentModificationException` in `ProfileManager.removeOrRenewExpiredProfiles`

**v5.7.3**:
- Fix the flatten OAuth profile attributes parsing (`CasOAuthWrapperClient`)
- Upgrade the `nimbus-jose-jwt` library to version `9.37.2` for security reasons

**v5.7.2**:
- Allow to disable user info endpoint calls (OIDC)
- Handle non-existing ID tokens (OIDC)
- Added prefixed `SessionStore`

**v5.7.1**:
- Allow to disable tokens rotation on the `DefaultCsrfTokenGenerator`

**v5.7.0**:
- The `oauth.getProfileCreator()` and the `oidc.getProfileCreator()` can directly be used in the `ParameterClient`, `HeaderClient` and `DirectBearerAuthClient` for bearer calls; Deprecated the `UserInfoOidcAuthenticator`
- Created a new `pac4j-saml-opensamlv5` module based on OpenSAML v5 (JDK 17)
- Created a new `pac4j-cas-clientv4` module based on the Apereo CAS client v4 (JDK 17)
- Deprecated old modules (`pac4j-javaee`, `pac4j-cas`, `pac4j-springboot` and `pac4j-saml`)
- SAML2 service provider metadata generators can be discovered using [Java's `ServiceLoader` API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ServiceLoader.html).
- Added support for `SAML2MongoMetadataGenerator` to manage SAML2 metadata via `pac4j-saml-opensamlv5`.
- Added support for `SAML2JdbcMetadataGenerator` to manage SAML2 metadata via `pac4j-saml-opensamlv5`.
- Added a `LocalCachingProfileCreator`
- Added the `PRIVATE_KEY_JWT` client authentication method support for the OIDC protocol
- Deprecated the `permission` concept in the user profile

**v5.6.1**:
- Allow to override the "computation" of the `defaultUrl` in the `DefaultLogoutLogic`
- Security fix on the `logoutUrlPattern`

**v5.6.0**:
- Added a new `pac4j-springbootv3` module for Spring Boot v3 (JDK 17)
- Can control if the CSRF token is added as an attribute (`true` by default), as a header (`false` by default) or as a cookie (`true` by default) for the `CsrfTokenGeneratorMatcher`
- Removed all `javax.annotation.Nullable` and `javax.annotation.Nonnull` references from the `pac4j-saml` module
- Added the `JEEAdapter` class in the `pac4j-core` module based on the `JEEAdapterImpl` class in the `pac4j-javaee` or `pac4j-jakartaee` module
- Removed unwanted "javax" dependencies
- Removed any `org.jasig.cas.client.util.CommonUtils` usage from the `pac4j-cas` module
- Added a `addClient` method to the `Clients` component
- Made the `Config` parameter optional for the `SecurityEndpointBuilder`

**v5.5.0**:
- Allow to override a default `Matcher` (even the `securityheaders` shortcut)
- Remove the deprecated `pac4j-jee` module
- Allow to include paths for the `PathMatcher`
- Add the `Pac4jConstants.EMPTY_STRING` constant
- Can set the content on the `BadRequestAction`, `ForbiddenAction`, `StatusAction` and `UnauthorizedAction` actions
- Add the new concept of `SessionStoreFactory` to replace any direct `SessionStore` instantation
- Add a `SecurityEndpointBuilder` to help build configuration for security endpoints from multiple parameters
- Improve (SAML) user attribute types handling
- Use `destroySessionBack` on `DefaultLogoutHandler` when session can't be inferred from the Logout Request's context
- Fix "nosuchelement error" on `SessionStore.getSessionId(context, true)`

**v5.4.6**:
- Disable JWT access token parsing by default, use `OidcConfiguration.setIncludeAccessTokenClaimsInProfile` to re-enable.
- Upgrade nimbus-jwt to v9.24.2
- Deprecate the static methods: `Config.set|defaultProfileManagerFactory(name,ProfileManagerFactory)` in favor of the class methods: `config.set|defaultProfileManagerFactory(ProfileManagerFactory)`. Add the `config.defaultSessionStore(SessionStore)` method
- <div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Upgrade to slf4j v2.0.0: be careful as it may break logging!</div>

**v5.4.5**:
- Deprecated the `new PathMatcher(regex)` constructor
- Fix NPE on JWT access token parsing

**v5.4.4**:
- Fixes the behavior of the `RequireAnyRoleAuthorizer` and `RequireAnyPermissionAuthorizer` with no roles or permissions
- Allows the `DefaultSAML2MetadataSigner` to accept a `SAML2Configuration`
- Fixes `pac4j-springboot` dependencies
- OIDC support: collect claims from the access token if it is a valid JWT

**v5.4.3**:
- Fix [CVE-2022-22968](https://spring.io/blog/2022/03/31/spring-framework-rce-early-announcement)

**v5.4.2**:
- Fix [CVE-2022-22965](https://spring.io/blog/2022/03/31/spring-framework-rce-early-announcement)

**v5.4.0**:
- Deprecated the `pac4j-jee` dependency (JEE components in the `org.pac4j.core` and `org.pac4j.saml` packages, based on the `javax.servlet-api` library v4) to be replaced by:
  - the `pac4j-javaee` dependency (JEE components in the `org.pac4j.jee` package, based on the `javax.servlet-api` library v4) or
  - the `pac4j-jakartaee` dependency (JEE components in the `org.pac4j.jee` package, based on the `jakarta.servlet-api` library v5)
- Refactored the SAML2 attributes conversion (from the SAML2 authn response) to rely on a defined `AttributeConverter` at the `SAML2Configuration` level
- Implemented RFC 9207 OAuth 2.0 Authorization Server Issuer Identification in `pac4j-oidc`

**v5.3.1**:
- Added Cronofy support (OAuth v2)
- Fully fix CVE-2021-44878 by checking the OIDC response type when creating the token validator

**v5.3.0**:
- `InitializableObject`: the number of attempts and the last attempt time are tracked and checked with the max attempts and the min time interval between attempts (for the CAS implementation)
- `InitializableObject`: allow re-initialization
- Refactor the `CasOAuthWrapperClient` configuration

**v5.2.1**:
- Update `log4j-to-slf4j` and `log4j-api` to v2.17.0 although only `log4j-core` has security vulnerabilities

**v5.2.0**:
- The JEE core components are now in the `pac4j-jee` dependency (and no longer in the `pac4j-core` dependency)
- CVE-2021-44878: reinforce security on the OIDC protocol support: the `none` algorithm must be explicitly accepted on client side (`allowUnsignedIdTokens`)

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

---

### JDK8:

**v4.5.8**:
- Only 'SAML version 2' in metadata
- Fix `ConcurrentModificationException` in `ProfileManager.removeOrRenewExpiredProfiles`
- Security fix: cannot accept empty OIDC credentials

**v4.5.7**:
- Security fix on the `logoutUrlPattern`

**v4.5.6**:
- Fix [CVE-2022-22965](https://spring.io/blog/2022/03/31/spring-framework-rce-early-announcement)

- **v4.5.5**:
- Fix CVE-2021-44878

**v4.5.4**:
- Update `log4j-to-slf4j` and `log4j-api` to v2.17.0 although only `log4j-core` has security vulnerabilities

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

[&#9656; Previous versions...](release-notes-previous.html)
