---
layout: doc
title: Main concepts and components&#58;
---

1) A [**client**](clients.html) represents an authentication mechanism (flow). It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication

2) An [**authenticator**](authenticators.html) is a subcomponent of **clients** to validate credentials. It is related to the `ProfileService` component which handles the creation, update, and removal of users

3) An [**authorizer**](authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context

4) A [**matcher**](matchers.html) defines whether the security must apply on the **security filter** or additional web processing (security headers for example)

---

5) A [**config**](config.html) defines the security configuration composed of clients, authorizers and matchers

---

6) A [**user profile**](user-profile.html) is the profile of the authenticated user. It has an identifier, attributes, roles, a "remember-me" nature and a linked identifier (to another account)

---

7) The [**web context**](web-context.html) is an abstraction of the HTTP request and response specific to the *pac4j* implementation

8) The [**session store**](session-store.html) is an abstraction of the HTTP session specific to the *pac4j* implementation

---

9) The ["**security filter**"](how-to-implement-pac4j-for-a-new-framework.html#a-secure-an-url) (or whatever the mechanism used to intercept HTTP requests) protects an URL by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

10) The ["**callback endpoint**"](how-to-implement-pac4j-for-a-new-framework.html#b-handle-callback-for-indirect-client) finishes the login process for an indirect client

11) The ["**logout endpoint**"](how-to-implement-pac4j-for-a-new-framework.html#c-logout) handles the application and/or the identity server logouts
