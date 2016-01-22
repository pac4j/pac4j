<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

`pac4j` is an [**easy and powerful**](#main-concepts) **Java security engine** to authenticate users, get their profiles and manage authorizations in order to secure a Java web application. It's available under the Apache 2 license.

It is currently **available for most [frameworks / tools](#frameworks--tools-implementing-pac4j)** and **supports most [authentication](https://github.com/pac4j/pac4j/wiki/Clients) / [authorization](https://github.com/pac4j/pac4j/wiki/Authorizers) mechanisms**.

[![pac4j big picture](https://pac4j.github.io/pac4j/img/pac4j.png)](https://pac4j.github.io/pac4j/img/pac4j.png)

## Frameworks / tools implementing `pac4j`:

| The framework / tool you develop with | The `*-pac4j` library you must use | The demo(s) for tests
|---------------------------------------|------------------------------------|----------------------
| [J2E environment](http://docs.oracle.com/javaee/) | [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) | [j2e-pac4j-demo](https://github.com/pac4j/j2e-pac4j-demo)
| [Spring Web MVC](http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/mvc.html) and [Spring Boot](http://projects.spring.io/spring-boot) | [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) | [spring-webmvc-pac4j-demo](https://github.com/pac4j/spring-webmvc-pac4j-demo) or [spring-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-webmvc-pac4j-boot-demo)
| [Play 2.x framework](http://www.playframework.org) | [play-pac4j](https://github.com/pac4j/play-pac4j) | [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) or [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo)
| [Vertx](http://vertx.io) | [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) | [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo)
| [Spark Java framework](http://sparkjava.com) | [spark-pac4j](https://github.com/pac4j/spark-pac4j) | [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo)
| [Ratpack](http://www.ratpack.io) | [ratpack-pac4j](http://ratpack.io/manual/current/pac4j.html#pac4j) | [ratpack-pac4j-demo](https://github.com/pac4j/ratpack-pac4j-demo)
| [Undertow](http://undertow.io) | [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) | [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo)
| [Jooby framework](http://jooby.org) |  [jooby-pac4j](http://jooby.org/doc/pac4j) | [jooby-pac4j-demo](https://github.com/pac4j/jooby-pac4j-demo)
| [Apache Shiro](http://shiro.apache.org) | [buji-pac4j](https://github.com/bujiio/buji-pac4j) | [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo)
| [Spring Security](http://projects.spring.io/spring-security) | [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) | [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo)
| [SSO CAS server](https://github.com/Jasig/cas) | [cas-server-support-pac4j](http://jasig.github.io/cas/4.1.x/integration/Delegate-Authentication.html) | [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo)

You can even implement `pac4j` for a new framework / tool by following these [guidelines](https://github.com/pac4j/pac4j/wiki/Implement-pac4j-for-a-new-framework---tool).

## Main concepts:

| In the pac4j project | In a pac4j implementation |
|----------------------|---------------------------|
| A [**client**](https://github.com/pac4j/pac4j/wiki/Clients) represents an authentication mechanism.<p />It performs the login process and returns a user profile.<p />An indirect client is for UI authentication while a direct client is for web services|The "**security filter**" (or whatever the mechanism used to intercept HTTP requests) protects an url by checking that:<ul><li>the user is authenticated or starts / performs the login process (according to the clients configuration)</li><li>the authorizations are valid (according to the authorizers configuration)</li></ul>|
|An [**authorizer**](https://github.com/pac4j/pac4j/wiki/Authorizers) is meant to check authorizations on the authenticated user profile or on the current web context|The "**callback controller**" finishes the authentication process for an indirect client|


## Versions

The next version **1.9.0-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

Read the [Javadoc](http://www.pac4j.org/apidocs/pac4j/1.8.2/index.html) and the [technical components](https://github.com/pac4j/pac4j/wiki/Technical-components) documentation for more information.


## Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
