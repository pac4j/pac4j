---
layout: doc
title: Web context authorizers&#58;
---

Some authorizers only apply on the web context:

## 1) CORS
- [`CorsAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CorsAuthorizer.java) defines how CORS requests are authorized via the `Access-Control-*` response headers


## 2) CSRF

- [`CsrfTokenGeneratorAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/CsrfTokenGeneratorAuthorizer.java) generates a CSRF token based on a provided `CsrfTokenGenerator` and adds it to the current request (`pac4jCsrfToken` attribute) and saves it in the `pac4jCsrfToken` cookie

- [`CsrfAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/CsrfAuthorizer.java) check sthat the web context has the appropriate CSRF token in order to protect against CSRF attacks. Using the [`DefaultCsrfTokenGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/DefaultCsrfTokenGenerator.java) or the `csrfToken` authorizer, you can get the CSRF token and send it as a parameter or as a header. The `CsrfAuthorizer` checks that the request is a POST and has a CSRF token (found in a parameter or header)

## 3) Security headers

- [`XFrameOptionsHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XFrameOptionsHeader.java) checks that the website is not called in a Iframe

- [`XSSProtectionHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XSSProtectionHeader.java) protects against XSS attacks

- [`XContentTypeOptionsHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XContentTypeOptionsHeader.java) prevents the browser from doing MIME-type sniffing

- [`StrictTransportSecurityHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/StrictTransportSecurityHeader.java) enforces the browser that it should only be communicated with using HTTPS

- [`CacheControlHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CacheControlHeader.java) enforces the browser that it should only be communicated with using HTTPS

## 4) Others

- [`IpRegexpAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/authorizer/IpRegexpAuthorizer.java) checks the incoming IP address

- [`CheckHttpMethodAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CheckHttpMethodAuthorizer.java) checks that the request was performed with the appropriate HTTP method
