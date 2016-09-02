---
layout: doc
title: Clients&#58;
---

A [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) represents an **authentication mechanism**. It performs the login process and returns (if successful) a [user profile](/docs/user-profile.html). Many clients are available for the:

- [OAuth protocol](/docs/clients/oauth.html)
- [SAML protocol](/docs/clients/saml.html)
- [CAS protocol](/docs/clients/cas.html)
- [OpenID Connect protocol](/docs/clients/openid-connect.html)
- [HTTP protocol](/docs/clients/http.html)
- [OpenID protocol](/docs/clients/openid.html)
- [Google App Engine support](/docs/clients/google-app-engine.html)

While most clients are self-sufficient, the HTTP clients require to define an [Authenticator](/docs/authenticators.html) to handle the credentials validation.

Clients (like Authorizers) are generally defined in a [security configuration](/docs/config.html).

Each client has a name which is by default the class name (like `FacebookClient`), but it can be explicitly set to another value with the `setName` method.

Understand the main features:

- 1) [Direct vs indirect clients](#direct-vs-indirect-clients)
- 2) [Compute roles and permissions](#compute-roles-and-permissions)
- 3) [The callback url](#the-callback-url)
- 4) [AJAX requests](#ajax-requests)
- 5) [The `Client` methods](#the-client-methods)

---

## 1) Direct vs indirect clients

Clients are of two kinds: direct for web services authentication and indirect for UI authentication. Here are their behaviours and differences:

| | Direct clients = web services authentication | Indirect clients = UI authentication
|------|----------------|-----------------
| [Authentication flows](/docs/authentication-flows.html) | 1) Credentials are passed for each HTTP request (to the "[security filter](/docs/how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url)") | 1) The originally requested url is saved in session (by the "security filter")<br />2) The user is redirected to the identity provider (by the "security filter")<br />3) Authentication happens at the identity provider (or locally for the `FormClient` and the `IndirectBasicAuthClient`)<br />4) The user is redirected back to the callback endpoint / url ("callback filter")<br />5) The user is redirected to the originally requested url (by the "[callback filter](/docs/how-to-implement-pac4j-for-a-new-framework.html#b-handle-callback-for-indirect-client)") |
| How many times the login process occurs? | The authentication happens for every HTTP request (in the "security filter") via the defined [`Authenticator`](/dcos/authenticators.html) and `ProfileCreator`.<br />For performance reasons, a cache may be used by wrapping the current `Authenticator` in a `LocalCachingAuthenticator` or the user profile can be saved (by the `Authenticator` or `ProfileCreator`) into the web session using the available web context and the `ProfileManager` class | The authentication happens only once (in the "callback filter") |
| Where is the user profile saved by default? | In the HTTP request  (stateless) | In the web session (statefull) |
| Where are the credentials? | Passed for every HTTP request (processed by the "security filter") | On the callback endpoint returned by the identity provider (and retrieved by the "callback filter") |
| Are the credentials mandatory? | Generally, no. If no credentials are provided, the direct client will be ignored (by the "security filter") | Generally, yes. Credentials are expected on the callback endpoint |
| What are the protected urls? | The urls of the web service are protected by the "security filter" | The urls of the web application are protected by the "security filter", but the callback url is not protected as it is used during the login process when the user is still anonymous |
{:.table-striped}

---

## 2) Compute roles and permissions

To compute the appropriate roles and permissions of the authenticated user profile, you need to define an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) and attach it to the client.

**Example:**

```java
AuthorizationGenerator authGen = profile -> {
  String roles = profile.getAttribute("roles");
  for (String role: roles.split(",")) {
    profile.addRole(role);
  }
};
client.addAuthorizationGenerator(authGen);
```

In fact, you can add as many authorization generators as you want using the `addAuthorizationGenerator` method or a list of authorization generators using the `setAuthorizationGenerators` method.

---

## 3) The callback url

For an indirect client, you must define the callback url which will be used in the login process: after a successful login, the identity provider will redirect the user back to the application on the callback url.

On this callback url, the "callback filter" must be installed to finish the login process.

As the callback url can be shared between multiple clients, an additionnal parameter: `client_name` (by default) which value is the client name, is used to distinguish between the different clients.

**Example:**

```java
FacebookClient facebookClient = new FacebookClient(fbKey, fbSecret);
TwitterClient twitterClient = new TwitterClient(twKey, twSecret);
Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient);
```

In that case, the callback url of the `FacebookClient` is `http://localhost:8080/callback?client_name=FacebookClient` and the callback url of the `TwitterClient` is `http://localhost:8080/callback?client_name=TwitterClient`.

This is the callback url you must define on the identity provider side.

Though, some identity providers do not allow to define callback url with query string which may cause an error during the login process. To handle that, you need to prevent the addition of the `client_name` parameter and make the client as the default one.

**Example:**

```java
OidcConfiguration configuration = new OidcConfiguration();
configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
configuration.setDiscoveryURI("https://login.microsoftonline.com/38c46e5a-21f0-46e5-940d-3ca06fd1a330/.well-known/openid-configuration");
AzureAdClient azureAdClient = new AzureAdClient(configuration);
client.setIncludeClientNameInCallbackUrl(false);
Clients clients = new Clients("http://localhost:8080/callback", azureAdClient);
clients.setDefaultClient(azureAdClient);
Config config = new Config(clients);
```

In that case, the callback url of the `AzureAdClient` is `http://localhost:8080/callback`.

In fact, the callback url is expected to be an absolute url and is passed "as is" (by the `DefaultCallbackUrlResolver`). Though, you can provide your own [`CallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/CallbackUrlResolver.java) to compute the appropriate callback url in a specific way (example: `client.setCallbackUrlResolver(myCallbackUrlResolver);`).

The other existing implementation you can use, is the [`RelativeCallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/RelativeCallbackUrlResolver.java) which turns any relative callback url into an absolute one by adding the scheme, server name and port before the relative callback url.

---

## 4) AJAX requests

For an indirect client, if the user tries to access a protected url, he will be redirected to the identity provider for login.

Though, if the incoming HTTP request is an AJAX one, no redirection will be performed and a 401 error page will be returned.

The HTTP request is considered to be an AJAX one if the value of the `X-Requested-With` header is `XMLHttpRequest` or if the `is_ajax_request` parameter or header is `true`. This is the behaviour of the [`DefaultAjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/DefaultAjaxRequestResolver.java).

But you can provide your own [`AjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/AjaxRequestResolver.java) with: `client.setAjaxRequestResolver(myAjaxRequestResolver);`.

---

## 5) The `Client` methods

The `Client` interface has the following methods:

| Method | Usage |
|--------|-------|
| `HttpAction redirect(WebContext context) throws HttpAction` (only for indirect clients) | It redirects the user to the identity provider for login.<br />The redirection of the user to the identity provider can be defined via a [`RedirectActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectActionBuilder.java) |
| `C getCredentials(WebContext context) throws HttpAction` | It extracts the credentials from the HTTP request and validates them.<br />The extraction of the credentials can be done by a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) while the credentials validation is ensured by an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) |
| `U getUserProfile(C credentials, WebContext context) throws HttpAction` | It builds the authenticated user profile.<br />The creation of the authenticated user profile can be performed by a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) |
{:.table-striped}

<br />

Clients are generally populated with default sub-components: `RedirectActionBuilder`, `CredentialsExtractor`, `ProfileCreator` and `Authenticator`, except for HTTP clients where the `Authenticator` must be defined. Sub-components can of course be changed for various [customizations](/docs/customizations.html).
