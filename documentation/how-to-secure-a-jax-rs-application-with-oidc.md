---
layout: guide
title: How to secure a JAX-RS application with OIDC (using pac4j)
seo_title: "How to secure a JAX-RS (Jersey, RESTEasy) application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) to a JAX-RS application with pac4j on Jersey, RESTEasy or Dropwizard: browser login with @Pac4JSecurity, bearer tokens for a REST API."
---

Before adding OIDC to a JAX-RS application, let's distinguish two cases. A browser visits a protected page and needs to be redirected to a login form. An API caller, on the other hand, already has an **access token** and sends it with the request. The application must handle these differently.

The [jax-rs-pac4j](https://github.com/pac4j/jax-rs-pac4j) library supports both. It connects pac4j clients to your resource methods through annotations, on Jersey or RESTEasy (JAX-RS is now called Jakarta REST).

We'll start with browser login on **Jersey 4**, then protect a REST API with bearer tokens. We'll also see what changes for Jersey 3, RESTEasy and **Dropwizard**, whose pac4j bundle handles registration for us.

The [jax-rs-pac4j-demo](https://github.com/pac4j/jax-rs-pac4j-demo) provides a browser-login example. If you've followed the [Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html), the OIDC client configuration will be familiar; here, we'll connect it to JAX-RS.

**What you need:**

- Java 17 or later and Maven
- a Jakarta REST runtime: Jersey 3 or 4, RESTEasy 6 or 7, standalone on Grizzly or inside a servlet container, or Dropwizard 5
- an OpenID Connect provider where you can register an application, or the public demo server used below.

## 1) Get the demo

The demo runs Jersey on an embedded Grizzly server, without any container:

```bash
git clone https://github.com/pac4j/jax-rs-pac4j-demo.git
cd jax-rs-pac4j-demo
mvn clean package
java -jar target/jax-rs-pac4j-demo-*.jar
```

It starts on [http://localhost:8080](http://localhost:8080) with form, HTTP Basic and CAS logins. The sections below apply the same structure to OIDC. If using the `org.example.App` class below, update the Maven Shade plugin's main class to match and replace the resource-package registration.

## 2) Add the Maven dependencies

Pick the `jax-rs-pac4j` module matching your runtime, and add the OpenID Connect module. All modules share the `org.pac4j` group and version `8.0.0`:

| Your runtime | Maven artifact |
|--------------|----------------|
| Jersey 3.1 | `jersey3-pac4j` |
| Jersey 4.0 | `jersey4-pac4j` |
| RESTEasy 6.2 | `resteasy6-pac4j` |
| RESTEasy 7.0 | `resteasy7-pac4j` |
{:.striped}

```xml
<!-- pac4j integration for Jersey 4 -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>jersey4-pac4j</artifactId>
    <version>8.0.0</version>
</dependency>
<!-- pac4j support for OpenID Connect -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
```

Your application or server supplies the Jersey or RESTEasy runtime itself (Jersey 4.0.2 for this example): for the standalone setup of this guide, that is `jersey-server`, `jersey-hk2` and `jersey-container-grizzly2-http`. The [dependency guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Dependencies) lists the tested combinations.

## 3) Configure pac4j and register the features

First, build the `Config` with our OIDC client. We then need to tell JAX-RS three things: how pac4j should access requests and sessions, how to process its security annotations, and how to inject profiles into resource methods.

These are the runtime feature, security feature and value factory registered below:

```java
package org.example;

import java.net.URI;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.grizzly.features.Pac4JGrizzlyFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

public class App {

    public static void main(final String[] args) {
        final var baseUrl = "http://localhost:8080";

        // configuration of the authentication via the OpenID Connect protocol
        final var oidcConfiguration = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        final var config = new Config(baseUrl + "/callback", new OidcClient(oidcConfiguration));

        final var application = new ResourceConfig()
            .register(new Pac4JGrizzlyFeature(config))          // request context and session on Grizzly
            .register(new Pac4JSecurityFeature())               // @Pac4JSecurity, @Pac4JCallback, @Pac4JLogout
            .register(new Pac4JValueFactoryProvider.Binder())   // @Pac4JProfile, @Pac4JProfileManager
            .packages("org.example.resources");

        final var server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl + "/"), application);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
    }
}
```

For OIDC, we set the discovery URI, client ID and secret as usual. Remove `setAllowUnsignedIdTokens(true)` when leaving the public demo server. The [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider) covers registration at Keycloak, Google and Entra ID.

Our `new Config(baseUrl + "/callback", ...)` supplies the callback URL. Register it at the provider with the `?client_name=OidcClient` suffix that pac4j appends.

The runtime choice matters for sessions. Here, `Pac4JGrizzlyFeature` uses Grizzly. Inside Tomcat, Jetty or WildFly, use `Pac4JServletFeature(config)` to access the container's `HttpSession`. **Browser login needs a session** to keep state between the redirect and the callback.

The profile injection is runtime-specific too: `Pac4JValueFactoryProvider.Binder` is for Jersey. With RESTEasy and CDI, register `Pac4JSecurityFeature` as a class and use `Pac4JProfileInjectorFactory` instead, following the [RESTEasy configuration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Security-configuration#resteasy-62-and-70-with-servlet-and-cdi).

**On Dropwizard**, none of this registration code is needed: see the [Dropwizard section](#using-dropwizard) below, then continue with step 4.

## 4) Declare the callback and logout endpoints

The callback and logout look like ordinary resource methods with annotations. Their bodies never run, though: the pac4j filter processes the request and responds before JAX-RS reaches them.

```java
package org.example.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JLogout;

@Path("/")
public class AuthResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String home() {
        return "Visit /protected/index to sign in, or /logout to sign out.";
    }

    @GET
    @Path("callback")
    @Pac4JCallback(defaultUrl = "/", renewSession = false)
    public void callback() {
        // handled by pac4j
    }

    @POST
    @Path("callback")
    @Pac4JCallback(defaultUrl = "/", renewSession = false)
    public void callbackPost() {
        // handled by pac4j, for the form_post response mode
    }

    @GET
    @Path("logout")
    @Pac4JLogout(destroySession = true, defaultUrl = "/")
    public void logout() {
        // handled by pac4j
    }
}
```

This Grizzly example follows the demo with `renewSession = false`, so it does not rotate the session identifier after login. For deployment, use a servlet-backed runtime with `renewSession = true`, or validate session renewal with your Grizzly version before enabling it. Session renewal protects against session fixation.

## 5) Protect a resource and read the profile

Annotate the resource method, or the whole class, with `@Pac4JSecurity` and name the client. Add a `@Pac4JProfile` parameter to receive the authenticated user:

```java
package org.example.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.profile.OidcProfile;

@Path("/protected")
public class ProtectedResource {

    @GET
    @Path("/index")
    @Produces(MediaType.TEXT_PLAIN)
    @Pac4JSecurity(clients = "OidcClient")
    public String index(@Pac4JProfile OidcProfile profile) {
        return "Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")"
            + "\nVisit /logout to sign out.";
    }
}
```

An anonymous request is redirected to the provider; after login, the callback sends the user back to the requested URL and the method runs with the profile. `@Pac4JSecurity` also accepts `authorizers`, for example a role check declared in the `Config`, and `matchers`. The parameter can be typed as any `CommonProfile` subclass, as `Optional<CommonProfile>` when the resource also serves anonymous users, or you can ask for the whole `@Pac4JProfileManager ProfileManager`.

## 6) Protect a REST API with the access token

Now let's take the second case: an API caller that already has an access token. For this setup, add `org.pac4j:pac4j-http:6.5.8` for `HeaderClient` and a JSON provider such as `jersey-media-json-jackson`, matching your Jersey version. Replace the stateful registration from step 3 with the configuration below.

The caller sends its **access token** in the `Authorization: Bearer` header. There is no browser redirect and no local session. Our **direct client** checks the token at the OIDC provider's user info endpoint, using the OIDC client's profile creator:

```java
final var oidcClient = new OidcClient(oidcConfiguration);
oidcClient.setCallbackUrl("notused");
oidcClient.init();
final var bearerClient = new HeaderClient("Authorization", "Bearer ", oidcClient.getProfileCreator());

final var config = new Config(bearerClient);
config.setSessionStoreFactory(NoOpSessionStoreFactory.INSTANCE);

final var application = new ResourceConfig()
    .register(new Pac4JJaxRsFeature(config))    // no session at all
    .register(new Pac4JSecurityFeature())
    .register(new Pac4JValueFactoryProvider.Binder())
    .packages("org.example.api");
```

```java
package org.example.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

@Path("/api/me")
public class MeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Pac4JSecurity(clients = "HeaderClient")
    public Map<String, Object> me(@Pac4JProfile CommonProfile profile) {
        final Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", profile.getId());
        user.put("email", profile.getEmail());
        return user;
    }
}
```

```bash
curl -H "Authorization: Bearer eyJhbGciOi..." http://localhost:8080/api/me
```

This checks credentials on every request. The provider must expose UserInfo and accept the access token there. We have identified the user, but **we still need to enforce our API's audience, scopes and application-specific permissions**. Configure these checks for your provider and API.

If the provider issues **JWT access tokens**, you can also validate them locally. Replace the profile creator with a `JwtAuthenticator` from `pac4j-jwt`, configured with the provider's JWKS and the expected issuer, audience and token lifetime checks.

An application can offer both browser login and bearer authentication. Keep a stateful configuration for the browser routes and a separate stateless one for the API: an API request must supply valid bearer credentials, even when the caller also has a browser session.

## 7) Logout

The `/logout` endpoint of step 4 performs the **local logout**. To also end the session at the identity provider, enable the central logout in the annotation:

```java
@GET
@Path("logout")
@Pac4JLogout(destroySession = true, centralLogout = true, defaultUrl = "http://localhost:8080/")
public void logout() {
}
```

For a provider supporting OIDC logout, pac4j redirects to its `end_session_endpoint`. Register `http://localhost:8080/` as an allowed post-logout redirect URI. If accepting a dynamic `url` parameter, also restrict it with `logoutUrlPattern`.

The stateless API has no local session to destroy. Stopping token use on the client does not revoke the token; it remains valid until expiry or provider-side revocation.

## 8) Run the application

```bash
mvn clean package
java -jar target/*.jar
```

Open [http://localhost:8080/protected/index](http://localhost:8080/protected/index). You are redirected to the identity provider to sign in, then returned to the resource, which greets you by name.

**If something goes wrong:**

- **"Invalid redirect URI"** at the provider: the registered URI must be the full callback URL, including `?client_name=OidcClient`.
- **The login loops or the state is lost**: an indirect client such as `OidcClient` needs a session. Use `Pac4JGrizzlyFeature` or `Pac4JServletFeature`, not `Pac4JJaxRsFeature`, for the browser login.
- **`@Pac4JProfile` is not injected**: the value factory is missing. Register `Pac4JValueFactoryProvider.Binder` on Jersey, or `Pac4JProfileInjectorFactory` on RESTEasy.
- **401 on the API with a valid token**: the provider rejects the token at the user info endpoint. Check that the token was issued for this provider and carries the `openid` scope.

## Using Dropwizard

What if your application uses Dropwizard? It runs Jersey inside Jetty, so the resource annotations above still apply. The [dropwizard-pac4j](https://github.com/pac4j/dropwizard-pac4j) bundle takes care of step 3: it builds `Config` from a factory named in YAML, registers the servlet and security features and the profile value factory, and enables Jetty sessions.

The bundle below targets Dropwizard 5.0.2, which uses Jersey 3. It already brings `jersey3-pac4j`, so you do not need to add that dependency yourself:

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>dropwizard-pac4j</artifactId>
    <version>8.0.1</version>
</dependency>
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
```

Move the OIDC configuration of step 3 into a `ConfigFactory`. Use the externally reachable callback URL, including any configured application context path:

```java
package org.example.security;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        final var oidcConfiguration = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        return new Config("http://localhost:8080/callback", new OidcClient(oidcConfiguration));
    }
}
```

Reference it in the YAML configuration, under a `pac4j` section:

```yaml
pac4j:
  configFactory: org.example.security.SecurityConfigFactory
```

Expose that section in your configuration class as a `Pac4jFactory` property, and add the bundle to the application:

```java
package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.constraints.NotNull;
import org.pac4j.dropwizard.Pac4jFactory;

public class MyConfiguration extends Configuration {

    @NotNull
    @JsonProperty("pac4j")
    private Pac4jFactory pac4jFactory = new Pac4jFactory();

    public Pac4jFactory getPac4jFactory() {
        return pac4jFactory;
    }
}
```

```java
package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.example.resources.AuthResource;
import org.example.resources.ProtectedResource;
import org.pac4j.dropwizard.Pac4jBundle;
import org.pac4j.dropwizard.Pac4jFactory;

public class MyApplication extends Application<MyConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MyApplication().run(args);
    }

    private final Pac4jBundle<MyConfiguration> pac4j = new Pac4jBundle<>() {
        @Override
        public Pac4jFactory getPac4jFactory(final MyConfiguration configuration) {
            return configuration.getPac4jFactory();
        }
    };

    @Override
    public void initialize(final Bootstrap<MyConfiguration> bootstrap) {
        bootstrap.addBundle(pac4j);
    }

    @Override
    public void run(final MyConfiguration configuration, final Environment environment) {
        environment.jersey().register(AuthResource.class);
        environment.jersey().register(ProtectedResource.class);
    }
}
```

Reuse `AuthResource` and `ProtectedResource`, but set `renewSession = true` on both callbacks: Dropwizard uses servlet sessions.

You can also declare `globalFilters` in the `pac4j` section to protect the whole API, or servlet-level filters for the non-Jersey parts of the application. The [bundle README](https://github.com/pac4j/dropwizard-pac4j#configuring-the-bundle) describes these options.

For the bearer-token API from step 6, set `sessionEnabled: false` and build the `HeaderClient` in the factory. The [dropwizard-pac4j-demo](https://github.com/pac4j/dropwizard-pac4j-demo) brings these cases together, with views, a REST API and servlets in one application.

## Switching to SAML or CAS

For browser login, add `pac4j-saml` or `pac4j-cas`, replace the `OidcClient` and update `@Pac4JSecurity`. Adapt the injected profile type or use `CommonProfile`, and register the protocol-specific callback and logout settings. SAML also needs a keystore and metadata exchange. The bearer-token example is a separate authentication mechanism and is not converted by changing this browser client. The protocol-specific setup is described in the [SAML guide](/how-to-secure-a-java-application-with-saml.html) and the [CAS guide](/how-to-secure-a-java-application-with-cas.html); the demo already includes a CAS login.

## Learn more

- The [jax-rs-pac4j](https://github.com/pac4j/jax-rs-pac4j) library and its [documentation](https://github.com/pac4j/jax-rs-pac4j/wiki), including the Servlet, Grizzly, CDI and sessionless setups.
- The [jax-rs-pac4j-demo](https://github.com/pac4j/jax-rs-pac4j-demo) application, and the [release post on Jersey 4 and RESTEasy 7 support](/blog/what_s_new_in_jax_rs_pac4j_v8.html).
- The [dropwizard-pac4j](https://github.com/pac4j/dropwizard-pac4j) bundle and the [dropwizard-pac4j-demo](https://github.com/pac4j/dropwizard-pac4j-demo) application.
- The documentation for the [OIDC client for Java](/docs/clients/openid-connect.html) for the complete client configuration.
- [Direct OIDC authentication](/docs/clients/openid-connect-clients.html#2-direct-clients) and [JWT validation](/docs/authenticators/jwt.html) for the access token case.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
