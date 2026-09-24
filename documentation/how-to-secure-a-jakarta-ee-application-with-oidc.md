---
layout: content
title: How to secure a Jakarta EE application with OIDC (using pac4j)
seo_title: "How to secure a Jakarta EE application with OIDC | pac4j"
description: "Add OpenID Connect (OIDC) login to a Jakarta EE or servlet application with pac4j: security, callback and logout filters, user profile, SAML and CAS variants."
---

# How to secure a Jakarta EE application with OIDC (using pac4j)

You do not need Spring to add OpenID Connect login to a Java web application. If you already have servlets and a servlet container, we can use those directly.

The [jee-pac4j](https://github.com/pac4j/jee-pac4j) library provides **three filters**: one to protect your pages, one to handle the callback and one for logout. We'll configure them to authenticate users with an OIDC provider such as Keycloak, Google, Microsoft Entra ID or Okta.

The OIDC configuration is the same as in the [Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html). Here, we'll connect it to the servlet container with a configuration class and `web.xml`; CDI is optional. The [jee-pac4j-demo](https://github.com/pac4j/jee-pac4j-demo) goes further, with SAML, CAS, OAuth and form login as well.

**What you need:**

- Java 17 or later and Maven
- a Servlet 6.0 container, such as Tomcat 10.1 or Jetty 12 with its Jakarta EE 10 environment, to match the Servlet API and `web.xml` below
- an OpenID Connect provider where you can register an application, or the public demo server used below.

## 1) Add the Maven dependencies

Start from a Maven web application with `<packaging>war</packaging>` and a Java 17 compiler configuration. Two pac4j artifacts are needed: the Jakarta EE integration from [jee-pac4j](https://github.com/pac4j/jee-pac4j), which provides the filters, and the OpenID Connect module.

```xml
<!-- pac4j integration for Jakarta EE (servlet filters) -->
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
<!-- the servlet API, provided by your container -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

For a legacy `javax.servlet` application, use the `javaee-pac4j` artifact instead of `jakartaee-pac4j`, the corresponding `javax.servlet-api` dependency, `javax.servlet` imports and a compatible `web.xml` descriptor. The pac4j configuration follows the same pattern.

## 2) Write the security configuration

First, we need a `Config` to hold our authentication client. How do the filters get it? Through a `ConfigFactory`: they instantiate this class at startup and call its `build` method.

```java
package org.example.security;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        // configuration of the authentication via the OpenID Connect protocol
        final var config = new OidcConfiguration()
            .setDiscoveryURI("https://www.casserverpac4j.dev/oidc/.well-known/openid-configuration")
            .setClientId("myclient")
            .setSecret("mysecret")
            .setAllowUnsignedIdTokens(true);
        return new Config("http://localhost:8080/callback", new OidcClient(config));
    }
}
```

`setDiscoveryURI` gives pac4j the provider's `.well-known/openid-configuration` document. From it, pac4j reads the authorization, token, user info and JWKS endpoints. `setClientId` and `setSecret` are the credentials obtained when registering the application.

The callback is configured by `new Config("http://localhost:8080/callback", ...)`. pac4j appends `?client_name=OidcClient`, so **register that full URL as the redirect URI** at the provider. In production, use the public URL of your application.

By default pac4j uses the authorization code flow, with PKCE when the provider supports it. `setAllowUnsignedIdTokens(true)` only exists because the public demo server issues unsigned ID tokens: remove it for a real provider. To register the application at Keycloak, Google or Entra ID, follow the [provider section of the Spring Boot guide](/how-to-secure-a-java-application-with-oidc.html#4-register-the-application-at-your-identity-provider): the Java configuration is the same, only the wiring below differs.

## 3) Declare the filters in web.xml

The integration provides three servlet filters. Declare them in `WEB-INF/web.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

    <!-- protects /protected/* with the OIDC login -->
    <filter>
        <filter-name>securityFilter</filter-name>
        <filter-class>org.pac4j.jee.filter.SecurityFilter</filter-class>
        <init-param>
            <param-name>configFactory</param-name>
            <param-value>org.example.security.SecurityConfigFactory</param-value>
        </init-param>
        <init-param>
            <param-name>clients</param-name>
            <param-value>OidcClient</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>securityFilter</filter-name>
        <url-pattern>/protected/*</url-pattern>
    </filter-mapping>

    <!-- finishes the login when the provider redirects the user back -->
    <filter>
        <filter-name>callbackFilter</filter-name>
        <filter-class>org.pac4j.jee.filter.CallbackFilter</filter-class>
        <init-param>
            <param-name>defaultUrl</param-name>
            <param-value>/</param-value>
        </init-param>
        <init-param>
            <param-name>renewSession</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>callbackFilter</filter-name>
        <url-pattern>/callback</url-pattern>
    </filter-mapping>

    <!-- logs the user out -->
    <filter>
        <filter-name>logoutFilter</filter-name>
        <filter-class>org.pac4j.jee.filter.LogoutFilter</filter-class>
        <init-param>
            <param-name>defaultUrl</param-name>
            <param-value>/</param-value>
        </init-param>
        <init-param>
            <param-name>destroySession</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>logoutFilter</filter-name>
        <url-pattern>/logout</url-pattern>
    </filter-mapping>

</web-app>
```

Let's follow a request to `/protected/*`. The `SecurityFilter` checks whether the user is authenticated. If not, it redirects the browser to the provider using the client named by `clients`. You can also configure `authorizers`, such as `isAuthenticated` or a role check, and use `matchers` to exclude paths.

After login, the provider sends the browser to `/callback`. The `CallbackFilter` exchanges the authorization code for tokens, validates the ID token and saves the profile in the session. It then redirects to the originally requested page, or to `defaultUrl`. With `renewSession`, the session identifier is renewed to protect against session fixation.

The third filter, `LogoutFilter`, removes the profile when the user logs out. `destroySession` also invalidates the HTTP session.

The `configFactory` parameter builds the configuration once and shares it with the other pac4j filters, so declaring it on one filter is sufficient. You can declare several `SecurityFilter` instances with different clients or authorizers for different URL patterns.

If you prefer Java configuration, you can register the same filters from a `ServletContextListener` with `FilterHelper`. Choose this or the XML declarations above: registering both would run the filters twice.

```java
package org.example.security;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.pac4j.jee.filter.CallbackFilter;
import org.pac4j.jee.filter.LogoutFilter;
import org.pac4j.jee.filter.SecurityFilter;
import org.pac4j.jee.util.FilterHelper;

@WebListener
public class SecurityInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final var config = new SecurityConfigFactory().build();
        final var filters = new FilterHelper(event.getServletContext());
        filters.addFilterMapping("securityFilter", new SecurityFilter(config, "OidcClient"), "/protected/*");
        filters.addFilterMapping("callbackFilter", new CallbackFilter(config, "/"), "/callback");
        final var logoutFilter = new LogoutFilter(config, "/");
        logoutFilter.setDestroySession(true);
        filters.addFilterMapping("logoutFilter", logoutFilter, "/logout");
    }
}
```

## 4) Access the authenticated user

Once the filters have done their work, our servlet can read the user profile. Wrap the request and response in a `JEEContext`, then use the `ProfileManager`:

```java
package org.example.security;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.pac4j.oidc.profile.OidcProfile;

@WebServlet("/protected/index")
public class ProtectedServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final var context = new JEEContext(request, response);
        final var manager = new ProfileManager(context, new JEESessionStore());
        final var profile = (OidcProfile) manager.getProfile().orElseThrow();

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("Hello " + profile.getDisplayName() + " (" + profile.getEmail() + ")");
        response.getWriter().println("Visit /logout to sign out.");
    }
}
```

The `OidcProfile` exposes the standard claims as getters, provides the mapped profile attributes through `getAttributes()`, and gives access to the raw tokens with `getIdToken()` and `getAccessToken()`. Which claims are present depends on the scopes: the default is `openid profile email`.

In a **CDI application**, the integration can produce a `ProfileManager` and a `WebContext` for injection. This requires a CDI-managed `Config` and injectable HTTP request and response objects; the XML `ConfigFactory` alone does not supply them. Follow the setup in the [jee-pac4j-cdi-demo](https://github.com/pac4j/jee-pac4j-cdi-demo). Inject the `WebContext` interface, rather than `JEEContext`.

## 5) Logout

The `/logout` URL mapped above performs the **local logout**. To also end the session at an identity provider that supports OIDC logout, add `centralLogout` to the `LogoutFilter` and replace its existing `defaultUrl` parameter with an absolute return URL:

```xml
<init-param>
    <param-name>centralLogout</param-name>
    <param-value>true</param-value>
</init-param>
<init-param>
    <param-name>defaultUrl</param-name>
    <param-value>http://localhost:8080/</param-value>
</init-param>
```

pac4j then redirects the browser to the provider's `end_session_endpoint`, read from the discovery document. Register `http://localhost:8080/` as an allowed post-logout redirect URI at the provider. In production, use your application's public HTTPS URL.

If you accept a dynamic return URL through the logout request's `url` parameter, restrict it with `logoutUrlPattern`. That pattern validates the requested URL; it does not set the return URL itself.

## 6) Run the application

Package the application as a WAR:

```bash
mvn clean package
```

Deploy it to your Servlet 6.0 container at the root context on port 8080, matching the URLs above. With Tomcat, deploy the WAR as `ROOT.war`. For a different context path or port, update the callback URL, the registered redirect URI and the logout return URL accordingly. Provide a public home page, such as `src/main/webapp/index.html`, for the default redirect after logout.

The [jee-pac4j-demo](https://github.com/pac4j/jee-pac4j-demo) also demonstrates running through a configured Jetty Maven plugin. Its plugin configuration, servlet version and example paths belong together; `jetty:run` is not available merely by adding the dependencies above.

Open [http://localhost:8080/protected/index](http://localhost:8080/protected/index). You are redirected to the identity provider to sign in, then returned to the protected page, which greets you by name.

**If something goes wrong:**

- **"Invalid redirect URI"** at the provider: the registered URI must be the full callback URL, including `?client_name=OidcClient`.
- **The security filter never triggers**: check the filter mapping. If an annotated servlet or listener is missing, remove `metadata-complete="true"` from `web.xml` or declare that component explicitly: this setting disables annotation scanning.
- **The callback loops back to the login page**: the callback URL in the `Config` does not match the URL the browser actually uses, typically behind a reverse proxy. Set the public URL.
- **No profile in the servlet**: the `SecurityFilter` is not mapped on that URL. The profile is only guaranteed on URLs the filter protects.

## Switching to SAML or CAS

Add the corresponding `pac4j-saml` or `pac4j-cas` dependency, replace the `OidcClient` in the `ConfigFactory` with a `SAML2Client` or a `CasClient`, and update the security filter's `clients` parameter. Adapt the `OidcProfile` cast in the servlet to the new profile type, or use the common `UserProfile` interface. The same filter structure applies, with the callback and logout URLs registered for the chosen protocol. The protocol-specific configuration, the SAML keystore and metadata exchange, and the CAS service registration, are described in the [SAML guide](/how-to-secure-a-java-application-with-saml.html) and the [CAS guide](/how-to-secure-a-java-application-with-cas.html).

## Learn more

- The [jee-pac4j](https://github.com/pac4j/jee-pac4j) library and its [documentation](https://github.com/pac4j/jee-pac4j/wiki) for every filter parameter and the JSF and CDI support.
- The [jee-pac4j-demo](https://github.com/pac4j/jee-pac4j-demo) web application, with JSP pages and many authentication mechanisms, and the [jee-pac4j-cdi-demo](https://github.com/pac4j/jee-pac4j-cdi-demo) with JSF and CDI.
- The [OpenID Connect reference](/docs/clients/openid-connect.html) for all the `OidcConfiguration` options.
- [Authorizers](/docs/authorizers.html) to restrict access by role or attribute once the user is authenticated.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
