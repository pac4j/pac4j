---
layout: doc
title: Clients&#58;
---

A [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) represents an **authentication mechanism**. It performs the login process and returns (if successful) a user profile ([`CommonProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/CommonProfile.java)). Many clients are available for the:

- [CAS protocol](/docs/clients/cas.html)
- [SAML protocol](/docs/clients/saml.html)
- [OpenID Connect protocol](/docs/clients/openid-connect.html)
- [OAuth protocol](/doc/clients/oauth.html)
- [HTTP protocol](/docs/clients/http.html)
- [OpenID protocol](/docs/clients/openid.html)
- [Google App Engine support](/docs/clients/google-app-engine.html)

While most clients are self-sufficient, the HTTP clients require to define an [Authenticator](/docs/authenticators.html) to handle the credentials validation.

Clients (like Authenticators) are generally defined in a [security configuration](/docs/config.html).

Understand the main features:

- [What are the different kind of clients for the different kinds of authentication?](#direct-vs-indirect-clients)
- [What is the technical implementation of the `Client`?](#the-client-methods)

---

## &#9656; Direct vs indirect clients

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

## &#9656; The `Client` methods

The `Client` interface has the following methods:

| Method | Usage |
|--------|-------|
| `HttpAction redirect(WebContext context) throws HttpAction` (only for indirect clients) | It redirects the user to the identity provider for login.<br />The redirection of the user to the identity provider can be defined via a [`RedirectActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectActionBuilder.java) |
| `C getCredentials(WebContext context) throws HttpAction` | It extracts the credentials from the HTTP request and validates them.<br />The extraction of the credentials can be done by a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) while the credentials validation is ensured by an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) |
| `U getUserProfile(C credentials, WebContext context) throws HttpAction` | It builds the authenticated user profile.<br />The creation of the authenticated user profile can be performed by a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) |
{:.table-striped}

<br />

Clients are generally populated with default sub-components: `RedirectActionBuilder`, `CredentialsExtractor`, `ProfileCreator` and `Authenticator`, except for HTTP clients where the `Authenticator` must be defined. Sub-components can of course be changed for various [customizations](/docs/customizations.html).

---

## Compute roles and permissions

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

You can attach it to the `Clients` object (in the `Config`) if you want the `AuthorizationGenerator` to be used for all clients.

---

## Custom `AjaxRequestResolver` and `CallbackUrlResolver`

For an indirect client, you define the callback url which will be used in the login process: after a successful login, the identity provider will redirect the user back to the application on the callback url.

By default, the callback url is expected to be an absolute url and is passed "as is" (by the `DefaultCallbackUrlResolver`). Though, you can provide your own [`CallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/CallbackUrlResolver.java) to compute the appropriate callback url. The available [`RelativeCallbackUrlResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/RelativeCallbackUrlResolver.java) turns the defined relative callback url into an absolute one. Example: `client.setCallbackUrlResolver(myCallbackUrlResolver);`.

For an indirect client, if the user tries to access a protected url, he will be redirected to the identity provider for login. Though, if the incoming HTTP request is an AJAX one, no redirection will be performed and a 401 error page will be returned. The HTTP request is considered to be an AJAX one if the value of the `X-Requested-With` header is `XMLHttpRequest` or if the `is_ajax_request` parameter or header is `true`. This is the behaviour of the [`DefaultAjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/DefaultAjaxRequestResolver.java), but you can provide your own [`AjaxRequestResolver`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/AjaxRequestResolver.java). Example: `client.setAjaxRequestResolver(myAjaxRequestResolver);`.
