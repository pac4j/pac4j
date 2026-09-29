---
permalink: /how-to-secure-a-vertx-application-with-cas.html
layout: guide
title: How to secure a Vert.x client application with CAS (using pac4j)
seo_title: "How to secure a Vert.x Web client application with CAS | pac4j"
description: "Add CAS single sign-on to a Vert.x Web application with pac4j: session handler, CasClient, a SecurityHandler on a route, callback and logout handlers."
---

Let's connect a Vert.x Web application to a CAS server. We'll use the [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) library to add three handlers to our router: `SecurityHandler`, `CallbackHandler` and `LogoutHandler`.

The CAS server handles the login page and authenticates the user. It can be the [Apereo](https://apereo.github.io/cas/) server already used by your organization, or the public pac4j test server we'll use below.

The example uses **vertx-pac4j v7.1.0, Vert.x v5 and Java 17**. The CAS client is configured just as in the [Spring Boot CAS guide](/how-to-secure-a-java-application-with-cas.html). The application installs the session handler before pac4j runs and the pac4j handlers take care of moving blocking authentication work off the event loop.

For a more complete example, see the [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo).


## 1) Create the project

Start from an empty Maven project with Java 17 or later:

```bash
mkdir -p vertx-cas-app/src/main/java/org/example
cd vertx-cas-app
```

Create a `pom.xml` at the project root. The compiler targets Java 17, and the exec plugin will run the `App` class we write in section 7:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>vertx-cas-app</artifactId>
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

Inside the `<dependencies>` element, add Vert.x Web, the Vert.x integration for the handlers and the CAS module for the client:

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>5.2.0</version>
</dependency>
<!-- pac4j integration for Vert.x -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>vertx-pac4j</artifactId>
    <version>7.1.0</version>
</dependency>
<!-- pac4j support for CAS -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>6.5.9</version>
</dependency>
```

`vertx-pac4j` v7.1 targets Vert.x v5 and pac4j v6.


## 3) Write the security configuration

Create `src/main/java/org/example/SecurityConfigFactory.java`. It builds the pac4j `Config` with the CAS client:

```java
package org.example;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        // configuration of the authentication via the CAS protocol
        final var casConfiguration = new CasConfiguration("https://www.casserverpac4j.dev/login");
        return new Config("http://localhost:8080/callback", new CasClient(casConfiguration));
    }
}
```

The only mandatory CAS setting is the login URL. pac4j derives the ticket validation URL from it and uses CAS 3.0 by default, so the validation response can include user attributes.

On the server side, CAS must recognize our application as a service. Register the full callback URL, including the `?client_name=CasClient` suffix added by pac4j, as a CAS service in your CAS server. The [service registration section of the Spring Boot guide](/how-to-secure-a-java-application-with-cas.html#4-register-the-service-on-the-cas-server) shows how.


## 4) Wire the handlers into the router

Create `src/main/java/org/example/ServerVerticle.java`. We install the session handler first, then the pac4j handlers on their routes. Add the `protectedPage` helper from section 5 inside this class before compiling:

```java
package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.vertx.VertxProfileManager;
import org.pac4j.vertx.VertxWebContext;
import org.pac4j.vertx.context.session.VertxSessionStore;
import org.pac4j.vertx.handler.impl.CallbackHandler;
import org.pac4j.vertx.handler.impl.CallbackHandlerOptions;
import org.pac4j.vertx.handler.impl.LogoutHandler;
import org.pac4j.vertx.handler.impl.LogoutHandlerOptions;
import org.pac4j.vertx.handler.impl.SecurityHandler;
import org.pac4j.vertx.handler.impl.SecurityHandlerOptions;

public class ServerVerticle extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) {
        final var router = Router.router(vertx);

        // the Vert.x session, and the pac4j session store on top of it
        final var vertxSessionStore = LocalSessionStore.create(vertx);
        router.route().handler(SessionHandler.create(vertxSessionStore));
        final var sessionStore = new VertxSessionStore(vertxSessionStore);

