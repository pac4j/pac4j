---
layout: doc
title: Main concepts and components&#58;
---

1) A [**client**](/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication

2) An [**authenticator**](/docs/authenticators.html) is required for HTTP clients to validate credentials

3) An [**authorizer**](/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context

4) A [**matcher**](/docs/matchers.html) defines whether the security must apply on the **security filter**

5) A [**config**](/docs/config.html) defines the security configuration via clients, authorizers and matchers

---

6) A [**user profile**](/docs/user-profile.html) is the profile of the authenticated user. It has an identifier, attributes, roles, permissions and a "remember-me" nature

---

7) The [**web context**](/docs/session-store.html) is an abstraction of the HTTP request and response specific to the *pac4j* implementation and the associated **session store** represents an implementation of the session


8) The ["**security filter**"](/docs/how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url) (or whatever the mechanism used to intercept HTTP requests) protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

9) The ["**callback controller**"](/docs/how-to-implement-pac4j-for-a-new-framework.html#b-handle-callback-for-indirect-client) finishes the login process for an indirect client

10) The [**application logout controller**"](/docs/how-to-implement-pac4j-for-a-new-framework.html#c-application-logout) logs out the user from the application
