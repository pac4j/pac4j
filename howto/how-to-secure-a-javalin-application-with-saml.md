---
permalink: /how-to-secure-a-javalin-application-with-saml.html
layout: guide
title: How to secure a Javalin client application with SAML (using pac4j)
seo_title: "How to secure a Javalin client application with SAML | pac4j"
description: "Add SAML 2.0 single sign-on to a Javalin application with pac4j: keystore, SAML2Client, a SecurityHandler on a before handler, callback and logout handlers."
---

In Javalin, we define routes and add handlers around them. So where should SAML authentication go? In a handler that runs before the protected route.

For that, the [javalin-pac4j](https://github.com/pac4j/javalin-pac4j) library provides that `SecurityHandler`, along with a `CallbackHandler` and a `LogoutHandler`. With these three components, our application becomes a **SAML 2.0 service provider**. Users can then sign in through Microsoft Entra ID, Okta, ADFS, Shibboleth, Keycloak or another SAML identity provider.

We'll use **Javalin v7 and Java 17**, with the public pac4j test IdP. For your own IdP, you'll need its metadata. The SAML settings are the same as in any other pac4j implementation, except that here, we'll focus on the Javalin handlers.

You can find more authentication mechanisms in the library's [example application](https://github.com/pac4j/javalin-pac4j/blob/master/src/test/java/org/pac4j/javalin/example/JavalinPac4jExample.java). If you're coming from Spark Java, the [Spark guide](/how-to-secure-a-spark-java-application-with-oidc.html) shows a similar approach with OIDC.


## 1) Add the Maven dependencies

We need two pac4j dependencies alongside Javalin: `javalin-pac4j` for the handlers, and `pac4j-saml` for the protocol support. The SAML module relies on the popular OpenSAML library.

```xml
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin</artifactId>
    <version>7.0.1</version>
</dependency>
<!-- pac4j integration for Javalin -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>javalin-pac4j</artifactId>
    <version>8.0.1</version>
</dependency>
<!-- pac4j support for SAML2 -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-saml</artifactId>
    <version>6.5.9</version>
</dependency>
```

`javalin-pac4j` v8 targets Javalin v7 and pac4j v6. Version 7 is the equivalent for Javalin v5.6.


## 2) Create the keystore and write the security configuration

A SAML service provider needs its own key pair. Generate a keystore with `keytool` and put it in `src/main/resources`:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -storetype JKS -keystore src/main/resources/samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

Then build the pac4j `Config` with the SAML client, in a `ConfigFactory` or directly in your main class:

```java
package org.example;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        // configuration of the authentication via the SAML2 protocol
        final var cfg = new SAML2Configuration();
        cfg.getKeystore().setKeystorePath("classpath:samlKeystore.jks");
        cfg.getKeystore().setKeystorePassword("pac4j-demo-passwd");
        cfg.getKeystore().setPrivateKeyPassword("pac4j-demo-passwd");
        cfg.setIdentityProviderMetadataPath("https://www.casserverpac4j.dev/idp/metadata");
        cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
        cfg.setSpLogoutRequestSigned(true);
        cfg.setServiceProviderMetadataPath("file:metadata/sp-metadata.xml");
        return new Config("http://localhost:8080/callback", new SAML2Client(cfg));
    }
}
```

The configuration describes both sides of the SAML connection: our application's keystore and entity ID, and the IdP's metadata. It also tells pac4j where to write our SP metadata.

The [SAML client documentation](/docs/clients/saml.html) explains these settings and SP metadata generation in detail. When using your own IdP, import the generated `metadata/sp-metadata.xml` into its configuration so it recognizes your application. pac4j generates this file when the SAML client is first initialized, for example on the first request to `/protected`. Our callback URL, including `?client_name=SAML2Client`, is the Assertion Consumer Service URL: the address where the IdP sends its authentication response.

## 3) Wire the handlers into Javalin

Now we can connect pac4j to the routes. A `before` handler protects the page, while two routes handle callback and logout. **The IdP posts the SAML assertion**, so we must have a `POST` callback route:

```java
package org.example;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.javalin.CallbackHandler;
import org.pac4j.javalin.JavalinFrameworkParameters;
import org.pac4j.javalin.LogoutHandler;
import org.pac4j.javalin.SecurityHandler;
import org.pac4j.saml.profile.SAML2Profile;

public class App {

    public static void main(final String[] args) {
        final var config = new SecurityConfigFactory().build();
        final var callback = new CallbackHandler(config, "/", true);
        final var logout = new LogoutHandler(config, "/");
        logout.destroySession = true;

        Javalin.create(cfg -> cfg.routes
                .get("/", ctx -> ctx.html("<a href='/protected'>Protected area</a>"))
                // the SAML login protects /protected and everything below
                .before("/protected", new SecurityHandler(config, "SAML2Client"))
                .before("/protected/*", new SecurityHandler(config, "SAML2Client"))
                .get("/protected", ctx -> ctx.contentType("text/plain; charset=UTF-8").result(protectedPage(ctx, config)))
                // the identity provider posts the assertion here after login
                .get("/callback", callback)
                .post("/callback", callback)
                // logs the user out
                .get("/logout", logout))
            .start(8080);
    }
}
```

When an anonymous user requests the protected page, `SecurityHandler` redirects to the IdP and cancels the remaining handlers for that request. An authenticated user can continue to the route. The handler also accepts `authorizers` and `matchers`: for example, `new SecurityHandler(config, "SAML2Client", "admin")` uses a role check declared with `config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))`.

On the way back, `CallbackHandler` validates the signed assertion and saves the profile in the session. It redirects to the originally requested URL, or to the default URL passed as its second argument. The third argument enables session renewal to protect against session fixation attacks. This handler also receives the `LogoutResponse` during single logout.

Finally, `LogoutHandler` removes the profile, and `destroySession` invalidates the session.

Javalin runs on Jetty, and pac4j uses the Jetty session through its servlet session store. For an IdP on another site, the browser must also send the session cookie with the SAML callback POST. Javalin's default `SameSite=Lax` cookie does not allow this: use HTTPS with a session cookie configured as `SameSite=None; Secure` to preserve the session across the login flow.

## 4) Access the authenticated user

Add these helper methods to `App`. They read the profile through the factories of the `Config` and return plain text, so attribute values are not interpreted as HTML:

```java
private static List<UserProfile> getProfiles(final Context ctx, final Config config) {
    final var parameters = new JavalinFrameworkParameters(ctx);
    final var context = config.getWebContextFactory().newContext(parameters);
    final var sessionStore = config.getSessionStoreFactory().newSessionStore(parameters);
    return config.getProfileManagerFactory().apply(context, sessionStore).getProfiles();
}

private static String protectedPage(final Context ctx, final Config config) {
    final var profile = (SAML2Profile) getProfiles(ctx, config).get(0);
    return "Hello " + profile.getId() + "\nAttributes: " + profile.getAttributes()
        + "\nVisit /logout to sign out.";
}
```

The `SAML2Profile` identifier is the assertion's `NameID`. For attributes, pac4j keeps the names released by the IdP, often URNs, and the values can be lists. You can give them friendlier names with `cfg.setMappedAttributes(Map.of("urn:oid:0.9.2342.19200300.100.1.3", "email"))`.

If you prefer to build the profile manager directly, `new ProfileManager(new JavalinWebContext(ctx), new JEESessionStore())` also works.

## 5) Single logout

So far, `/logout` only logs the user out of our application. SAML **single logout** (SLO) also asks the IdP to end its session and notify the other applications. Let's add a second handler for that:

```java
final var centralLogout = new LogoutHandler(config, "http://localhost:8080/", "http://localhost:8080/.*");
centralLogout.localLogout = true;
centralLogout.centralLogout = true;
centralLogout.destroySession = true;
// ...
.get("/central-logout", centralLogout)
```

pac4j then sends a `LogoutRequest` to the IdP's single logout endpoint, read from the IdP metadata. The configuration above enables logout-request signing with `cfg.setSpLogoutRequestSigned(true)`. SLO only works if that metadata declares a `SingleLogoutService`. The third constructor argument, the logout URL pattern, restricts the URLs the user may be sent back to after logout.

## 6) Run the application

Start the `App` class from your IDE or with a configured Maven Exec plugin and `mvn compile exec:java -Dexec.mainClass=org.example.App`, and open [http://localhost:8080/protected](http://localhost:8080/protected). You are redirected to the identity provider to sign in, then posted back to the callback with the assertion, and the protected page shows your profile.

If sign-in fails because the IdP does not recognize your application, check that you have imported `metadata/sp-metadata.xml` into the IdP configuration. The application's identifier registered there must exactly match the value passed to `cfg.setServiceProviderEntityId(...)`.

If a page opens without requiring sign-in, check that a `SecurityHandler` covers its URL. In our example, `before("/protected", ...)` protects the `/protected` page, while `before("/protected/*", ...)` protects URLs below it, such as `/protected/account`. Keep both handlers to protect both the page and its subpaths.

## 7) Switching to OIDC or CAS

Add `pac4j-oidc` or `pac4j-cas`, replace the `SAML2Client` in the `Config` and update the client name in the `SecurityHandler`. Adapt the `SAML2Profile` cast or use `UserProfile`, and register the callback and logout URLs for the chosen protocol. The SAML keystore is no longer needed. The protocol-specific setup is described in the [OIDC guide](/how-to-secure-a-java-application-with-oidc.html) and the [CAS client documentation](/docs/clients/cas.html).

## 8) Learn more

- The [javalin-pac4j](https://github.com/pac4j/javalin-pac4j) library and its [example application](https://github.com/pac4j/javalin-pac4j/tree/master/src/test/java/org/pac4j/javalin/example), with many authentication mechanisms.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
