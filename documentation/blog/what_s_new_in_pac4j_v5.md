---
layout: blog
title: What's new in pac4j v5?
author: Jérôme LELEU
date: October 2020
---

One of the primary goals of pac4j has always been to be easy. One must admit that along the versions, it has gained some complexity and weight and time has come for cleaning.

Version 5 will be focused on cleaning with breaking changes as was the version 4.1 (with none-breaking changes).

The documentation will be updated too.

## Upgrades

pac4j v5 is now based on Java 11.

## Removed

The `pac4j-saml-opensamlv3` module has been removed as it was based on JDK 8 and OpenSAML v3. It is replaced by the current `pac4j-saml` module, already based on JDK 11 and OpenSAML v4.

The `pac4j-openid` module has been removed as the OpenID protocol is no longer supported (the OpenID Connect protocol is still supported).
