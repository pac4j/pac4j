---
layout: blog
title: What's new in pac4j v6?
author: Jérôme LELEU
date: January 2024
---

### 1) Java version

First of all, pac4j v6 is based on the JDK 17 (LTS).

It means that you must pick the right pac4j version according to your JDK:
- pac4j v4.x for JDK 8
- pac4j v5.x for JDK 11
- pac4j v6.x for JDK 17

### 2) Renaming/cleaning

Several components have been removed or renamed. Notice that:
- the `LogoutHandler` is now the `SessionLogoutHandler`
- the `pac4j-cas`, `pac4j-saml` and `pac4j-springboot` are the modules to use for the CAS, SAML and Spring Boot supports.

### 3) API contract

The API contract has changed on some points:
- the `Authenticator` now returns an `Optional<Credentials>`
- the `WebContext`, the `SessionStore` and the `ProfileManagerFactory` are no longer used directly in method signatures but are gathered in a `CallContext` component
- regarding the `Client` interface:
  + the `getCredentials` method has been split into the `getCredentials` and `validateCredentials` methods
  + the `processLogout` method has been added, based on the `LogoutProcessor` component.

### 4) Lombok

Lombok is now used for pac4j and all its implementations.

### 5) Implementation design

The `FindBest` component, widely used in implementations, has been removed.

The customisations for the filters/controllers can only be done via the `Config` component and thanks to the `FrameworkParameters`.

Framework specificities (to set up by default) are specified (order matters):
- in the `org.pac4j.framework.adapter.FrameworkAdapterImpl` class if it exists
- or the `org.pac4j.jee.adapter.JEEFramworkAdapter` class is used if it exists on the classpath
- or the `DefaultFrameworkAdapter` class is used as a last resort.

Here is the old JEE `SecurityFilter` based on pac4j v5:

```java
public class SecurityFilter extends AbstractConfigFilter implements SecurityEndpoint {

    ...

    @Override
    protected final void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain filterChain) throws IOException, ServletException {

        final Config config = getSharedConfig();

        final HttpActionAdapter bestAdapter = FindBest.httpActionAdapter(httpActionAdapter, config, JEEHttpActionAdapter.INSTANCE);
        final SecurityLogic bestLogic = FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);

        final WebContext context = FindBest.webContextFactory(null, config, JEEContextFactory.INSTANCE).newContext(request, response);
        final SessionStore sessionStore = FindBest.sessionStoreFactory(null, config, JEESessionStoreFactory.INSTANCE).newSessionStore(request, response);

        bestLogic.perform(context, sessionStore, config, (ctx, session, profiles, parameters) -> {
            filterChain.doFilter(profiles.isEmpty() ? request : new Pac4JHttpServletRequestWrapper(request, profiles), response);
            return null;
        }, bestAdapter, clients, authorizers, matchers);
    }
}
```

Here is the new JEE `SecurityFilter` based on pac4j v6:

```java
@Getter
@Setter
public class SecurityFilter extends AbstractConfigFilter implements SecurityEndpoint {

    ...

    @Override
    protected final void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain filterChain) throws IOException, ServletException {

        val config = getSharedConfig();

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        config.getSecurityLogic().perform(config, (ctx, session, profiles) -> {
            filterChain.doFilter(profiles.isEmpty() ? request : new Pac4JHttpServletRequestWrapper(request, profiles), response);
            return null;
        }, clients, authorizers, matchers, new JEEFrameworkParameters(request, response));
    }
}
```

### 6) New implementations

As usual, new implementation versions have been released, based on pac4j v6:
- play-pac4j v12
- spring-webmvc-pac4j v8
- spring-security-pac4j v10
- ratpack-pac4j v5
- jee-pac4j v8
- buji-pac4j v9

More implementation upgrades are coming.

### 6) Learn more

Read the [release notes](../docs/release-notes.html) for a thorough presentation of the changes.