        final var config = new SecurityConfigFactory().build();

        // the CAS login protects /protected
        final var securityOptions = new SecurityHandlerOptions().setClients("CasClient");
        router.get("/protected").handler(new SecurityHandler(vertx, sessionStore, config, securityOptions));
        router.get("/protected").handler(rc -> protectedPage(rc, sessionStore));

        // the CAS server redirects the user here with the service ticket
        final var callbackOptions = new CallbackHandlerOptions().setDefaultUrl("/").setRenewSession(true);
        final var callback = new CallbackHandler(vertx, sessionStore, config, callbackOptions);
        router.get("/callback").handler(callback);
        router.post("/callback").handler(BodyHandler.create().setMergeFormAttributes(true));
        router.post("/callback").handler(callback);

        // logs the user out
        final var logoutOptions = new LogoutHandlerOptions().setDefaultUrl("/").setDestroySession(true);
        router.get("/logout").handler(new LogoutHandler(vertx, sessionStore, logoutOptions, config));

        router.get("/").handler(rc -> rc.response().end("<a href='/protected'>Protected area</a>"));

        vertx.createHttpServer().requestHandler(router).listen(8080)
            .onSuccess(server -> startPromise.complete())
            .onFailure(startPromise::fail);
    }
}
```

Here are the main points in this setup:

- **Session handling:** `SessionHandler` must run before pac4j on the protected, callback and logout routes. `VertxSessionStore` reads and writes that session.

- **Session-store configuration:** since v7.1.0, the `SecurityHandler`, `CallbackHandler` and `LogoutHandler` constructors register their supplied session store in the `Config` when no factory is configured. There is no need to call `config.setSessionStoreFactoryIfUndefined(...)`; an explicitly configured factory is preserved.

- **Direct clients:** keep `SessionHandler` on their routes too if they should reuse an existing login. Since pac4j v5, security logic checks the session profiles before attempting direct authentication. Without the session handler, that reuse is unavailable even when the browser sends its session cookie.

- **Protected routes:** `SecurityHandler` redirects anonymous users to the configured CAS server. For authenticated users, it sets the Vert.x user and passes control to our handler. `SecurityHandlerOptions` also accepts `setAuthorizers` and `setMatchers`. For example, `"admin"` can refer to a role check declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`.

- **Login callback:** `CallbackHandler` validates the CAS service ticket, saves the profile and redirects to the requested page or the default URL. `setRenewSession(true)` renews the session identifier to protect against session fixation. The callback's `POST` route and its `BodyHandler` also receive CAS single logout notifications when the user logs out elsewhere.

- **Local logout:** `LogoutHandler` removes the profile. With `setDestroySession(true)`, it destroys the Vert.x session as well.

- **Event loop:** register all three pac4j handlers with `.handler(...)`. Since v7.1.0, `SecurityHandler` also runs synchronous pac4j logic through `executeBlocking`, then resumes the route on its original Vert.x context. The callback and logout handlers already offload their logic. No `blockingHandler` wrapper is needed.

