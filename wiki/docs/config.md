---
layout: doc
title: Security configuration&#58;
---

In most `pac4j` implementations, the security configuration can be defined via a [`Config`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java) object.

It gathers the required:

- [Clients](/docs/clients.html)
- [Authorizers](/docs/authorizers.html)
- [Matchers](/docs/matchers.html)

**Example:**

```java
FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(salt));

Config config = new Config("http://localhost:8080/callback", facebookClient, twitterClient, parameterClient);

config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
config.addAuthorizer("custom", new CustomAuthorizer());

config.addMatcher("excludedPath", new ExcludedPathMatcher("^/facebook/notprotected\\.jsp$"));
```

You can also use an intermediate [`Clients`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Clients.java) object to build the `Config` one.

**Example:**

```java
Clients clients = new Clients("http://localhost:8080/callback", facebookClient, twitterClient, parameterClient);

Config config = new Config(clients);
```

Thus, you could define for **all** clients:

- a [`CallbackUrlResolver`](http://localhost:4000/docs/clients.html#the-callback-url): `clients.setCallbackUrlResolver(callbackUrlResolver);`
- a [default client](http://localhost:4000/docs/clients.html#the-callback-url): `clients.setDefaultClient(facebookClient);`
- an [`AjaxRequestResolver`](http://localhost:4000/docs/clients.html#ajax-requests): `clients.setAjaxRequestResolver(ajaxRequestResolver);`
- an [`AuthorizationGenerator`](http://localhost:4000/docs/clients.html#compute-roles-and-permissions): `clients.addAuthorizationGenerator(authorizationGenerator);`
