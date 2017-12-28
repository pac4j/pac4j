<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

### `pac4j` is an easy and powerful security engine for Java to authenticate users, get their profiles and manage authorizations in order to secure web applications.

It provides a comprehensive set of [**concepts and components**](http://www.pac4j.org/docs/main-concepts-and-components.html). It is based on Java 8 and available under the Apache 2 license. It is **available for most frameworks/tools** and **supports most authentication/authorization mechanisms**.

## Available implementations (*Get started by clicking on your framework*):

[J2E](https://github.com/pac4j/j2e-pac4j) &bull; [Spring Web MVC (Spring Boot)](https://github.com/pac4j/spring-webmvc-pac4j) &bull; [Spring Security (Spring Boot)](https://github.com/pac4j/spring-security-pac4j) &bull; [Apache Shiro](https://github.com/bujiio/buji-pac4j)

[Play 2.x](https://github.com/pac4j/play-pac4j) &bull; [Vertx](https://github.com/pac4j/vertx-pac4j) &bull; [Spark Java](https://github.com/pac4j/spark-pac4j) &bull; [Ratpack](http://ratpack.io/manual/current/pac4j.html#pac4j) &bull; [Pippo](http://www.pippo.ro/doc/security.html#pac4j-integration) &bull; [Undertow](https://github.com/pac4j/undertow-pac4j)

[CAS server](https://apereo.github.io/cas/5.2.x/integration/Delegate-Authentication.html) &bull; [JAX-RS](https://github.com/pac4j/jax-rs-pac4j) &bull; [Dropwizard](https://github.com/evnm/dropwizard-pac4j) &bull; [Apache Knox](http://knox.apache.org/books/knox-0-9-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect) &bull; [Jooby](http://jooby.org/doc/pac4j)

## Authentication mechanisms:

[OAuth (Facebook, Twitter, Google...)](http://www.pac4j.org/docs/clients/oauth.html) - [SAML](http://www.pac4j.org/docs/clients/saml.html) - [CAS](http://www.pac4j.org/docs/clients/cas.html) - [OpenID Connect](http://www.pac4j.org/docs/clients/openid-connect.html) - [HTTP](http://www.pac4j.org/docs/clients/http.html) - [OpenID](http://www.pac4j.org/docs/clients/openid.html) - [Google App Engine](http://www.pac4j.org/docs/clients/google-app-engine.html) - [Kerberos (SPNEGO/Negotiate)](http://www.pac4j.org/docs/clients/kerberos.html)

[LDAP](http://www.pac4j.org/docs/authenticators/ldap.html) - [SQL](http://www.pac4j.org/docs/authenticators/sql.html) - [JWT](http://www.pac4j.org/docs/authenticators/jwt.html) - [MongoDB](http://www.pac4j.org/docs/authenticators/mongodb.html) - [CouchDB](http://www.pac4j.org/docs/authenticators/couchdb.html) - [IP address](http://www.pac4j.org/docs/authenticators/ip.html) - [REST API](http://www.pac4j.org/docs/authenticators/rest.html)

## Authorization mechanisms:

[Roles/permissions](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#roles--permissions) - [Anonymous/remember-me/(fully) authenticated](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#authentication-levels) - [Profile type, attribute](http://www.pac4j.org/docs/authorizers/profile-authorizers.html#others)

[CORS](http://www.pac4j.org/docs/authorizers/web-authorizers.html#cors) - [CSRF](http://www.pac4j.org/docs/authorizers/web-authorizers.html#csrf) - [Security headers](http://www.pac4j.org/docs/authorizers/web-authorizers.html#security-headers) - [IP address, HTTP method](http://www.pac4j.org/docs/authorizers/web-authorizers.html#others)

---

## Versions

The version **3.0.0-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](http://www.pac4j.org/docs/release-notes.html).

Read the [documentation](http://www.pac4j.org/docs/index.html) for more information.

## Third-party extensions

There exist extensions to pac4j developed by third parties. The extensions provide features not available in the core pac4j distribution. At the moment, the following extension are known:
- [IDC Extensions to PAC4J](https://github.com/jkacer/pac4j-extensions), developed internally by IDC and published as open source.

## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any question, please use the [pac4j mailing lists](http://www.pac4j.org/mailing-lists.html):

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
