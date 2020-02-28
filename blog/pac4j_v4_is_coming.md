---
layout: blog
title: pac4j v4 is coming
author: Jérôme LELEU
date: February 2020
---

A few days ago, I released *pac4j* v4.0.0-RC3 as well as three of the main implementations:

- *buji-pac4j* v5.0.0-RC3
- *spring-webmvc-pac4j* v4.0.0-RC3
- *play-pac4j* v9.0.0-RC3

based on it.

We are close to the final release which should happen in a month or two. It's high time for testing, but also to take a closer look of what's new in *pac4j* v4.

Although *pac4j* v3 is a very mature version, several major new features are coming with *pac4j* v4.

### 1) User profiles

The signature of the `Client` interface has moved from `Client<C extends Credentials, U extends CommonProfile>` to `Client<C extends Credentials>`.

It means that the returned profile is no longer a constraint and that even from the `FacebookClient`, you can return a `DbProfile` and not only a `FacebookProfile`.
This is useful when you have linked accounts and want to use the Facebook authentication to finally returned a linked account found in the database for example.

The root class for the profiles was the `CommonProfile`, but it's now the `UserProfile` and the `CommonProfile` extends the `BasicUserProfile` which implements the `UserProfile`.
All *pac4j* components still return an instance of the `CommonProfile`, but you can now return more minimal user profiles from your own clients.

### 2) HTTP actions and web context

In *pac4j* v3, requesting a redirection was actually applying the redirection on the web context (that is, setting the `Location` header and the 302 status).
This is no longer the case in *pac4j* v4. HTTP actions are just actions which must be explicitly applied to the web context by the `HttpActionAdapater` (`JEEActionHttpAdapter`).
The idea is to have a clear separation of concerns between the *pac4j* machinery and the resulting action on the web context.

Thus, the `setResponseStatus` and `writeResponseContent` methods have been removed from the `WebContext` interface and are now internally handled in the `HttpActionAdapter`.
Multiple HTTP actions (inheriting from `HttpAction`) have been created to handle the necessary HTTP actions. The `RedirectAction` is replaced by the new HTTP actions inheriting from `RedirectionAction`.
The `redirect` method of the `Client` interface is renamed as `getRedirectionAction`.

Nonetheless, some operations are still performed on the web context like adding a cookie (`addResponseCookie` method).
In the final release, these operations should be delayed until the `HttpActionAdapter` kicks in.

### 3) `Optional`

The APIs have been changed in many places to return `Optional`  when the returned value could be `null`.

For example, to get a request parameter, the call: `j2eContext.getRequestParameter(name)` becomes `jeeContext.getRequestParameter(name).orElse(null)`.

### 4) Default authorizers and matchers

In *pac4j* v3, all web checks were performed by authorizers, like adding the `X-Frame-Options` header (to `DENY`) to ensure the web application could not be included in an iframe:

```java
public class XFrameOptionsHeader implements Authorizer<CommonProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) {
        context.setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }
}
```

The problem with this approach was that this kind of authorizers were not applied until the user has a profile and on a redirection for login.

In *pac4j* v4, most web checks are now matchers (that always returned `true`):

```java
public class XFrameOptionsMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context) {
        context.setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }
}
```

Now, the default matchers are: `hsts`, `nosniff`, `securityheaders`, `xssprotection`, `allowAjaxRequests`, `nocache`, `csrfToken` (CSRF token generation), `get`, `post`, `put` and `delete`.
And the default authorizers are: `csrfCheck` (CSRF token check), `isAnonymous`, `isAuthenticated`, `isFullyAuthenticated` and `isRemembered`.

If no configuration is provided for the matchers, the `securityHeaders,csrfToken` matchers are applied.
If no configuration is provided for the authorizers, the `csrfCheck` authorizer is applied.

### 5) `FindBest`

There has been always questions on the order of using components in the *pac4j* framework and in its implementations.

Now, the `FindBest` utility must be used as much as possible to make things consistent:

```java
public static HttpActionAdapter httpActionAdapter(final HttpActionAdapter localAdapter, final Config config,
                                                  final HttpActionAdapter defaultAdapter) {
    if (localAdapter != null) {
        return localAdapter;
    } else if (config != null && config.getHttpActionAdapter() != null) {
        return config.getHttpActionAdapter();
    } else {
        CommonHelper.assertNotNull("defaultAdapter", defaultAdapter);
        return defaultAdapter;
    }
}
```

which is used in the `SecurityInterceptor` component of the *spring-webmvc-pac4j* implementation as follows: `final HttpActionAdapter<Boolean, JEEContext> bestAdapter = FindBest.httpActionAdapter(httpActionAdapter, config, JEEHttpActionAdapter.INSTANCE);`.

If we have a local component (that is, only defined in this filter), we use it first. Then, we try to use the same kind of component defined at the `Config` level.
Finally, we fall back to the default one.

### 6) Other things

As you can read on the release notes, there are many other smaller improvements and fixes:

- Improved the profile manager configuration
- Renamed `J2E` components as `JEE`
- Started updating dependencies via Renovate
- By default, the CSRF check applies on the PUT, PATCH and DELETE requests in addition to the POST requests
- Renamed the `SAMLMessageStorage*` classes as `SAMLMessageStore*` (based on `Store`)
- For `Google2Client`, change profile URL from `https://www.googleapis.com/plus/v1/people/me` to `https://www.googleapis.com/oauth2/v3/userinfo`. This change is to prepare for the shutdown of Google plus API. This change will remove the `birthday` and `emails` attribute for `Google2Client`.
- For an AJAX request, only generates the redirection URL when requested (`addRedirectionUrlAsHeader` property of the `DefaultAjaxRequestResolver`)
- Use the 303 "See Other" and 307 "Temporary Redirect" HTTP actions after a POST request (`RedirectionActionHelper`)
- Handles originally requested URLs with POST method
- Add HTTP POST Simple-Sign protocol implementation
- Properly handle states and nonces for multiple OIDC clients
- A profile can be renewed by its client when it's expired
- REVERT: remove the ID token in the `removeLoginData`  method (previously `clearSensitiveData`)

## Enjoy *pac4j* v4!
