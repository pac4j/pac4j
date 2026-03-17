---
layout: doc
title: Web context authorizers&#58;
---

Some authorizers only apply on the web context:

## 1) CSRF

- [`CsrfAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/CsrfAuthorizer.java) checks that the web context has the appropriate CSRF token in order to protect against CSRF attacks. Using the [`DefaultCsrfTokenGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/matcher/csrf/DefaultCsrfTokenGenerator.java) or the `csrfToken` matcher, you can get the CSRF token and send it as a parameter or as a header. The `CsrfAuthorizer` checks that the request is a POST and has a CSRF token (found in a parameter or header)

## 2) Others

- [`IpRegexpAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/authorizer/IpRegexpAuthorizer.java) checks the incoming IP address

- [`CheckHttpMethodAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CheckHttpMethodAuthorizer.java) checks that the request was performed with the appropriate HTTP method
