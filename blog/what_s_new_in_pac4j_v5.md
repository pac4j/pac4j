---
layout: blog
title: What's new in pac4j v5?
author: Jérôme LELEU
date: January 2021
---

One of the primary goals of pac4j has always been to be easy. One must admit that along the versions, it has gained some complexity and weight and time has come for cleaning.

Version 5 will be focused on cleaning with breaking changes as was the version 4.1 (with none-breaking changes).

The documentation will be updated too.

## 1) Java upgrade

pac4j v5 is now based on Java 11.

## 2) Removed

The `pac4j-saml-opensamlv3` module has been removed as it was based on JDK 8 and OpenSAML v3. It is replaced by the current `pac4j-saml` module, already based on JDK 11 and OpenSAML v4.

The `pac4j-openid` module has been removed as the OpenID protocol is no longer supported (the OpenID Connect protocol is still supported).

The `client_name` parameter can no longer be used to choose a client on the security filter. You must use the `force_client` parameter.

## 3) Generics

While generics should have brought value to the pac4j source code, they have, in fact, cluttered it.
With inconsistencies especially in the `Clients` and `Config` components where the generics were "forgotten".

So almost all generics constraints have been removed from the source code. Only remain the generics in the `ProfileService` and `Store` components.

## 4) Session management

Web applications require a web session while web services generally don't need one.
These latter generally don't write into the web session, they just read from it so reads should not create a web session when it does not already exist (because of course, if the web session does not exist, the read will only return `Optional.empty()`).

Instead of explicitly requesting the use of the web session in several places (`ProfileManager`, `DefaultSecuritylogic`), the solution is to have session implementations (`SessionStore`) which don't create a new session for reads, they just try to find it back.
This way, no need to explicitly define if you want to read from the web session or not. For writes, nothing changes: a session is always created if it doesn't exist.

`profileManager.getProfiles()` replaces `profileManager.getAll(readFromSession)` and `profileManager.getProfile()` replaces `profileManager.get(readFromSession)`

The fact that a profile is saved in the session or not after a succesful login is now override at the `Client` level, and no longer in the "security filter" and "callback endpoint".
BTW, the multi-profile option is now also set at the `Client` level.

```java
client.setMultiProfile(true);
client.setSaveProfileInSession(true);
```

The `SessionStore` is no longer available via the `WebContext`, but it must be explicitly passed everywhere it is needed.

## 5) SAML SLO

Up to v5, when a central logout was triggered for the SAML protocol, a local logout was performed as well. This is no longer the case in v5 to be consistent with the CAS and OpenID Connect protocols.

The local logout should be triggered by a logout request from the IdP (received on the callback endpoint) or explicitly by enabling the local logout.

## 6) Authorizers and matchers

### a) CSRF only for web applications

When using the "security filter", the clients (authentication mechanisms), the authorizers (authorization checks) and the matchers can be defined.

If no matchers is defined, the `securityHeaders` is applied to add the security headers to the HTTP request and the `csrfToken` is applied for the web applications (at least one `IndirectClient` is defined),
it means that a CSRF token is generated and added in the request/session/cookie.

If no authorizers is defined, the `csrfCheck` is used for web applications, meaning that the CSRF token is expected for a POST request.

### b) `isAuthenticated` by default

Since v5, a new default authorizer is added if no authorizers is defined: `isAuthenticated` to check that the user is authenticated and not only that he has a profile. This check is removed if an `AnonymousClient` has been defined in the `clients`.

Indeed, since a few versions, you can use the `AnonymousClient` and its `AnonymousProfile` put in the HTTP request when no other authenticated profile is available. The edge case is that this profile can be saved into the session and be available on other secured endpoints and URLs.

So the idea here is to be protected by default against any `AnonymousProfile` "leaking" to the session.

### c) In addition to the default ones

You can now use the "+" character before the new authorizers or matchers to say that they apply in addition of the default ones.

### d) Overriding default ones

You can also override the default autorizers and matchers by defining your owns with the same names.

## 7) User profiles

### a) Refactoring

The `UserProfile` has been turned into a pure interface without any default method and all implementations are made in the `BasicUserProfile`.

The `UserProfile` interface is used as much as possible in all *pac4j* classes.

### b) serializedprofile

When using the `ProfileService` for RDBMS, LDAP, MongoDB or CouchDB, there is a core issue in the format used to serialize profiles: it might block upgrades.
Indeed, we use the Java serialization which is a very bad idea because of the changes that can happen to the profiles classes like the fact that the `UserProfile` has moved from an abstract class in v3.x to an interface in v4.x.
So the idea is to use JSON instead of Java serialization.

The new `Serializer` (`encode` + `decode` methods) used by the profile services is the `ProfileServiceSerializer` which relies on the `JsonSerializer` and the `JavaSerializer` and is able to read JSON or Java serialized data and write JSON data.

Before upgrading to a new major *pac4j* version, it might be necessary to re-encode all data in the `serializedprofile` attribute with the `ProfileServiceSerializer`.

These fixes are available in v3.9.0, v4.2.0 and in the v5.x stream.

### c) Restoring the profile from the typed identifier

In previous versions, after the authentication process, the profile was built from the typed identifier if possible for the CAS protocol and the JWT support.

This is now controlled in the `ProfileDefinition` via the `setRestoreProfileFromTypedId` method and this is only enabled by default for the JWT support.

## 8) CSRF

The CSRF protection has been improved:

- longer CSRF token values (32 bytes)
- CSRF tokens generated per HTTP request
- internal expiration date (4 hours)
- CSRF token verification protected against time-based attacks.

## 9) Specifcation compliance

The compliance with the HTTP specifcation has been improved as well:

- the 307 HTTP status code is no longer misused
- the "WWW-Authenticate" header is added when missing for the 401 HTTP status code or the 403 HTTP status code is used instead.
