---
layout: doc
title: Matchers&#58;
---

## 1) Definition

*pac4j* provides a [security model and engine (specific behaviours)](how-to-implement-pac4j-for-a-new-framework.html). The "security filter" is in charge of protecting url, requesting authentication and optionally authorization.

In some cases, you may want to bypass this "security filter" and this can be done using the **matchers** parameter which is generally a list of matcher names. A matcher is generally defined in the [security configuration](config.html).

The matchers can also be used to always apply some logic on the URLs, like adding some security headers.


## 2) Available matchers

A matcher can be defined by implementing the [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) interface. It has only one method: `boolean matches(WebContext context)` to say if the "security filter" must be applied.

A few matchers are available (but you can of course develop yours):

- the [`PathMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/PathMatcher.java) allows you to exclude some paths from the security checks

- the [`HeaderMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/HeaderMatcher.java) allows you to check if a given header is `null` or matches a regular expression

- the [`HttpMethodMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/HttpMethodMatcher.java) allows you to check if the method of the HTTP request is one of the expected defined methods.


## 3) Default matchers

In the `DefaultMatchingChecker` class (used by the `DefaultSecurityLogic` class), the following keywords are automatically available for the following matchers:

- the `get`, `post`, `put` and `delete` keywords for the related configurations of the `HttpMethodMatcher` (if they do not already exist)
- the `hsts` keyword for the `StrictTransportSecurityMatcher`
- the `nosniff` keyword for the `XContentTypeOptionsMatcher`
- the `noframe` keyword for the `XFrameOptionsMatcher`
- the `xssprotection` keyword for the `XSSProtectionMatcher`
- the `nocache` keyword for the `CacheControlMatcher`
- the `securityheaders` keyword as a shortcut for `hsts,nosniff,noframe,xssprotection,nocache`
- the `csrfToken` keyword for the `CsrfTokenGeneratorMatcher` with the `DefaultCsrfTokenGenerator` (it generates a CSRF token and saves it as the `pac4jCsrfToken` request attribute and in the `pac4jCsrfToken` cookie)
- the `allowAjaxRequests` keyword for a default configuration of the `CorsMatcher` with the `Access-Control-Allow-Origin` header set to `*`.
- the `none` keyword for no matchers at all.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Since <i>pac4j</i> v4, if no matchers are defined, the <code>DefaultMatchingChecker</code> applies the <b>securityHeaders,csrfToken</b> configuration.</div>
