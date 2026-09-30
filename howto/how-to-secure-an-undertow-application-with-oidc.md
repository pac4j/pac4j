---
permalink: /how-to-secure-an-undertow-application-with-oidc.html
layout: guide
title: How to secure an Undertow client application with OIDC (using pac4j)
seo_title: "How to secure an Undertow application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) login to an Undertow application with pac4j: session attachment handler, OidcClient, a SecurityHandler on a path, callback and logout handlers."
---

You can connect an Undertow application to an OpenID Connect provider. You just need to use the [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) library to add three handlers to your `PathHandler`: `SecurityHandler`, `CallbackHandler` and `LogoutHandler`.

The OIDC provider handles the login page and authenticates the user. It can be Keycloak, Google, Microsoft Entra ID, Okta or another OIDC server, or the public pac4j demo provider we'll use below.

This example uses **undertow-pac4j v6.1.0, Undertow v2.4 and Java 17**. The OIDC client is configured just as in the [Spring Boot OIDC guide](/how-to-secure-a-java-application-with-oidc.html).

For a more complete example, see the [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo).


## 1) Create the Maven project

Start from an empty Maven project with Java 17 or later:

```bash
mkdir -p undertow-oidc-app/src/main/java/org/example
cd undertow-oidc-app
```

Create a `pom.xml` at the project root. The compiler targets Java 17, and the exec plugin will run the `App` class we write in section 4:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>undertow-oidc-app</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Add the dependencies from section 2 here. -->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.16.0</version>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.6.4</version>
                <configuration>
                    <mainClass>org.example.App</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


## 2) Add the Maven dependencies

Inside the `<dependencies>` element, add Undertow, the Undertow integration for the handlers and the OpenID Connect module for the client:

```xml
<dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-core</artifactId>
    <version>2.4.3.Final</version>
</dependency>
<!-- pac4j integration for Undertow -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>undertow-pac4j</artifactId>
    <version>6.1.0</version>
</dependency>
<!-- pac4j support for OpenID Connect -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oidc</artifactId>
    <version>6.5.9</version>
</dependency>
```

`undertow-pac4j` v6.1 targets Undertow v2.4 and pac4j v6. It declares `undertow-core` in the `provided` scope, so your application must bring its own Undertow dependency.


## 3) Write the security configuration

Create `src/main/java/org/example/SecurityConfigFactory.java`. It builds the pac4j `Config` with the OIDC client:

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

There is nothing specific to Undertow in this configuration and this is the beauty of pac4j!

pac4j reads the provider metadata (endpoints, algorithms, etc.) from the discovery URI. The callback URL, with `?client_name=OidcClient` appended by pac4j, is the redirect URI to register at your provider.

One demo setting should be removed when using your own provider: `setAllowUnsignedIdTokens(true)`. It is only here because the public demo server issues unsigned ID tokens.


## 4) Wire the handlers and start the server

Create `src/main/java/org/example/App.java`. We register the pac4j handlers on their paths, then wrap the whole `PathHandler` with the session handler. Add the `protectedPage` helper from section 5 inside this class before compiling:

```java
package org.example;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.util.Headers;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.undertow.account.Pac4jAccount;
import org.pac4j.undertow.handler.CallbackHandler;
import org.pac4j.undertow.handler.LogoutHandler;
import org.pac4j.undertow.handler.SecurityHandler;

public class App {

    public static void main(final String[] args) {
        final var config = new SecurityConfigFactory().build();
        final var path = new PathHandler();

        // the OIDC login protects /protected
        path.addExactPath("/protected", SecurityHandler.build(App::protectedPage, config, "OidcClient"));

        // the provider redirects the user here after login
        path.addExactPath("/callback", CallbackHandler.build(config, "/", true, null, null));

        // logs the user out
        final var logout = new LogoutHandler(config, "/");
        logout.setDestroySession(true);
        path.addExactPath("/logout", logout);

        path.addExactPath("/", exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
            exchange.getResponseSender().send("<a href='/protected'>Protected area</a>");
        });

        // the Undertow session, around all the routes
        final var sessionHandler = new SessionAttachmentHandler(path,
            new InMemorySessionManager("sessions"), new SessionCookieConfig().setHttpOnly(true));

        Undertow.builder()
            .addHttpListener(8080, "localhost")
            .setHandler(sessionHandler)
            .build()
            .start();
    }
}
```

Here are the main points in this setup:

- **Session handling:** `SessionAttachmentHandler` must wrap the protected, callback and logout paths: it attaches the Undertow `SessionManager` and `SessionConfig` to each request, and pac4j's `UndertowSessionStore` reads and writes the session through them. Here the session cookie is `JSESSIONID`, marked `HttpOnly`.

- **Default components:** undertow-pac4j provides a `FrameworkAdapterImpl` that pac4j discovers on the classpath. Its `applyDefaultSettingsIfUndefined` method supplies the Undertow-specific web context, session store and profile manager factories, along with the HTTP action adapter, as defaults in the `Config`. Explicitly configured components are preserved.

