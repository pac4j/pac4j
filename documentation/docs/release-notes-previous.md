---
layout: doc
title: Release notes (older versions)&#58;
---

[&#9656; Newer versions...](release-notes.html)

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

*Older release notes have been removed.*
