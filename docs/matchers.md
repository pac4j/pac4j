---
layout: doc
title: Matchers&#58;
---

## 1) Definition

*pac4j* provides a [security model and engine (specific behaviours)](how-to-implement-pac4j-for-a-new-framework.html). The "security filter" is in charge of protecting url, requesting authentication and optionally authorization.

In some cases, you may want to bypass this "security filter" and this can be done using the **matchers** parameter which is generally a list of matcher names. A matcher is generally defined in the [security configuration](config.html).

## 2) Implementation

A matcher can be defined by implementing the [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) interface. It has only one method: `boolean matches(WebContext context)` to say if the "security filter" must be applied.

A few default matchers are available (but you can of course develop yours):

- the [`PathMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/PathMatcher.java) allows you to exclude some paths from the security checks

- the [`HeaderMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/HeaderMatcher.java) allows you to check if a given header is `null` or matches a regular expression

- the [`HttpMethodMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/HttpMethodMatcher.java) allows you to check if the method of the HTTP request is one of the expected defined methods.
