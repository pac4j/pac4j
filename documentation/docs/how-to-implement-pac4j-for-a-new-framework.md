---
layout: ddoc
title: How to implement <i>pac4j</i> for a new framework/tool&#58;
---

*pac4j* is an easy and powerful security engine. It comes with the appropriate concepts and components to be implemented in any framework/tools.


## 1) Dependency

Add the `pac4j-core` dependency to benefit from the core API of `pac4j`. Other dependencies will be optionally added for specific support: `pac4j-oauth` for OAuth, `pac4j-cas` for CAS, `pac4j-saml` for SAML...


## 2) Configuration

To define your security configuration, gather all your authentication mechanisms = [**clients**](clients.html) via the `Clients` class (to share the same callback url). Also define your [**authorizers**](authorizers.html) to check authorizations and aggregate both (clients and authorizers) on the `Config`:

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

Notice you may also use the `ConfigSingleton` object to keep one instance of your configuration and share it among the different components (if you don't have any dependency injection capability). You can also use the `ConfigFactory` to build you configuration if no other mean is available.
You can also add **matchers** to define whether the security must apply or not.

---

## 3) "Filters/controllers"

To secure your Java web application, **the reference implementation is to create one filter and two endpoints**:

- one filter to **protect urls**
- one endpoint to **receive callbacks** for stateful authentication processes (indirect clients)
- another endpoint **to perform logout**.

In your framework, you will need to create:

1) a specific `EnvSpecificWebContext` implementing the [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) interface except for JEE environment where you can already use the existing `JEEContext`.
Your `EnvSpecificWebContext` should delegate to a [`SessionStore`](session-store.html) the calls regarding the web session management

2) a specific `EnvSpecificHttpActionAdapter` implementing the [`HttpActionAdapter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/HttpActionAdapter.java) if you need to turn actions performed on the web context into specific framework actions.


### A) Secure an URL

The [logic to secure an URL](security_filter.html) is defined by the `SecurityLogic` interface and its default implementation: [`DefaultSecurityLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java).

In your framework, you must define the appropriate "filter", "interceptor", "controller" or whatever the mechanism used to intercept the HTTP request and delegate to the `SecurityLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected final void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain filterChain) throws IOException, ServletException {

        final Config config = getSharedConfig();

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final SecurityLogic<Object, JEEContext> bestLogic = FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);

        final JEEContext context = new JEEContext(request, response, bestSessionStore);
        bestLogic.perform(context, config, (ctx, profiles, parameters) -> {
            // if no profiles are loaded, pac4j is not concerned with this request
            filterChain.doFilter(profiles.isEmpty() ? request : new Pac4JHttpServletRequestWrapper(request, profiles), response);
            return null;
        }, bestAdapter, clients, authorizers, matchers, multiProfile);
    }
```

- In Play:

```java
    protected CompletionStage<Result> internalCall(final Http.Request req, final PlayWebContext webContext, final String clients, final String authorizers, final String matchers, final boolean multiProfile) throws Throwable {

        final HttpActionAdapter<Result, PlayWebContext> bestAdapter = FindBest.httpActionAdapter(null, config, PlayHttpActionAdapter.INSTANCE);
        final SecurityLogic<CompletionStage<Result>, PlayWebContext> bestLogic = FindBest.securityLogic(securityLogic, config, DefaultSecurityLogic.INSTANCE);


        final HttpActionAdapter<CompletionStage<Result>, PlayWebContext> actionAdapterWrapper = (action, webCtx) -> CompletableFuture.completedFuture(bestAdapter.adapt(action, webCtx));
        return bestLogic.perform(webContext, config, (webCtx, profiles, parameters) -> {
	            // when called from Scala
	            if (delegate == null) {
	                return CompletableFuture.completedFuture(null);
	            } else {
	                return delegate.call(webCtx.supplementRequest(req));
	            }
            }, actionAdapterWrapper, clients, authorizers, matchers, multiProfile);
    }
```


### B) Handle callback for indirect client

The [logic to handle callbacks](callback_endpoint.html) is defined by the `CallbackLogic` interface and its default implementation: [`DefaultCallbackLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultCallbackLogic.java).

In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `CallbackLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException {

        final Config config = getSharedConfig();

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final CallbackLogic<Object, JEEContext> bestLogic = FindBest.callbackLogic(callbackLogic, config, DefaultCallbackLogic.INSTANCE);

        final JEEContext context = new JEEContext(request, response, bestSessionStore);
        bestLogic.perform(context, config, bestAdapter, this.defaultUrl, this.saveInSession, this.multiProfile, this.renewSession, this.defaultClient);
    }
```

- In Play:

```java
    public CompletionStage<Result> callback(final Http.Request request) {

        final HttpActionAdapter<Result, PlayWebContext> bestAdapter = FindBest.httpActionAdapter(null, config, PlayHttpActionAdapter.INSTANCE);
        final CallbackLogic<Result, PlayWebContext> bestLogic = FindBest.callbackLogic(callbackLogic, config, DefaultCallbackLogic.INSTANCE);

        final PlayWebContext playWebContext = new PlayWebContext(request, playSessionStore);
        return CompletableFuture.supplyAsync(() -> bestLogic.perform(playWebContext, config, bestAdapter,
                this.defaultUrl, this.saveInSession, this.multiProfile, this.renewSession, this.defaultClient), ec.current());
    }
```


### C) Logout

The [logic to perform the application/identity provider logout](logout_endpoint.html) is defined by the `LogoutLogic` interface and its default implementation: [`DefaultLogoutLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultLogoutLogic.java).
In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `LogoutLogic` class.

**Examples**:

- In JEE:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException {

        final Config config = getSharedConfig();

        final SessionStore<JEEContext> bestSessionStore = FindBest.sessionStore(null, config, JEESessionStore.INSTANCE);
        final HttpActionAdapter<Object, JEEContext> bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final LogoutLogic<Object, JEEContext> bestLogic = FindBest.logoutLogic(logoutLogic, config, DefaultLogoutLogic.INSTANCE);

        final JEEContext context = new JEEContext(request, response, bestSessionStore);
        bestLogic.perform(context, config, bestAdapter, this.defaultUrl, this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout);
    }
```

- In Play:

```java
    public CompletionStage<Result> logout(final Http.Request request) {

        final HttpActionAdapter<Result, PlayWebContext> bestAdapter = FindBest.httpActionAdapter(null, config, PlayHttpActionAdapter.INSTANCE);
        final LogoutLogic<Result, PlayWebContext> bestLogic = FindBest.logoutLogic(logoutLogic, config, DefaultLogoutLogic.INSTANCE);

        final PlayWebContext playWebContext = new PlayWebContext(request, playSessionStore);
        return CompletableFuture.supplyAsync(() -> bestLogic.perform(playWebContext, config, bestAdapter, this.defaultUrl,
                this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout), ec.current());
    }
```
