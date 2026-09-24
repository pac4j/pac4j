<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

### `pac4j` is an easy and powerful security framework for Java to authenticate users, get their profiles and manage authorizations in order to secure web applications and web services.

It provides a comprehensive set of [**concepts and components**](https://www.pac4j.org/docs/main-concepts-and-components.html).
It is **available for most frameworks/tools** and **supports most authentication/authorization mechanisms**.
It is licensed under the Apache 2 license.

| JDK | pac4j | Usage of Lombok |
|-----|-------|-----------------|
| 17  | v6.x  | Yes             |
| 11  | v5.x  | No              |
| 8   | v4.x  | No              |

## Available implementations (*Get started by clicking on your framework*):

[Spring Web MVC (Spring Boot)](https://www.pac4j.org/how-to-secure-a-java-application-with-oidc.html)
&bull; [Jakarta EE](https://www.pac4j.org/how-to-secure-a-jakarta-ee-application-with-oidc.html)
&bull; [Spring WebFlux (Spring Boot)](https://www.pac4j.org/how-to-secure-a-spring-webflux-application-with-oidc.html)
&bull; [Apache Shiro](https://www.pac4j.org/how-to-secure-a-shiro-application-with-cas.html)
&bull; [Spring Security (Spring Boot)](https://www.pac4j.org/how-to-secure-a-spring-security-application-with-oidc.html)

[CAS server](https://apereo.github.io/cas/6.6.x/integration/Delegate-Authentication.html)
&bull; [Syncope](https://syncope.apache.org)
&bull; [Apache Knox](http://knox.apache.org/books/knox-1-6-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect)

[Play 2.x/3.x](https://www.pac4j.org/how-to-secure-a-play-application-with-saml.html)
&bull; [Vert.x](https://www.pac4j.org/how-to-secure-a-vertx-application-with-cas.html)
&bull; [Spark Java](https://www.pac4j.org/how-to-secure-a-spark-java-application-with-oidc.html)
&bull; [Ratpack](http://ratpack.io/manual/current/pac4j.html#pac4j)
&bull; [JAX-RS](https://www.pac4j.org/how-to-secure-a-jax-rs-application-with-oidc.html)
&bull; [Dropwizard](https://www.pac4j.org/how-to-secure-a-jax-rs-application-with-oidc.html#using-dropwizard)

[Javalin](https://www.pac4j.org/how-to-secure-a-javalin-application-with-saml.html)
&bull; [Pippo](http://www.pippo.ro/doc/security.html#pac4j-integration)
&bull; [Undertow](https://github.com/pac4j/undertow-pac4j)
&bull; [Lagom](https://github.com/pac4j/lagom-pac4j)
&bull; [Akka HTTP](https://github.com/StackVista/akka-http-pac4j)
&bull; [Jooby](https://jooby.io/modules/pac4j)

## Authentication mechanisms:

[OpenID Connect](https://www.pac4j.org/how-to-secure-a-java-application-with-oidc.html) - [SAML](https://www.pac4j.org/how-to-secure-a-java-application-with-saml.html) - [CAS](https://www.pac4j.org/how-to-secure-a-java-application-with-cas.html) - [OAuth](https://www.pac4j.org/docs/clients/oauth.html) - [HTTP](https://www.pac4j.org/docs/clients/http.html) - [Kerberos](https://www.pac4j.org/docs/clients/kerberos.html) - [OpenID4VP (EUDI wallet, eIDAS 2.0)](https://www.pac4j.org/docs/clients/openid4vp.html)

[LDAP](https://www.pac4j.org/docs/authenticators/ldap.html) - [SQL](https://www.pac4j.org/docs/authenticators/sql.html) - [JWT](https://www.pac4j.org/docs/authenticators/jwt.html) - [MongoDB](https://www.pac4j.org/docs/authenticators/mongodb.html) - [IP address](https://www.pac4j.org/docs/authenticators/ip.html) - [REST API](https://www.pac4j.org/docs/authenticators/rest.html)

## Authorization mechanisms:

[Roles](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#1-roles) - [Anonymous/remember-me/(fully) authenticated](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#2-authentication-levels) - [Profile type, attribute](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#3-others)

[CORS](https://www.pac4j.org/docs/matchers.html#3-cors) - [CSRF](https://www.pac4j.org/docs/authorizers/web-authorizers.html#1-csrf) - [Security headers](https://www.pac4j.org/docs/matchers.html#4-securityheaders) - [IP address, HTTP method](https://www.pac4j.org/docs/authorizers/web-authorizers.html#2-others)

## Advanced mechanisms:

[OpenID Federation](https://www.pac4j.org/docs/clients/openid-connect-federation.html) - [OpenID for Verifiable Presentations (EUDI wallet, eIDAS 2.0)](https://www.pac4j.org/docs/clients/openid4vp.html)

---

## Use of AI

This project is developed using various AI tools across multiple areas, including development, testing, and documentation.


## Versions

The latest released version is the [![Maven Central](https://img.shields.io/maven-central/v/org.pac4j/pac4j-core.svg)](https://repo1.maven.org/maven2/org/pac4j/pac4j-core/).
The [next version](https://www.pac4j.org/docs/next-version.html) is under development.

Read the [documentation](https://www.pac4j.org/docs/index.html) for more information.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).


## Supported by

[![CAS in the cloud](https://www.pac4j.org/img/new_cas_in_the_cloud_logo.png)](https://www.casinthecloud.com) *The CAS and pac4j consulting company*

[![NLnet](https://www.pac4j.org/img/nlnet-logo.png)]((https://nlnet.nl)) *NLnet foundation*
