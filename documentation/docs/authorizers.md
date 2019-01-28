---
layout: doc
title: Authorizers&#58;
---

**Authorizers** are meant to check authorizations to access an url (in the "[security filter](how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url)"):

* either on the authenticated user profile: has the user the appropriate role?
* or on the web context: can you call this resource in an iframe?

Notice that this concept of <code>Authorizer</code> has a broader meaning than generally in the security field.

Generally, authorizers are defined in the [security configuration](config.html) of the application.

Various authorizers are available:

- [Roles/permissions](authorizers/profile-authorizers.html#roles--permissions) - [Anonymous/remember-me/(fully) authenticated](authorizers/profile-authorizers.html#authentication-levels) - [Profile type, attribute](authorizers/profile-authorizers.html#others)
- [CORS](authorizers/web-authorizers.html#cors) - [CSRF](authorizers/web-authorizers.html#csrf) - [Security headers](authorizers/web-authorizers.html#security-headers) - [IP address, HTTP method](authorizers/web-authorizers.html#others)


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

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Since <i>pac4j</i> v4, if no authorizers are defined, the <code>DefaultAuthorizationChecker</code> applies the <b>csrf,securityheaders</b> configuration.</div>

These short names are defined as constants in [`DefaultAuthorizers`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/DefaultAuthorizers.java).

## &#9656; The composition of authorizers

You can create a composition (conjunction or disjunction) of authorizers. 
For example:

```java
final Authorizer<CommonProfile> authorizer = or(
    and(
        requireAnyRole("profile_role1"),
        requireAnyPermission("profile_permission1")
    ),
    and(
        requireAnyRole("profile_role2"),
        requireAnyPermission("profile_permission2")
    )
);
```