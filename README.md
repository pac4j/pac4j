<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

### `pac4j` is an easy and powerful security engine for Java to authenticate users, get their profiles and manage authorizations in order to secure web applications.

It provides a comprehensive set of [**concepts and components**](http://www.pac4j.org/docs/main-concepts-and-components.html). It is based on Java 8 and available under the Apache 2 license. It is **available for most frameworks / tools** and **supports most authentication / authorization mechanisms**.

## Available implementations (*Get started by clicking on your framework*):

[J2E](https://github.com/pac4j/j2e-pac4j) - [Spring Boot](https://github.com/pac4j/spring-webmvc-pac4j) - [Spring Web MVC](https://github.com/pac4j/spring-webmvc-pac4j) - [Spring Security](https://github.com/pac4j/spring-security-pac4j) - [Apache Shiro](https://github.com/bujiio/buji-pac4j)

[Play 2 framework](https://github.com/pac4j/play-pac4j) - [Vertx](https://github.com/pac4j/vertx-pac4j) - [Spark Java framework](https://github.com/pac4j/spark-pac4j) - [Ratpack](http://ratpack.io/manual/current/pac4j.html#pac4j) - [Undertow](https://github.com/pac4j/undertow-pac4j)

[CAS server](https://apereo.github.io/cas/4.2.x/integration/Delegate-Authentication.html) - [Dropwizard](https://github.com/evnm/dropwizard-pac4j) - [Knox gateway for Hadoop](http://knox.apache.org/books/knox-0-9-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect) - [Jooby framework](http://jooby.org/doc/pac4j)

## Authentication mechanims:

[OAuth](http://www.pac4j.org/docs/clients/oauth.html) - [SAML](http://www.pac4j.org/docs/clients/saml.html) - [CAS](http://www.pac4j.org/docs/clients/cas.html) - [OpenID Connect](http://www.pac4j.org/docs/clients/openid-connect.html) - [HTTP](http://www.pac4j.org/docs/clients/http.html) - [OpenID](http://www.pac4j.org/docs/clients/openid.html) - [Google App Engine](http://www.pac4j.org/docs/clients/google-app-engine.html)

[LDAP](http://www.pac4j.org/docs/authenticators/ldap.html) - [SQL](http://www.pac4j.org/docs/authenticators/sql.html) - [JWT](http://www.pac4j.org/docs/authenticators/jwt.html) - [MongoDB](http://www.pac4j.org/docs/authenticators/mongodb.html) - [Stormpath](http://www.pac4j.org/docs/authenticators/stormpath.html) - [IP address](http://www.pac4j.org/docs/authenticators/ip.html)

## Authorization mechanisms:

[Roles / permissions](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#roles--permissions) - [Anonymous / remember-me / (fully) authenticated](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#authentication-levels) - [Profile type, attribute](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#others)

[CORS](http://www.pac4j.org/docs/authorizers/web-authorizers.html#cors) - [CSRF](http://www.pac4j.org/docs/authorizers/web-authorizers.html#csrf) - [Security headers](http://www.pac4j.org/docs/authorizers/web-authorizers.html#security-headers) - [IP address, HTTP method](http://www.pac4j.org/docs/authorizers/web-authorizers.html#others)

---

## Versions

The version **1.9.2-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](http://www.pac4j.org/docs/release-notes.html).

Read the [documentation](http://www.pac4j.org/docs/index.html) for more information.


## Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
