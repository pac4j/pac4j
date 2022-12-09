---
layout: doc
title: Security configuration&#58;
---

The security configuration must be defined via a [`Config`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java) object.

### 1) The basics

It gathers the required:

- [Clients](clients.html) (authentication mechanisms)
- [Authenticators](authenticators.html) (credentials validation)
- [Authorizers](authorizers.html) (authorization checks)
- [Matchers](matchers.html)

**Example:**

```java
FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");

Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient);

config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
config.addAuthorizer("custom", new CustomAuthorizer());

config.addMatcher("excludedPath", new ExcludedPathMatcher("^/facebook/notprotected\\.jsp$"));
```

`http://localhost:8080/callback` is the URL of the callback endpoint, which is only necessary for indirect clients and can be removed for web services:

```java
ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(salt));

Config config = new Config(parameterClient);
```

### 2) `Clients`

You can also use an intermediate [`Clients`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Clients.java) object to build the `Config` one.

**Example:**

```java
Clients clients = new Clients("http://localhost:8080/callback", facebookClient, twitterClient, parameterClient);

Config config = new Config(clients);
```

In that case, you can define for **all** the clients:

- the same callback URL, `UrlResolver` and [`CallbackUrlResolver`](clients.html#3-the-callback-url): `clients.setCallbackUrl(callbackUrl)`, `clients.setUrlResolver(urlResolver)` and `clients.setCallbackUrlResolver(callbackUrlResolver)`
- the same [`AjaxRequestResolver`](clients.html#5-ajax-requests): `clients.setAjaxRequestResolver(ajaxRequestResolver)`
- the same [`AuthorizationGenerator`](clients.html#2-compute-roles): `clients.addAuthorizationGenerator(authorizationGenerator)`

### 3) Advanced

You can define at the `Config` level a few components that will be used by the security filter and callback/logout endpoints:

- `config.setProfileManagerFactory(x)` to build a specific [`ProfileManager`](profile-manager.html) from the `WebContext`
- `config.setSessionStoreFactory(x)` to set a specific [`SessionStore`](session-store.html)
- `config.setHttpActionAdapter(x)` to set a specific [`HttpActionAdapter`](http-action-adapter.html)
- `config.setSecurityLogic(x)` to set a specific `SecurityLogic`
- `config.setCallbackLogic(x)` to set a specific `CallbackLogic`
- `config.setLogoutLogic(x)` to set a specific `LogoutLogic`
- `config.setWebContextFactory(x)` to set a specific [`WebContextFactory`](web-context.html).
