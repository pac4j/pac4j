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

1) a specific `EnvSpecificWebContext` implementing the [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) interface except for J2E environment where you can already use the existing `J2EContext`.
Your `EnvSpecificWebContext` should delegate to a [`SessionStore`](session-store.html) the calls regarding the web session management

2) optionally a specific `EnvSpecificHttpActionAdapter` implementing the [`HttpActionAdapter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/http/HttpActionAdapter.java) if you need to turn actions performed on the web context into specific framework actions.


### A) Secure an URL

The logic to secure an URL is defined by the `SecurityLogic` interface and its default implementation: [`DefaultSecurityLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java). In your framework, you must define the appropriate "filter", "interceptor", "controller" or whatever the mechanism used to intercept the HTTP request and delegate to the `SecurityLogic` class:

1) If the HTTP request matches the **matchers** configuration (or no **matchers** are defined), the security is applied. Otherwise, the user is automatically granted access

2) First, if the user is not authenticated (no profile) and if some clients have been defined in the **clients** parameter, a login is tried for the direct clients

3) Then, if the user has profile, authorizations are checked according to the **authorizers** configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed

4) Finally, if the user is still not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the **clients** configuration. Otherwise, a 401 error page is displayed.

**Examples**:

- In J2E:

```java
    @Override
    protected final void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                        final FilterChain filterChain) throws IOException, ServletException {

        assertNotNull("securityLogic", securityLogic);

        final Config config = getConfig();
        assertNotNull("config", config);
        final J2EContext context = new J2EContext(request, response, config.getSessionStore());

        securityLogic.perform(context, config, (ctx, profiles, parameters) -> {
            // if no profiles are loaded, pac4j is not concerned with this request
            filterChain.doFilter(profiles.isEmpty() ? request : new Pac4JHttpServletRequestWrapper(request, profiles), response);
            return null;
        }, J2ENopHttpActionAdapter.INSTANCE, clients, authorizers, matchers, multiProfile);
    }
```

- In Play:

```java
    public CompletionStage<Result> internalCall(final Context ctx, final String clients, final String authorizers, final String matchers, final boolean multiProfile) throws Throwable {

        assertNotNull("securityLogic", securityLogic);

        assertNotNull("config", config);
        final PlayWebContext playWebContext = new PlayWebContext(ctx, sessionStore);
        final HttpActionAdapter<Result, WebContext> actionAdapter = config.getHttpActionAdapter();
        assertNotNull("actionAdapter", actionAdapter);
        final HttpActionAdapter<CompletionStage<Result>, PlayWebContext> actionAdapterWrapper = (code, webCtx) -> CompletableFuture.completedFuture(actionAdapter.adapt(code, webCtx));

        return securityLogic.perform(playWebContext, config, (webCtx, profiles, parameters) -> {
	            // when called from Scala
	            if (delegate == null) {
	                return CompletableFuture.completedFuture(null);
	            } else {
	                return delegate.call(ctx);
	            }
            }, actionAdapterWrapper, clients, authorizers, matchers, multiProfile);
    }
```


### B) Handle callback for indirect client

The logic to handle callbacks is defined by the `CallbackLogic` interface and its default implementation: [`DefaultCallbackLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultCallbackLogic.java).
In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `CallbackLogic` class:

1) the credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session.

2) finally, the user is redirected back to the originally requested URL (or to the **defaultUrl**).

**Examples**:

- In J2E:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException {

        assertNotNull("callbackLogic", callbackLogic);

        final Config config = getConfig();
        assertNotNull("config", config);
        final J2EContext context = new J2EContext(request, response, config.getSessionStore());

        callbackLogic.perform(context, config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.saveInSession, this.multiProfile, this.renewSession, this.defaultClient);
    }
```

- In Play:

```java
    public CompletionStage<Result> callback() {

        assertNotNull("callbackLogic", callbackLogic);

        assertNotNull("config", config);
        final PlayWebContext playWebContext = new PlayWebContext(ctx(), playSessionStore);

        return CompletableFuture.supplyAsync(() -> callbackLogic.perform(playWebContext, config, config.getHttpActionAdapter(), this.defaultUrl, this.saveInSession, this.multiProfile, false, this.defaultClient), ec.current());
    }
```


### C) Logout

The logic to perform the application/identity provider logout is defined by the `LogoutLogic` interface and its default implementation: [`DefaultLogoutLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultLogoutLogic.java).
In your framework, you must define the appropriate "controller" to reply to an HTTP request and delegate the call to the `LogoutLogic` class:

1) If the `localLogout` property is true, the pac4j profiles are removed from the web session (and the web session is destroyed if the `destroySession` property is `true`)

2) A post logout action is computed as the redirection to the url request parameter if it matches the `logoutUrlPattern` or to the `defaultUrl` if it is defined or as a blank page otherwise

3) If the `centralLogout` property is `true`, the user is redirected to the identity provider for a central logout and then optionally to the post logout redirection URL (if it's supported by the identity provider and if it's an absolute URL). If no central logout is defined, the post logout action is performed directly.

**Examples**:

- In J2E:

```java
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException {

        assertNotNull("applicationLogoutLogic", logoutLogic);

        final Config config = getConfig();
        assertNotNull("config", config);
        final J2EContext context = new J2EContext(request, response, config.getSessionStore());

        logoutLogic.perform(context, config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout);
    }
```

- In Play:

```java
    public CompletionStage<Result> logout() {

        assertNotNull("logoutLogic", logoutLogic);

        assertNotNull("config", config);
        final PlayWebContext playWebContext = new PlayWebContext(ctx(), playSessionStore);

        return CompletableFuture.supplyAsync(() -> logoutLogic.perform(playWebContext, config, config.getHttpActionAdapter(), this.defaultUrl,
                this.logoutUrlPattern, this.localLogout, this.destroySession, this.centralLogout), ec.current());
    }
```
