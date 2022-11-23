---
layout: doc
title: Clients&#58;
---

A [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) represents a **web authentication mechanism**. It performs the login process and returns (if successful) a [user profile](user-profile.html). Many clients are available for the:

- [OAuth protocol](clients/oauth.html)
- [SAML protocol](clients/saml.html)
- [CAS protocol](clients/cas.html)
- [OpenID Connect protocol](clients/openid-connect.html)
- [HTTP protocol](clients/http.html)
- [Google App Engine support](clients/google-app-engine.html)
- [Kerberos (SPNEGO Negotiate) protocol](clients/kerberos.html)

While most clients are self-sufficient, the HTTP clients require defining an [Authenticator](authenticators.html) to handle the credentials validation.

Clients (like authorizers and matchers) are generally defined in a [security configuration](config.html).

Each client has a name which is by default the class name (like `FacebookClient`), but it can be explicitly set to another value with the `setName` method.

Understanding the main features:

- [Direct vs indirect clients](#1-direct-vs-indirect-clients)
- [Compute roles](#2-compute-roles)
- [The callback URL](#3-the-callback-url)
- [Profile options](#4-profile-options)
- [AJAX requests](#5-ajax-requests)
- [The `Client` methods](#6-the-client-methods)
- [The originally requested URL](#7-the-originally-requested-url)
- [Silent login](#8-silent-login)


---

## 1) Direct vs indirect clients

Clients are of two kinds: direct clients are for web services authentication and indirect clients are for UI authentication.

Here are their behaviors and differences:

| | Direct clients = web services authentication | Indirect clients = UI authentication
|------|----------------|-----------------
| [Authentication flows](authentication-flows.html) | 1) Credentials are passed for each HTTP request (to the "[security filter](security-filter.html)") | 1) The originally requested URL is saved in session (by the "security filter")<br />2) The user is redirected to the identity provider (by the "security filter")<br />3) Authentication happens at the identity provider (or locally for the `FormClient` and the `IndirectBasicAuthClient`)<br />4) The user is redirected back to the callback endpoint/URL ("callback endpoint")<br />5) The user is redirected to the originally requested URL (by the "[callback endpoint](callback-endpoint.html)") |
| How many times the login process occurs? | The authentication happens for every HTTP request (in the "security filter") via the defined [`Authenticator`](/docs/authenticators.html) and `ProfileCreator`.<br />For performance reasons, a cache may be used by wrapping the current `Authenticator` in a `LocalCachingAuthenticator` or the "security filter" can be configured to save the profile in session (`ProfileStorageDecision`) | The authentication happens only once (in the "callback filter") |
| Where is the user profile saved by default? | In the HTTP request  (stateless) | In the web session (stateful) |
| Where are the credentials? | Passed for every HTTP request (processed by the "security filter") | On the callback endpoint returned by the identity provider (and retrieved by the "callback endpoint") |
| What are the protected URLs? | The URLs of the web service are protected by the "security filter" | The URLs of the web application are protected by the "security filter", but the callback URL is not protected as it is used during the login process when the user is still anonymous |
{:.striped}

---


## 2) Compute roles

To compute the appropriate roles of the authenticated user profile, you need to define an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) and attach it to the client.

**Example:**

```java
AuthorizationGenerator authGen = (ctx, session, profile) -> {
  String roles = profile.getAttribute("roles");
  for (String role: roles.split(",")) {
    profile.addRole(role);
  }
  return Optional.of(profile);
};
client.addAuthorizationGenerator(authGen);
```

You can add as many authorization generators as you want using the `addAuthorizationGenerator` method or a list of authorization generators using the `setAuthorizationGenerators` method.

---


## 3) The callback URL

For an indirect client, you must define the callback URL which will be used in the login process: after a successful login, the identity provider will redirect the user back to the application on the callback URL.

On this callback URL, the "callback endpoint" must be defined to finish the login process.

As the callback URL can be shared between multiple clients, the callback URL must hold the information of the client (to be able to distinguish between the different clients), as a query parameter or as a path parameter.

**Example:**

```java
FacebookClient facebookClient = new FacebookClient(fbKey, fbSecret);
TwitterClient twitterClient = new TwitterClient(twKey, twSecret);
Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient);
```

In this case, the callback URL of the `FacebookClient` is `http://localhost:8080/callback?client_name=FacebookClient` and the callback URL of the `TwitterClient` is `http://localhost:8080/callback?client_name=TwitterClient`.

This is the callback URL you must define on the identity provider side.

This happens because the default [`CallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/callback/CallbackUrlResolver.java) of the clients is the [`QueryParameterCallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/callback/QueryParameterCallbackUrlResolver.java).

You can change the `client_name` parameter using the `setClientNameParameter` method of the `QueryParameterCallbackUrlResolver`.

But you can also use the [`PathParameterCallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/callback/PathParameterCallbackUrlResolver.java), which adds the client name as a path parameter.

**Example:**

```java
OidcConfiguration configuration = new OidcConfiguration();
configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
configuration.setDiscoveryURI("https://login.microsoftonline.com/38c46e5a-21f0-46e5-940d-3ca06fd1a330/.well-known/openid-configuration");
AzureAd2Client client = new AzureAd2Client(configuration);
Clients clients = new Clients("http://localhost:8080/callback", client);
Config config = new Config(clients);
```

In that case, the callback URL will be `http://localhost:8080/callback/AzureAd2Client` for the `AzureAd2Client`.

You may even use the [`NoParameterCallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/callback/NoParameterCallbackUrlResolver.java) which left the callback URL untouched.
In that case, no parameter will be added to the callback URL and no client will be retrieved on the callback endpoint. You will be forced to define a "default client" at the `CallbackLogic` level.

**Example:**

```java
defaultCallbackLogic.setClient("FacebookClient");
```

The `CallbackUrlResolver` relies on a [`UrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/url/UrlResolver.java) to complement the URL according to the current web context.
The `UrlResolver` can be retrieved via the `getUrlResolver()` method of the client.

You can use the [`DefaultUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/url/DefaultUrlResolver.java) and handle relative URLs by using: `defaultUrlResolver.setCompleteRelativeUrl(true)`.
Or provide your own `UrlResolver` using the `setUrlResolver` method.

---

## 4) Profile options

You can control if the profile is saved in session or not via the `setSaveProfileInSession` method. By default, it's `true` for indirect clients and `false` for direct clients.

You can control if the profile is saved in addition to the existing authenticated profile or in replacement via the `setMultiProfile` method (`false` by default).

Most clients rely on the `Authenticator` and `ProfileCreator` components to validate credentials and create the user profile.

At the end of the login process, the returned user profile is created by the (internal) `Authenticator` or `ProfileCreator`, which holds a [profile definition](user-profile.html#8-profile-definition).

This profile definition can be overridden using the `setProfileDefinition` method.


---

## 5) AJAX requests

For an indirect client, if the user tries to access a protected URL, he will be redirected to the identity provider for login.

Though, if the incoming HTTP request is an AJAX one, no redirection will be performed and a 401 error page will be returned.

The HTTP request is considered to be an AJAX one if the value of the `X-Requested-With` header is `XMLHttpRequest` or if the `is_ajax_request` parameter or header is `true`. This is the behaviour of the [`DefaultAjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/ajax/DefaultAjaxRequestResolver.java).

The `DefaultAjaxRequestResolver` will only compute the redirection URL and add it as a header if the `addRedirectionUrlAsHeader` property is set to `true`.

But you can provide your own [`AjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/ajax/AjaxRequestResolver.java) with: `client.setAjaxRequestResolver(myAjaxRequestResolver);`.


---

## 6) The `Client` methods

The `Client` interface has the following methods:

| Method | Usage |
|--------|-------|
| `Optional<RedirectionAction> getRedirectionAction(WebContext context, SessionStore sessionStore)` | It returns the redirection action to redirect the user to the identity provider for login. It only makes sense for indirect clients.<br />The redirection of the user to the identity provider is internally computed via a [`RedirectionActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectionActionBuilder.java) |
| `Optional<Credentials> getCredentials(WebContext context, SessionStore sessionStore)` | It extracts the credentials from the HTTP request and validates them.<br />The extraction of the credentials are done by a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) while the credentials validation is ensured by an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) |
| `Optional<UserProfile> getUserProfile(Credentials credentials, WebContext context, SessionStore sessionStore)` | It builds the authenticated user profile.<br />The creation of the authenticated user profile is performed by a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) |
| `Optional<UserProfile> renewUserProfile(UserProfile profile, WebContext context, SessionStore sessionStore)` | It returns the renewed user profile |
| `Optional<RedirectionAction> getLogoutAction(WebContext context, SessionStore sessionStore, UserProfile currentProfile, String targetUrl)` | It returns the redirection action to call the identity provider logout.<br />The logout redirection action computation is done by a [`LogoutActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/LogoutActionBuilder.java) |
{:.striped}

Clients are generally populated with default sub-components: `RedirectionActionBuilder`, `CredentialsExtractor`, `ProfileCreator`, `LogoutActionBuilder` and `Authenticator`, except for HTTP clients where the `Authenticator` must be explicitely defined. Sub-components can of course be changed for various [customizations](customizations.html).


---

## 7) The originally requested URL

The originally requested URL is the URL called before the authenticated process starts: it is restored by the "callback endpoint" after the login process has been completed.

It is handled in the `DefaultSecurityLogic` and in the `DefaultCallbackLogic` by the `SavedRequestHandler` component.
By default, it's the `DefaultSavedRequestHandler` which handles GET and POST requests.


---

## 8) Silent login

When using an `IndirectClient`, the login process can fail or be cancelled at the external identity provider level.

Thus, no user profile is created and the access is not granted to the secured resources (401 error).

Though, you may still want to access the web resources if the login process has failed or been cancelled.

For that, you can return a custom profile instead of no profile by using the `setProfileFactoryWhenNotAuthenticated` method of the client.

**Example:**

```java
myClient.setProfileFactoryWhenNotAuthenticated(p -> AnonymousProfile.INSTANCE);
```

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> In that case, the access is granted to all secured resources for the whole web session unless the proper authorizers have been defined.</div>
