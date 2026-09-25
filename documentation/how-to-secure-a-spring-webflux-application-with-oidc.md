---
layout: guide
title: How to secure a Spring WebFlux application with OIDC (using pac4j)
seo_title: "How to secure a Spring WebFlux application with OIDC | pac4j"
description: "Add OpenID Connect login to Spring WebFlux with pac4j: protect routes, handle the OIDC callback, read the user profile and configure logout."
---

The OIDC client does not change when we move from Spring MVC to WebFlux. The surrounding HTTP and session APIs do: we have `WebFilter`, `ServerWebExchange` and reactive return types instead of servlet filters.

The [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) integration connects these APIs to pac4j. We'll use the same OIDC configuration as in the [Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html), then look at the WebFlux-specific parts.

The example uses **Spring Boot 3, Java 17 and spring-webflux-pac4j 3.0.1**, with the default OIDC authorization code flow.

The spring-webflux-pac4j integration bridges pac4j's synchronous security logic with Spring WebFlux's reactive model: it runs that logic asynchronously on worker threads, without blocking the event loop. The integration takes care of this for us.

## 1) Add the Maven dependencies

Start with a Spring Boot 3 Maven application using the WebFlux starter and the Spring Boot Maven plugin. Add these dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<!-- Spring 6 logging bridge; version managed by Spring Boot -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jcl</artifactId>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>spring-webflux-pac4j</artifactId>
    <version>3.0.1</version>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
```

Why declare `spring-jcl` explicitly? pac4j 6.5.8 excludes it from its transitive `spring-core` dependency, but Spring 6 still needs it at runtime.

The [spring-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-webflux-pac4j-boot-demo) contains a broader application. If adapting it, replace its security configuration with the one below. Place the following classes under the package scanned by your `@SpringBootApplication`.

## 2) Configure pac4j and protect the routes

First, let's create the OIDC client and protect the routes under `/protected/`:

```java
package org.example;

import org.pac4j.core.config.Config;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.springframework.web.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
public class SecurityConfig {

    @Bean
    public Config config() {
        final var oidc = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        return new Config("http://localhost:8080/callback", new OidcClient(oidc));
    }

    @Bean
    public SecurityFilter protectedFilter(final Config config) {
        return SecurityFilter.build(config, "OidcClient",
            new PathMatcher().includePath("/protected/"));
    }
}
```

`setAllowUnsignedIdTokens(true)` is only for the public demo server: remove it for your own provider. Register `http://localhost:8080/callback?client_name=OidcClient` as the redirect URI. The [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider) covers Keycloak, Google and Microsoft Entra ID.

Look closely at the path: `PathMatcher.includePath` uses a **prefix match**. `/protected/` covers `/protected/index` and deeper paths, but not `/protected` itself or `/protected-other`. Add rules for those paths if you need them, and keep callback and logout outside the protected prefix.

The `SecurityFilter` loads the session and dispatches synchronous pac4j work to Reactor's bounded elastic scheduler. The OIDC HTTP calls remain blocking, but they run away from the Netty event loop. There is no scheduler wrapper to add in the application.

## 3) Handle the callback and logout

The `@ComponentScan` above registers the library's `CallbackController` and `LogoutController`. Their default paths are `/callback` and `/logout`. The callback receives the provider's response, saves the authenticated profile and sends the user back to the requested page.

Let's set the callback and logout options in `src/main/resources/application.properties`:

```properties
pac4j.callback.defaultUrl=/
pac4j.callback.renewSession=true
pac4j.logout.defaultUrl=http://localhost:8080/
pac4j.logout.logoutUrlPattern=^http://localhost:8080/$
pac4j.logout.localLogout=true
pac4j.logout.destroySession=true
pac4j.logout.centralLogout=false
```

The callback renews the session identifier after authentication to protect against session fixation. Logout removes the local profile and destroys the session. The integration waits for these session operations before continuing.

The logout regex only allows the local home URL in the dynamic `url` parameter. For production, update both the return URL and this allowlist to your public HTTPS origin.

If you used the earlier version of this guide, remove its custom `AuthController` when enabling the built-in controllers, so each endpoint has a single mapping.

## 4) Access the authenticated user

With the session loaded and the user authenticated, we can read the profile in a controller. We'll return plain text so attribute values are not interpreted as HTML:

```java
package org.example;

import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.springframework.context.SpringWebfluxSessionStore;
import org.pac4j.springframework.context.SpringWebfluxWebContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
public class ProtectedController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String home() {
        return "Visit /protected/index to sign in, or /logout to sign out.";
    }

    @GetMapping(value = "/protected/index", produces = MediaType.TEXT_PLAIN_VALUE)
    public String index(final ServerWebExchange exchange) {
        final var context = new SpringWebfluxWebContext(exchange);
        final var manager = new ProfileManager(context, new SpringWebfluxSessionStore(exchange));
        final var profile = (OidcProfile) manager.getProfile().orElseThrow();
        return "Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")";
    }
}
```

The filter has already loaded the session before the protected controller runs. `OidcProfile` exposes standard claims and provides access to the ID token and access token; the available attributes depend on the provider and requested scopes.

## 5) Enable central logout

The endpoint above defaults to local logout. To also request logout at the provider, set these properties:

```properties
pac4j.logout.centralLogout=true
pac4j.logout.defaultUrl=http://localhost:8080/
```

The provider must expose an `end_session_endpoint` and allow `http://localhost:8080/` as a post-logout redirect URI. Use the public HTTPS URL in production.

## 6) Run the application

```bash
mvn spring-boot:run
```

Open [http://localhost:8080/protected/index](http://localhost:8080/protected/index), sign in and verify that you return to the protected page. Visiting `/logout` removes the local profile and session.

If the filter never triggers, check the full request path against the `/protected/` prefix. If the provider rejects the redirect URI, include `?client_name=OidcClient` in its registration. If `/callback` or `/logout` returns 404, check that the component scan registers the library's controllers. If Spring reports duplicate mappings, remove any custom controller left over from the earlier example.

## Switching to SAML or CAS

The protocol configuration from the [SAML guide](/how-to-secure-a-java-application-with-saml.html) or [CAS guide](/how-to-secure-a-java-application-with-cas.html) can be reused with this integration. Add the corresponding module, replace the `OidcClient` and update the client name in the filter. Adapt the profile type and attributes, and register the callback and logout URLs at the provider. SAML also needs a keystore and metadata exchange.

The built-in callback accepts GET and POST requests and makes the raw request body available to clients such as SAML. Check the chosen protocol's bindings and single logout requirements, and test them with your provider and session store.

## Learn more

- The documentation for the [OIDC client for Java](/docs/clients/openid-connect.html) for the complete client configuration.
- The [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) library and its [documentation](https://github.com/pac4j/spring-webflux-pac4j/wiki).
- The [spring-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-webflux-pac4j-boot-demo) and [Spring Security WebFlux demo](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo). The [Spring Security guide](/how-to-secure-a-spring-security-application-with-oidc.html) explains the bridge concept using servlet filters; reactive security requires its own configuration.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
