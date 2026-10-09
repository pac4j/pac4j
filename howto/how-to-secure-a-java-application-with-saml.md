---
permalink: /how-to-secure-a-java-application-with-saml.html
layout: guide
title: How to secure a Java client application with SAML (using Spring Boot)
seo_title: "How to secure a Java client application with SAML | pac4j"
description: "Add SAML 2.0 single sign-on to a Java application with pac4j and Spring Boot: keystore, IdP metadata, SP metadata exchange, user attributes and single logout."
---

Your organization already has an identity provider and asks you to connect your Java application using SAML 2.0. Before writing any code, let's clarify the two sides of that connection.

The **identity provider** (IdP) authenticates the user. It might be Microsoft Entra ID, Okta, ADFS, Shibboleth, Keycloak or a CAS server. Your application is the **service provider** (SP): it receives a signed XML assertion from the IdP describing the authenticated user.

We'll make our application a SAML service provider with **pac4j and Spring Boot**. The example uses the public pac4j test IdP. Along the way, we'll see how to exchange metadata with your own IdP, read user attributes and handle logout.

**What you need:**

- Java 17 or later and Maven
- the **metadata** of your IdP, as a URL or an XML file. Every IdP publishes it; ask your identity team if you do not know where it is. The example uses the metadata URL of the public test IdP.

## 1) Create the Maven project

Create an empty Spring Boot project with Java 17:

```bash
mkdir -p spring-saml-app/src/main/java/org/example
mkdir -p spring-saml-app/src/main/resources
cd spring-saml-app
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
    <artifactId>spring-saml-app</artifactId>
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

Inside the `<dependencies>` element, on top of Spring MVC, you need the pac4j Spring MVC integration and the SAML module, which embeds the OpenSAML library:

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
<!-- pac4j support for SAML2 -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-saml</artifactId>
    <version>6.5.9</version>
</dependency>
```

## 3) Create the service provider keystore

Our service provider needs a key pair to sign requests and decrypt encrypted assertions. Let's create a Java keystore with `keytool`:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -storetype JKS -keystore src/main/resources/samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

Create a `metadata` directory at the project root for the generated SP metadata (`mkdir -p metadata`). If the keystore path you configure does not exist and is writable, pac4j generates the keystore and its key pair for you at first use.

## 4) Configure SAML 2.0 authentication

Create `src/main/java/org/example/SecurityConfig.java`:

```java
package org.example;

import org.pac4j.core.config.Config;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUri;

    @Bean
    public Config config() {
        // configuration of the authentication via the SAML2 protocol
        final var cfg = new SAML2Configuration();
        cfg.getKeystore().setKeystorePath("classpath:samlKeystore.jks");
        cfg.getKeystore().setKeystorePassword("pac4j-demo-passwd");
        cfg.getKeystore().setPrivateKeyPassword("pac4j-demo-passwd");
        cfg.setIdentityProviderMetadataPath("https://www.casserverpac4j.dev/idp/metadata");
        cfg.setServiceProviderEntityId(baseUri + "/callback?client_name=SAML2Client");
        cfg.setServiceProviderMetadataPath("file:metadata/sp-metadata-8080.xml");
        return new Config(baseUri + "/callback", new SAML2Client(cfg));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // the /protected/** URLs require the SAML2 authentication
        addSecurity(registry, "SAML2Client").addPathPatterns("/protected/**");
    }
}
```

The first settings point to the keystore from step 3 and use the two passwords we gave to `keytool`. The `classpath:` prefix loads it from the application resources; `file:` and `https:` are also supported.

Then we tell pac4j about the IdP with `setIdentityProviderMetadataPath`. Its metadata contains the single sign-on URL, signing certificate and logout endpoint. There is no need to copy these values into separate settings.

Our application also needs an identity of its own. `setServiceProviderEntityId` gives it a unique name in the SAML world. Any URI works; the callback URL is a convenient choice. `setServiceProviderMetadataPath` tells pac4j where to write our SP metadata. We'll give that file to the IdP in the next step.

Finally, `Pac4jSecurityConfig` registers `/callback` and `/logout`, and `addSecurity(registry, "SAML2Client")` protects `/protected/**`. In SAML terms, the callback is our **Assertion Consumer Service** (ACS).

The IdP returns the assertion by a cross-site POST. Configure the session cookie in `src/main/resources/application.properties` so that the browser sends it on the callback:

