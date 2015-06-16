## What is `pac4j`?

`pac4j` is a **Java authentication / authorization engine to authenticate users, get their profiles and manage their authorizations** in order to secure your Java web applications. It's available under the Apache 2 license.

It is actually **implemented by many frameworks and supports many authentication mechanisms**. See the [big picture](https://github.com/pac4j/pac4j/wiki/The-big-picture).

### Frameworks / tools implementing `pac4j`:

1. the SSO [CAS server](https://github.com/Jasig/cas) using the [cas-server-support-pac4j](https://github.com/Jasig/cas/tree/master/cas-server-support-pac4j) module (demo: [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo))
2. the [Play 2.x framework](http://www.playframework.org) using the the [play-pac4j](https://github.com/pac4j/play-pac4j) library (demos: [play-pac4j-java-demo](https://github.com/pac4j/play-pac4j-java-demo) & [play-pac4j-scala-demo](https://github.com/pac4j/play-pac4j-scala-demo))
3. any [J2E environment](http://docs.oracle.com/javaee/) using the [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) library (demo: [j2e-pac4j-demo](https://github.com/pac4j/j2e-pac4j-demo))
4. the [Apache Shiro project](http://shiro.apache.org) library using the [buji-pac4j](https://github.com/bujiio/buji-pac4j) library (demo: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo))
5. the [Spring Security](http://projects.spring.io/spring-security/) library using the [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) library (demo: [spring-security-pac4j-demo](https://github.com/pac4j/spring-security-pac4j-demo))
6. the [Ratpack](http://www.ratpack.io/) JVM toolkit using the [ratpack-pac4j](https://github.com/ratpack/ratpack/tree/master/ratpack-pac4j) module (demo: [ratpack-pac4j-demo](https://github.com/pac4j/ratpack-pac4j-demo))
7. the [Vertx](http://vertx.io/) framework using the [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) module (demo: [vertx-pac4j-demo](https://github.com/pac4j/vertx-pac4j-demo))
8. the [Undertow](http://undertow.io/) web server using the [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) module (demo: [undertow-pac4j-demo](https://github.com/pac4j/undertow-pac4j-demo))
9. the [Spark Java framework](http://sparkjava.com) using the [spark-pac4j](https://github.com/pac4j/spark-pac4j) library (demo: [spark-pac4j-demo](https://github.com/pac4j/spark-pac4j-demo))
10. the [Jooby framework](http://jooby.org) using the [jooby-pac4j](https://github.com/jooby-project/jooby/tree/master/jooby-pac4j) module (demo: [jooby-pac4j-demo](https://github.com/pac4j/jooby-pac4j-demo)).

### Supported authentication mechanisms:

`pac4j` supports stateful and stateless [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows) using external identity providers or direct internal credentials authenticator and user profile creator:

1. OAuth (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... using the `pac4j-oauth` module
2. CAS (1.0, 2.0, SAML, logout & proxy) + REST API support using the `pac4j-cas` module
3. HTTP (form, basic auth, IP, header, GET/POST parameter authentications) using the `pac4j-http` module
4. OpenID using the `pac4j-openid` module
5. SAML (2.0) using the `pac4j-saml` module
6. Google App Engine UserService using the `pac4j-gae` module
7. OpenID Connect 1.0 using the `pac4j-oidc` module

See [all authentication mechanisms](https://github.com/pac4j/pac4j/wiki/Clients).


## How to use `pac4j`

### Versions

The current version **1.8.0-SNAPSHOT** is under development. Maven artefacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and built locally via the Maven command: `mvn clean compile`. See the [tests strategy](https://github.com/pac4j/pac4j/wiki/Tests).

The latest released version is the **1.7.0**, available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).

### Implementation

`pac4j` is a powerful and easy security engine which can be used in many ways.

To secure your Java web application, a good implementation is to create two filters: one to protect urls, the other one to receive callback calls for stateful authentication processes ("indirect clients").

Gather all your clients via the `Clients` class to share the same callback url:

    FacebookClient facebookClient = new FacebookClient(FB_KEY, FB_SECRET);
    TwitterClient twitterClient = new TwitterClient(TW_KEY, TW_SECRET);
    FormClient formClient = new FormClient("http://localhost:8080/theForm.jsp", new SimpleTestUsernamePasswordAuthenticator(), new UsernameProfileCreator());
    CasClient casClient = new CasClient();
    casClient.setCasLoginUrl("http://mycasserver/login");
    Clients clients = new Clients("http://localhost:8080/callback", facebookClient, twitterClient, formClient, casClient);

In your protection filter, use the following logic:

    EnvSpecificWebContext context = new EnvSpecificWebContex(...);
    EnvSpecificProfileManager manager = new EnvSpecificProfileManager(...);
    UserProfile profile = manager.get();
    if (profile != null) {
      grantAccess();
    } else {
      saveRequestedUrl();
      Client client = clients.findClient(configName);
      try {
        client.redirect(context);
      } catch (RequiresHttpAction e) {
        handleSpecialHttpBehaviours();
      }
    }

In your callback filter:

    EnvSpecificWebContext context = new EnvSpecificWebContex(...);
    EnvSpecificProfileManager manager = new EnvSpecificProfileManager(...);
    Client client = clients.findClient(context);
    Credentials credentials;
    try {
      credentials = client.getCredentials(context);
    } catch (RequiresHttpAction e) {
      handleSpecialHttpBehaviours();
    }
    UserProfile profile = client.getUserProfile(credentials, context);
    if (profile != null) {
      maager.save(profile);
    }
    redirectToTheOriginallyRequestedUrl();

### Core concepts:

1. [Client](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java): a client is a way to authenticate, it is responsible for starting the authentication process if necessary (stateful use case), validating the user credentials and getting the user profile (all cases). A hierarchy of user profile exists: DirectClient, IndirectClient, FacebookClient, CasClient...
2. [Clients](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Clients.java): it's a helper class to define all clients together and reuse the same callback url
3. [UserProfile](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/UserProfile.java): it's the profile of an authenticated user. A hierarchy of user profiles exists: CommonProfile, OAuth20Profile, FacebookProfile...
4. [AttributesDefinition](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/AttributesDefinition.java): it's the attributes definition for a specific type of profile
5. [ProfileManager](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/ProfileManager.java): it's a manager to get/set/remove the current user profile
6. [Credentials](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java): it's a user credentials. A hierarchy of credentials exists: CasCredentials, Saml2Credentials, UsernamePasswordCredentials...
7. [WebContext](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java): it represents the current HTTP request and response, regardless of the framework
8. [AuthorizationGenerator](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/AuthorizationGenerator.java): it's a class to compute the roles and permissions for a user profile
9. [Authorizer](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/Authorizer.java): it's a generic concept to manage authorizations given a user profile and a web context
10. [Authenticator](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/credentials/authenticator/Authenticator.java): it's the interface to implement to validate user credentials. Several implementations exist
11. [ProfileCreator](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/profile/creator/ProfileCreator.java): it's the interface to implement to create a user profile from credentials. Several implementations exist.


## Any questions / Need help?

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" />
</p>
