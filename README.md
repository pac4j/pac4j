<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" alt="pac4j" />
</p>

<h3 align="center">The security engine for Java: one API to add OpenID Connect, SAML, CAS, OAuth, JWT, LDAP... authentication and authorization to any Java framework.</h3>

<p align="center">
  <a href="https://central.sonatype.com/artifact/org.pac4j/pac4j-core"><img src="https://img.shields.io/maven-central/v/org.pac4j/pac4j-core?label=Maven%20Central" alt="Maven Central" /></a>
  <a href="https://github.com/pac4j/pac4j/actions/workflows/ci.yml"><img src="https://github.com/pac4j/pac4j/actions/workflows/ci.yml/badge.svg" alt="Build status" /></a>
  <img src="https://img.shields.io/badge/Java-17%2B-blue" alt="Java 17+" />
  <a href="LICENSE"><img src="https://img.shields.io/github/license/pac4j/pac4j" alt="Apache 2 license" /></a>
  <a href="https://github.com/pac4j/pac4j"><img src="https://img.shields.io/github/stars/pac4j/pac4j?style=social" alt="GitHub stars" /></a>
</p>

<p align="center">
  <a href="https://www.pac4j.org/docs/index.html"><b>Documentation</b></a> &bull;
  <a href="#-quick-start-spring-boot--openid-connect"><b>Quick start</b></a> &bull;
  <a href="#-get-started-with-your-framework"><b>Frameworks</b></a> &bull;
  <a href="https://www.pac4j.org/docs/main-concepts-and-components.html"><b>Concepts</b></a> &bull;
  <a href="#-need-help"><b>Help</b></a>
</p>

---

## ✨ Why pac4j?

