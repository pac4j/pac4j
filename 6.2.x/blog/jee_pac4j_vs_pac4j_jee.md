---
layout: blog
title: jee-pac4j versus pac4j-jee
author: Jérôme LELEU
date: December 2021
---

With `pac4j` v5.2, there is a very important new dependency: `pac4j-jee`. Though, there already exists a `jee-pac4j` artifact and this may seem confusing!

Let's come back to the basics: unlike any other security framework, `pac4j` is first of all a security engine agnostic from any framework and it's hosted in the Github project: *https://github.com/pac4j/pac4j*

### 1) `pac4j-XXX` dependencies = protocol supports

The core components which will be used for all protocol supports and `pac4j` implementations are in the `pac4j-core` dependency. There are:
- abstractions for the HTTP request/response (`WebContext`), the HTTP session (`SessionStore`), ... and the default behaviors named "logics": `DefaultSecurityLogic`, `DefaultCallbackLogic` and `DefaultLogoutLogic`.
- abstractions for the security model: `Client`, `Authenticator`, `ProfileService`, ...

To support various protocols, there are several dependencies in the `pac4j` project with their related implementations of the security model:
- the `FacebookClient`, `TwitterClient` classes and many others in the `pac4j-oauth` dependency for the OAuth protocol
- the `OidcClient` class in the `pac4j-oidc` dependency for the OpenID Connect protocol
- the `SAML2Client` class in the `pac4j-saml` dependency for the SAML v2 protocol
and so on.

So, on one side, `pac4j-XXX` dependencies are protocol supports in the main `pac4j` project.

### 2) `YYY-pac4j` libraries = framework implementations

On the other hand, you want to use `pac4j` in your development framework and the `pac4j-XXX` dependencies are not sufficient for that. You need to implement `pac4j` for your framework.

For example, there is a `play-pac4j` library (hostead at: *https://github.com/pac4j/play-pac4j*) which adapts/implements `pac4j` for the Play framework v2. In this library, there are a `PlayWebContext`, a `SecurityAction` using the `DefaultSecurityLogic`, ...

The `YYY-pac4j` libraries are the [implementations](/implementations.html) of `pac4j` for various environments: Play, Spring Web, Shiro, Ratpack, Vert.x, ... and especially JEE.

In the `jee-pac4j` library (hostead at: *https://github.com/pac4j/jee-pac4j*), there is a `SecurityFilter` which is a simple JEE filter using the `DefaultSecurityLogic`.
You could also expect to find a `JEEContext` which is the local implementation of the abstracted `WebContext`, but in fact, this component is not located in the `jee-pac4j` library.
**As several development environnements are JEE based (like Spring, SparkJava, Shiro, JEE), the core JEE components were in the `pac4j-core` dependency.
They are now in the `pac4j-jee` dependency.**

This way, things are cleaner: **any non-JEE-based implementation must pull `pac4j-core` while any JEE-based implementation must pull `pac4j-jee`.**

So with the release of `pac4j` v5.2.0, come the updated releases of `jee-pac4j` v6.1.0, `spring-webmvc-pac4j` v5.1.0, `spring-security-pac4j` v6.1.0 and `buji-pac4j` v6.1.0, all pulling the `pac4j-jee` dependency v5.2.0.
