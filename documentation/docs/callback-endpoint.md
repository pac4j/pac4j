---
layout: doc
title: Callback endpoint&#58;
---

To handle authentication for web applications, the *pac4j* implementation provides a callback endpoint which delegates the work to the `DefaultCallbackLogic` component.

For indirect clients (like Facebook), the user is redirected to an external identity provider for login and then back to the application on the callback endpoint.


## 1) Behavior

The `DefaultCallbackLogic` has the following behaviour:

1. the credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved (or not) in the web session

2. finally, the user is redirected back to the originally requested url (or to the `defaultUrl`).


## 2) Options

The following options are available:

### a) `config`

It's the [security configuration](config.html).

### b) `defaultUrl`

It's the default URL after login if no URL was originally requested. It is an optional parameter which equals `/` by default.

### c) `saveInSession`

It indicates whether the profile should be saved into the web session. It is an optional parameter, `true` by default.

### d) `multiProfile`

It indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time. It is an optional parameter, `false` by default.

### e) `renewSession`

It indicates whether the web session must be renewed after login, to avoid session hijacking. It is an optional parameter, `true` by default.

### f) `defaultClient`

It defines the default client to use to finish the login process if none is provided on the URL. It is an optional parameter, not defined by default.


## 3) Logout

With the [logout endpoint](logout-endpoint.html), you can trigger the local and central logout process. Yet, in case of a Single-Log-Out process happening at the identity provider,
it's this callback endpoint which will receive a logout request (with some session key) to destroy the application session.
