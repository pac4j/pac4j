---
layout: content
title: How to secure a Spring WebFlux application with OIDC (using pac4j)
seo_title: "How to secure a Spring WebFlux application with OIDC | pac4j"
description: "Add OpenID Connect login to Spring WebFlux with pac4j: protect routes, handle the OIDC callback, read the user profile and configure logout."
---

# How to secure a Spring WebFlux application with OIDC (using pac4j)

The OIDC client does not change when we move from Spring MVC to WebFlux. The surrounding HTTP and session APIs do: we have `WebFilter`, `ServerWebExchange` and reactive return types instead of servlet filters.

The [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) integration connects these APIs to pac4j. We'll use the same OIDC configuration as in the [Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html), then look at the WebFlux-specific parts.

The example uses **Spring Boot 3, Java 17 and spring-webflux-pac4j 3.0.0**, with the default OIDC authorization code flow and a **GET callback**.

There is a detail to keep in mind: the pac4j authentication engine is still synchronous. We must load the reactive session before calling it, then run its blocking work on Reactor's bounded elastic scheduler.

Version 3.0.0 also has a callback issue: its built-in `CallbackController` only invokes the callback logic when the request has a body. A normal OIDC GET callback has none.

We'll therefore write an explicit GET endpoint, and use the reactive `WebSession` API for session renewal and invalidation. **Do not register the built-in callback and logout controllers alongside these endpoints.**

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
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
```

Why declare `spring-jcl` explicitly? pac4j 6.5.8 excludes it from its transitive `spring-core` dependency, but Spring 6 still needs it at runtime.

The [spring-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-webflux-pac4j-boot-demo) contains a broader application. If adapting it, replace its security configuration with the one below and remove its component scan of `org.pac4j.springframework.web` to avoid duplicate endpoint mappings. Place the following classes under the package scanned by your `@SpringBootApplication`.

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
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Configuration
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
    public WebFilter protectedFilter(final Config config) {
        final var security = SecurityFilter.build(config, "OidcClient",
            new PathMatcher().includePath("/protected/"));
        return (exchange, chain) -> exchange.getSession().then(
            Mono.defer(() -> security.filter(exchange, chain))
                .subscribeOn(Schedulers.boundedElastic()));
    }
}
```

`setAllowUnsignedIdTokens(true)` is only for the public demo server: remove it for your own provider. Register `http://localhost:8080/callback?client_name=OidcClient` as the redirect URI. The [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider) covers Keycloak, Google and Microsoft Entra ID.

Look closely at the path: `PathMatcher.includePath` uses a **prefix match**. `/protected/` covers `/protected/index` and deeper paths, but not `/protected` itself or `/protected-other`. Add rules for those paths if you need them, and keep callback and logout outside the protected prefix.

The scheduler wrapper follows [Reactor's pattern for blocking calls](https://projectreactor.io/docs/core/release/reference/faq.html#faq.wrap-blocking). The OIDC HTTP calls remain blocking, but they now run away from the Netty event loop.

## 3) Handle the callback and logout

Now let's handle the return from the provider. We wait for session renewal before processing the callback. For logout, we remove the profile through pac4j, then wait for session invalidation before sending the response. These endpoints are for the default authorization code flow:

```java
package org.example;

import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.springframework.context.SpringWebFluxFrameworkParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class AuthController {

    private final Config config;

    @Value("${pac4j.logout.centralLogout:false}")
    private boolean centralLogout;

    @Value("${pac4j.logout.defaultUrl:http://localhost:8080/}")
    private String logoutReturnUrl;

    public AuthController(final Config config) {
        this.config = config;
    }

    @GetMapping("/callback")
    public Mono<Void> callback(final ServerWebExchange exchange) {
        return exchange.getSession().flatMap(session -> session.changeSessionId().then(
            Mono.defer(() -> {
                FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);
                return (Mono<Void>) config.getCallbackLogic().perform(config, "/", false, null,
                    new SpringWebFluxFrameworkParameters(exchange));
            }).subscribeOn(Schedulers.boundedElastic())));
    }

    @GetMapping("/logout")
    public Mono<Void> logout(final ServerWebExchange exchange) {
        return exchange.getSession().flatMap(session ->
            Mono.fromCallable(() -> {
                FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);
                return (Mono<Void>) config.getLogoutLogic().perform(config, logoutReturnUrl,
                    "^http://localhost:8080/$", true, false, centralLogout,
                    new SpringWebFluxFrameworkParameters(exchange));
            }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(response -> session.invalidate().then(response)));
    }
}
```

The `false` arguments may look surprising: they disable pac4j's synchronous session renewal and destruction because our controller already performs those operations through `WebSession`.

The logout regex only allows the local home URL in the dynamic `url` parameter. For production, update both the return URL and this allowlist to your public HTTPS origin.

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

If the filter never triggers, check the full request path against the `/protected/` prefix. If the provider rejects the redirect URI, include `?client_name=OidcClient` in its registration. If the GET callback returns an empty response, check that the custom `AuthController` is registered and the built-in callback controller is not.

## Switching to SAML or CAS

The protocol configuration from the [SAML guide](/how-to-secure-a-java-application-with-saml.html) or [CAS guide](/how-to-secure-a-java-application-with-cas.html) can be reused with this integration. Add the corresponding module, change the client name and adapt the profile type.

There is more to adapt than the client, though. Our callback only accepts GET requests. SAML POST responses, OIDC `form_post` and CAS back-channel logout need POST endpoints and request-body handling as well.

Implement those parts before changing protocols, then test callback parsing and session handling with your provider and session store.

## Learn more

- The [OpenID Connect reference](/docs/clients/openid-connect.html) for the complete client configuration.
- The [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j) library and its [documentation](https://github.com/pac4j/spring-webflux-pac4j/wiki).
- The [spring-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-webflux-pac4j-boot-demo) and [Spring Security WebFlux demo](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo). The [Spring Security guide](/how-to-secure-a-spring-security-application-with-oidc.html) explains the bridge concept using servlet filters; reactive security requires its own configuration.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
