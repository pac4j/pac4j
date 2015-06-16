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

### Core concepts:

1. [Client](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java): a client is a way to authenticate, it is responsible for starting the authentication process if necessary (stateful use case), validating the user credentials and getting the user profile (all cases). A hierarchy of user profile exists: DirectClient, IndirectClient, FacebookClient, CasClient...
2. [Clients](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Clients.java): it's a helper class to define all clients together and reuse the same callback url
3. [UserProfile](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/UserProfile.java): it's the profile of an authenticated . A hierarchy of user profiles exists: CommonProfile, OAuth20Profile, FacebookProfile...
4. [AttributesDefinition](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/AttributesDefinition.java): it's the attributes definition for a specific type of profile
5. [ProfileManager](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/ProfileManager.java): it's a manager to get/set/remove the current user profile
6. [Credentials](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java): it's a user credentials. A hierarchy of credentials exists: CasCredentials, Saml2Credentials, UsernamePasswordCredentials...
7. [WebContext](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java): it represents the current HTTP request and response, regardless of the framework
8. [AuthorizationGenerator](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/AuthorizationGenerator.java): it's a class to compute the roles and permissions for a user profile
9. [Authorizer](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/Authorizer.java): it's a generic concept to manage authorizations given a user profile and a web context
10. [Authenticator](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/credentials/authenticator/Authenticator.java): it's the interface to implement to validate user credentials. Several implementations exist
11. [ProfileCreator](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/profile/creator/ProfileCreator.java): it's the interface to implement to create a user profile from credentials. Several implementations exist.


## How to use `pac4j`

## Code sample

### Maven dependencies

First, you have define the right dependency: pac4j-oauth for OAuth support or/and pac4j-cas for CAS support or/and pac4j-http for HTTP support or/and pac4j-openid for OpenID support or/and pac4j-saml for SAML support or/and pac4j-gae for Google App Engine support or/and pac4j-oidc for OpenID Connect support.
For example:

    <dependency>
      <groupId>org.pac4j</groupId>
      <artifactId>pac4j-oauth</artifactId>
      <version>1.7.0</version>
    </dependency>

As the pac4j snapshots libraries are stored in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/), this repository may need be added in the Maven *pom.xml* file:

    <repository>
      <id>sonatype-nexus-snapshots</id>
	  <name>Sonatype Nexus Snapshots</name>
	  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
	  <releases>
	    <enabled>false</enabled>
	  </releases>
	  <snapshots>
	    <enabled>true</enabled>
	  </snapshots>
    </repository>

### OAuth support

If you want to authenticate and get the user profile from Facebook, you have to use the *org.pac4j.oauth.client.FacebookClient*:

    // declare the client (use default scope and fields)
    FacebookClient client = new FacebookClient(MY_KEY, MY_SECRET);
    // define the client application callback url
    client.setCallbackUrl("http://myserver/myapp/callbackUrl");
    // send the user to Facebook for authentication and permissions
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url (for Facebook)...

    // get OAuth credentials
    OAuthCredentials credentials = client.getCredentials(context);
    // get the facebook profile
    FacebookProfile facebookProfile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + facebookProfile.getDisplayName() + " born the " + facebookProfile.getBirthday());</code></pre>

### CAS support

For integrating an application with a CAS server, you should use the *org.pac4j.cas.client.CasClient*:

    // declare the client
    CasClient client = new CasClient();
    // define the client application callback url
    client.setCallbackUrl("http://myserver/myapp/callbackUrl");
    // send the user to the CAS server for authentication
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url...

    // get CAS credentials
    CasCredentials credentials = client.getCredentials(context);
    // get the CAS profile
    CasProfile casProfile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + casProfile.getAttribute("anAttribute"));</code></pre>

