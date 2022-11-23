---
layout: doc
title: Authorizers&#58;
---

**Authorizers** are meant to check authorizations when accessing an URL (in the "[security filter](security-filter.html)"):

* either on the authenticated user profile: has the user the appropriate role?
* or on the web context: can you call this resource with that HTTP method?

Notice that this concept of <code>Authorizer</code> has a broader meaning than generally in the security field.

Generally, authorizers are defined in the [security configuration](config.html) of the application.

Various authorizers are available:

- [Roles](authorizers/profile-authorizers.html#roles) - [Anonymous/remember-me/(fully) authenticated](authorizers/profile-authorizers.html#authentication-levels) - [Profile type, attribute](authorizers/profile-authorizers.html#others)
- [CSRF](authorizers/web-authorizers.html#csrf) - [IP address, HTTP method](authorizers/web-authorizers.html#others)


## &#9656; Default authorizer names

Most *pac4j* implementations use the *pac4j* logics and authorizers and thus the [`DefaultAuthorizationChecker`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/checker/DefaultAuthorizationChecker.java) component. In that case, the following authorizers are automatically available via the following short keywords:

- `csrfCheck` (for the `CsrfAuthorizer` authorizer) to check that the CSRF token has been sent as the `pac4jCsrfToken` header or parameter in a POST request
- `isAnonymous` (for the `IsAnonymousAuthorizer` authorizer) to ensure the user is not authenticated
- `isAuthenticated` (for the `IsAuthenticatedAuthorizer` authorizer) to ensure the user is authenticated (not necessary by default unless you use the `AnonymousClient`)
- `isFullyAuthenticated` (for the `IsFullyAuthenticatedAuthorizer` authorizer) to check if the user is authenticated but not remembered
- `isRemembered` (for the `IsRememberedAuthorizer` authorizer) for a remembered user
- `none` for no authorizers at all.

These short names are defined as constants in [`DefaultAuthorizers`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/DefaultAuthorizers.java). You can override them with your own authorizers using the same names.

## &#9656; The composition of authorizers

You can create a composition (conjunction or disjunction) of authorizers.
For example:

```java
final Authorizer authorizer = or(
    and(
        requireAnyRole("profile_role1"),
        requireAnyRole("profile_role2")
    ),
    and(
        requireAnyRole("profile_role3"),
        requireAnyRole("profile_role4"),
    )
);
```
