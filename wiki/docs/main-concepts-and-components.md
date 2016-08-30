---
layout: doc
title: Main concepts and components&#58;
---

1) A [**client**](/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication

2) An [**authorizer**](/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context

3) A [**matcher**](/docs/matchers.html) defines whether the security must apply on a specific url

4) A [**config**](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java) defines the security configuration via clients, authorizers and matchers

5) The ["**security filter**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java) (or whatever the mechanism used to intercept HTTP requests) protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

6) The ["**callback controller**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultCallbackLogic.java) finishes the login process for an indirect client

7) The [**application logout controller**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultApplicationLogoutLogic.java) logs out the user from the application.