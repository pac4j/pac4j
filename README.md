<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

`pac4j` is an **easy** and **powerful Java security engine** to authenticate users, get their profiles and manage authorizations in order to secure a Java web application. It provides a comprehensive set of [concepts and components](#main-concepts-and-components). It is based on Java 8 and available under the Apache 2 license.

It is currently **available for most [frameworks / tools](#frameworks--tools-implementing-pac4j)** and **supports most [authentication](https://github.com/pac4j/pac4j/wiki/Clients) / [authorization](https://github.com/pac4j/pac4j/wiki/Authorizers) mechanisms**.

[![pac4j big picture](https://pac4j.github.io/pac4j/img/pac4j.png)](https://pac4j.github.io/pac4j/img/pac4j.png)

## Frameworks / tools implementing `pac4j`:

| The framework / tool you develop with | The `*-pac4j` library you must use | The demo(s) for tests
|---------------------------------------|------------------------------------|----------------------
| [J2E environment](http://docs.oracle.com/javaee/) | [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) | [j2e-pac4j-demo](https://github.com/pac4j/j2e-pac4j-demo) |
| [Spring Web MVC](http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/mvc.html) and [Spring Boot](http://projects.spring.io/spring-boot) | [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) | [spring-webmvc-pac4j-demo](https://github.com/pac4j/spring-webmvc-pac4j-demo) or [spring-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-webmvc-pac4j-boot-demo) |
| [Play 2.x framework](http://www.playframework.org) | [play-pac4j](https://github.com/pac4j/play-pac4j) | [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) or [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo) |
| [Vertx](http://vertx.io) | [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) | [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo) |
| [Spark Java framework](http://sparkjava.com) | [spark-pac4j](https://github.com/pac4j/spark-pac4j) | [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo) |
| [Ratpack](http://www.ratpack.io) | [ratpack-pac4j](http://ratpack.io/manual/current/pac4j.html#pac4j) | [ratpack-pac4j-demo](https://github.com/pac4j/ratpack-pac4j-demo) |
| [Undertow](http://undertow.io) | [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) | [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo) |
| [Jooby framework](http://jooby.org) |  [jooby-pac4j](http://jooby.org/doc/pac4j) | [jooby-pac4j-demo](https://github.com/pac4j/jooby-pac4j-demo) |
| [Apache Shiro](http://shiro.apache.org) | [buji-pac4j](https://github.com/bujiio/buji-pac4j) | [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) |
| [Spring Security](http://projects.spring.io/spring-security) | [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) | [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo) |
| [SSO CAS server](https://github.com/Jasig/cas) | [cas-server-support-pac4j](https://apereo.github.io/cas/4.2.x/integration/Delegate-Authentication.html) | [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo) |
| [Dropwizard](http://www.dropwizard.io/) | [dropwizard-pac4j](https://github.com/evnm/dropwizard-pac4j) | |
| [Knox gateway for Hadoop](https://knox.apache.org) | [gateway-provider-security-pac4j](http://knox.apache.org/books/knox-0-8-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect) | [knox-pac4j-demo](https://github.com/pac4j/knox-pac4j-demo) |




You can implement `pac4j` for a new framework / tool by following these [guidelines](https://github.com/pac4j/pac4j/wiki/How-to-implement-pac4j-for-a-new-framework---tool).

## Main concepts and components:

1) A [**client**](https://github.com/pac4j/pac4j/wiki/Clients) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication

2) An [**authorizer**](https://github.com/pac4j/pac4j/wiki/Authorizers) is meant to check authorizations on the authenticated user profile(s) or on the current web context

3) A [**matcher**](https://github.com/pac4j/pac4j/wiki/Matchers) defines whether the security must apply on a specific url

4) A [**config**](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/config/Config.java) defines the security configuration via clients, authorizers and matchers

5) The ["**security filter**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java) (or whatever the mechanism used to intercept HTTP requests) protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

6) The ["**callback controller**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultCallbackLogic.java) finishes the login process for an indirect client

7) The [**application logout controller**"](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultApplicationLogoutLogic.java) logs out the user from the application.


## Versions

The version **1.9.2-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

Read the [documentation](https://github.com/pac4j/pac4j/wiki) for more information.


## Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
