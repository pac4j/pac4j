---
layout: doc
title: Matchers&#58;
---

*pac4j* provides a [security model and engine (specific behaviours)](/docs/how-to-implement-pac4j-for-a-new-framework.html). The "security filter" is in charge of protecting url, requesting authentication and optionally authorization.

In some cases, you may want to bypass this "security filter" and this can be done using the **matchers** parameter which is generally a list of matcher names. A matcher is defined in the [security configuration](/docs/config.html) as a [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) implementation.

This interface has only one method: `boolean matches(WebContext context)` to say if the "security filter" must be applied.

The [`ExcludedPathMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/ExcludedPathMatcher.java) allows you to exclude some paths (defined as a regular expression) from the security checks. But other implementations could be developed.
