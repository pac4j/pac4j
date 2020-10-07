---
layout: doc
title: Security filter&#58;
---

In order to secure an URL, the *pac4j* implementation provides the security filter which delegates the work to the `DefaultSecurityLogic` component.

## 1) Behavior

The `DefaultSecurityLogic` has the following behaviour:

1. If the HTTP request matches the matchers configuration (or no matchers are defined), the security is applied. Otherwise, the user is automatically granted access.

2. First, if the user is not authenticated (no profile) and if some clients have been defined in the clients parameter, a login is tried for the direct clients.

3. Then, if the user has a profile, authorizations are checked according to the authorizers configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.

4. Finally, if the user is not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the clients configuration. Otherwise, a 401 error page is displayed.

## 2) Options

The following options are available:

### a) `config`

It's the [security configuration](config.html).

### b) `clients`

It's a string of the list of the [client](clients.html) names (separated by commas) used for authentication. It is an optional parameter.

In all cases, this filter requires the user to be authenticated. Thus, if the `clients` is blank or not defined, the user must have been previously authenticated or a 401 error is returned.

A specific client may be chosen among all defined clients for the filter by using the request parameter: `force_client`.

### c) `authorizers`

It's a string of the list of the [authorizer](authorizers.html) names (separated by commas) used to check authorizations. It is an optional parameter.

If the `authorizers` is blank or not defined, the default authorizer is applied: `csrfCheck` for web applications (at least one `IndirectClient` is defined) and no authorizer is applied for web services.
The `isAuthenticated` authorizer is also applied by default if no `AnonymousClient` is configured.

You can also use the [out-of-the-box authorizers](authorizers.html#-default-authorizer-names), already available without defining them in the security configuration.

### d) `matchers`

It's the list of the [matcher](matchers.html) names (separated by commas) that the request must satisfy to check authentication/authorizations. It is an optional parameter.

If the `matchers` is blank or not defined, it is satisfied and the `securityHeaders` is applied and the `csrfToken` is applied only for web applications (at least one `IndirectClient` is defined).

You can also use the [out-of-the-box matchers](matchers.html#3-default-matchers), already available without defining them in the security configuration.
