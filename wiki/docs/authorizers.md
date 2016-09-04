---
layout: doc
title: Authorizers&#58;
---

**Authorizers** are meant to check authorizations to access an url (in the "[security filter](/docs/how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url)"):

* either on the authenticated user profile: has the user the appropriate role?
* or on the web context: can you call this resource in an iframe?

<div class="alert alert-danger"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Notice that this concept of `Authorizer` has a broader meaning than generally in the security field.</div>

Generally, authorizers are defined in the [security configuration](/docs/config.html) of the application.

Various authorizers are available:

- [Roles / permissions](/docs/authorizers/profile-authorizers.html#roles--permissions) - [Anonymous / remember-me / (fully) authenticated](/docs/authorizers/profile-authorizers.html#authentication-levels) - [Profile type, attribute](/docs/authorizers/profile-authorizers.html#others)
- [CORS](/docs/authorizers/web-authorizers.html#cors) - [CSRF](/docs/authorizers/web-authorizers.html#csrf) - [Security headers](/docs/authorizers/web-authorizers.html#security-headers) - [IP address, HTTP method](/docs/authorizers/web-authorizers.html#others)


## &#9656; Default authorizer names

Most *pac4j* implementations use *pac4j* logics and authorizers and thus the [`DefaultAuthorizationChecker`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/checker/DefaultAuthorizationChecker.java) component. In that case, the following `Authorizer` are automatically available via the following short names:

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