For proxy support, the *org.pac4j.cas.client.CasProxyReceptor* class must be used (on the same or new callback url) and declared within the *CasClient* class:

    casClient.setCasProxyReceptor(new CasProxyReceptor());
    // casClient.setAcceptAnyProxy(false);
    // casClient.setAllowedProxyChains(proxies);

In this case, the *org.pac4j.cas.profile.CasProxyProfile* must be used to get proxy tickets for other CAS services:

    CasProxyProfile casProxyProfile = (CasProxyProfile) casProfile;
    String proxyTicket = casProxyProfile.getProxyTicketFor(anotherCasService);

### HTTP support

To use form authentication in a web application, you should use the *org.pac4j.http.client.indirect.FormClient* class:

    // declare the client
    FormClient client = new FormClient("/myloginurl", new MyUsernamePasswordAuthenticator());
    client.setCallbackUrl("http://myserver/myapp/callbackUrl");
    // send the user to the form for authentication
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication...

    // get username/password credentials
    UsernamePasswordCredentials credentials = client.getCredentials(context);
    // get the HTTP profile
    HttpProfile httpProfile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + httpProfile.getUsername());</code></pre>

To use basic auth authentication in a web application, you should use the *org.pac4j.http.client.indirect.IndirectBasicAuthClient* class the same way:

    // declare the client
    BasicAuthClient client = new BasicAuthClient(new MyUsernamePasswordAuthenticator(), new UsernameProfileCreator());

### OpenID support

To use Yahoo and OpenID for authentication, you should use the *org.pac4j.openid.client.YahooOpenIdClient* class:

    // declare the client
    YahooOpenIdClient client = new YahooOpenIdClient();
    client.setCallbackUrl("/callbackUrl");
    // send the user to Yahoo for authentication
    WebContext context = new J2EContext(request, response);
    // we assume the user identifier is in the "openIdUser" request parameter
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url...

    // get the OpenID credentials
    OpenIdCredentials credentials = client.getCredentials(context);
    // get the YahooOpenID profile
    YahooOpenIdProfile profile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + profile.getDisplayName());

### SAML support

For integrating an application with a SAML2 Identity Provider server, you should use the *org.pac4j.saml.client.SAML2Client*:

    //Generate a keystore for all signature and encryption stuff:
    keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -keystore samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650

    // declare the client
    Saml2Client client = new Saml2Client();
    // configure keystore
    client.setKeystorePath("samlKeystore.jks");
    client.setKeystorePassword("pac4j-demo-passwd");
    client.setPrivateKeyPassword("pac4j-demo-passwd");
    // Configure a file containing the Identity Provider (IDP) metadata.
    // It is the IDP's responsibility to make its metadata freely accessible.
    client.setIdpMetadataPath("testshib-providers.xml");
    // Configure the callback url either directly or with the Clients container
    // The callback url will be the SP entity ID
    client.setCallbackUrl("http://localhost:8080/callback");

    // generate pac4j SAML2 Service Provider metadata to import on Identity Provider side
    String spMetadata = client.printClientMetadata();

    // send the user to the Identity Provider server for authentication
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication, in the Service Provider application, on the assertion consumer service url...

    // get SAML2 credentials
    Saml2Credentials credentials = client.getCredentials(context);
    // get the SAML2 profile
    Saml2Profile saml2Profile = client.getUserProfile(credentials, context);

#### Additional configuration:

Once you have an authenticated web session on the Identity Provider, usually it won't prompt you again to enter your credentials and it will automatically generate you a new assertion. By default, the SAML pac4j client will accept assertions based on a previous authentication for one hour. If you want to change this behaviour, set the maximumAuthenticationLifetime parameter:

    // Lifetime in seconds
    client.setMaximumAuthenticationLifetime(600);

By default, the entity ID of your application (the Service Provider) will be equals to the pac4j callback url. This can lead to problems with some IDP because of the query string not being accepted (like ADFS2.0). You can force your own entity ID with the serviceProviderEntityId parameter:

    // custom SP entity ID
    client.setSpEntityId("http://localhost:8080/callback");

