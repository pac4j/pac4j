---
layout: content
title: How to secure a Java application with CAS (using Spring Boot)
seo_title: "How to secure a Java application with CAS | pac4j"
description: "Add CAS single sign-on to a Java application with pac4j and Spring Boot: Maven setup, CAS login URL, service registration, user attributes and logout."
---

# How to secure a Java application with CAS (using Spring Boot)

CAS (Central Authentication Service) is the open source single sign-on server widely used by universities and large organizations, maintained by the [Apereo foundation](https://apereo.github.io/cas/). The protocol is simple: your Java application redirects the user to the CAS login page, CAS sends the browser back with a short-lived **service ticket**, and your application validates that ticket with the CAS server to learn who the user is.

This guide uses **pac4j with Spring Boot** to protect a Java web application with a CAS server. The demo authenticates against the public pac4j test server, and the section on service registration shows what to configure on your own CAS server.

**What you need:**

- Java 17 or later and Maven
- the **login URL** of a CAS server, such as `https://cas.example.com/cas/login`, and the right to register a service on it. The demo uses the public test server.

## 1) Get the Spring Boot demo

The [CAS demo project](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/cas) contains the three classes shown in this guide, ready to run:

```bash
git clone --branch cas --single-branch https://github.com/pac4j/simple-spring-boot-pac4j-demos.git
cd simple-spring-boot-pac4j-demos
```

## 2) Add the Maven dependencies

The [demo's `pom.xml`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/pom.xml) uses the Spring Boot parent. On top of Spring MVC, you need the pac4j Spring MVC integration and the CAS module:

```xml
<!-- Spring Boot web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- pac4j implementation for Spring MVC so for Spring Boot as well -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>spring-webmvc-pac4j</artifactId>
    <version>8.0.3</version>
</dependency>
<!-- pac4j support for CAS -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>6.5.8</version>
</dependency>
```

`pac4j-cas` is a full CAS client: it speaks the CAS protocol versions 1.0, 2.0 and 3.0, and also supports proxy tickets and the CAS REST API, covered at the end of this guide.

## 3) Configure CAS authentication

The whole security setup fits in one class, [`SecurityConfig`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/src/main/java/org/pac4j/demos/SecurityConfig.java):

```java
@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUri;

    @Value("${cas.login-url:https://www.casserverpac4j.dev/login}")
    private String casLoginUrl;

    @Bean
    public Config config() {
        // configuration of the authentication via the CAS protocol
        return new Config(baseUri + "/callback", new CasClient(new CasConfiguration(casLoginUrl)));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // the /protected/** URLs require the CAS authentication
        addSecurity(registry, "CasClient").addPathPatterns("/protected/**");
    }
}
```

What each part does:

- **`Pac4jSecurityConfig`** registers the `/callback` endpoint, where CAS sends the service ticket, and the `/logout` endpoint.
- **`CasConfiguration(casLoginUrl)`** only needs the login URL: pac4j derives the ticket validation URL from it. If your server is reached through a different internal URL for validation, add `config.setPrefixUrl("http://cas-internal:8080/cas")`.
- **`new Config(baseUri + "/callback", ...)`** sets the callback URL. pac4j appends `?client_name=CasClient`, and this full URL is the **service** CAS will see.
- **`addSecurity(registry, "CasClient")`** protects `/protected/**`: an anonymous request there triggers the redirect to `casLoginUrl?service=http://localhost:8080/callback?client_name=CasClient`.

pac4j uses the **CAS 3.0 protocol** by default, which returns the user attributes with the validation response. Switch with `config.setProtocol(CasProtocol.CAS20)` for an older server. Two other options are worth knowing: `setRenew(true)` forces the user to re-enter credentials even with an active SSO session, and `setGateway(true)` returns silently when the user is not logged in instead of showing the login page.

## 4) Register the service on the CAS server

A CAS server only issues tickets for services it knows. In the CAS **service registry**, declare a service whose `serviceId` pattern matches the callback URL. With the JSON service registry of Apereo CAS 7, the definition looks like this:

```json
{
  "@class" : "org.apereo.cas.services.CasRegisteredService",
  "serviceId" : "^http://localhost:8080/callback\\?client_name=CasClient.*",
  "name" : "pac4j Spring Boot demo",
  "id" : 1000,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
  }
}
```

Two settings matter here:

- **`serviceId`** is a regular expression. Escape the `?` and keep the trailing `.*`, because CAS appends its own parameters to the callback.
- **`attributeReleasePolicy`** decides which user attributes your application receives. `ReturnAllAttributeReleasePolicy` is fine for a demo; production services usually list the allowed attributes with `ReturnAllowedAttributeReleasePolicy`.

See the [Apereo CAS service management documentation](https://apereo.github.io/cas/7.3.x/services/Service-Management.html) for the other registries (database, LDAP, Git...) and options.

## 5) Access the authenticated user

The [application controller](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/src/main/java/org/pac4j/demos/Application.java) exposes a public page and a protected page:

```java
@Autowired
private ProfileManager profileManager;

@RequestMapping("/")
@ResponseBody
public String index() {
    return "<h1>Public area</h1><p><a href='/protected/index'>Protected area</a></p>"
            + "<p><a href='/logout'>Logout</a></p>" + profileManager.getProfiles();
}

@RequestMapping("/protected/index")
@ResponseBody
public String secure() {
    return "<h1>Protected area</h1><a href='/'>Home</a><p/>"
            + "<p><a href='/logout'>Logout</a></p>" + profileManager.getProfiles();
}
```

After login, the `ProfileManager` returns a `CasProfile`. Its identifier is the CAS principal, usually the username, and its attributes are exactly those released by the service's attribute release policy:

```java
final var profile = (CasProfile) profileManager.getProfile().orElseThrow();
profile.getId();                 // the CAS principal
profile.getAttribute("email");
profile.getAttributes();         // everything the release policy allowed
```

An empty attribute map almost always means the release policy of the service is too restrictive, or the server still uses the CAS 2.0 protocol, which does not carry attributes.

## 6) Logout

The `/logout` link removes the profile from the local session. To also end the SSO session on the CAS server, enable the central logout in `application.properties`:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=/
```

pac4j then redirects the browser to the CAS `/logout` endpoint. The reverse direction works too: when the user logs out from CAS or from another application, the CAS server notifies every service that received a ticket during the session, and the `CasClient` processes these single logout requests to destroy the local session.

## 7) Run the application

Start [`SpringBootDemo`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/src/main/java/org/pac4j/demos/SpringBootDemo.java) from your IDE or with `mvn spring-boot:run`:

```java
@SpringBootApplication
public class SpringBootDemo {
    public static void main(final String[] args) {
        SpringApplication.run(SpringBootDemo.class, args);
    }
}
```

Open [http://localhost:8080/](http://localhost:8080/) and follow **Protected area**. You are redirected to the CAS login page, then sent back to the callback with a service ticket, and the protected page prints your profile.

**If something goes wrong:**

- **"Application Not Authorized to Use CAS"**: the service is not registered, or the `serviceId` pattern does not match the full callback URL.
- **Ticket validation fails after the redirect**: the validation URL derived from the login URL is not reachable from your server. Set `setPrefixUrl` explicitly.
- **The login page shows again and again**: the server and the application disagree on the service URL, typically because of a reverse proxy. Set `app.base-url` to the public URL of the application.
- **No attributes in the profile**: see the release policy and the protocol version in step 5.

## Beyond the login page

The same `pac4j-cas` module covers the other CAS use cases:

- **Proxy tickets**: a web application authenticated by CAS calls a web service on behalf of the user. Configure a `CasProxyReceptor` on the caller and protect the web service with a `DirectCasProxyClient`.
- **CAS REST API**: a mobile or standalone application sends the user credentials to your web service, which validates them against the CAS REST protocol with a `CasRestFormClient` or a `CasRestBasicAuthClient`.
- **CAS as an OpenID Connect or SAML provider**: a modern CAS server can also speak these protocols. In that case use the pac4j [OIDC](/how-to-secure-a-java-application-with-oidc.html) or [SAML](/how-to-secure-a-java-application-with-saml.html) support instead of the CAS protocol.

## Learn more

Read the [CAS reference](/docs/clients/cas.html) for the proxy and REST configurations, the stateless `DirectCasClient` and all the `CasConfiguration` options.

**Using a different integration?** The [Jakarta EE guide](/how-to-secure-a-jakarta-ee-application-with-oidc.html) and [Spring Security guide](/how-to-secure-a-spring-security-application-with-oidc.html) explain the integration using OIDC. Follow their “Switching to SAML or CAS” section to use the CAS client configuration from this guide.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
