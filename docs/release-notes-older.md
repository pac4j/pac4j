---
layout: doc
title: Release notes (older versions)&#58;
---

[&#9656; Newer versions...](/docs/release-notes.html)

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
