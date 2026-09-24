---
layout: content
title: How to secure a Spring Security application with OIDC (using pac4j)
seo_title: "How to secure a Spring Security application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) login to a Spring Security application: configure pac4j servlet filters, map roles and access the Spring Security context."
---

# How to secure a Spring Security application with OIDC (using pac4j)

Your application already runs on Spring Security: a form login, `hasRole` rules, `@PreAuthorize` on services, maybe years of code relying on `SecurityContextHolder`. Now users must log in through an OpenID Connect (OIDC) provider such as Keycloak, Microsoft Entra ID, Okta or a CAS server, and you would rather not rewrite the authorization layer.

This is what the [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) bridge is for. The essential thing to understand is that **it is only a bridge**: it pushes the pac4j user profile into the Spring Security context, and nothing more. It does not authenticate anyone and **cannot be used alone**. The authentication is done by a real pac4j implementation that you must choose:

- [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j), servlet filters that fit in any Spring Security filter chain, used in this guide
- [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j), interceptors for Spring MVC
- [spring-webflux-pac4j](https://github.com/pac4j/spring-webflux-pac4j), for reactive applications.

The implementation runs the OIDC login and produces a pac4j profile. The bridge turns that profile into a Spring Security `Authentication`, with the pac4j roles as authorities. Your authorization layer can then use that authentication through `hasRole` rules, method security and `SecurityContextHolder`. If you start a new application, prefer a [pac4j implementation alone](/how-to-secure-a-java-application-with-oidc.html): it is simpler. The bridge is for applications that already depend on Spring Security.

**What you need:**

- Java 17 or later and Maven
- Spring Boot 3.x, which brings Spring Security 6
- an OpenID Connect provider where you can register an application, or the public demo server used below.

## 1) Get the demo

The [spring-security-jee-pac4j-boot-demo](https://github.com/pac4j/spring-security-jee-pac4j-boot-demo) project demonstrates Spring Boot, Spring Security, the pac4j servlet filters and the bridge. It includes several authentication mechanisms; the steps below adapt it to OIDC.

```bash
git clone https://github.com/pac4j/spring-security-jee-pac4j-boot-demo.git
cd spring-security-jee-pac4j-boot-demo
```

Replace the demo's `Pac4jConfig` and `SecurityConfig` with the configurations below, and add `ProtectedController` in the same package as the application class. Update existing Maven dependency versions rather than declaring the same artifact twice.

Two sibling demos cover the other combinations: [spring-security-webmvc-pac4j-boot-demo](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo) with the Spring MVC interceptors instead of the servlet filters, and [spring-security-webflux-pac4j-boot-demo](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo) for reactive applications.

## 2) Add the Maven dependencies

On top of the Spring Boot web and security starters, you need three pac4j artifacts: the pac4j implementation that authenticates, here the `jakartaee-pac4j` servlet filters, the OpenID Connect module, and the bridge.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- the pac4j implementation: security, callback and logout filters -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>jakartaee-pac4j</artifactId>
    <version>8.0.3</version>
</dependency>
<!-- pac4j support for OpenID Connect -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
<!-- the bridge: pushes the pac4j profile into the Spring Security context -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>spring-security-pac4j</artifactId>
    <version>10.0.0</version>
</dependency>
```

Without `jakartaee-pac4j` (or `spring-webmvc-pac4j`, or `spring-webflux-pac4j`), the bridge does nothing: there would be no filter to start the login and no profile to push. The bridge itself has no configuration: as soon as it is on the classpath, pac4j detects it and replaces its profile manager with a `SpringSecurityProfileManager`, which writes the Spring Security context every time a profile is saved or removed. The bridge version 10.0.0 used here targets pac4j 6 and Spring Security 6.

## 3) Configure pac4j

Declare the pac4j `Config` as a Spring bean. The OIDC part is the same as in every other pac4j guide; what is specific here is the **authorization generator**, which maps trusted profile attributes to application roles:

```java
package org.pac4j.demo.spring;

import java.util.Collection;
import java.util.Optional;
import org.pac4j.core.config.Config;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Pac4jConfig {

    @Bean
    public Config config() {
        // configuration of the authentication via the OpenID Connect protocol
        final var oidcConfiguration = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        final var oidcClient = new OidcClient(oidcConfiguration);

        // the roles of the pac4j profile become the Spring Security authorities
        oidcClient.setAuthorizationGenerator((ctx, profile) -> {
            profile.addRole("ROLE_USER");
            if (profile.getAttribute("groups") instanceof Collection<?> groups
                    && groups.contains("administrators")) {
                profile.addRole("ROLE_ADMIN");
            }
            return Optional.of(profile);
        });

        return new Config("http://localhost:8080/callback", oidcClient);
    }
}
```

The bridge maps every pac4j role to a `GrantedAuthority` with the same name, so keep the `ROLE_` prefix if your rules use `hasRole("ADMIN")`. This example grants `ROLE_ADMIN` only when the provider supplies a `groups` attribute containing `administrators`. Configure the provider to release this claim and ensure group membership is managed by trusted administrators. Adapt the mapping to your provider's actual claim structure; Keycloak realm roles, for example, use a different structure. Without the expected group, the user receives only `ROLE_USER`.

The callback URL, with the `?client_name=OidcClient` suffix pac4j appends, is the redirect URI to register at the provider. `setAllowUnsignedIdTokens(true)` only exists for the public demo server: remove it for a real provider. To register the application at Keycloak, Google or Entra ID, see the [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider).

## 4) Put the pac4j filters in the Spring Security chain

The pac4j filters run **inside** the Spring Security filter chains, one chain per URL pattern, so that the session and the security context are shared:

```java
package org.pac4j.demo.spring;

import org.pac4j.core.config.Config;
import org.pac4j.jee.filter.CallbackFilter;
import org.pac4j.jee.filter.LogoutFilter;
import org.pac4j.jee.filter.SecurityFilter;
import org.pac4j.springframework.security.web.Pac4jEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Config config;

    // the OIDC login protects /protected/**
    @Bean
    @Order(1)
    public SecurityFilterChain protectedFilterChain(final HttpSecurity http) throws Exception {
        http
            .securityMatcher("/protected/**")
            .addFilterBefore(new SecurityFilter(config, "OidcClient"), BasicAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        return http.build();
    }

    // the provider redirects the user here after login
    @Bean
    @Order(2)
    public SecurityFilterChain callbackFilterChain(final HttpSecurity http) throws Exception {
        http
            .securityMatcher("/callback")
            .addFilterBefore(new CallbackFilter(config), BasicAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // logs the user out of pac4j and of Spring Security
    @Bean
    @Order(3)
    public SecurityFilterChain logoutFilterChain(final HttpSecurity http) throws Exception {
        final var logoutFilter = new LogoutFilter(config, "/");
        logoutFilter.setDestroySession(true);
        http
            .securityMatcher("/pac4jLogout")
            .addFilterBefore(logoutFilter, BasicAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // rules for requests not handled by the chains above
    @Bean
    @Order(4)
    public SecurityFilterChain defaultFilterChain(final HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll())
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(new Pac4jEntryPoint(config, "OidcClient")));
        return http.build();
    }
}
```

What happens at runtime:

- **`SecurityFilter`** on `/protected/**` starts the OIDC login for anonymous users. `SessionCreationPolicy.ALWAYS` guarantees the session pac4j needs to keep the state of the login.
- **`CallbackFilter`** on `/callback` validates the ID token, saves the pac4j profile and, through the bridge, a Spring Security `Authentication` in the session. The callback chain disables Spring Security CSRF checks to allow provider responses sent by POST, such as OIDC `form_post`; the default authorization code flow returns by GET. pac4j validates the OIDC response and its `state`. Keep CSRF protection on the application's other state-changing endpoints.
- **The default chain** contains the rules for all remaining requests. `/admin/**` is protected by a plain `hasRole("ADMIN")`, satisfied by the role added in the authorization generator.
- **`Pac4jEntryPoint`** is optional: it makes Spring Security start the OIDC login when an anonymous user hits a URL protected by the default chain, instead of showing a form login page.

**Only the first matching Spring Security chain runs.** Rules in `defaultFilterChain` do not apply to `/protected/**`, `/callback` or `/pac4jLogout`. The protected chain above delegates access control to pac4j; add any additional Spring Security authorization rules to that chain, or use pac4j authorizers. When adapting an existing application, preserve its required rules in the appropriate chains.

Using Spring MVC interceptors instead of servlet filters? Follow the [webmvc bridge demo](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo). Interceptors run after Spring Security's servlet filters, so its chains must allow requests to reach the pac4j interceptors where required. Switching the dependency alone is not sufficient.

## 5) Access the authenticated user

For a user authenticated by pac4j, the `Authentication` in the Spring Security context is a `Pac4jAuthenticationToken`: its name is the user identifier, its authorities are the pac4j roles, and its principal is the pac4j profile.

```java
package org.pac4j.demo.spring;

import org.pac4j.oidc.profile.OidcProfile;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @GetMapping(value = "/protected/oidc", produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var profile = (OidcProfile) authentication.getPrincipal();
        return "Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")"
            + "\nAuthorities: " + authentication.getAuthorities();
    }
}
```

`@EnableMethodSecurity` in the configuration above enables method authorization. On a Spring-managed service, the authorities come from the same context:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteAccount(final String accountId) {
    // delete the account
}
```

You can also inject the principal with `@AuthenticationPrincipal OidcProfile profile` in a controller method, or use the pac4j `ProfileManager` when you need the list of profiles.

## 6) Logout

Use the pac4j logout URL, `/pac4jLogout` above, rather than Spring Security's `/logout`: the bridge clears both the pac4j profile and the Spring Security context, and `destroySession` invalidates the HTTP session. To also end the session at the identity provider, enable the central logout on the filter:

```java
logoutFilter.setCentralLogout(true);
logoutFilter.setDefaultUrl("http://localhost:8080/");
```

For providers that support OIDC logout, pac4j redirects the browser to the `end_session_endpoint` in the discovery document. Register the absolute return URL as an allowed post-logout redirect URI at the provider; use your public HTTPS URL in production. If you accept a dynamic return URL through the logout request's `url` parameter, also restrict it with `setLogoutUrlPattern(...)`.

## 7) Run the application

```bash
mvn spring-boot:run
```

Open [http://localhost:8080/protected/oidc](http://localhost:8080/protected/oidc): you are redirected to the identity provider, then back to the page, where both the Spring Security context and the pac4j profile show the same user. Then try the demo's [admin page](http://localhost:8080/admin/index.html): it returns 403 unless the provider supplies the `administrators` group expected by the authorization generator.

**If something goes wrong:**

- **403 on `/admin/**` after login**: the role is missing or lacks the `ROLE_` prefix. Check the authorization generator.
- **The form login page shows instead of the provider**: the anonymous request hit the default chain. Either map the URL to a chain with the pac4j `SecurityFilter`, or set the `Pac4jEntryPoint` as above.
- **403 on a POST to `/callback`**: check that the callback chain matches and that Spring Security CSRF checks are disabled on that chain.
- **Logged in for pac4j but anonymous for Spring Security**: the bridge is not on the classpath, or a custom `ProfileManagerFactory` overrides its `SpringSecurityProfileManager`.
- **Nothing happens at all**: the bridge is alone on the classpath. Add a pac4j implementation, it is the one that authenticates.

## Switching to SAML or CAS

Add the corresponding `pac4j-saml` or `pac4j-cas` dependency, replace the `OidcClient` in the `Config` bean with a `SAML2Client` or a `CasClient`, and update the client name in the `SecurityFilter` and the `Pac4jEntryPoint`. Adapt the authorization generator to the attributes supplied by that provider, and replace the controller's `OidcProfile` cast with the corresponding profile type or the common `UserProfile` interface. The filter-chain structure and the bridge remain the same; callback and logout registration depend on the protocol. The protocol-specific setup is described in the [SAML guide](/how-to-secure-a-java-application-with-saml.html) and the [CAS guide](/how-to-secure-a-java-application-with-cas.html).

## Learn more

- The [OpenID Connect reference](/docs/clients/openid-connect.html) for all the `OidcConfiguration` options.
- The [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) bridge and its [documentation](https://github.com/pac4j/spring-security-pac4j/wiki).
- The three demos: [with the servlet filters](https://github.com/pac4j/spring-security-jee-pac4j-boot-demo), [with Spring MVC](https://github.com/pac4j/spring-security-webmvc-pac4j-boot-demo) and [with Spring WebFlux](https://github.com/pac4j/spring-security-webflux-pac4j-boot-demo).
- Why you may not need Spring Security at all: [Spring Boot security: choose spring-webmvc-pac4j over Spring Security](/blog/spring-boot-security-choose-spring-webmvc-pac4j.html) and [the REST API follow-up](/blog/spring-webmvc-pac4j-vs-spring-security-round-2-rest-apis.html).

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
