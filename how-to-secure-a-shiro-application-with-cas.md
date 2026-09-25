---
layout: guide
title: How to secure a Shiro application with CAS (using pac4j)
seo_title: "How to secure an Apache Shiro application with CAS | pac4j"
description: "Add CAS single sign-on to an Apache Shiro application: a pac4j implementation authenticates, the buji-pac4j bridge fills the Shiro subject and roles."
---

Your application already uses Shiro: URL rules in `shiro.ini`, calls to `SecurityUtils.getSubject()`, role checks in the code. Now you need to connect it to a **CAS server**, while keeping those authorization rules.

Since the `shiro-cas` module was retired, Shiro no longer provides its own CAS client. We can use pac4j for the authentication and pass the result to Shiro.

This is the role of [buji-pac4j](https://github.com/bujiio/buji-pac4j). But there is a distinction to make: **buji-pac4j is a bridge, not an authentication implementation**. It transfers the pac4j profile to the Shiro subject. It cannot start a CAS login on its own.

So we also need a pac4j implementation:

- [jakartaee-pac4j](https://github.com/pac4j/jee-pac4j), servlet filters that Shiro can map on URLs like its own filters, used in this guide
- [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j), if your Shiro application is also a Spring MVC application.

The implementation handles CAS and produces a user profile. The bridge then logs that profile into Shiro through a dedicated realm, exposing the pac4j roles as Shiro roles. Your `roles[...]` rules and `subject.hasRole` checks can use them; we'll look at the principals below.

For a new application, a [pac4j implementation alone](/how-to-secure-a-java-application-with-cas.html) is simpler. The bridge is useful when you already have Shiro code to keep.

**What you need:**

- Java 17 or later and Maven
- Apache Shiro 3.x on a Servlet 5 or 6 container: Tomcat 10+, Jetty 11+, or the Jetty Maven plugin used below
- the **login URL** of a CAS server and the right to register a service on it, or the public pac4j test server used below.

## 1) Get the demo

The [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) project is the reference for this guide: Shiro, the pac4j servlet filters and the bridge, configured entirely in `shiro.ini`, with a CAS login among others.

```bash
git clone https://github.com/pac4j/buji-pac4j-demo.git
cd buji-pac4j-demo
mvn clean package jetty:run
```

## 2) Add the Maven dependencies

On top of `shiro-web`, you need three pac4j artifacts: the pac4j implementation that authenticates, here the `jakartaee-pac4j` servlet filters, the CAS module, and the bridge.

```xml
<!-- Apache Shiro for web applications -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-web</artifactId>
    <version>3.0.1</version>
</dependency>
<!-- the pac4j implementation: security, callback and logout filters -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>jakartaee-pac4j</artifactId>
    <version>8.0.3</version>
</dependency>
<!-- pac4j support for CAS -->
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>6.5.8</version>
</dependency>
<!-- the bridge: pushes the pac4j profile into the Shiro subject -->
<dependency>
    <groupId>io.buji</groupId>
    <artifactId>buji-pac4j</artifactId>
    <version>10.0.0</version>
</dependency>
```

Each dependency has a job: without `jakartaee-pac4j` (or `spring-webmvc-pac4j`), nothing starts the login, so the bridge has no profile to pass to Shiro.

The versions above use Jakarta APIs. Shiro 3 does not need a `jakarta` classifier, and bridge version 10 targets pac4j 6 and Shiro 3. For Shiro 2 with `javax.servlet`, use bridge version 9.1 and the `javaee-pac4j` filters.

You may notice that we haven't declared a realm yet. The bridge supplies a default Shiro INI alongside yours, loaded through its `WebEnvironment`. It declares `config`, `clients`, a `Pac4jRealm` and a subject factory.

On the pac4j side, it installs a `ShiroProfileManager`. Saving a profile logs the Shiro subject in; removing the profile logs it out. This is where the two security models meet.

## 3) Configure pac4j in shiro.ini

Everything else is plain Shiro INI. The `config` and `clients` objects come from the bridge; you declare the CAS client, plug it into `clients`, and define the pac4j filters as Shiro filters:

```ini
[main]
# the CAS client
casConfig = org.pac4j.cas.config.CasConfiguration
casConfig.loginUrl = https://www.casserverpac4j.dev/login

casClient = org.pac4j.cas.client.CasClient
casClient.configuration = $casConfig

# the callback URL and the list of clients (the "clients" object is declared by the bridge)
clients.callbackUrl = http://localhost:8080/callback
clients.clients = $casClient

# the pac4j filters, declared like any Shiro filter
casSecurityFilter = org.pac4j.jee.filter.SecurityFilter
casSecurityFilter.config = $config
casSecurityFilter.clients = CasClient

callbackFilter = org.pac4j.jee.filter.CallbackFilter
callbackFilter.config = $config

pac4jLogout = org.pac4j.jee.filter.LogoutFilter
pac4jLogout.config = $config
pac4jLogout.defaultUrl = /
pac4jLogout.destroySession = true

[urls]
/protected/** = casSecurityFilter
/callback = callbackFilter
/pac4jLogout = pac4jLogout
/admin/** = casSecurityFilter, roles[ROLE_ADMIN]
/** = anon
```

For CAS itself, `casConfig.loginUrl` is the only mandatory setting. pac4j derives the validation URL from it and uses CAS 3.0 by default to retrieve user attributes. If validation needs an internal address, set `casConfig.prefixUrl = http://cas-internal:8080/cas`. For an older server, `casConfig.protocol = CAS20` selects CAS 2.0.

`clients.callbackUrl`, with `?client_name=CasClient` appended by pac4j, is the service URL seen by CAS. Register it as explained in the [service registration section of the Spring Boot guide](/how-to-secure-a-java-application-with-cas.html#4-register-the-service-on-the-cas-server).

Now follow a request to `/protected/**`: `casSecurityFilter` starts the login for an anonymous user. On the way back, `callbackFilter` validates the service ticket, saves the pac4j profile and, through the bridge, logs the Shiro subject in. The same callback also receives CAS single logout notifications. You can declare more `SecurityFilter` instances for other clients or authorizers.

Pay attention to `/admin/**`: its chain first starts CAS authentication, then applies `roles[ROLE_ADMIN]`. **Only the first matching URL chain runs**, so the `/protected/**` rule does not protect the admin pages.

The `web.xml` is the standard Shiro one: the `EnvironmentLoaderListener` and the `ShiroFilter` mapped on `/*`. Nothing pac4j-specific goes there.

## 4) Map the CAS user to Shiro roles

We now have an authenticated user, but where do the Shiro roles come from? `Pac4jRealm` exposes the pac4j profile's roles with their names unchanged. CAS, however, sends **attributes**, not pac4j roles.

An **authorization generator** makes the connection. For example, we can derive a role from a `memberOf` attribute released by the CAS service:

```java
package org.example.security;

import java.util.Collection;
import java.util.Optional;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;

public class RoleAuthorizationGenerator implements AuthorizationGenerator {

    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        profile.addRole("ROLE_USER");
        final var groups = profile.getAttribute("memberOf");
        final var adminGroup = "cn=admins,ou=groups,dc=example,dc=com";
        if (adminGroup.equals(groups)
                || (groups instanceof Collection<?> values && values.contains(adminGroup))) {
            profile.addRole("ROLE_ADMIN");
        }
        return Optional.of(profile);
    }
}
```

Add these declarations to the `[main]` section, before `[urls]`:

```ini
roleGenerator = org.example.security.RoleAuthorizationGenerator
casClient.authorizationGenerator = $roleGenerator
```

Replace `adminGroup` with the exact group value released by your CAS server. Group membership must be controlled by administrators; a substring check could grant access to a different group with a similar name.

Shiro **permissions** work the same way: put a list of permission strings in the profile attribute named by `Pac4jRealm.SHIRO_PERMISSIONS`, and `subject.isPermitted(...)` sees them.

## 5) Access the authenticated user

We can now read the user through the Shiro API. The subject is authenticated and its primary principal is the CAS principal, usually the username. A `Pac4jPrincipal` in the principal collection gives us the full pac4j profile:

```java
final var subject = SecurityUtils.getSubject();
subject.isAuthenticated();               // true after the CAS login
subject.hasRole("ROLE_ADMIN");           // from the authorization generator
subject.getPrincipal();                  // the CAS principal

final var principal = subject.getPrincipals().oneByType(Pac4jPrincipal.class);
final var profile = (CasProfile) principal.getProfile();
profile.getAttribute("email");
profile.getAttributes();                 // everything the release policy allowed
```

An empty attribute map can mean the release policy of the service is too restrictive, or the server still uses the CAS 2.0 protocol. To use another attribute as the principal name, set `pac4jRealm.principalNameAttribute = email` in the `[main]` section.

## 6) Logout

For logout, use `/pac4jLogout`, the URL configured above. The pac4j filter and bridge remove the profile and log the Shiro subject out together; `destroySession` invalidates the session. Shiro's built-in `logout` filter does not perform this whole operation.

To also end the CAS SSO session, enable central logout on the filter:

```ini
pac4jLogout.centralLogout = true
pac4jLogout.defaultUrl = http://localhost:8080/
```

pac4j then redirects the browser to the CAS `/logout` endpoint. The CAS server must allow the requested return URL. If you accept a dynamic `url` parameter, restrict it separately with `logoutUrlPattern`.

When CAS single logout is enabled, the server can notify participating services on their callback URLs. Processing those notifications also requires a session store that can recover and destroy the recorded session; verify this with your Shiro session configuration. A local logout alone does not notify other applications.

## 7) Run the application

```bash
mvn clean package jetty:run
```

Open [http://localhost:8080/protected/index.jsp](http://localhost:8080/protected/index.jsp): you are redirected to the CAS login page, then back with a service ticket, and the page shows both the Shiro principals and the pac4j profile. Then try [http://localhost:8080/admin/index.jsp](http://localhost:8080/admin/index.jsp): access is denied unless CAS releases the exact administrator group expected by the generator.

**If something goes wrong:**

- **"Application Not Authorized to Use CAS"**: the service is not registered, or its `serviceId` pattern does not match the full callback URL.
- **The subject is anonymous after the callback**: the bridge is not on the classpath, or another `WebEnvironment` is configured in `web.xml` and shadows the bridge's `Pac4jIniEnvironment`.
- **`roles[ROLE_ADMIN]` denies a CAS user**: no authorization generator sets the role, or the attribute it reads is not released by the service.
- **Nothing happens at all**: the bridge is alone on the classpath. Add a pac4j implementation, it is the one that authenticates.

## Switching to OIDC or SAML

Add `pac4j-oidc` or `pac4j-saml`, declare the corresponding client in the INI, reference it in `clients.clients` and update the security filter's `clients` property. Keep the bridge and realm, but adapt profile casts, attribute-to-role mappings and callback/logout registration to the provider. The protocol-specific setup is described in the [OIDC guide](/how-to-secure-a-java-application-with-oidc.html) and the [SAML guide](/how-to-secure-a-java-application-with-saml.html). The demo's `shiro.ini` declares all three side by side. A CAS server can also be configured as an OIDC or SAML provider; choose the protocol required by your deployment.

## Learn more

- The [buji-pac4j](https://github.com/bujiio/buji-pac4j) bridge and its [documentation](https://github.com/bujiio/buji-pac4j/wiki).
- The [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) application, with CAS, OIDC, SAML and HTTP basic authentication.
- The [CAS reference](/docs/clients/cas.html) for proxy tickets, the CAS REST API and all the `CasConfiguration` options, and the [Jakarta EE guide](/how-to-secure-a-jakarta-ee-application-with-oidc.html) for the pac4j filters used here, outside Shiro.

**Discover more [pac4j frameworks](/implementations.html) and more [authentication mechanisms](/docs/clients.html)…**
