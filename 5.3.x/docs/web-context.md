---
layout: doc
title: Web context&#58;
---

## 1) `WebContext`

The [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) is an abstraction to deal with the HTTP request and response of any framework.

It is used to work with:

- the HTTP request by retrieving the parameters, the attributes, the headers, the method, the remote address, the server name, the server port, the server scheme, the path, the protocol, the cookies, the full URL, the content
- the HTTP response by setting the content type, the headers and cookies.

Its implementations are different depending on the *pac4*j implementations.

For example, there is a [`JEEContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/JEEContext.java) for JEE applications, a `PlayWebContext` for Play applications, etc.

## 2) `WebContextFactory`

For a given framework/*pac4j* implementation, generally, the same type of web context is instantiated over and over again. Though, there are edge cases when you want to instantiate another `WebContext`.

This is controlled by the [`WebContextFactory`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContextFactory.java).

Like the `WebContext`, its implementations are different depending on the *pac4*j implementations.

For example, there is a `JEEContextFactory.INSTANCE` to instantiate `JEEContext` for JEE applications.
