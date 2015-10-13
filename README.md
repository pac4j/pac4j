<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="300" />
</p>

`pac4j` is a **Java security engine** to authenticate users, get their profiles and manage their authorizations in order to secure your Java web applications. It's available under the Apache 2 license.

It is currently **available for many frameworks / tools and supports many authentication mechanisms**. See the [big picture](https://github.com/pac4j/pac4j/wiki/The-big-picture).

Its core API is provided by the `pac4j-core` submodule (groupId: `org.pac4j`).


## Frameworks / tools implementing `pac4j`:

| The framework / tool you develop with | The `*-pac4j` library you must use | The demo(s) for tests
|---------------------------------------|------------------------------------|----------------------
| [J2E environment](http://docs.oracle.com/javaee/) | [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) | [j2e-pac4j-demo](https://github.com/pac4j/j2e-pac4j-demo)
| [Play 2.x framework](http://www.playframework.org) | [play-pac4j](https://github.com/pac4j/play-pac4j) | [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) or [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo)
| [Vertx](http://vertx.io) | [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) | [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo)
| [Spark Java framework](http://sparkjava.com) | [spark-pac4j](https://github.com/pac4j/spark-pac4j) | [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo)
| [Ratpack](http://www.ratpack.io) | [ratpack-pac4j](http://ratpack.io/manual/current/pac4j.html#pac4j) | [ratpack-pac4j-demo](https://github.com/pac4j/ratpack-pac4j-demo)
| [Undertow](http://undertow.io) | [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) | [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo)
| [Jooby framework](http://jooby.org) |  [jooby-pac4j](http://jooby.org/doc/pac4j) | [jooby-pac4j-demo](https://github.com/pac4j/jooby-pac4j-demo)
| [Apache Shiro](http://shiro.apache.org) | [buji-pac4j](https://github.com/bujiio/buji-pac4j) | [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo)
| [Spring Security](http://projects.spring.io/spring-security) | [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) | [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo)
| [SSO CAS server](https://github.com/Jasig/cas) | [cas-server-support-pac4j](http://jasig.github.io/cas/4.1.x/integration/Delegate-Authentication.html) | [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo)


## Supported authentication / authorization mechanisms:

`pac4j` supports most authentication mechanisms, called [**clients**](https://github.com/pac4j/pac4j/wiki/Clients):

- **indirect / stateful clients** are for UI when the user authenticates once at an external provider (like Facebook, a CAS server...) or via a local form (or basic auth popup)  
- **direct / stateless clients** are for web services when credentials (like basic auth, tokens...) are passed for each HTTP request.

See the [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows).

| The authentication mechanism you want | The `pac4j-*` submodule you must use
|---------------------------------------|-------------------------------------
| OAuth (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... | `pac4j-oauth`
| CAS (1.0, 2.0, 3.0, SAML, logout, proxy, REST) | `pac4j-cas`
| HTTP (form, basic auth, IP, header, cookie, GET/POST parameter) | `pac4j-http`
| OpenID | `pac4j-openid`
| SAML (2.0) | `pac4j-saml`
| Google App Engine UserService | `pac4j-gae`
| OpenID Connect (1.0) | `pac4j-oidc`
| JWT | `pac4j-jwt`
| LDAP | `pac4j-ldap`
| Relational DB | `pac4j-sql`
| MongoDB | `pac4j-mongo`
| Stormpath | `pac4j-stormpath`

`pac4j` supports many authorization checks, called [**authorizers**](https://github.com/pac4j/pac4j/wiki/Authorizers) available in the `pac4j-core` and `pac4j-http` submodules: role / permission checks, CSRF token validation...


## How to develop you own `pac4j` implementation for your framework / tool?

### Versions

The next version **1.8.1-SNAPSHOT** is under development. Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). See the [tests strategy](https://github.com/pac4j/pac4j/wiki/Tests).

The source code can be cloned and locally built via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install -DskipITs
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

### Implementations

`pac4j` is an easy and powerful security engine. Add the `pac4j-core` dependency to benefit from the core API of `pac4j`. Other dependencies will be optionally added for specific support: `pac4j-oauth` for OAuth, `pac4j-cas` for CAS, `pac4j-saml` for SAML...

To secure your Java web application, **the reference implementation is to create two filters**: **one to protect urls**, **the other one to receive callbacks** for stateful authentication processes ("indirect clients").

Gather all your authentication mechanisms = [**clients**](https://github.com/pac4j/pac4j/wiki/Clients) via the `Clients` class (to share the same callback url). Also define your [**authorizers**](https://github.com/pac4j/pac4j/wiki/Authorizers) to check authorizations and aggregate both (clients and authorizers) on the `Config`:

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

1) **For your protection filter, use the following logic (loop on direct clients for authentication then check the user profile and authorizations)**:

```java
EnvSpecificWebContext context = new EnvSpecificWebContex(...);
Clients configClients = config.getClients();
List<Client> currentClients = clientFinder.find(configClients, context, clientName);

boolean useSession = useSession(context, currentClients);
ProfileManager manager = new ProfileManager(context);
UserProfile profile = manager.get(useSession);

if (profile == null && currentClients != null && currentClients.size() > 0) {
  for (final Client currentClient: currentClients) {
    if (currentClient instanceof DirectClient) {
      final Credentials credentials;
      try {
        credentials = currentClient.getCredentials(context);
      } catch (RequiresHttpAction e) { ... }
      profile = currentClient.getUserProfile(credentials, context);
      if (profile != null) {
        manager.save(useSession, profile);
        break;
      }
    }
  }
}

if (profile != null) {
  if (authorizationChecker.isAuthorized(context, profile, authorizerName, config.getAuthorizers())) {
    grantAccess();
  } else {
    forbidden(context, currentClients, profile);
  }
} else {
  if (startAuthentication(context, currentClients)) {
    saveRequestedUrl(context, currentClients);
    redirectToIdentityProvider(context, currentClients);
  } else {
    unauthorized(context, currentClients);
  }
}
```

The `EnvSpecificWebContext` class is a specific implementation of the `WebContext` interface for your framework.

See the final implementations in [j2e-pac4j](https://github.com/pac4j/j2e-pac4j/blob/master/src/main/java/org/pac4j/j2e/filter/RequiresAuthenticationFilter.java#L91) and [play-pac4j](https://github.com/pac4j/play-pac4j/blob/master/play-pac4j-java/src/main/java/org/pac4j/play/java/RequiresAuthenticationAction.java#L95).

2) **For your callback filter, get the credentials and the user profile on the callback url**:

```java
EnvSpecificWebContext context = new EnvSpecificWebContex(...);
Clients clients = config.getClients();
Client client = clients.findClient(context);

Credentials credentials;
try {
  credentials = client.getCredentials(context);
} catch (RequiresHttpAction e) {
  handleSpecialHttpBehaviours();
}

UserProfile profile = client.getUserProfile(credentials, context);
saveUserProfile(context, profile);
redirectToOriginallyRequestedUrl(context, response);
```

See the final implementations in [j2e-pac4j](https://github.com/pac4j/j2e-pac4j/blob/master/src/main/java/org/pac4j/j2e/filter/CallbackFilter.java#L65) and [play-pac4j](https://github.com/pac4j/play-pac4j/blob/master/play-pac4j-java/src/main/java/org/pac4j/play/CallbackController.java#L63).

Read the [Javadoc](http://www.pac4j.org/apidocs/pac4j/index.html) and the [technical components](https://github.com/pac4j/pac4j/wiki/Technical-components) for more information.


## Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
