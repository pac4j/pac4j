---
permalink: /how-to-secure-a-java-application-with-cas.html
layout: guide
title: How to secure a Java client application with CAS (using Spring Boot)
seo_title: "How to secure a Java client application with CAS | pac4j"
description: "Add CAS single sign-on to a Java application with pac4j and Spring Boot: Maven setup, CAS login URL, service registration, user attributes and logout."
---

CAS can mean two things: the open source single sign-on server maintained by the [Apereo foundation](https://apereo.github.io/cas/), or the Central Authentication Service protocol. Here, we'll use the CAS protocol to connect our Java application to a CAS server.

The flow is easy to follow. Your application redirects the user to the CAS login page. After authentication, CAS sends the browser back with a short-lived **service ticket**. Your application validates this ticket with the CAS server to find out who the user is.

Let's see how to do this with **pac4j and Spring Boot**. We'll use the public pac4j test server first, then look at service registration for your own CAS server.

**What you need:**

- Java 17 or later and Maven
- the **login URL** of a CAS server, such as `https://cas.example.com/cas/login`, and the right to register a service on it. The demo uses the public test server.

## 1) Create the Maven project

Create an empty Spring Boot project with Java 17:

```bash
mkdir -p spring-cas-app/src/main/java/org/example
mkdir -p spring-cas-app/src/main/resources
cd spring-cas-app
```

Create `pom.xml` at the project root. The Spring Boot parent manages the Spring dependencies, and its Maven plugin runs the application:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.1</version>
        <relativePath/>
    </parent>
    <groupId>org.example</groupId>
    <artifactId>spring-cas-app</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Add the dependencies from section 2 here. -->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

Save the Java classes below in the `org.example` package so that Spring Boot discovers the configuration and controllers. Put application properties in `src/main/resources/application.properties`.

## 2) Add the Maven dependencies

Inside the `<dependencies>` element, on top of Spring MVC, you need the pac4j Spring MVC integration and the CAS module:

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
    <version>6.5.9</version>
</dependency>
```

`pac4j-cas` is a full CAS client: it speaks the CAS protocol versions 1.0, 2.0 and 3.0, and also supports proxy tickets and the CAS REST API, covered at the end of this guide.

## 3) Configure CAS authentication

Create `src/main/java/org/example/SecurityConfig.java`:

```java
package org.example;

import org.pac4j.core.config.Config;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

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

The CAS configuration starts with a single URL: `CasConfiguration(casLoginUrl)`. pac4j derives the ticket validation URL from it. If validation must use an internal address, you can set it separately with `config.setPrefixUrl("http://cas-internal:8080/cas")`.

`Pac4jSecurityConfig` registers `/callback`, where CAS sends the service ticket, and `/logout`. With `new Config(baseUri + "/callback", ...)`, we tell pac4j where the callback lives. It appends `?client_name=CasClient`, and **this full URL is the service CAS will see**.

We then protect `/protected/**` with `addSecurity(registry, "CasClient")`. When an anonymous user requests a protected page, pac4j redirects the browser to `casLoginUrl` with that service URL as a parameter.

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

There are two settings to pay attention to:

- **`serviceId`** is a regular expression. Escape the `?` and keep the trailing `.*`, because CAS appends its own parameters to the callback.
- **`attributeReleasePolicy`** decides which user attributes your application receives. `ReturnAllAttributeReleasePolicy` is fine for a demo; production services usually list the allowed attributes with `ReturnAllowedAttributeReleasePolicy`.

See the [Apereo CAS service management documentation](https://apereo.github.io/cas/7.3.x/services/Service-Management.html) for the other registries (database, LDAP, Git...) and options.

## 5) Access the authenticated user

Create `src/main/java/org/example/Application.java` to expose a public page and a protected page:

```java
package org.example;

import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Application {

    @Autowired
    private ProfileManager profileManager;

    @GetMapping(value = "/", produces = "text/html")
    public String index() {
        return "<h1>Public area</h1><p><a href='/protected/index'>Protected area</a></p>"
            + "<p><a href='/logout'>Logout</a></p>";
    }

    @GetMapping(value = "/protected/index", produces = "text/plain")
    public String secure() {
        return "Protected area\n" + profileManager.getProfiles() + "\nVisit /logout to sign out.";
    }
}
```

After login, the `ProfileManager` returns a `CasProfile`. Its identifier is the CAS principal, usually the username, and its attributes are exactly those released by the service's attribute release policy:

```java
final var profile = (CasProfile) profileManager.getProfile().orElseThrow();
profile.getId();                 // the CAS principal
profile.getAttribute("email");
profile.getAttributes();         // everything the release policy allowed
```

No attributes in the profile? Check what the service is allowed to receive. The release policy may filter them out, or the server may still use CAS 2.0, which does not carry attributes.

## 6) Logout

Logging out of the application and logging out of CAS are two different operations. The `/logout` link removes the local profile. To also end the SSO session on the CAS server, enable central logout in `application.properties`:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=/
```

pac4j then redirects the browser to the CAS `/logout` endpoint. The reverse direction works too: when the user logs out from CAS or from another application, the CAS server notifies every service that received a ticket during the session, and the `CasClient` processes these single logout requests to destroy the local session.

## 7) Run the application

Create `src/main/java/org/example/SpringBootDemo.java`:

```java
package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDemo {
    public static void main(final String[] args) {
        SpringApplication.run(SpringBootDemo.class, args);
    }
}
```

Start the application with `mvn spring-boot:run`.

Open [http://localhost:8080/](http://localhost:8080/) and follow **Protected area**. You are redirected to the CAS login page, then sent back to the callback with a service ticket, and the protected page prints your profile.

**If something goes wrong:**

- **"Application Not Authorized to Use CAS"**: the service is not registered, or the `serviceId` pattern does not match the full callback URL.
- **Ticket validation fails after the redirect**: the validation URL derived from the login URL is not reachable from your server. Set `setPrefixUrl` explicitly.
- **The login page shows again and again**: the server and the application disagree on the service URL, typically because of a reverse proxy. Set `app.base-url` to the public URL of the application.
- **No attributes in the profile**: see the release policy and the protocol version in step 5.

## 8) Beyond the login page

So far, we have used CAS to log in through a browser. The same `pac4j-cas` module also handles other situations:

- **Proxy tickets**: a web application authenticated by CAS calls a web service on behalf of the user. Configure a `CasProxyReceptor` on the caller and protect the web service with a `DirectCasProxyClient`.
- **CAS REST API**: a mobile or standalone application sends the user credentials to your web service, which validates them against the CAS REST protocol with a `CasRestFormClient` or a `CasRestBasicAuthClient`.
- **CAS as an OpenID Connect or SAML provider**: a modern CAS server can also speak these protocols. In that case use the pac4j [OIDC](/how-to-secure-a-java-application-with-oidc.html) or [SAML](/how-to-secure-a-java-application-with-saml.html) support instead of the CAS protocol.

## 9) Learn more

- The [Spring Boot CAS demo](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/cas) for a complete reference application.
- Read the documentation for the [CAS client for Java](/docs/clients/cas.html) for the proxy and REST configurations, the stateless `DirectCasClient` and all the `CasConfiguration` options.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
