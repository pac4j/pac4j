---
layout: guide
title: How to secure a Play application with SAML (using pac4j)
seo_title: "How to secure a Play Framework application with SAML | pac4j"
description: "Add SAML 2.0 single sign-on to a Play Framework Java application with pac4j: sbt dependencies, keystore, Guice module, routes, @Secure actions and logout."
---

In a Play application, we want to protect actions while keeping the usual routes and controllers. The [play-pac4j](https://github.com/pac4j/play-pac4j) library lets us do that with a `@Secure` annotation, a `CallbackController` and a `LogoutController`.

Here, we'll use SAML 2.0. Our application will act as a **service provider**, delegating login to an IdP such as Microsoft Entra ID, Okta, ADFS, Shibboleth or Keycloak. The same integration also supports the other pac4j authentication mechanisms.

We'll work with **Play 3.0 and Java**. The [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) contains the starting point, along with OIDC, CAS, OAuth, form and JWT examples. There is also a [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo) for Scala developers.

If you've read the [Spring Boot SAML guide](/how-to-secure-a-java-application-with-saml.html), you'll recognize the protocol configuration. What changes here is the Guice module, the session store and the way we protect actions.

**What you need:**

- Java 17 or later and sbt
- Play 3.0, with Scala 2.13 or Scala 3
- the **metadata** of your identity provider, as a URL or an XML file. The demo uses the public pac4j test IdP.

## 1) Get the demo

```bash
git clone https://github.com/pac4j/play-pac4j-java-demo.git
cd play-pac4j-java-demo
sbt run
```

The application starts on [http://localhost:9000](http://localhost:9000). The sections below adapt its SAML setup. Replace the existing security module and matching routes rather than registering duplicate bindings. The protected action below returns plain text; you can replace it with a Twirl view that escapes profile values.

## 2) Add the sbt dependencies

Add `play-pac4j` and the SAML module to `build.sbt`. The `%%` selects the artifact for your Scala version: the library is built for Scala 2.13 and 3. The version suffix matters too: `13.0.3-PLAY3.0` is for Play 3.0.

We'll also need Guice for our module and a cache for the session store in step 4.

```scala
libraryDependencies += guice
libraryDependencies += caffeine

val playPac4jVersion = "13.0.3-PLAY3.0"
val pac4jVersion = "6.5.8"

libraryDependencies ++= Seq(
  "org.pac4j" %% "play-pac4j" % playPac4jVersion,
  "org.pac4j" % "pac4j-saml" % pac4jVersion
)
```

Keep the dependencies required by pac4j SAML. If adapting the demo's existing exclusions, check its explicit Spring and Jackson dependencies as well. For Play 2.9 or 2.8, use the matching `-PLAY2.9` or `-PLAY2.8` versions listed in the [play-pac4j README](https://github.com/pac4j/play-pac4j#readme).

## 3) Create the service provider keystore

A SAML service provider signs its requests and decrypts the assertions it receives, so it needs its own key pair. Generate a Java keystore with `keytool` and put it in the `conf` directory, which is on Play's classpath:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -storetype JKS -keystore conf/samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

The demo already ships one. See the [keystore section of the Spring Boot guide](/how-to-secure-a-java-application-with-saml.html#3-create-the-service-provider-keystore) for the details.

## 4) Write the security module

Play uses Guice, so let's put our configuration in a module. It provides the `Config` and its SAML client, and binds the session store and the callback and logout controllers:

```java
package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.play.CallbackController;
import org.pac4j.play.LogoutController;
import org.pac4j.play.store.PlayCacheSessionStore;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import play.Environment;
import play.cache.SyncCacheApi;

public class SecurityModule extends AbstractModule {

    private final String baseUrl;

    public SecurityModule(final Environment environment, final com.typesafe.config.Config configuration) {
        this.baseUrl = configuration.getString("baseUrl");
    }

    @Override
    protected void configure() {
        // where pac4j keeps the login state and the user profile
        final var sessionStore = new PlayCacheSessionStore(getProvider(SyncCacheApi.class));
        bind(SessionStore.class).toInstance(sessionStore);

        // finishes the login when the identity provider posts the assertion back
        final var callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        callbackController.setRenewSession(true);
        bind(CallbackController.class).toInstance(callbackController);

        // logs the user out
        final var logoutController = new LogoutController();
        logoutController.setDefaultUrl("/");
        logoutController.setDestroySession(true);
        bind(LogoutController.class).toInstance(logoutController);
    }

    @Provides @Singleton
    protected SAML2Client provideSaml2Client() {
        // configuration of the authentication via the SAML2 protocol
        final var cfg = new SAML2Configuration();
        cfg.getKeystore().setKeystorePath("resource:samlKeystore.jks");
        cfg.getKeystore().setKeystorePassword("pac4j-demo-passwd");
        cfg.getKeystore().setPrivateKeyPassword("pac4j-demo-passwd");
        cfg.setIdentityProviderMetadataPath("https://www.casserverpac4j.dev/idp/metadata");
        cfg.setServiceProviderEntityId(baseUrl + "/callback?client_name=SAML2Client");
        cfg.setSpLogoutRequestSigned(true);
        cfg.setServiceProviderMetadataPath("file:target/sp-metadata.xml");
        return new SAML2Client(cfg);
    }

    @Provides @Singleton
    protected Config provideConfig(final SAML2Client saml2Client, final SessionStore sessionStore) {
        final var config = new Config(baseUrl + "/callback", saml2Client);
        config.setSessionStoreFactory(p -> sessionStore);
        return config;
    }
}
```

Then enable the module and set the base URL in `conf/application.conf`:

```hocon
play.modules.enabled += "modules.SecurityModule"
baseUrl = "http://localhost:9000"
```

Why do we need a session store? Play has a session cookie, but no server-side session of its own. `PlayCacheSessionStore` keeps the pac4j data in the cache and puts only a session identifier in the cookie. Without a configured store, pac4j fails at startup with an explicit message. You can also use `PlayCookieSessionStore`, which encrypts everything into the cookie and needs no cache.

The `SAML2Configuration` describes our keystore, the IdP metadata, our entity ID and the output path for our SP metadata. These are the same settings explained in the [Spring Boot guide](/how-to-secure-a-java-application-with-saml.html#4-configure-saml-20-authentication).

Finally, `new Config(baseUrl + "/callback", saml2Client)` sets the callback URL. pac4j appends `?client_name=SAML2Client`: the full URL is our **Assertion Consumer Service** address.

## 5) Declare the routes and exchange metadata

Add the callback and logout routes to `conf/routes`, next to the action you want to protect. The `@` prefix tells Play to inject the controllers, so the instances bound in the module are used. The identity provider **posts** the assertion to the callback, so the `POST` route is the one that matters here:

```
GET     /protected/index.html    controllers.Application.protectedIndex(request: Request)

GET     /callback                @org.pac4j.play.CallbackController.callback(request: Request)
+ nocsrf
POST    /callback                @org.pac4j.play.CallbackController.callback(request: Request)
GET     /logout                  @org.pac4j.play.LogoutController.logout(request: Request)
```

The `+ nocsrf` modifier is needed on the callback: the IdP sends a cross-origin POST, which Play's CSRF filter would otherwise reject.

We have configured the IdP metadata, but the IdP also needs ours. When the client initializes, pac4j writes the SP metadata to `target/sp-metadata.xml`. Register this file, or its entity ID and ACS URL, at the IdP. The [metadata exchange section of the Spring Boot guide](/how-to-secure-a-java-application-with-saml.html#5-exchange-metadata-with-your-identity-provider) covers this step.

## 6) Protect the action with @Secure

Annotate the action, or the whole controller, with `@Secure` and name the client to use:

```java
package controllers;

import com.google.inject.Inject;
import java.util.List;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.play.context.PlayFrameworkParameters;
import org.pac4j.play.java.Secure;
import org.pac4j.saml.client.SAML2Client;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Application extends Controller {

    @Inject
    private Config config;

    @Secure(clients = "SAML2Client")
    public Result protectedIndex(final Http.Request request) {
        final var profile = getProfiles(request).get(0);
        return ok("Hello " + profile.getId()).as("text/plain");
    }
}
```

An anonymous request to `/protected/index.html` is redirected to the identity provider; after login, the callback restores the originally requested URL. The annotation also accepts `authorizers`, for example `authorizers = "admin"` for a role check declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`, and `matchers`.

**Prefer URL patterns to annotations?** The `SecurityFilter` protects paths by regular expression from `application.conf`:

```hocon
play.http.filters = "filters.Filters"

pac4j.security.rules = [
  {"/protected/.*" = {
    clients = "SAML2Client"
  }}
]
```

with a `Filters` class that returns `securityFilter.asJava()` in its list, as the demo does. Both mechanisms can coexist.

## 7) Access the authenticated user

Add this helper method to the `Application` controller. It builds the `ProfileManager` through the factories of the `Config`:

```java
private List<UserProfile> getProfiles(final Http.Request request) {
    final var parameters = new PlayFrameworkParameters(request);
    final var context = config.getWebContextFactory().newContext(parameters);
    final var sessionStore = config.getSessionStoreFactory().newSessionStore(parameters);
    final var profileManager = config.getProfileManagerFactory().apply(context, sessionStore);
    return profileManager.getProfiles();
}
```

After a SAML login, we get a `SAML2Profile`. Its identifier comes from the assertion's `NameID`. The attributes keep the names supplied by the IdP, often URNs, and can have several values:

```java
final var profile = (SAML2Profile) getProfiles(request).get(0);
profile.getId();                                            // the NameID
profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.3");  // "mail" in many directories
profile.getAttributes();                                    // everything the IdP sent
```

Map the attributes to readable names once with `cfg.setMappedAttributes(Map.of("urn:oid:0.9.2342.19200300.100.1.3", "email"))`. If a value is missing, the attribute release rule for your SP at the IdP is the place to look.

## 8) Single logout

The `/logout` route only removes the local login. To log out at the IdP and, through it, at the other applications, we need SAML **single logout** (SLO). Add a second controller and its route:

```java
package controllers;

import org.pac4j.play.LogoutController;

public class CentralLogoutController extends LogoutController {

    public CentralLogoutController() {
        setDefaultUrl("http://localhost:9000/");
        setLocalLogout(true);
        setCentralLogout(true);
        setDestroySession(true);
        setLogoutUrlPattern("http://localhost:9000/.*");
    }
}
```

```
GET     /centralLogout           controllers.CentralLogoutController.logout(request: Request)
```

pac4j then sends a `LogoutRequest` to the IdP's single logout endpoint, read from the IdP metadata, and processes the `LogoutResponse` on the callback URL. The configuration above enables logout-request signing with `cfg.setSpLogoutRequestSigned(true)`. SLO only works if the IdP metadata declares a `SingleLogoutService`; pick the binding your IdP expects with `cfg.setSpLogoutRequestBindingType(...)`.

## 9) Run the application

```bash
sbt run
```

Open [http://localhost:9000/protected/index.html](http://localhost:9000/protected/index.html). You are redirected to the identity provider to sign in, then posted back to the callback URL with the assertion, and the protected page shows your profile.

**If something goes wrong:**

- **"Please create a SessionStore and define it in the config"** at startup: `config.setSessionStoreFactory(...)` is missing in the module.
- **The IdP rejects the request as an unknown service provider**: the SP metadata is not registered, or the entity ID at the IdP differs from `setServiceProviderEntityId`.
- **403 on the POST callback**: the `+ nocsrf` modifier is missing on the route.
- **"Authentication issue instant is too old"**: check the clocks and any positive limit configured with `cfg.setMaximumAuthenticationLifetime(seconds)`. In pac4j 6.5.8, the default is `0`, which disables this authentication-age check; assertion validity timestamps are still checked.

## Switching to OIDC or CAS

Add `pac4j-oidc` or `pac4j-cas` to `build.sbt`, provide the corresponding client in the module and pass it to `Config`. Update `@Secure` and any URL rules, adapt profile casts and attribute mappings, and register the protocol-specific callback and logout URLs. The same session store and controller structure apply; the SAML keystore is no longer needed. The protocol-specific setup is described in the [OIDC guide](/how-to-secure-a-java-application-with-oidc.html) and the [CAS guide](/how-to-secure-a-java-application-with-cas.html).

## Learn more

- The [play-pac4j](https://github.com/pac4j/play-pac4j) library and its [documentation](https://github.com/pac4j/play-pac4j/wiki), including the `Security` trait for Scala controllers and the Twirl template helper.
- The [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) and [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo) applications, with many authentication mechanisms.
- The [SAML 2.0 reference](/docs/clients/saml.html) for bindings, signature algorithms, attribute converters and IdP-specific notes.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
