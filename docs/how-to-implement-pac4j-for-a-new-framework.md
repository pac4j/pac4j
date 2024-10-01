---
layout: ddoc
title: How to implement <i>pac4j</i> for a new framework/tool&#58;
---

*pac4j* is an easy and powerful security engine. It comes with the appropriate concepts and components to be implemented in any framework/tools.


## 1) Dependency

Add the `pac4j-core` dependency to benefit from the core API of `pac4j` or the `pac4j-javaee` (deprecated) / `pac4j-jakartaee` dependency in a JEE environment.

Other dependencies will be optionally added for specific support: `pac4j-oauth` for OAuth, `pac4j-cas` for CAS, `pac4j-saml` for SAML...


## 2) Configuration

To define your security configuration, gather all your authentication mechanisms = [**clients**](clients.html) via the `Clients` class (to share the same callback url).
Also define your [**authorizers**](authorizers.html) to check authorizations and aggregate both (clients and authorizers) on the `Config`:

```java
FacebookClient facebookClient = new FacebookClient(FB_KEY, FB_SECRET);
TwitterClient twitterClient = new TwitterClient(TW_KEY, TW_SECRET);
FormClient formClient = new FormClient("http://localhost:8080/theForm.jsp", new SimpleTestUsernamePasswordAuthenticator(), new UsernameProfileCreator());
CasClient casClient = new CasClient();
casClient.setCasLoginUrl("http://mycasserver/login");
Clients clients = new Clients("http://localhost:8080/callback", facebookClient, twitterClient, formClient, casClient);
Config config = new Config(clients);
config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
config.addAuthorizer("custom", new CustomAuthorizer());
```

You can also add **matchers** to define whether the security must apply or not.

---

## 3) "Filters/controllers"

To secure your Java web application, **the reference implementation is to create one filter and two endpoints**:

- one filter to **protect urls**
- one endpoint to **receive callbacks** for stateful authentication processes (indirect clients)
- another endpoint **to perform logout**.

In each of its component, you will always need to initialize the specific framework configuration by using: `FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);`.

It will try to set the specific framework configuration :

1) from the `org.pac4j.framework.adapter.FrameworkAdapterImpl` class if it exists

2) then from the `org.pac4j.jee.adapter.JEEFrameworkAdapter` class if it is available in the classpath

3) and finally from the `DefaultFrameworkAdapter` class.

In your framework, you will also need to create:

1) a specific `EnvSpecificWebContext` implementing the `WebContext` interface. For JEE environment, you already have the `JEEContext`

2) a specific `EnvSpecificWebContextFactory` implementing the `WebContextFactory` interface to instantiate the `EnvSpecificWebContext`. For JEE environment, you already have the `JEEContextFactory`

3) a specific `EnvSpecificSessionStore` implementing the `SessionStore` interface to deal with the web session. For JEE environment, you already have the `JEESessionStore`

4) a specific `EnvSpecificSessionStoreFactory` implementing the `SessionStoreFactory` interface to instantiate the `EnvSpecificSessionStore`. For JEE environment, you already have the `JEESessionStoreFactory`

5) a specific `EnvSpecificHttpActionAdapter` implementing the `HttpActionAdapter` to perform actions on the web context. For JEE environment, you already have the `JEEHttpActionAdapter`

6) a specific `EnvFrameworkParameters` implementing the `FrameworkParameters` to handle specific framework parameters. For JEE environment, you already have the `JEEFrameworkParameters`.


### A) Secure an URL

The [logic to secure an URL](security-filter.html) is defined by the `SecurityLogic` interface and its default implementation: [`DefaultSecurityLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java).

In your framework, you must define the appropriate "filter", "interceptor", "controller" or whatever the mechanism used to intercept the HTTP request and delegate to the `SecurityLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected final void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain filterChain) throws IOException, ServletException {

        val config = getSharedConfig();

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        config.getSecurityLogic().perform(config, (ctx, session, profiles) -> {
            // if no profiles are loaded, pac4j is not concerned with this request
            filterChain.doFilter(profiles.isEmpty() ? request : new Pac4JHttpServletRequestWrapper(request, profiles), response);
            return null;
        }, clients, authorizers, matchers, new JEEFrameworkParameters(request, response));
    }
```

- In Play:

```java
    protected CompletionStage<Result> internalCall(final PlayFrameworkParameters parameters, final String clients, final String authorizers, final String matchers) throws Throwable {

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        final HttpActionAdapter actionAdapterWrapper = (action, webCtx) -> CompletableFuture.completedFuture(config.getHttpActionAdapter().adapt(action, webCtx));

        val configSecurity = new Config()
            .withClients(config.getClients())
            .withAuthorizers(config.getAuthorizers())
            .withMatchers(config.getMatchers())
            .withSecurityLogic(config.getSecurityLogic())
            .withCallbackLogic(config.getCallbackLogic())
            .withLogoutLogic(config.getLogoutLogic())
            .withWebContextFactory(config.getWebContextFactory())
            .withSessionStoreFactory(config.getSessionStoreFactory())
            .withProfileManagerFactory(config.getProfileManagerFactory())
            .withHttpActionAdapter(actionAdapterWrapper);

        return (CompletionStage<Result>) configSecurity.getSecurityLogic().perform(configSecurity, (webCtx, session, profiles) -> {
            val playWebContext = (PlayWebContext) webCtx;
            // when called from Scala
            if (delegate == null) {
                return CompletableFuture.completedFuture(new PlayWebContextResultHolder(playWebContext));
            } else {
                return delegate.call(playWebContext.supplementRequest((Http.Request)
                    playWebContext.getNativeJavaRequest())).thenApply(result -> playWebContext.supplementResponse(result));
            }
        }, clients, authorizers, matchers, parameters);
    }
```


### B) Handle callback for indirect client

The [logic to handle callbacks](callback-endpoint.html) is defined by the `CallbackLogic` interface and its default implementation: [`DefaultCallbackLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultCallbackLogic.java).

In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `CallbackLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain chain) throws IOException, ServletException {

        val config = getSharedConfig();

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        config.getCallbackLogic().perform(config, defaultUrl, renewSession, defaultClient, new JEEFrameworkParameters(request, response));
    }
```

- In Play:

```java
    public CompletionStage<Result> callback(final Http.Request request) {

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        return CompletableFuture.supplyAsync(() ->
                   (Result) config.getCallbackLogic().perform(config, defaultUrl, renewSession, defaultClient, new PlayFrameworkParameters(request))
               , ec.current());
    }
```


### C) Logout

The [logic to perform the application/identity provider logout](logout-endpoint.html) is defined by the `LogoutLogic` interface and its default implementation: [`DefaultLogoutLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultLogoutLogic.java).
In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `LogoutLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain chain) throws IOException, ServletException {

        val config = getSharedConfig();

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        config.getLogoutLogic().perform(config, defaultUrl, logoutUrlPattern, localLogout, destroySession, centralLogout, new JEEFrameworkParameters(request, response));
    }
```

- In Play:

```java
    public CompletionStage<Result> logout(final Http.Request request) {

        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        return CompletableFuture.supplyAsync(() ->
                   (Result) config.getLogoutLogic().perform(config, defaultUrl, logoutUrlPattern, localLogout,
                       destroySession, centralLogout, new PlayFrameworkParameters(request))
               , ec.current());
    }
```