- **Multiple instances:** use a `ClusteredSessionStore` with clustered Vert.x instances. CAS single logout also needs shared ticket-to-session mappings: configure `DefaultSessionLogoutHandler` with a `VertxClusteredMapStore<String, Object>`. Sharing HTTP sessions alone does not share those mappings. The [session stores and clustering guide](https://github.com/pac4j/vertx-pac4j/wiki/Session-stores-and-clustering) shows both parts of the configuration.

## 5) Access the authenticated user

Add this helper method to `ServerVerticle`. It reads the profile from the `RoutingContext` and session store and returns plain text:

```java
private static void protectedPage(final RoutingContext rc, final VertxSessionStore sessionStore) {
    final var profileManager = new VertxProfileManager(new VertxWebContext(rc), sessionStore);
    final var profile = (CasProfile) profileManager.getProfiles().get(0);
    rc.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
        .end("Hello " + profile.getId() + "\nAttributes: " + profile.getAttributes()
            + "\nVisit /logout to sign out.");
}
```

The identifier of a `CasProfile` is the CAS principal, usually the username, and its attributes are exactly those released by the service's attribute release policy on the CAS server. An empty attribute map can mean that the service does not release the expected attributes.

You can also stay with the Vert.x API: on a protected route, `rc.user()` is a `Pac4jUser`, and its `profiles()` method returns the same list of profiles.


## 6) Logout

Our `/logout` route ends the local session. To also end the SSO session on the CAS server, add a second handler with central logout enabled inside `ServerVerticle.start`, before starting the HTTP server:

```java
final var centralLogoutOptions = new LogoutHandlerOptions()
    .setDefaultUrl("http://localhost:8080/")
    .setLogoutUrlPattern("http://localhost:8080/.*")
    .setLocalLogout(true)
    .setDestroySession(true)
    .setCentralLogout(true);
router.get("/centralLogout").handler(new LogoutHandler(vertx, sessionStore, centralLogoutOptions, config));
```

pac4j clears the local profile and redirects the browser to CAS `/logout`. CAS must allow the requested return URL. `setLogoutUrlPattern` validates an optional dynamic `url` parameter (`setDefaultUrl` selects the default return URL).

CAS can also send a back-channel logout notification to `/callback`, without the browser's session cookie. pac4j uses its ticket-to-session mapping to find the session. In v7.1.0, `VertxSessionStore` persists profile removal or session deletion to the underlying store before that operation completes, including for clustered sessions.

To destroy the whole session on a CAS notification, configure `setDestroySession(true)` on the `DefaultSessionLogoutHandler`. This is different from `LogoutHandlerOptions.setDestroySession(true)`, which controls our `/logout` route.


## 7) Run the application

Create `src/main/java/org/example/App.java` to deploy the verticle:

```java
package org.example;

import io.vertx.core.Vertx;
import java.util.concurrent.CountDownLatch;

public class App {

    public static void main(final String[] args) throws Exception {
        final var vertx = Vertx.vertx();
        try {
            vertx.deployVerticle(new ServerVerticle())
                .toCompletionStage().toCompletableFuture().get();
            new CountDownLatch(1).await();
        } finally {
            vertx.close().toCompletionStage().toCompletableFuture().get();
        }
    }
}
```

The main method waits for deployment to succeed, then stays alive so `exec:java` keeps the application running. From the directory containing `pom.xml`, run:

```bash
mvn clean compile exec:java
```

Open [http://localhost:8080/protected](http://localhost:8080/protected). You are redirected to the CAS login page, then sent back with a service ticket, and the protected page shows your profile.

If CAS answers "Application Not Authorized to Use CAS", the service is not registered or its pattern does not match the full callback URL.

If the login loops or loses its state, the `SessionHandler` is missing or registered after the pac4j handler: it must come first, on the protected route and on the callback.


## 8) Switching to OIDC or SAML

Add `pac4j-oidc` or `pac4j-saml`, replace the `CasClient` and update `SecurityHandlerOptions`. Adapt the profile cast and attribute mapping, register callback/logout URLs, and keep body parsing on the callback for SAML POST responses. The SAML setup also needs a keystore and metadata exchange.

The protocol-specific setup is described in the [OIDC guide](/docs/clients/openid-connect.html) and the [SAML guide](/docs/clients/saml.html). A CAS server can also be configured as an OIDC or SAML provider.


## 9) Learn more

- The [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) library and its [documentation](https://github.com/pac4j/vertx-pac4j/wiki), and the [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo) application.
- The documentation for the [CAS client for Java](/docs/clients/cas.html) for proxy tickets, the CAS REST API and all the `CasConfiguration` options.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
