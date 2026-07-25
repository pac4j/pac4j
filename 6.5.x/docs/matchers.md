---
layout: doc
title: Matchers&#58;
---

## 1) Definition

The ["security filter"](security-filter.html) is in charge of protecting URL, requesting authentication and optionally authorization.

In some cases, you may want to bypass this "security filter" and this can be done using the **matchers** parameter which is generally a list of matcher names. A matcher is generally defined in the [security configuration](config.html).

The matchers can also be used to always apply some logic on the URLs, like adding some security headers.


## 2) Available matchers

A matcher can be defined by implementing the [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) interface. It has only one method: `boolean matches(WebContext context)` to say if the "security filter" must be applied.

A few matchers are available (but you can of course develop your own matchers):

- the [`PathMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/PathMatcher.java) allows you to include/exclude some paths in/from the security checks

- the [`HeaderMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/HeaderMatcher.java) allows you to check if a given header is `null` or matches a regular expression

- the [`HttpMethodMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/HttpMethodMatcher.java) allows you to check if the method of the HTTP request is one of the expected defined methods.


## 3) CORS

The [`CorsMatcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/CorsMatcher.java) adds CORS headers on the response (`Access-Control-Allow-Origin`, `Access-Control-Expose-Headers`, `Access-Control-Max-Age`, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`) and then returns `true`.

By default, the `allowAjaxRequests` keyword maps to a preconfigured `CorsMatcher` with:

- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Credentials: true`
- `Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS`


## 4) securityHeaders

The `securityHeaders` matchers are part of the default matchers in the [`DefaultMatchingChecker`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/checker/DefaultMatchingChecker.java) (via the `securityheaders` keyword).

They are equivalent to: `xssprotection,noframe,hsts,nosniff,nocache`.


## 5) Other matchers

Most *pac4j* implementations use the *pac4j* logics and matchers and thus the [`DefaultMatchingChecker`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/checker/DefaultMatchingChecker.java) component. In that case, the following matchers are automatically available via the following short keywords:

- the `get`, `post`, `put` and `delete` keywords for the related configurations of the `HttpMethodMatcher` (if they do not already exist)
- the `hsts` keyword for the `StrictTransportSecurityMatcher`
- the `nosniff` keyword for the `XContentTypeOptionsMatcher`
- the `noframe` keyword for the `XFrameOptionsMatcher`
- the `xssprotection` keyword for the `XSSProtectionMatcher`
- the `nocache` keyword for the `CacheControlMatcher`
- the `csrfToken` keyword for the `CsrfTokenGeneratorMatcher` with the `DefaultCsrfTokenGenerator` (it generates a CSRF token and saves it as the `pac4jCsrfToken` request attribute and in the `pac4jCsrfToken` cookie)
- the `none` keyword for no matchers at all.

These short names are defined as constants in [`DefaultMatchers`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/DefaultMatchers.java). You can override them with your own matchers using the same names.
