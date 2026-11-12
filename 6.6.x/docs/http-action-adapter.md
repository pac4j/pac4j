---
layout: doc
title: HTTP action adapter&#58;
seo_title: "HTTP action adapters for Java web frameworks | pac4j"
description: "Learn how pac4j HTTP action adapters translate authentication and authorization outcomes into responses for your Java web framework."
---

The [`HttpActionAdapter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/adapter/HttpActionAdapter.java) is an abstraction to apply *pac4j* HTTP actions
(= [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/http/HttpAction.java)) returned by the logics on the HTTP request/response = the [web context](web-context.html).

Its implementations are different depending on the *pac4j* implementations.

For example, there is a [`JEEHttpActionAdapter`](https://github.com/pac4j/pac4j/blob/master/pac4j-jakartaee/src/main/java/org/pac4j/jee/http/adapter/JEEHttpActionAdapter.java) for JEE applications, a `PlayHttpActionAdapter` for Play applications, etc.
