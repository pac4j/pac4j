---
layout: doc
title: Security filter&#58;
---

To protect URLs, a security filter is necessary which defines which authentication/authorization mechanisms which are applied.

## 1) Behavior

By default, it relies on the `DefaultSecurityLogic` which has the following behaviour:

1. If the HTTP request matches the matchers configuration (or no matchers are defined), the security is applied. Otherwise, the user is automatically granted access.

2. First, if the user is not authenticated (no profile) and if some clients have been defined in the clients parameter, a login is tried for the direct clients.

3. Then, if the user has a profile, authorizations are checked according to the authorizers configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.

4. Finally, if the user is not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the clients configuration. Otherwise, a 401 error page is displayed.

When the 401 HTTP status code is set, a `WWW-Authenticate` header is added with the value: `Bearer ream="pac4j"` if it does not already exist (to be compliant with the HTTP spec). A 403 HTTP status code can be used instead if the `WWW-Authenticate` header does not exist by setting: `HttpActionHelper.setAlwaysUse401ForUnauthenticated(false);`.

## 2) Options

The following options are available for the security filter. They can be defined via setters, constructors, servlet parameters, etc... depending on the *pac4j* implementation:

### a) config

It's the [security configuration](config.html).

### b) clients

It's a string of the list of the [client](clients.html) names (separated by commas) used for authentication. It is an optional parameter.

In all cases, this filter requires the user to be authenticated. Thus, if the `clients` is blank or not defined, the user must have been previously authenticated or a 401 error is returned.

A specific client may be chosen among all defined clients for the filter by using the request parameter: `force_client`.

### c) authorizers

It's a string of the list of the [authorizer](authorizers.html) names (separated by commas) used to check authorizations. It is an optional parameter.

If the `authorizers` is blank or not defined, the default authorizer is applied: `csrfCheck` for web applications (at least one `IndirectClient` is defined) but not for web services.
The `isAuthenticated` authorizer is also applied by default if no `AnonymousClient` is configured.

You can also use the [out-of-the-box authorizers](authorizers.html#-default-authorizer-names), already available without defining them in the security configuration.
Start the `authorizers` string by "+" to add other authorizers to the default ones or without to replace them.

### d) matchers

It's the list of the [matcher](matchers.html) names (separated by commas) that the request must satisfy to check authentication/authorizations. It is an optional parameter.

If the `matchers` is blank or not defined, it matches as by default the `securityHeaders` is applied and the `csrfToken` is applied only for web applications (at least one `IndirectClient` is defined).

You can also use the [out-of-the-box matchers](matchers.html#3-default-matchers), already available without defining them in the security configuration.
Start the `matchers` string by "+" to add other matchers to the default ones or without to replace them.


---

## 3) Advanced options

Advanced options can be set at:

- the `Config` level
- directly at the security filter level via setters, constructors, servlet parameters, etc... depending on the *pac4j* implementation:

### a) sessionStore

You may define a specific [`SessionStore`](session-store.html) instead of the default one of the *pac4j* implementation.

### b) httpActionAdapter

You may define a specific [`HttpActionAdapter`](http-action-adapter.html) instead of the default one of the *pac4j* implementation.

### c) logoutLogic

You may define a specific `SecurityLogic` instead of the default `DefaultSecurityLogic`.

### d) webContextFactory

You may define a specific [`WebContextFactory`](web-context.html) instead of the default one of the *pac4j* implementation.
