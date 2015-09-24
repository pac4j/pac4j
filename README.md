<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" width="50%" height="50%" />
</p>

## What is `pac4j`?

`pac4j` is a **Java security engine to authenticate users, get their profiles and manage their authorizations** in order to secure your Java web applications. It's available under the Apache 2 license.

It is actually **implemented by many frameworks and supports many authentication mechanisms**. See the [big picture](https://github.com/pac4j/pac4j/wiki/The-big-picture).

### Frameworks / tools implementing `pac4j`:

They depend on the `pac4j-core` module (groupId: `org.pac4j`):

1. the SSO [CAS server](https://github.com/Jasig/cas) using the [cas-server-support-pac4j](https://github.com/Jasig/cas/tree/master/cas-server-support-pac4j) module (demo: [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo))
2. the [Play 2.x framework](http://www.playframework.org) using the the [play-pac4j](https://github.com/pac4j/play-pac4j) library (demos: [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) & [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo))
3. any [J2E environment](http://docs.oracle.com/javaee/) using the [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) library (demo: [j2e-pac4j-demo](https://github.com/pac4j/j2e-pac4j-demo))
4. the [Apache Shiro project](http://shiro.apache.org) library using the [buji-pac4j](https://github.com/bujiio/buji-pac4j) library (demo: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo))
5. the [Spring Security](http://projects.spring.io/spring-security/) library using the [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) library (demo: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo))
6. the [Ratpack](http://www.ratpack.io/) JVM toolkit using the [ratpack-pac4j](https://github.com/ratpack/ratpack/tree/master/ratpack-pac4j) module (demo: [ratpack-pac4j-demo](https://github.com/pac4j/ratpack-pac4j-demo))
7. the [Vertx](http://vertx.io/) framework using the [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) module (demo: [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo))
8. the [Undertow](http://undertow.io/) web server using the [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) module (demo: [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo))
9. the [Spark Java framework](http://sparkjava.com) using the [spark-pac4j](https://github.com/pac4j/spark-pac4j) library (demo: [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo))
10. the [Jooby framework](http://jooby.org) using the [jooby-pac4j](https://github.com/jooby-project/jooby/tree/master/jooby-pac4j) module (demo: [jooby-pac4j-demo](https://github.com/pac4j/jooby-pac4j-demo))

### Supported authentication mechanisms:

`pac4j` supports stateful / indirect and stateless / direct [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows) using external identity providers or internal credentials authenticators and user profile creators:

1. **OAuth** (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... using the `pac4j-oauth` module
2. **CAS** (1.0, 2.0, SAML, logout & proxy) + REST API support using the `pac4j-cas` module
3. **HTTP** (form, basic auth, IP, header, GET/POST parameter authentications) using the `pac4j-http` module
4. **OpenID** using the `pac4j-openid` module
5. **SAML** (2.0) using the `pac4j-saml` module
6. **Google App Engine** UserService using the `pac4j-gae` module
7. **OpenID Connect** 1.0 using the `pac4j-oidc` module
8. **JWT** using the `pac4j-jwt` module
9. **LDAP** using the `pac4j-ldap` module
10. **relational DB** using the `pac4j-sql` module
11. **MongoDB** using the `pac4j-mongo` module
12. **Stormpath** using the `pac4j-stormpath` module.


## How to use `pac4j` for a specific framework?

Read the appropriate documentation for the [SSO CAS server](http://jasig.github.io/cas/4.0.x/integration/Delegate-Authentication.html), [Play 2.x framework](https://github.com/pac4j/play-pac4j), [J2E](https://github.com/pac4j/j2e-pac4j), [Apache Shiro](https://github.com/bujiio/buji-pac4j), [Spring Security](https://github.com/pac4j/spring-security-pac4j), [Ratpack](https://github.com/ratpack/ratpack/tree/master/ratpack-pac4j), [Vertx](https://github.com/pac4j/vertx-pac4j), [Undertow](https://github.com/pac4j/undertow-pac4j), [Spark Java framework](https://github.com/pac4j/spark-pac4j) or [Jooby](https://github.com/jooby-project/jooby/tree/master/jooby-pac4j). See the **"Frameworks / tools implementing `pac4j`"**.


## How to implement `pac4j` for your own framework?

### Versions

The current version **1.8.0-RC2-SNAPSHOT** is under development. Maven artefacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). See the [tests strategy](https://github.com/pac4j/pac4j/wiki/Tests).

The source code can be cloned and built locally via Maven:

```shell
git clone git@github.com:pac4j/pac4j.git
cd pac4j
mvn clean install -DskipITs
```

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.pac4j/pac4j), available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

### Implementations

`pac4j` is an easy and powerful security engine which can be used in many ways.

Add the `pac4j-core` dependency to benefit from the core API of `pac4j`. Other dependencies will be optionally added for specific support: `pac4j-oauth` for OAuth, `pac4j-cas` for CAS, `pac4j-saml` for SAML...

To secure your Java web application, **a good implementation is to create two filters**: **one to protect urls**, **the other one to receive callbacks** for stateful authentication processes ("indirect clients").

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
