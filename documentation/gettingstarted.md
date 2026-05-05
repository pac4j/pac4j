---
layout: content
title: Getting started
---

# 1) Let's jump right in with a Spring Boot demo secured by an OpenID Connect server

## a) The dependencies: [pom.xml](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/pom.xml)

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
<!-- pac4j support for OpenID Connect -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.0</version>
</dependency>
```

## b) The [SpringBootDemo](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/SpringBootDemo.java)

```java
@SpringBootApplication
public class SpringBootDemo {
    public static void main(final String[] args) {
        SpringApplication.run(SpringBootDemo.class, args);
    }
}
```

Just the usual, nothing to say. Run this class.


## c) The [SecurityConfig](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/SecurityConfig.java) for OpenID Connect

```java
@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUri;

    @Bean
    public Config config() {
        // configuration of the authentication via the OpenID Connect protocol
        final var config = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        return new Config(baseUri + "/callback", new OidcClient(config));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // the /protected/** URLs require the OIDC authentication
        addSecurity(registry, "OidcClient").addPathPatterns("/protected/**");
    }
}
```

Notice the `extends Pac4jSecurityConfig`.

Two definitions are required:
- the authentication mechanism which is a client (built from a config when needed)
- the protected URLs.

Automatically, the `/callback` and `/logout` endpoints are created.


## d) The [controller](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/Application.java)

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

The injected `ProfileManager` gives you access to the authenticated user.


# 2) Let's switch to a Spring Boot demo secured by a CAS server

Nothing to change, except [one dependency](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/pom.xml#L27):


```xml
<!-- pac4j support for CAS -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>6.5.0</version>
</dependency>
```

(instead of `pac4j-oidc`) and the [security configuration](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/cas/src/main/java/org/pac4j/demos/SecurityConfig.java#L20):

```java
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
```

A new client (= authn mechanism): `CasClient` for the CAS protocol (like `OidcClient` for the OIDC protocol).

The `CasConfiguration` class is optional on such an easy configuration: `return new Config(baseUri + "/callback", new CasClient(casLoginUrl));`.


# 3) Let's update to a Spring Boot demo secured by a SAML2 IdP

We need a [new dependency](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/pom.xml#L27) for the SAML2 protocol support:

```xml
<!-- pac4j support for SAML2 -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-saml</artifactId>
    <version>6.5.0</version>
</dependency>
```

and a new [`SecurityConfig`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/saml2/src/main/java/org/pac4j/demos/SecurityConfig.java#L19):

```java
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
```

The `SAML2Configuration` defines the keystore (in the classpath), its passwords, the SAML2 server metadata, the SP (= pac4j = client) identifier as well as the location of its generated metadata.

A little more complicated but the SAML2 protocol requires that and generally a lot more :-(

**Discover more [pac4j frameworks](implementations.html) and more [authentication mechanisms](docs/clients.html)...**

<hr/>

# 4) The grand tour

<iframe src="https://docs.google.com/presentation/d/1ScGtdqRBUpGYA915sn6L3CXOB3axzJa1H3rj7i27MZs/embed?start=false&loop=false&delayms=60000" frameborder="0" width="1108" height="652" allowfullscreen="true" mozallowfullscreen="true" webkitallowfullscreen="true"></iframe>
