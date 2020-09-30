---
layout: blog
title: What's new in pac4j v5?
author: Jérôme LELEU
date: October 2020
---

One of the primary goals of pac4j has always been to be easy. One must admit that along the versions, it has gained some complexity and weight and time has come for cleaning.

Version 5 will be focused on cleaning with breaking changes as was the version 4.1 (with none-breaking changes).

The documentation will be updated too.

## Java upgrade

pac4j v5 is now based on Java 11.

## Removed

The `pac4j-saml-opensamlv3` module has been removed as it was based on JDK 8 and OpenSAML v3. It is replaced by the current `pac4j-saml` module, already based on JDK 11 and OpenSAML v4.

The `pac4j-openid` module has been removed as the OpenID protocol is no longer supported (the OpenID Connect protocol is still supported).

The `client_name` parameter can no longer be used to choose a client on the security filter. You must use the `force_client` parameter.

## Generics

While generics should have brought value to the pac4j source code, they have, in fact, cluttered it.
With inconsistencies especially in the `Clients` and `Config` components where the generics were "forgotten".

So almost all generics constraints have been removed from the source code. Only remains the generics in the `ProfileService`, `Store` and `ProfileManager` components.

## Session management

Web applications require a web session while web services generally don't create one.
So far, the use of the web session was explicitly requested in several places (`ProfileManager`, `DefaultSecuritylogic`) even if web services don't write into the web session, they just read from it.
Thus, the solution is to have session implementations (`SessionStore`) which don't create a session for reads, they just try to find it back (a session is created for writes if it doesn't exist).
And reads can always happen from the web session and from the current request.

`profileManager.getAll(readFromSession)` can be replaced by `profileManager.getProfiles()`, no need to specify if the read must happen from the web session (this will be the case only if the session exists and no session will be created otherwise).
