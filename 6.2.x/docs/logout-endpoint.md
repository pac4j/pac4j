---
layout: doc
title: Logout endpoint&#58;
---

To handle the logout, a logout endpoint is necessary to perform:

- the local logout by removing the pac4j profiles from the session
- the central logout by calling the identity provider logout endpoint. This is the Single-Log-Out (= SLO) process.


## 1) Behavior

By default, it relies on the `DefaultLogoutLogic` which has the following behaviour:

1. If the `localLogout` property is `true`, the *pac4j* profiles are removed from the web session (and the web session is destroyed if the `destroySession` property is `true`)

2. A post logout action is computed as the redirection to the `url` request parameter if it matches the `logoutUrlPattern` or to the `defaultUrl` if it is defined or as a blank page otherwise

3. If the `centralLogout` property is `true`, the user is redirected to the identity provider for a central logout and then optionally to the post logout redirection URL (if it's supported by the identity provider and if it's an absolute URL).
If no central logout is defined, the post logout action is performed directly.


## 2) Options

The following options are available for the logout endpoint. They can be defined via setters, constructors, servlet parameters, etc... depending on the *pac4j* implementation:

### a) config

It's the [security configuration](config.html).

### b) defaultUrl

It's the default logout URL if no `url` request parameter is provided or if the `url` does not match the `logoutUrlPattern`. It is an optional parameter, not defined by default.

### c) logoutUrlPattern

It's the logout URL pattern that the `url` parameter must match. It is an optional parameter and only relative URLs are allowed by default.

### d) localLogout

It indicates whether a local logout must be performed. It is an optional parameter, `true` by default.

### e) destroySession

It defines whether we must destroy the web session during the local logout. It is an optional parameter, `false` by default.

### f) centralLogout

It defines whether a central logout must be performed. It is an optional parameter, `false` by default.


## 3) Logout requests from the identity provider

In case of a central logout, the SLO process happening at the identity provider will send logout requests to the applications.
Yet, these logout requests will be received by the [callback endpoint](callback-endpoint.html) and not this logout endpoint.
