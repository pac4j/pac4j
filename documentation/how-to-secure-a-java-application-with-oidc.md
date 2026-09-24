---
layout: content
title: How to secure a Java application with OIDC (using Spring Boot)
seo_title: "How to secure a Java application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) login to a Java application with pac4j and Spring Boot: Maven setup, Keycloak, Google or Azure AD, user profile and logout."
---

# How to secure a Java application with OIDC (using Spring Boot)

OpenID Connect (OIDC) adds an identity layer on top of OAuth 2.0. Your Java application never sees the user's password: it redirects the browser to an identity provider (Keycloak, Google, Microsoft Entra ID, Okta, Auth0, a CAS server...), the provider authenticates the user and sends back an **ID token**, a signed JWT that tells your application who signed in.

This guide uses **pac4j with Spring Boot** to add OIDC login to a Java web application in seven steps. It works with any OpenID Connect provider: the demo points to the public pac4j test server, and a dedicated section shows what changes for Keycloak, Google and Azure AD.

**What you need:**

- Java 17 or later and Maven
- an OpenID Connect provider where you can register an application, or the public demo server used below.

## 1) Get the Spring Boot demo

The [OIDC demo project](https://github.com/pac4j/simple-spring-boot-pac4j-demos/tree/oidc) contains the three classes shown in this guide, ready to run:

```bash
git clone --branch oidc --single-branch https://github.com/pac4j/simple-spring-boot-pac4j-demos.git
cd simple-spring-boot-pac4j-demos
```

## 2) Add the Maven dependencies

The [demo's `pom.xml`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/pom.xml) uses the Spring Boot parent. On top of Spring MVC, you need two pac4j artifacts: the Spring MVC integration and the OpenID Connect module.

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
    <version>6.5.8</version>
</dependency>
```

The `pac4j-oidc` module is framework-agnostic: the same `OidcClient` works with Jakarta EE, Play, Vert.x, JAX-RS and the other [pac4j integrations](/implementations.html). The `spring-webmvc-pac4j` dependency provides the Spring MVC integration used by this Spring Boot application.

## 3) Configure OpenID Connect (OIDC) authentication

The whole security setup fits in one class, [`SecurityConfig`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/SecurityConfig.java):

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

What each part does:

- **`Pac4jSecurityConfig`** registers the two endpoints every redirect-based login needs: `/callback`, where the provider sends the user back, and `/logout`.
- **`setDiscoveryURI`** points to the provider's `.well-known/openid-configuration` document. pac4j reads the authorization, token, user info and JWKS endpoints from it, so you never type them by hand.
- **`setClientId` and `setSecret`** are the credentials the provider gave you when you registered the application.
- **`new Config(baseUri + "/callback", ...)`** sets the callback URL. pac4j appends `?client_name=OidcClient` to it, and this full URL is the **redirect URI** you must register at the provider.
- **`addSecurity(registry, "OidcClient")`** protects `/protected/**`: an anonymous request there triggers the redirect to the provider.

By default pac4j uses the **authorization code flow**, and it adds **PKCE** automatically when the provider advertises support for it in its discovery document. `setAllowUnsignedIdTokens(true)` only exists because the public demo server issues unsigned ID tokens: remove it for any real provider, so that the ID token signature is always verified against the provider's JWKS.

## 4) Register the application at your identity provider

Every provider needs the same three things: an application (or "client") with a **redirect URI** of `http://localhost:8080/callback?client_name=OidcClient`, and the resulting client ID and secret. What changes is where the discovery document lives, and pac4j ships dedicated clients for the common providers.

**Keycloak**: create a confidential client in your realm and set its "Valid redirect URIs". The `KeycloakOidcConfiguration` builds the discovery URL from the server base URL and the realm name:

```java
final var config = new KeycloakOidcConfiguration()
    .setBaseUri("https://keycloak.example.com")
    .setRealm("myrealm");
config.setClientId("myclient");
config.setSecret("mysecret");
return new Config(baseUri + "/callback", new KeycloakOidcClient(config));
```

**Google**: create an "OAuth client ID" of type "Web application" in the Google Cloud console and add the redirect URI to "Authorized redirect URIs". The `GoogleOidcClient` already knows Google's discovery URL:

```java
final var config = new OidcConfiguration()
    .setClientId("xxx.apps.googleusercontent.com")
    .setSecret("GOCSPX-...");
return new Config(baseUri + "/callback", new GoogleOidcClient(config));
```

**Microsoft Entra ID (Azure AD)**: register an application in the Entra portal, add the redirect URI under "Web" platform and create a client secret. The `AzureAd2Client` takes the tenant identifier:

```java
final var config = new AzureAd2OidcConfiguration("38c46e5a-21f0-46e5-940d-3ca06fd1a330");
config.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
config.setSecret("...");
return new Config(baseUri + "/callback", new AzureAd2Client(config));
```

For Okta, Auth0, a CAS server or any other provider, the generic `OidcClient` with the provider's discovery URL is all you need. Replace `client_name=OidcClient` in the redirect URI with the name of the client you use (`KeycloakOidcClient`, `GoogleOidcClient`, `AzureAd2Client`).

**Update the security interceptor as well:** the name passed to `addSecurity` must match the configured client. For example, with `KeycloakOidcClient`, replace the interceptor configuration from step 3 with:

```java
addSecurity(registry, "KeycloakOidcClient").addPathPatterns("/protected/**");
```

Use `GoogleOidcClient` or `AzureAd2Client` in both places for the corresponding provider.

## 5) Access the authenticated user

The [application controller](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/Application.java) exposes a public page and a protected page:

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

The `ProfileManager` gives you the profile saved in the session after login. For OpenID Connect it is an `OidcProfile`, which exposes the standard claims as typed getters and keeps the raw tokens:

```java
final var profile = (OidcProfile) profileManager.getProfile().orElseThrow();
profile.getId();            // the "sub" claim
profile.getEmail();
profile.getDisplayName();
profile.getAttribute("preferred_username");
profile.getIdToken();       // the parsed ID token (JWT)
profile.getAccessToken();   // to call the provider's APIs
```

Which claims are present depends on the **scopes** you request. The default is `openid profile email`; add more with `config.setScope("openid profile email phone")`.

## 6) Logout

The `/logout` link created by the integration removes the profile from the session: this is the **local logout**. To also end the session at the identity provider, enable the central logout in `application.properties`:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=http://localhost:8080/
```

For providers supporting OIDC logout, pac4j redirects the browser to the `end_session_endpoint` from the discovery document and supplies the absolute default URL as `post_logout_redirect_uri`. Register `http://localhost:8080/` as an allowed post-logout redirect URI at the provider. A relative default URL such as `/` works for local logout but is not passed to the provider as a return URL.

If your provider supports OIDC logout but does not publish its endpoint, set it explicitly with `config.setLogoutUrl(...)`. Logout support and registration requirements vary by provider; the dedicated `GoogleOidcClient` uses its own logout action rather than this discovery-based flow.

## 7) Run the application

Start [`SpringBootDemo`](https://github.com/pac4j/simple-spring-boot-pac4j-demos/blob/oidc/src/main/java/org/pac4j/demos/SpringBootDemo.java) from your IDE or with `mvn spring-boot:run`:

```java
@SpringBootApplication
public class SpringBootDemo {
    public static void main(final String[] args) {
        SpringApplication.run(SpringBootDemo.class, args);
    }
}
```

Open [http://localhost:8080/](http://localhost:8080/) and follow **Protected area**. You are redirected to the identity provider to sign in, then returned to the protected page, where the controller prints your profile.

**If something goes wrong:**

- **"Invalid redirect URI"** or **"redirect_uri_mismatch"** at the provider: the registered URI must be the full callback URL, including `?client_name=OidcClient` (or the name of the dedicated client configured in step 4).
- **"invalid_client"** on the token request: wrong client secret, or the client is public at the provider while pac4j sends a secret.
- **The ID token signature fails**: check that discovery points to the intended provider, that its JWKS is reachable and contains the current signing key, and that the expected signing algorithm matches the provider's client configuration. If an explicit algorithm is needed, use `config.setIdTokenSigningAlgorithm(...)` with that configured algorithm; changing it to `ES256` is not a general fix for signature errors.
- **Empty name or email**: the scopes or the provider's claim mapping do not release them. Check the scopes first, then the client configuration at the provider.

## Learn more

- The [OpenID Connect reference](/docs/clients/openid-connect.html): all the `OidcConfiguration` options, the implicit flow, `private_key_jwt` client authentication, nonce and state handling.
- [Direct OIDC authentication](/docs/clients/openid-connect-clients.html#2-direct-clients) to protect a REST API with the access tokens issued by your provider.
- [OpenID Federation](/docs/clients/openid-connect-federation.html) when your application belongs to a trust federation.

**Using a different integration?** Reuse the OIDC client configuration with [Jakarta EE servlet filters](/how-to-secure-a-jakarta-ee-application-with-oidc.html), or connect pac4j authentication to an existing [Spring Security application](/how-to-secure-a-spring-security-application-with-oidc.html).

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
