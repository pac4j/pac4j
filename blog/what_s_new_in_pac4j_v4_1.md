---
layout: blog
title: What's new in pac4j v4.1?
author: Jérôme LELEU
date: October 2020
---

One of the primary goals of pac4j has always been to be easy. One must admit that along the versions, it has gained some complexity and weight and time has come for cleaning.

Version 4.1 will be focused on cleaning (with none-breaking changes) and version 5 as well (with breaking changes).

The documentation will be updated too.


## Deprecated before removal

The `YahooOpenIdClient` has been marked as `@Deprecated` and will likely be removed in the next version (v5). The OpenID protocol is dead and even Yahoo is migrating to OAuth v2.

The `RememberMeAuthorizationGenerator` has also been marked as `@Deprecated`. This component turns a checked box in a form into a "remember me" nature of the authenticated profile.
This is very misleading as the authenticated profile should not be marked as "remembered" in case we want to remember it.
It should be saved somehow on the client browser (like a JWT in a cookie) with the "remember me" nature and restored later on.
