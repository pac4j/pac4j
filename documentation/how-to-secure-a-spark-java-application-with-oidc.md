---
layout: content
title: How to secure a Spark Java application with OIDC (using pac4j)
seo_title: "How to secure a Spark Java application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) login to a Spark Java application with pac4j: a SecurityFilter on a before filter, callback and logout routes, user profile."
---

# How to secure a Spark Java application with OIDC (using pac4j)

You have a few Spark Java routes and want to require an OIDC login before users can reach some of them. Spark's `before` filters are a natural place to do this.

The [spark-pac4j](https://github.com/pac4j/spark-pac4j) library gives us a `SecurityFilter` for these checks, a `CallbackRoute` for the return from the provider and a `LogoutRoute`. We'll use them with OpenID Connect, whether your provider is Keycloak, Google, Microsoft Entra ID, Okta or another OIDC server.

We'll use **Spark 2.9 and Java 17**, starting with the public pac4j demo provider. The OIDC configuration is the same as in the [Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html), so the part to look at here is how we connect it to Spark. The [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo) contains a more complete application.

For a Javalin application, see the [Javalin guide](/how-to-secure-a-javalin-application-with-saml.html), which demonstrates SAML with a similar route-based integration.

## 1) Get the demo

```bash
git clone https://github.com/pac4j/spark-pac4j-demo.git
cd spark-pac4j-demo
mvn clean compile exec:java
```

The application starts on [http://localhost:8080](http://localhost:8080). The sections below adapt the demo into a minimal OIDC setup. Keep its Maven Exec plugin configuration, or set `-Dexec.mainClass=org.example.App` when running the `App` class below.

## 2) Add the Maven dependencies

Add the Spark integration and the OpenID Connect module alongside Spark. One connects pac4j to the framework; the other handles the protocol.

```xml
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-core</artifactId>
    <version>2.9.4</version>
</dependency>
<!-- pac4j integration for Spark Java -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>spark-pac4j</artifactId>
    <version>6.0.0</version>
</dependency>
<!-- pac4j support for OpenID Connect -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.8</version>
</dependency>
```

`spark-pac4j` 6 targets pac4j 6 and Spark 2.9, which is a `javax.servlet` framework: the integration brings the matching `pac4j-javaee` module itself.

## 3) Write the security configuration

Build the pac4j `Config` with the OIDC client, in a `ConfigFactory` or directly in your main class:

```java
package org.example;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        // configuration of the authentication via the OpenID Connect protocol
        final var oidcConfiguration = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        return new Config("http://localhost:8080/callback", new OidcClient(oidcConfiguration));
    }
}
```

There is nothing Spark-specific in this configuration. The callback URL, with `?client_name=OidcClient` appended by pac4j, is the redirect URI to register at your provider. The [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider) covers Keycloak, Google and Entra ID.

One demo setting must go when using your own provider: `setAllowUnsignedIdTokens(true)`. It is only here because the public demo server issues unsigned ID tokens.

## 4) Wire the filter and the routes into Spark

Now let's register the filter and routes. We can do all of this in the main class with the static methods of `spark.Spark`:

```java
package org.example;

import java.util.List;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.LogoutRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkFrameworkParameters;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class App {

    public static void main(final String[] args) {
        port(8080);
        final var config = new SecurityConfigFactory().build();
        // applies the Spark defaults (web context, session, HTTP actions) to the configuration
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        get("/", (request, response) -> "<a href='/protected'>Protected area</a>");

        // the OIDC login protects /protected
        before("/protected", new SecurityFilter(config, "OidcClient"));
        get("/protected", (request, response) -> protectedPage(request, response, config));

        // the provider redirects the user here after login
        final var callback = new CallbackRoute(config, "/", true);
        get("/callback", callback);
        post("/callback", callback);

        // logs the user out
        final var logout = new LogoutRoute(config, "/");
        logout.setDestroySession(true);
        get("/logout", logout);
    }
}
```

`SecurityFilter` runs before our route. If the user is anonymous, it redirects to the provider and halts the request. Otherwise, the route can run. The second argument names the client; the optional `authorizers` and `matchers` arguments let you refine access control. For instance, `new SecurityFilter(config, "OidcClient", "admin")` uses an authorizer declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`.

After login, `CallbackRoute` validates the ID token, saves the profile in the session and sends the user back to the requested URL. Its second argument supplies a default URL, and the third enables session renewal to protect against session fixation. The `POST` route is for the OIDC `form_post` response mode; the default code flow returns by GET.

For logout, `LogoutRoute` removes the profile. We also use `setDestroySession(true)` to invalidate the session.

Spark runs on Jetty, and pac4j uses the Jetty session through its servlet session store, so nothing has to be configured for the session.

## 5) Access the authenticated user

Add these helper methods to `App`. They build the `ProfileManager` through the factories of the `Config` and display the profile as plain text:

```java
private static List<UserProfile> getProfiles(final Request request, final Response response, final Config config) {
    final var parameters = new SparkFrameworkParameters(request, response);
    final var context = config.getWebContextFactory().newContext(parameters);
    final var sessionStore = config.getSessionStoreFactory().newSessionStore(parameters);
    return config.getProfileManagerFactory().apply(context, sessionStore).getProfiles();
}

private static String protectedPage(final Request request, final Response response, final Config config) {
    final var profile = (OidcProfile) getProfiles(request, response, config).get(0);
    response.type("text/plain; charset=UTF-8");
    return "Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")"
        + "\nVisit /logout to sign out.";
}
```

The `OidcProfile` gives us getters for the standard claims, plus `getIdToken()` and `getAccessToken()` for the raw tokens. The claims depend on the requested scopes, which default to `openid profile email`.

You can also create the context directly with `new SparkWebContext(request, response)`.

## 6) Logout

The user can be logged out of Spark and still have a session at the identity provider. Our `/logout` route handles the first part, the **local logout**. For central logout as well, add a second route:

```java
final var centralLogout = new LogoutRoute(config);
centralLogout.setDefaultUrl("http://localhost:8080/");
centralLogout.setLogoutUrlPattern("http://localhost:8080/.*");
centralLogout.setLocalLogout(true);
centralLogout.setCentralLogout(true);
centralLogout.setDestroySession(true);
get("/centralLogout", centralLogout);
```

For a provider supporting OIDC logout, pac4j clears the local profile and redirects to its `end_session_endpoint`. Register `http://localhost:8080/` as an allowed post-logout redirect URI. `setLogoutUrlPattern` validates an optional dynamic `url` parameter; it does not set the return URL.

## 7) Run the application

```bash
mvn clean compile exec:java
```

Open [http://localhost:8080/protected](http://localhost:8080/protected). You are redirected to the identity provider to sign in, then returned to the protected page, which greets you by name.

If the provider rejects the redirect URI, register the full callback URL including `?client_name=OidcClient`. If a route runs for anonymous users, the `before` path does not match it: Spark matches `before("/protected")` and `before("/protected/*")` separately.

## Switching to SAML or CAS

Add `pac4j-saml` or `pac4j-cas`, replace the `OidcClient` in `Config` and update the `SecurityFilter` client name. Adapt the `OidcProfile` cast and provider attributes, and register callback/logout URLs for the selected protocol. SAML also needs a keystore and metadata exchange. The protocol-specific setup is described in the [SAML guide](/how-to-secure-a-java-application-with-saml.html) and the [CAS guide](/how-to-secure-a-java-application-with-cas.html).

## Learn more

- The [spark-pac4j](https://github.com/pac4j/spark-pac4j) library and its [documentation](https://github.com/pac4j/spark-pac4j/wiki).
- The [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo) application, with many authentication mechanisms.
- The [Javalin guide](/how-to-secure-a-javalin-application-with-saml.html) for a similar integration using SAML.
- The [OpenID Connect reference](/docs/clients/openid-connect.html) for client configuration and provider options.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
