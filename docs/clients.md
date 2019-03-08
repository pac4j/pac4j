---
layout: doc
title: Clients&#58;
---

A [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) represents an **authentication mechanism**. It performs the login process and returns (if successful) a [user profile](user-profile.html). Many clients are available for the:

- [OAuth protocol](clients/oauth.html)
- [SAML protocol](clients/saml.html)
- [CAS protocol](clients/cas.html)
- [OpenID Connect protocol](clients/openid-connect.html)
- [HTTP protocol](clients/http.html)
- [OpenID protocol](clients/openid.html)
- [Google App Engine support](clients/google-app-engine.html)
- [Kerberos (SPNEGO Negotiate) protocol](clients/kerberos.html)

While most clients are self-sufficient, the HTTP clients require defining an [Authenticator](authenticators.html) to handle the credentials validation.

Clients (like Authorizers) are generally defined in a [security configuration](config.html).

Each client has a name which is by default the class name (like `FacebookClient`), but it can be explicitly set to another value with the `setName` method.

Understand the main features:

- [Direct vs indirect clients](#1-direct-vs-indirect-clients)
- [Compute roles and permissions](#2-compute-roles-and-permissions)
- [The callback URL](#3-the-callback-url)
- [Profile definition](#4-profile-definition)
- [AJAX requests](#5-ajax-requests)
- [The `Client` methods](#6-the-client-methods)
- [Originally requested URLs](#7-originally-requested-urls)

---

## 1) Direct vs indirect clients

Clients are of two kinds: direct for web services authentication and indirect for UI authentication. Here are their behaviors and differences:

| | Direct clients = web services authentication | Indirect clients = UI authentication
|------|----------------|-----------------
| [Authentication flows](authentication-flows.html) | 1) Credentials are passed for each HTTP request (to the "[security filter](how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url)") | 1) The originally requested URL is saved in session (by the "security filter")<br />2) The user is redirected to the identity provider (by the "security filter")<br />3) Authentication happens at the identity provider (or locally for the `FormClient` and the `IndirectBasicAuthClient`)<br />4) The user is redirected back to the callback endpoint/URL ("callback filter")<br />5) The user is redirected to the originally requested URL (by the "[callback filter](how-to-implement-pac4j-for-a-new-framework.html#b-handle-callback-for-indirect-client)") |
| How many times the login process occurs? | The authentication happens for every HTTP request (in the "security filter") via the defined [`Authenticator`](/docs/authenticators.html) and `ProfileCreator`.<br />For performance reasons, a cache may be used by wrapping the current `Authenticator` in a `LocalCachingAuthenticator` or the user profile can be saved (by the `Authenticator` or `ProfileCreator`) into the web session using the available web context and the `ProfileManager` class | The authentication happens only once (in the "callback filter") |
| Where is the user profile saved by default? | In the HTTP request  (stateless) | In the web session (stateful) |
| Where are the credentials? | Passed for every HTTP request (processed by the "security filter") | On the callback endpoint returned by the identity provider (and retrieved by the "callback filter") |
| Are the credentials mandatory? | Generally, no. If no credentials are provided, the direct client will be ignored (by the "security filter") | Generally, yes. Credentials are expected on the callback endpoint |
| What are the protected URLs? | The URLs of the web service are protected by the "security filter" | The URLs of the web application are protected by the "security filter", but the callback URL is not protected as it is used during the login process when the user is still anonymous |
{:.striped}

---


## 2) Compute roles and permissions

To compute the appropriate roles and permissions of the authenticated user profile, you need to define an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) and attach it to the client.

**Example:**

```java
AuthorizationGenerator authGen = (ctx, profile) -> {
  String roles = profile.getAttribute("roles");
  for (String role: roles.split(",")) {
    profile.addRole(role);
  }
  return profile;
};
client.addAuthorizationGenerator(authGen);
```

And you can add as many authorization generators as you want using the `addAuthorizationGenerator` method or a list of authorization generators using the `setAuthorizationGenerators` method.

In fact, the `AuthorizationGenerator` component can be used to do more than just computing roles and permissions, like defining the remember-me nature of a profile based on a remember-me checkbox of a form (see: [`RememberMeAuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/generator/RememberMeAuthorizationGenerator.java)).

---


## 3) The callback URL

For an indirect client, you must define the callback URL which will be used in the login process: after a successful login, the identity provider will redirect the user back to the application on the callback URL.

On this callback URL, the "callback endpoint" must be defined to finish the login process.

As the callback URL can be shared between multiple clients, the callback URL can hold the information of the client (to be able to distinguish between the different clients), as a query parameter or as a path parameter.

**Example:**

```java
FacebookClient facebookClient = new FacebookClient(fbKey, fbSecret);
TwitterClient twitterClient = new TwitterClient(twKey, twSecret);
Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient);
```

In that case, the callback URL of the `FacebookClient` is `http://localhost:8080/callback?client_name=FacebookClient` and the callback URL of the `TwitterClient` is `http://localhost:8080/callback?client_name=TwitterClient`.

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
AzureAdClient azureAdClient = new AzureAdClient(configuration);
client.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
Clients clients = new Clients("http://localhost:8080/callback", azureAdClient);
Config config = new Config(clients);
```

In that case, the callback URL will be `http://localhost:8080/callback/AzureAdClient` for the `AzureAdClient`.

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

## 4) Profile definition

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
| `RedirectionAction redirect(WebContext context) throws HttpAction` (only for indirect clients) | It redirects the user to the identity provider for login.<br />The redirection of the user to the identity provider is defined via a [`RedirectionActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectionActionBuilder.java) |
| `C getCredentials(WebContext context) throws HttpAction` | It extracts the credentials from the HTTP request and validates them.<br />The extraction of the credentials are done by a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) while the credentials validation is ensured by an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) |
| `CommonProfile getUserProfile(C credentials, WebContext context) throws HttpAction` | It builds the authenticated user profile.<br />The creation of the authenticated user profile is performed by a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) |
| `RedirectionAction getLogoutAction(WebContext context, CommonProfile currentProfile, String targetUrl)` | It returns the redirect action to call the identity provider logout.<br />The logout redirect action computation is done by a [`LogoutActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/LogoutActionBuilder.java) |
{:.striped}

Clients are generally populated with default sub-components: `RedirectionActionBuilder`, `CredentialsExtractor`, `ProfileCreator`, `LogoutActionBuilder` and `Authenticator`, except for HTTP clients where the `Authenticator` must be defined. Sub-components can of course be changed for various [customizations](customizations.html).


---

## 7) Originally requested URLs

An originally requested URL is the URL called before the authenticated process starts: it is restored from the callback URL after the login process has been completed.

It is handled in the `DefaultSecurityLogic` and in the `CallbackSecurityLogic` by the `SavedRequestHandler` component.
By default, it's a `DefaultSavedRequestHandler` which handles GET and POST requests.
