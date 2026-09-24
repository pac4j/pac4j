---
layout: content
title: How to secure a Java application with SAML (using Spring Boot)
seo_title: "How to secure a Java application with SAML | pac4j"
description: "Add SAML 2.0 single sign-on to a Java application with pac4j and Spring Boot: keystore, IdP metadata, SP metadata exchange, user attributes and single logout."
---

# How to secure a Java application with SAML (using Spring Boot)

SAML 2.0 is the single sign-on protocol of the enterprise world. Two parties exchange signed XML messages: the **identity provider** (IdP) authenticates the user (Microsoft Entra ID, Okta, ADFS, Shibboleth, Keycloak, a CAS server...), and the **service provider** (SP) is your Java application, which receives a signed assertion describing the user.

This guide uses **pac4j with Spring Boot** to turn a Java web application into a SAML service provider. The demo authenticates against the public pac4j test IdP, and the sections on metadata, attributes and logout show what to adapt for your own IdP.

**What you need:**

- Java 17 or later and Maven
- the **metadata** of your IdP, as a URL or an XML file. Every IdP publishes it; ask your identity team if you do not know where it is. The demo uses the metadata URL of the public test IdP.

## 1) Get the Spring Boot demo

The [SAML demo project](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/saml2) contains the classes shown in this guide, a ready-made keystore and a `metadata` directory:

```bash
git clone --branch saml2 --single-branch https://github.com/pac4j/simple-spring-boot-pac4j-demos.git
cd simple-spring-boot-pac4j-demos
```

## 2) Add the Maven dependencies

The [demo's `pom.xml`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/pom.xml) uses the Spring Boot parent. On top of Spring MVC, you need the pac4j Spring MVC integration and the SAML module, which embeds the OpenSAML library:

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
    <version>6.5.8</version>
</dependency>
```

## 3) Create the service provider keystore

A SAML service provider signs its requests and decrypts the assertions it receives, so it needs its own key pair. Generate a Java keystore with `keytool`:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -keystore samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

Put the file in `src/main/resources`. The demo already ships one. If the keystore path you configure does not exist and is writable, pac4j generates the keystore and its key pair for you at first use.

## 4) Configure SAML 2.0 authentication

The whole security setup fits in one class, [`SecurityConfig`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/src/main/java/org/pac4j/demos/SecurityConfig.java):

```java
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

What each part does:

- **`Pac4jSecurityConfig`** registers the `/callback` endpoint, which is the **Assertion Consumer Service** (ACS) of your service provider, and the `/logout` endpoint.
- **The keystore lines** point to the file created in step 3 and give the two passwords used with `keytool`. The `classpath:` prefix loads it from the resources; `file:` and `https:` prefixes are supported as well.
- **`setIdentityProviderMetadataPath`** is the IdP metadata from your prerequisites. pac4j reads the IdP's single sign-on URL, its signing certificate and its logout endpoint from it.
- **`setServiceProviderEntityId`** is the unique name of your application in the SAML world. Any URI works, but the callback URL is a common and convenient choice.
- **`setServiceProviderMetadataPath`** is where pac4j writes the metadata of your service provider, which you will give to the IdP in the next step.
- **`addSecurity(registry, "SAML2Client")`** protects `/protected/**`.

## 5) Exchange metadata with your identity provider

SAML trust is mutual: your application trusts the IdP through its metadata, and the IdP must know your application the same way.

When the `SAML2Client` initializes, pac4j generates the SP metadata into `metadata/sp-metadata-8080.xml`. This XML document contains your entity ID, your public certificate and your ACS URL, `http://localhost:8080/callback?client_name=SAML2Client`. Register it at the IdP:

- **Entra ID, Okta, Auth0, Keycloak**: create a SAML application and either upload this file or copy the entity ID and the ACS URL into the form.
- **Shibboleth, ADFS, simpleSAMLphp**: add the file, or a URL serving it, to the IdP's list of relying parties.

You can also get the same XML programmatically with `client.getServiceProviderMetadataResolver().getMetadata()`, for instance to serve it from an endpoint.

If the IdP metadata is not reachable over HTTP from your application, download it once and load it from the classpath with `cfg.setIdentityProviderMetadataPath("classpath:idp-metadata.xml")`.

## 6) Access the authenticated user

The [application controller](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/src/main/java/org/pac4j/demos/Application.java) exposes a public page and a protected page:

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

After login, the `ProfileManager` returns a `SAML2Profile`. Its identifier is the `NameID` of the assertion, and its attributes are the SAML attributes the IdP released, under the names the IdP used. Enterprise IdPs often send URNs rather than friendly names, and SAML attributes are multi-valued, so a value comes back as a list:

```java
final var profile = (SAML2Profile) profileManager.getProfile().orElseThrow();
profile.getId();                                            // the NameID
profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.3");  // "mail" in many directories
profile.getAttributes();                                    // everything the IdP sent
```

To work with readable names in your code, map the attributes once in the configuration:

```java
cfg.setMappedAttributes(Map.of("urn:oid:0.9.2342.19200300.100.1.3", "email"));
```

The IdP decides which attributes it releases: if a value is missing, the attribute release rule for your SP at the IdP is the place to look.

## 7) Single logout

The `/logout` link removes the profile from the local session. SAML also defines a **single logout** (SLO) that ends the session at the IdP and, through it, at the other applications. Enable it in `application.properties`:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=/
```

pac4j then sends a `LogoutRequest` to the IdP's single logout endpoint, read from the IdP metadata, and processes the `LogoutResponse` on the callback URL. To sign outgoing logout requests, add this setting to the `SAML2Configuration` in step 4 before creating the client:

```java
cfg.setSpLogoutRequestSigned(true);
```

In pac4j 6.5.8, this option is `false` by default: enabling central logout alone does not enable request signing. The HTTP-POST and HTTP-Redirect bindings are supported for these messages; pick the one your IdP expects with `cfg.setSpLogoutRequestBindingType(...)`. SLO only works if the IdP metadata declares a `SingleLogoutService`.

## 8) Run the application

Start [`SpringBootDemo`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/src/main/java/org/pac4j/demos/SpringBootDemo.java) from your IDE or with `mvn spring-boot:run`:

```java
@SpringBootApplication
public class SpringBootDemo {
    public static void main(final String[] args) {
        SpringApplication.run(SpringBootDemo.class, args);
    }
}
```

Open [http://localhost:8080/](http://localhost:8080/) and follow **Protected area**. You are redirected to the IdP to sign in, then posted back to the callback URL with the assertion, and the protected page prints your profile.

**If something goes wrong:**

- **The IdP rejects the request as an unknown service provider**: the SP metadata is not registered, or the entity ID at the IdP differs from `setServiceProviderEntityId`.
- **"Authentication issue instant is too old"**: check the clocks of both machines and any maximum authentication age configured with `cfg.setMaximumAuthenticationLifetime(seconds)`. In pac4j 6.5.8, the default is `0`, which disables this authentication-age check; an explicit positive value limits the age of the IdP authentication. Assertion validity timestamps are checked separately.
- **Signature validation fails**: the IdP rotated its certificate. Reload the IdP metadata.
- **The assertion is missing a `Destination`**: some IdPs omit it; pac4j requires it for security but you can relax it with `cfg.setResponseDestinationAttributeMandatory(false)`.

## Learn more

- The [SAML 2.0 reference](/docs/clients/saml.html): bindings, signature algorithms, forced and passive authentication, attribute converters and IdP-specific notes.
- The `SAML2Client` keeps a replay cache between authentications, so define it once as a singleton, which is what the Spring bean above does.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