### Gae User Service support

To use the Google App Engine authentication, you should use the *org.pac4j.gae.client.GaeUserServiceClient* class:

    // declare the client
    GaeUserServiceClient client = new GaeUserServiceClient();
    client.setCallbackUrl("/callbackUrl");
    // send the user to Google for authentication
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url...

    // get the OpenID credentials
    GaeUserServiceCredentials credentials = client.getCredentials(context);
    // get the GooglOpenID profile
    GaeUserServiceProfile profile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + profile.getDisplayName());

### OpenID Connect support

For integrating an application with an OpenID Connect Provider server, you should use the *org.pac4j.oidc.client.OidcClient*:

    // build the client
    final OidcClient client = new OidcClient();
    // set client ID
    client.setClientID("xxx.apps.googleusercontent.com");
    // set secret
    client.setSecret("yyy");
    // set discovery URI
    client.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");

    // send the user to the OpenID Connect Provider server for authentication
    WebContext context = new J2EContext(request, response);
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url...

    // get OpenID Connect credentials
    OidcCredentials credentials = client.getCredentials(context);
    // get the OpenID Connect profile
    OidcProfile oidcProfile = client.getUserProfile(credentials, context);

#### Additional configuration:

You can set additional parameters by using the `addCustomParam(String key, String value)` method. For instance:

    // use nonce in authentication request
    client.addCustomParam("useNonce", "true");
    // select display mode: page, popup, touch, and wap
    client.addCustomParam("display", "popup");
    // select prompt mode: none, consent, select_account
    client.addCustomParam("prompt", "none");

### Multiple clients

If you use multiple clients, you can use more generic objects. All profiles inherit from the *org.pac4j.core.profile.CommonProfile* class:

    // get credentials
    Credentials credentials = client.getCredentials(context);
    // get the common profile
    CommonProfile commonProfile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + commonProfile.getFirstName());

If you want to interact more with the OAuth providers (like Facebook), you can retrieve the access token from the (OAuth) profiles:

    OAuthProfile oauthProfile = (OAuthProfile) commonProfile;
    String accessToken = oauthProfile.getAccessToken();
    // or
    String accesstoken = facebookProfile.getAccessToken();

You can also group all clients on a single callback url by using the *org.pac4j.core.client.Clients* class:

    Clients clients = new Clients("http://server/app/callbackUrl", fbClient, casClient, formClient yahooOpenIdClient, samlClient);
    // on the callback url, retrieve the right client
    Client client = clients.findClient(context);

### Error handling

All methods of the clients may throw an unchecked *org.pac4j.core.exception.TechnicalException*, which could be trapped by an appropriate try/catch.
The *getRedirectionUrl(WebContext,boolean,boolean)* and the *getCredentials(WebContext)* methods may also throw a checked *org.pac4j.core.exception.RequiresHttpAction*, to require some additionnal HTTP action (redirection, basic auth...)

### Authorizations

Although the primary target of the pac4j library is to deal with authentication, authorizations can be handled as well.

After a successful authentication at a provider, the associated client can generate roles, permissions and a "remembered" status. These information are available in every user profile.

The generation of this information is controlled by a class implementing the *org.pac4j.core.authorization.AuthorizationGenerator* interface and set for this client.

    FromAttributesAuthorizationGenerator authGenerator = new FromAttributesAuthorizationGenerator(new String[]{"attribRole1"}, new String[]{"attribPermission1"})
    client.setAuthorizationGenerator(authGenerator);


## Versions

The current version **1.8.0-SNAPSHOT** is under development. Maven artefacts are built via Travis: [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j).

The source code can be cloned and built locally via the Maven command: `mvn clean compile`. See the [tests strategy](https://github.com/pac4j/pac4j/wiki/Tests).

The latest released version is the **1.7.0**, available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j-). See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).


## Contact

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" />
</p>
