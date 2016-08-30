---
layout: doc
title: Authorizers&#58;
---

**Authorizers** are meant to check authorizations to access an url (in the "[security filter](https://github.com/pac4j/pac4j/wiki/How-to-implement-pac4j-for-a-new-framework---tool)"):

* either on the authenticated user profile: has the user the appropriate role?
* or on the web context: can you call this resource in an iframe?

Notice that this concept of `Authorizer` has a broader meaning than generally in the security field.

To define the security configuration of the application, all authorizers are defined in the [`Config`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java)

## Default authorizer names

Most pac4j implementations using authorizers use the [`DefaultAuthorizationChecker`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/checker/DefaultAuthorizationChecker.java) component and thus, some of the following `Authorizer` are automatically available via the short names described below:

- `hsts` for the `StrictTransportSecurityHeader` authorizer
- `nosniff` for the `XContentTypeOptionsHeader` authorizer
- `noframe` for the `XFrameOptionsHeader` authorizer
- `xssprotection` for the `XSSProtectionHeader` authorizer
- `nocache` for the `CacheControlHeader` authorizer
- `securityheaders` as a shortcut for `hsts,nosniff,noframe,xssprotection,nocache`
- `csrfToken` for the `CsrfTokenGeneratorAuthorizer` authorizer
- `csrfCheck` for the `CsrfAuthorizer` authorizer
- `csrf` as a shortcut for `csrfToken,csrfCheck`
- `isAnonymous` for the `IsAnonymousAuthorizer` authorizer
- `isAuthenticated` for the `IsAuthenticatedAuthorizer` authorizer
- `isFullyAuthenticated` for the `IsFullyAuthenticatedAuthorizer` authorizer
- `isRemembered` for the `IsRememberedAuthorizer` authorizer
- `allowAjaxRequests` for a default configuration of the `CorsAuthorizer` authorizer with the `Access-Control-Allow-Origin` header set to `*`.

## Available **authorizers** for the user profile:

To check roles and permissions on the user profile, you first need to compute them by creating an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) and attaching it to the appropriate `Client` (using the `addAuthorizationGenerator` method).

- [`RequireAnyRoleAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAnyRoleAuthorizer.java) checks that a user profile has at least one of the expected roles

- [`RequireAllRolesAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAllRolesAuthorizer.java) checks that a user profile has all the expected roles

- [`RequireAnyPermissionAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAnyPermissionAuthorizer.java) checks that a user profile has at least one of the expected permissions

- [`RequireAllPermissionsAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAllPermissionsAuthorizer.java) checks that a user profile has all the expected permissions

- [`IsAnonymousAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsAnonymousAuthorizer.java) checks that the user is anonymous (no profile or an `AnonymousProfile`)

- [`IsAuthenticatedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsAuthenticatedAuthorizer.java) checks that the user has the profile which is not an `AnonymousProfile`

- [`IsFullyAuthenticatedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsFullyAuthenticatedAuthorizer.java) checks that the user is authenticated, but not remembered (`isRemembered` method)

- [`IsRememberedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsRememberedAuthorizer.java) checks that the user is authenticated and only remembered (`isRemembered` method)

- [`CheckProfileTypeAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CheckProfileTypeAuthorizer.java) checks the current profile type of the authenticated user


## Available **authorizers** for the web context:

- [`IpRegexpAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/authorizer/IpRegexpAuthorizer.java) checks the incoming IP address

- [`CheckHttpMethodAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CheckHttpMethodAuthorizer.java) checks that the request was performed with the appropriate HTTP method

- [`CsrfTokenGeneratorAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/CsrfTokenGeneratorAuthorizer.java) generates a CSRF token based on a provided `CsrfTokenGenerator` and adds it to the current request (`pac4jCsrfToken` attribute) and saves it in the `pac4jCsrfToken` cookie

- [`CsrfAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/CsrfAuthorizer.java) check sthat the web context has the appropriate CSRF token in order to protect against CSRF attacks. Using the [`DefaultCsrfTokenGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/csrf/DefaultCsrfTokenGenerator.java) or the `csrfToken` authorizer, you can get the CSRF token and send it as a parameter or as a header. The `CsrfAuthorizer` checks that the request is a POST and has a CSRF token (found in a parameter or header)

- [`XFrameOptionsHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XFrameOptionsHeader.java) checks that the website is not called in a Iframe

- [`XSSProtectionHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XSSProtectionHeader.java) protects against XSS attacks

- [`XContentTypeOptionsHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/XContentTypeOptionsHeader.java) prevents the browser from doing MIME-type sniffing

- [`StrictTransportSecurityHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/StrictTransportSecurityHeader.java) enforces the browser that it should only be communicated with using HTTPS

- [`CacheControlHeader`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CacheControlHeader.java) enforces the browser that it should only be communicated with using HTTPS

- [`CorsAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CorsAuthorizer.java) defines how CORS requests are authorized via the `Access-Control-*` response headers
