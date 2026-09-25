---
layout: guide
title: How to secure a Vert.x application with CAS (using pac4j)
seo_title: "How to secure a Vert.x Web application with CAS | pac4j"
description: "Add CAS single sign-on to a Vert.x Web application with pac4j: session handler, CasClient, a SecurityHandler on a route, callback and logout handlers."
---

Let's connect a Vert.x Web application to a CAS server. We'll use the [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) library to add three handlers to our router: `SecurityHandler`, `CallbackHandler` and `LogoutHandler`.

The CAS server handles the login page and authenticates the user. It can be the [Apereo](https://apereo.github.io/cas/) server already used by your organization, or the public pac4j test server we'll use below.

The example uses **Vert.x 5 and Java 17**. The CAS client is configured just as in the [Spring Boot CAS guide](/how-to-secure-a-java-application-with-cas.html), but there are two Vert.x details to get right: the session must be available before pac4j runs, and blocking authentication work must stay off the event loop.

We'll look at both. For a broader example, see the [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo). To use your own CAS server, you'll need its login URL.

## 1) Get the demo

```bash
git clone https://github.com/pac4j/vertx-pac4j-demo.git
cd vertx-pac4j-demo
mvn clean package
java -jar target/vertx-pac4j-demo-*-fat.jar
```

The application starts on [http://localhost:8080](http://localhost:8080). The sections below extract the CAS part of this demo into a minimal verticle.

## 2) Add the Maven dependencies

Alongside Vert.x Web, add the Vert.x integration for the handlers and the CAS module for the client:

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>5.1.5</version>
</dependency>
<!-- pac4j integration for Vert.x -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>vertx-pac4j</artifactId>
    <version>7.0.3</version>
</dependency>
<!-- pac4j support for CAS -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>6.5.8</version>
</dependency>
```

`vertx-pac4j` 7 targets Vert.x 5 and pac4j 6.

## 3) Write the security configuration

Build the pac4j `Config` with the CAS client, in a `ConfigFactory` or directly in the verticle:

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

On the server side, CAS must recognize our application as a service. Register the full callback URL, including the `?client_name=CasClient` suffix added by pac4j. The [service registration section of the Spring Boot guide](/how-to-secure-a-java-application-with-cas.html#4-register-the-service-on-the-cas-server) shows how.

## 4) Wire the handlers into the router

Now for the Vert.x part. We install the session handler first, then the pac4j handlers on their routes:

```java
package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.pac4j.cas.client.CasClient;
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
        config.setSessionStoreFactoryIfUndefined(parameters -> sessionStore);

        // the CAS login protects /protected
        final var securityOptions = new SecurityHandlerOptions().setClients("CasClient");
        router.get("/protected").blockingHandler(new SecurityHandler(vertx, sessionStore, config, securityOptions), false);
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

The order matters here. `SessionHandler` must run before pac4j on every route involved in login. `VertxSessionStore` reads and writes that session, and `setSessionStoreFactoryIfUndefined` tells the `Config` to use it. If you run several instances, use a `ClusteredSessionStore` instead of the local one.

On a protected route, `SecurityHandler` redirects anonymous users to CAS without calling `next()`. For authenticated users, it sets the Vert.x user and passes control to our handler. `SecurityHandlerOptions` also accepts `setAuthorizers` and `setMatchers`. For example, `"admin"` can refer to a role check declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`.

When CAS sends the browser back, `CallbackHandler` validates the service ticket, saves the profile and redirects to the requested page or the default URL. `setRenewSession(true)` renews the session identifier to protect against session fixation. The callback's `POST` route and its `BodyHandler` also receive CAS single logout notifications when the user logs out elsewhere.

`LogoutHandler` removes the profile. With `setDestroySession(true)`, it destroys the Vert.x session as well.

Notice the `blockingHandler` registration for `SecurityHandler`: this handler calls synchronous pac4j logic directly. The callback and logout handlers already use `executeBlocking` internally. This is how we keep ticket validation and other blocking work off the event loop. Central logout itself redirects the browser to CAS.

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

Our `/logout` route ends the local session. To also end the SSO session on the CAS server, add a second handler with central logout enabled:

```java
final var centralLogoutOptions = new LogoutHandlerOptions()
    .setDefaultUrl("http://localhost:8080/")
    .setLogoutUrlPattern("http://localhost:8080/.*")
    .setLocalLogout(true)
    .setDestroySession(true)
    .setCentralLogout(true);
router.get("/centralLogout").handler(new LogoutHandler(vertx, sessionStore, centralLogoutOptions, config));
```

pac4j clears the local profile and redirects the browser to CAS `/logout`. CAS must allow the requested return URL. `setLogoutUrlPattern` validates an optional dynamic `url` parameter; `setDefaultUrl` selects the default return URL.

## 7) Run the application

Deploy the verticle from a `main` method with `vertx.deployVerticle(new ServerVerticle())`, or package a fat jar as the demo does, and open [http://localhost:8080/protected](http://localhost:8080/protected). You are redirected to the CAS login page, then sent back with a service ticket, and the protected page shows your profile.

If CAS answers "Application Not Authorized to Use CAS", the service is not registered or its pattern does not match the full callback URL. If the login loops or loses its state, the `SessionHandler` is missing or registered after the pac4j handler: it must come first, on the protected route and on the callback.

## Switching to OIDC or SAML

Add `pac4j-oidc` or `pac4j-saml`, replace the `CasClient` and update `SecurityHandlerOptions`. Adapt the profile cast and attribute mapping, register callback/logout URLs, and keep body parsing on the callback for SAML POST responses. The SAML setup also needs a keystore and metadata exchange. The protocol-specific setup is described in the [OIDC guide](/how-to-secure-a-java-application-with-oidc.html) and the [SAML guide](/how-to-secure-a-java-application-with-saml.html). A CAS server can also be configured as an OIDC or SAML provider; choose the protocol required by your deployment.

## Learn more

- The [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) library and its [documentation](https://github.com/pac4j/vertx-pac4j/wiki), and the [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo) application.
- The [CAS reference](/docs/clients/cas.html) for proxy tickets, the CAS REST API and all the `CasConfiguration` options.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