```properties
server.servlet.session.cookie.same-site=none
server.servlet.session.cookie.secure=true
```

Use HTTPS when deploying beyond localhost: `SameSite=None` requires a secure cookie.

## 5) Exchange metadata with your identity provider

We have given pac4j the IdP metadata, but we are only halfway there: **the IdP must also know our application**.

When the `SAML2Client` initializes, pac4j generates the SP metadata into `metadata/sp-metadata-8080.xml`. This XML document contains your entity ID, your public certificate and your ACS URL, `http://localhost:8080/callback?client_name=SAML2Client`. Register it at the IdP:

- **Entra ID, Okta, Auth0, Keycloak**: create a SAML application and either upload this file or copy the entity ID and the ACS URL into the form.
- **Shibboleth, ADFS, simpleSAMLphp**: add the file, or a URL serving it, to the IdP's list of relying parties.

You can also get the same XML programmatically with `client.getServiceProviderMetadataResolver().getMetadata()`, for instance to serve it from an endpoint.

If the IdP metadata is not reachable over HTTP from your application, download it once and load it from the classpath with `cfg.setIdentityProviderMetadataPath("classpath:idp-metadata.xml")`.

## 6) Access the authenticated user

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

After login, the `ProfileManager` returns a `SAML2Profile`. Its identifier is the assertion's `NameID`, and its attributes are those released by the IdP.

Do not be surprised if the attribute names look like URNs instead of `email` or `name`: pac4j keeps the names supplied by the IdP. SAML attributes are also multi-valued, so you get a list:

```java
final var profile = (SAML2Profile) profileManager.getProfile().orElseThrow();
profile.getId();                                            // the NameID
profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.3");  // "mail" in many directories
profile.getAttributes();                                    // everything the IdP sent
```

Of course, you probably do not want these URNs all over your application code. Map them to readable names in the configuration:

```java
cfg.setMappedAttributes(Map.of("urn:oid:0.9.2342.19200300.100.1.3", "email"));
```

The IdP decides which attributes it releases: if a value is missing, the attribute release rule for your SP at the IdP is the place to look.

## 7) Single logout

The `/logout` link removes the local profile. But the user can still have a session at the IdP and in other applications.

SAML defines **single logout** (SLO) for this: our application asks the IdP to end its session and notify the other applications. Enable it in `application.properties`:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=/
```

pac4j then sends a `LogoutRequest` to the IdP's single logout endpoint, read from the IdP metadata, and processes the `LogoutResponse` on the callback URL. To sign outgoing logout requests, add this setting to the `SAML2Configuration` in step 4 before creating the client:

```java
cfg.setSpLogoutRequestSigned(true);
```

In pac4j 6.5.9, this option is `false` by default: enabling central logout alone does not enable request signing. The HTTP-POST and HTTP-Redirect bindings are supported for these messages; pick the one your IdP expects with `cfg.setSpLogoutRequestBindingType(...)`. SLO only works if the IdP metadata declares a `SingleLogoutService`.

## 8) Run the application

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

Open [http://localhost:8080/](http://localhost:8080/) and follow **Protected area**. You are redirected to the IdP to sign in, then posted back to the callback URL with the assertion, and the protected page prints your profile.

**If something goes wrong:**

- **The IdP rejects the request as an unknown service provider**: the SP metadata is not registered, or the entity ID at the IdP differs from `setServiceProviderEntityId`.
- **"Authentication issue instant is too old"**: check the clocks of both machines and any maximum authentication age configured with `cfg.setMaximumAuthenticationLifetime(seconds)`. In pac4j 6.5.9, the default is `0`, which disables this authentication-age check; an explicit positive value limits the age of the IdP authentication. Assertion validity timestamps are checked separately.
- **Signature validation fails**: the IdP rotated its certificate. Reload the IdP metadata.
- **The assertion is missing a `Destination`**: some IdPs omit it; pac4j requires it for security but you can relax it with `cfg.setResponseDestinationAttributeMandatory(false)`.

## 9) Learn more

- The [Spring Boot SAML demo](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/saml2) for a complete reference application.
- The documentation for the [SAML 2.0 client for Java](/docs/clients/saml.html): bindings, signature algorithms, forced and passive authentication, attribute converters and IdP-specific notes.
- The `SAML2Client` keeps a replay cache between authentications, so define it once as a singleton, which is what the Spring bean above does.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