- **Protected paths:** `SecurityHandler.build` wraps our handler: it redirects anonymous users to the OIDC provider, and for authenticated users, it calls our handler. The other `build` variants also accept authorizers and matchers. For example, `SecurityHandler.build(App::protectedPage, config, "OidcClient", "admin")` can refer to a role check declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`.

- **Login callback:** `CallbackHandler` receives the authorization code, exchanges it for an access token and an ID token, validates the ID token, saves the profile and redirects to the requested page or the default URL (`"/"`). The third argument (`true`) renews the session identifier to protect against session fixation. The same path also accepts the `POST` requests of the OIDC `form_post` response mode, while the default code flow returns by `GET`.

- **Local logout:** `LogoutHandler` removes the profile. With `setDestroySession(true)`, it invalidates the Undertow session as well.

- **IO threads:** `SecurityHandler.build` and `CallbackHandler.build` return handlers already wrapped in a `BlockingHandler` and a form parser: the pac4j logic and our protected handler run on a worker thread, in blocking mode, and `POST` parameters are available. `LogoutHandler` dispatches itself to a worker thread. So no extra `BlockingHandler` wrapper is needed.

- **Error pages:** the `401`, `403` and error responses have an empty body. To display your own pages, add a default response listener in front of the `PathHandler`, like the `ErrorHandler` of the [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo).

- **Multiple instances:** `InMemorySessionManager` keeps the sessions in the memory of one server. Behind a load balancer, use sticky sessions or a distributed `SessionManager`.


## 5) Access the authenticated user

Add this helper method to `App`. It reads the profile from the Undertow security context and returns plain text:

```java
private static void protectedPage(final HttpServerExchange exchange) {
    final var account = (Pac4jAccount) exchange.getSecurityContext().getAuthenticatedAccount();
    final var profile = (OidcProfile) account.getProfile();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
    exchange.getResponseSender().send("Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")"
        + "\nVisit /logout to sign out.");
}
```

When pac4j loads the profiles, it registers a `Pac4jAccount` as the authenticated account of the request. Its principal is the user identifier, its roles are those of the profiles, and `getProfiles()` returns all the profiles.

The `OidcProfile` gives us getters for the standard claims, plus `getIdTokenString()` for the raw ID token and `getAccessToken()` for the access token. The claims depend on the requested scopes, which default to `openid profile email` (but this is configurable).

You can also directly use the pac4j API: `new UndertowProfileManager(new UndertowWebContext(exchange), new UndertowSessionStore(exchange)).getProfiles()` returns the same list of profiles, even on a path that is not protected.


## 6) Logout

The user can be logged out of the Undertow application and still have a session at the identity provider. Our `/logout` path handles the first part, the **local logout**. For central logout as well, add a second handler inside `App.main`, before starting the server:

```java
final var centralLogout = new LogoutHandler(config, "http://localhost:8080/", "http://localhost:8080/.*");
centralLogout.setLocalLogout(true);
centralLogout.setDestroySession(true);
centralLogout.setCentralLogout(true);
path.addExactPath("/centralLogout", centralLogout);
```

For a provider supporting OIDC logout, pac4j clears the local profile and redirects to its `end_session_endpoint`. Register `http://localhost:8080/` as an allowed post-logout redirect URI. The logout URL pattern (third constructor argument) validates an optional dynamic `url` parameter (the second argument is the default return URL).


## 7) Run the application

The `main` method returns once the server is started: the Undertow worker threads keep the application running. From the directory containing `pom.xml`, run:

```bash
mvn clean compile exec:java
```

Open [http://localhost:8080/protected](http://localhost:8080/protected). You are redirected to the identity provider to sign in, then returned to the protected page, which greets you by name.

If the provider rejects the redirect URI, register the full callback URL including `?client_name=OidcClient`.

If the request fails with "No Undertow session manager or session config found in the exchange", the `SessionAttachmentHandler` is missing: it must wrap the protected path and the callback.


## 8) Switching to SAML or CAS

Add the `pac4j-saml` or `pac4j-cas` module, replace the `OidcClient` in `Config` and update the clients passed to `SecurityHandler.build`. Adapt the `OidcProfile` type and provider attributes, and register callback/logout URLs for the selected protocol. The callback already parses the form body of SAML POST responses and CAS logout requests. SAML also needs a keystore and metadata exchange.

The protocol-specific setup is described in the [SAML documentation](/docs/clients/saml.html) and the [CAS documentation](/docs/clients/cas.html).


## 9) Learn more

- The [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) library and its [documentation](https://github.com/pac4j/undertow-pac4j/wiki), and the [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo) application.
- The documentation for the [OIDC client for Java](/docs/clients/openid-connect.html) for client configuration and provider options.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
