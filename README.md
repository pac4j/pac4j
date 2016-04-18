<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

`pac4j` is a **Java security engine** to authenticate users, get their profiles and manage their authorizations in order to secure Java web applications. It's available under the Apache 2 license.

It is currently **available for many frameworks / tools and supports most authentication mechanisms**. Its core API is provided by the `pac4j-core` submodule (groupId: `org.pac4j`).

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

## Supported authentication / authorization mechanisms:

`pac4j` supports most authentication mechanisms, called [**clients**](https://github.com/pac4j/pac4j/wiki/Clients):

- **indirect / stateful clients** are for UI when the user authenticates once at an external provider (like Facebook, a CAS server...) or via a local form (or basic auth popup)  
- **direct / stateless clients** are for web services when credentials (like basic auth, tokens...) are passed for each HTTP request.

See the [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows).

| The authentication mechanism you want | The `pac4j-*` submodule(s) you must use
|---------------------------------------|----------------------------------------
| OAuth (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... | `pac4j-oauth`
| CAS (1.0, 2.0, 3.0, SAML, logout, proxy) | `pac4j-cas`
| SAML (2.0) | `pac4j-saml`
| OpenID Connect (1.0) | `pac4j-oidc`
| HTTP (form, basic auth, IP, header, cookie, GET/POST parameter)<br />+<br />JWT<br />or LDAP<br />or Relational DB<br />or MongoDB<br />or Stormpath<br />or CAS REST API| `pac4j-http`<br />+<br />`pac4j-jwt`<br />or `pac4j-ldap`<br />or `pac4j-sql`<br />or `pac4j-mongo`<br />or `pac4j-stormpath`<br />or `pac4j-cas`
| Google App Engine UserService | `pac4j-gae`
| OpenID | `pac4j-openid`

`pac4j` supports many authorization checks, called [**authorizers**](https://github.com/pac4j/pac4j/wiki/Authorizers) available in the `pac4j-core` (and `pac4j-http`) submodules: role / permission checks, IP check, profile type verification, HTTP method verification... as well as regular security protections for CSRF, XSS, cache control, Xframe...


## Versions

The next version **1.8.6-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

Read the [Javadoc](http://www.pac4j.org/apidocs/pac4j/1.8.8/index.html) and the [technical components](https://github.com/pac4j/pac4j/wiki/Technical-components) documentation for more information.


## Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
