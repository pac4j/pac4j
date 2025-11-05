---
layout: blog
title: What's new with Spring and pac4j?
author: Jérôme LELEU
date: September 2022
---

`pac4j` is a security framework available for many frameworks in the Java ecosystem.
Unlike other Java security libraries which are dedicated to one framework, `pac4j` is composed of a security engine (the "core" [pac4j](https://github.com/pac4j/pac4j) project) and many specific [implementations](https://www.pac4j.org/implementations.html).

`pac4j` being a security engine for many implementations allows developers to only learn one security model for all the frameworks and switch from one framework to another one very easily.
You can use it in a simple JEE web application, with Undertow, Spark Java, JAX-RS, Ratpack, etc., and of course with Spring.
It can even be used only for authentication delegation like in the [CAS SSO server](https://apereo.github.io/cas/index.html).

In the Java ecosystem, Spring is a very popular and important framework  and `pac4j` is integrated with three Spring libraries:
- Spring WebMVC
- Spring Webflux
- Spring Security.


## Spring Webflux support

This is a new implementation for `pac4j`: [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j).

While `pac4j` is not a reactive engine, it has been smartly implemented for many asynchronous frameworks: Play 2, Vert.x, ... and Spring Webflux.

In the Spring Webflux pac4j security library, you have:
- the `SecurityFilter` to protect URLs
- the `CallbackController` to finish external login processes
- the `LogoutController` to handle logout.


## Spring 6 support

As Spring fans know, a new major version 6 of the Spring framework will come soon. It is based on JDK 17 and JakartaEE 9.

All related `pac4j` security libraries have been already upgraded (using Spring 6 milestones) to anticipate this arrival:
- [`spring-webmvc-pac4j`](https://github.com/pac4j/spring-webmvc-pac4j) has been upgraded to Spring 6, it's a new version 7.0.0 (-SNAPSHOT for now) as well as the demo: [`spring-webmvc-pac4j-boot-demo`](https://github.com/pac4j/spring-webmvc-pac4j-boot-demo) (v7.0.0-SNAPSHOT)
- `spring-webflux-pac4j` has been upgraded to Spring 6, it's a new version 2.0.0 (-SNAPSHOT forw now) as well as the demo: [`spring-webflux-pac4j-boot-demo`](https://github.com/pac4j/spring-webflux-pac4j-boot-demo) (v2.0.0-SNAPSHOT)
- [`spring-security-pac4j`](https://github.com/pac4j/spring-security-pac4j) has been upgraded to Spring 6, it's a new version 9.0.0 (-SNAPSHOT forw now) as well as the demo: [`spring-security-webmvc-pac4j-boot-demo`](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo) (v9.0.0-SNAPSHOT)
- a new [`pac4j-springbootv3`](https://github.com/pac4j/pac4j/tree/master/pac4j-springbootv3) module has been added to the "core" `pac4j` project version 5.6.0 (-SNASPHOT for now): only this module is compiled in JDK 17 while the rest remains in JDK 11.


## Bridge from pac4j to Spring Security

Spring Security is a well-known security framework for Spring.

You can replace it with `spring-webmvc-pac4j` to only use a `pac4j` security framework or if it's too much work, you can make it work with `pac4j` thanks to the `spring-security-pac4j` security library.

In the Spring security pac4j library, you have:
- the `SecurityFilter` to protect URLs
- the `CallbackFilter` to finish external login processes
- the `LogoutFilter` to handle logout
- the `SpringSecurityProfileManager` to populate the Spring Security context from the `pac4j` security context.

Since version 8, the `SecurityFilter`, `CallbackFilter`, and `LogoutFilter` have been removed.
You must use another `pac4j` security library like the [`javaee-pac4j`](https://github.com/pac4j/jee-pac4j) or [`jakartaee-pac4j`](https://github.com/pac4j/jee-pac4j) security library which has similar filters (in different packages).
See the [migration guide](https://github.com/pac4j/spring-security-pac4j/wiki/Migration-guide#--8x).

Only the `SpringSecurityProfileManager` component remains and `spring-security-pac4j` version >= 8 is now only a bridge from `pac4j` to Spring Security.


## Smart "builders" for `spring-webmvc-pac4j` and `spring-webflux-pac4j`

All `pac4j` implementations share a similar way of configuring security: you need to create a `Config` and a "security filter":

```java
    // CAS login process
    var casClient = new CasClient(new CasConfiguration("https://casserverpac4j.herokuapp.com/login"));

    var clients = new Clients("http://localhost:8080/callback", casClient);

    var config = new Config(clients);
    // ROLE_ADMIN authorization check
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));

    ...

    // protect the /cas/ URL with a CAS login process and a ROLE_ADMIN check
    registry.addInterceptor(
            new SecurityInterceptor(config, "CasClient", "admin")
        ).addPathPatterns("/cas/*");
```

In the latest versions of `spring-webmvc-pac4j` (>= 6.1) and `spring-webflux-pac4j` (>= 1.1), you can directly create the `Authorizer` in the "security filter":

```java
    // CAS login process
    var casClient = new CasClient(new CasConfiguration("https://casserverpac4j.herokuapp.com/login"));

    var clients = new Clients("http://localhost:8080/callback", casClient);

    var Config config = new Config(clients);
    // no ROLE_ADMIN authorization check definition in the Config

    ...

    // protect the /cas/ URL with a CAS login process and a ROLE_ADMIN check
    registry.addInterceptor(
            new SecurityInterceptor(config, "CasClient", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))
        ).addPathPatterns("/cas/*");
```

## What's next?

Follow this blog or subscribe to the [`pac4j` mailing lists](https://www.pac4j.org/mailing-lists.html) to get updated news.
