---
layout: blog
title: What's new in pac4j v4.1?
author: Jérôme LELEU
date: September 2020
---

One of the primary goals of pac4j has always been to be easy. One must admit that along the versions, it has gained some complexity and weight and time has come for cleaning.

Version 4.1 will be focused on cleaning (with none-breaking changes) and version 5 as well (with breaking changes).

The documentation will be updated too.

## Removed

The `InternalAttributeHandler` component has been removed: it was meant to be used in the CAS server, but it isn't.

## Deprecated before removal

The `YahooOpenIdClient` has been marked as `@Deprecated` and will be removed in the next version (v5). The OpenID protocol is dead and even Yahoo is migrating to OAuth v2.

The `RememberMeAuthorizationGenerator` has also been marked as `@Deprecated` and will be removed in the next version (v5).
This component turns a checked box in a form into a "remember me" nature of the authenticated profile.
This is very misleading as the authenticated profile should not be marked as "remembered" for the first authentication in case we want to remember it.
It should be saved somehow on the client browser (like a JWT in a cookie) with the "remember me" nature and restored later on.

The `ProfileManagerFactory2` has also been marked as `@Deprecated` and will be removed in the next version (v5).
To be able to build a `ProfileManager` from a `WebContext`, we have the `ProfileManagerFactory`.
For the back channel logout calls, we also need to set the `sessionStore` for the `ProfileManager`: that's why the `ProfileManagerFactory2` exists.
But a simple `setSessionStore` method at the `ProfileManager` wil do the same job and remove its need.