- 🧩 **One security engine, every framework**: the same concepts and the same configuration for Spring Boot, Jakarta EE, Play, Vert.x, Javalin, JAX-RS and [many more](#-get-started-with-your-framework).
- 🔐 **All the major protocols**: OpenID Connect, SAML 2, CAS, OAuth 1.0 & 2.0, JWT, LDAP, Kerberos, HTTP... Log in with Keycloak, Microsoft Entra ID, Google, GitHub, Okta, Auth0, any SAML IdP or CAS server.
- 🛡️ **Authorization & web security built in**: roles, authentication levels, CSRF protection, CORS and security headers.
- 🚀 **Ready for the future**: [OpenID for Verifiable Presentations](https://www.pac4j.org/docs/clients/openid4vp.html) (EUDI wallet, eIDAS 2.0) and [OpenID Federation](https://www.pac4j.org/docs/clients/openid-connect-federation.html).
- 🏭 **Battle-tested**: developed since 2013 and embedded in [Apereo CAS](https://apereo.github.io/cas/8.0.x/integration/Delegate-Authentication.html), [Apache Syncope](https://syncope.apache.org) and [Apache Knox](http://knox.apache.org/books/knox-1-6-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect).
- 📜 **Open source** under the Apache 2 license.

## ⚡ Quick start (Spring Boot + OpenID Connect)

Add the Spring MVC integration and the OpenID Connect module:

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>spring-webmvc-pac4j</artifactId>
    <version>8.0.3</version>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.9</version>
</dependency>
```

Then define your identity provider and the URLs to protect:

```java
@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    @Bean
    public Config config() {
        final var oidc = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true); // only for this demo server
        return new Config("http://localhost:8080/callback", new OidcClient(oidc));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        addSecurity(registry, "OidcClient").addPathPatterns("/protected/**");
    }
}
```

That's it: `/protected/**` now requires an OpenID Connect login, and the user profile is available through the `ProfileManager`.
Switching to Keycloak, Google or Microsoft Entra ID is a matter of changing the client:
read the [full guide](https://www.pac4j.org/how-to-secure-a-java-application-with-oidc.html) or run the [demo](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/oidc).

## 🧭 Get started with your framework

| Framework | Get started |
|-----------|-------------|
| **Spring Web MVC / Spring Boot** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-java-application-with-oidc.html) |
| **Spring Security / Spring Boot** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-spring-security-application-with-oidc.html) |
| **Spring WebFlux / Spring Boot** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-spring-webflux-application-with-oidc.html) |
| **Jakarta EE** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-jakarta-ee-application-with-oidc.html) |
| **Play 2.x / 3.x** | [SAML guide](https://www.pac4j.org/how-to-secure-a-play-application-with-saml.html) |
| **Vert.x** | [CAS guide](https://www.pac4j.org/how-to-secure-a-vertx-application-with-cas.html) |
| **Javalin** | [SAML guide](https://www.pac4j.org/how-to-secure-a-javalin-application-with-saml.html) |
| **JAX-RS** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-jax-rs-application-with-oidc.html) |
| **Dropwizard** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-jax-rs-application-with-oidc.html#using-dropwizard) |
| **Spark Java** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-a-spark-java-application-with-oidc.html) |
| **Undertow** | [OpenID Connect guide](https://www.pac4j.org/how-to-secure-an-undertow-application-with-oidc.html) |
| **Apache Shiro** | [CAS guide](https://www.pac4j.org/how-to-secure-a-shiro-application-with-cas.html) |
| **Ratpack** &bull; **Lagom** (archived) &bull; **Akka HTTP** (archived) &bull; **Jooby** | [Ratpack](http://ratpack.io/manual/current/pac4j.html#pac4j) &bull; [Lagom](https://github.com/pac4j/lagom-pac4j) &bull; [Akka HTTP](https://github.com/StackVista/akka-http-pac4j) &bull; [Jooby](https://jooby.io/modules/pac4j) |

pac4j also powers the authentication delegation of [Apereo CAS](https://apereo.github.io/cas/8.0.x/integration/Delegate-Authentication.html), [Apache Syncope](https://syncope.apache.org) and [Apache Knox](http://knox.apache.org/books/knox-1-6-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect).

## 🔑 Authentication mechanisms

| Login protocols | Credentials validation |
|-----------------|------------------------|
| [OpenID Connect](https://www.pac4j.org/how-to-secure-a-java-application-with-oidc.html) &bull; [SAML](https://www.pac4j.org/how-to-secure-a-java-application-with-saml.html) &bull; [CAS](https://www.pac4j.org/how-to-secure-a-java-application-with-cas.html) &bull; [OAuth](https://www.pac4j.org/docs/clients/oauth.html) &bull; [HTTP](https://www.pac4j.org/docs/clients/http.html) &bull; [Kerberos](https://www.pac4j.org/docs/clients/kerberos.html) &bull; [OpenID4VP (EUDI wallet, eIDAS 2.0)](https://www.pac4j.org/docs/clients/openid4vp.html) | [LDAP](https://www.pac4j.org/docs/authenticators/ldap.html) &bull; [SQL](https://www.pac4j.org/docs/authenticators/sql.html) &bull; [JWT](https://www.pac4j.org/docs/authenticators/jwt.html) &bull; [MongoDB](https://www.pac4j.org/docs/authenticators/mongodb.html) &bull; [IP address](https://www.pac4j.org/docs/authenticators/ip.html) &bull; [REST API](https://www.pac4j.org/docs/authenticators/rest.html) |

## 🛡️ Authorization mechanisms

| User profile | Web request |
|--------------|-------------|
| [Roles](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#1-roles) &bull; [Anonymous / remember-me / (fully) authenticated](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#2-authentication-levels) &bull; [Profile type, attribute](https://www.pac4j.org/docs/authorizers/profile-authorizers.html#3-others) | [CORS](https://www.pac4j.org/docs/matchers.html#3-cors) &bull; [CSRF](https://www.pac4j.org/docs/authorizers/web-authorizers.html#1-csrf) &bull; [Security headers](https://www.pac4j.org/docs/matchers.html#4-securityheaders) &bull; [IP address, HTTP method](https://www.pac4j.org/docs/authorizers/web-authorizers.html#2-others) |

## 🧪 Advanced mechanisms

[OpenID Federation](https://www.pac4j.org/docs/clients/openid-connect-federation.html) &bull; [OpenID for Verifiable Presentations (EUDI wallet, eIDAS 2.0)](https://www.pac4j.org/docs/clients/openid4vp.html)

## 📦 Versions

| JDK | pac4j | Usage of Lombok |
|-----|-------|-----------------|
| 17  | v6.x  | Yes             |
| 11  | v5.x  | No              |
| 8   | v4.x  | No              |

The latest released version is [![Maven Central](https://img.shields.io/maven-central/v/org.pac4j/pac4j-core.svg)](https://central.sonatype.com/artifact/org.pac4j/pac4j-core).
The [next version](https://www.pac4j.org/docs/next-version.html) is under development.
See the [release notes](https://www.pac4j.org/docs/release-notes.html).

## 🤖 Use of AI

This project is developed using various AI tools across multiple areas, including development, testing, and documentation.

## 💬 Need help?

- 📖 Read the [documentation](https://www.pac4j.org/docs/index.html)
- ✉️ Ask on the [mailing lists](https://www.pac4j.org/mailing-lists.html)
- 🏢 Get [commercial support](https://www.pac4j.org/commercial-support.html)
- 🔒 Report a vulnerability: see the [security policy](SECURITY.md)

## 🤝 Contributing

Contributions are welcome: read the [contribution guide](CONTRIBUTING.md) to get started.

⭐ **If pac4j is useful to you, please star this repository**: it helps other developers discover it!

## 💙 Supported by

[![CAS in the cloud](https://www.pac4j.org/img/new_cas_in_the_cloud_logo.png)](https://www.casinthecloud.com) *The CAS and pac4j consulting company*

[![NLnet](https://www.pac4j.org/img/nlnet-logo.png)](https://nlnet.nl) *NLnet foundation*
