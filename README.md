<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo.png" />
</p>

- [What is pac4j ?](#what-is-pac4j--)
- [The "big picture"](#the-big-picture)
- [Technical description](#technical-description)
- [Providers supported](#providers-supported)
- [Code sample](#code-sample)
- [Libraries built with pac4j](#libraries-built-with-pac4j)
- [Versions](#versions)
- [Testing](#testing)
- [Bugs / Features tracking](#bugs--features-tracking)
- [Contact](#contact)



## What is pac4j ? [![Build Status](https://travis-ci.org/pac4j/pac4j.png?branch=master)](https://travis-ci.org/pac4j/pac4j)

**pac4j** is a Profile & Authentication Client for Java, it's a general security library to authenticate users, get their profiles, manage their authorizations in order to secure web applications for all Java frameworks.


### Main concepts

- **Client**: an authentication client is responsible for starting the authentication process, getting the user credentials and the user profile
- **UserProfile**: it's the profile of an authenticated user (a hierarchy of user profiles exists: CommonProfile, OAuth20Profile, FacebookProfile...)
- **AttributesDefinition**: it's the attributes definition for a specific type of profile
- **Credentials**: it's a user credentials (a hierarchy of credentials exists: CasCredentials, Saml2Credentials, UsernamePasswordCredentials...)
- **Clients**: it's a helper class to define all clients together
- **Context**: it represents the current HTTP request and response, regardless of the framework
- **AuthorizationGenerator**: it's a class to compute the roles and permissions of a user from his profile
- **Authenticator**: it's a method to validate user credentials
- **ProfileCreator**: it's a method to create a user profile from credentials.


### Supported authentication methods

Although **pac4j** historically targets external authentication protocols, it supports direct authentication methods as well. See the [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows).

#### External/stateful authentication protocols

1. From the client application, save the requested url and redirect the user to the identity provider for authentication (HTTP 302)
2. After a successful authentication, redirect back the user from the identity provider to the client application (HTTP 302) and get the user credentials
3. With these credentials, get the profile of the authenticated user (direct call from the client application to the identity provider)
4. Redirect the user to the originally requested url and allow or disallow the access.

Supported protocols are:

1. OAuth (1.0 & 2.0)
2. CAS (1.0, 2.0, SAML, logout & proxy)
3. HTTP (form & basic auth authentications)
4. OpenID
5. SAML (2.0)
6. Google App Engine UserService
7. OpenID Connect 1.0

[Example of the CAS flow](https://github.com/pac4j/pac4j/wiki/CAS-flow)

#### Stateless authentication protocols (REST operations)

The current HTTP request contains the required credentials to validate the user identity and retrieve his profile. It works from a basic authentication.

It relies on specific **Authenticator** to validate user credentials and **ProfileCreator** to create user profiles.


### Implementations

**pac4j** is primarily meant to be used through its implementations in the following frameworks and environments:

1. the CAS server (using the *cas-server-support-pac4j* library)
2. the Play 2.x framework (using the *play-pac4j_java* and *play-pac4j_scala* libraries)
3. any basic J2E environment (using the *j2e-pac4j* library)
4. the Apache Shiro library (using the *buji-pac4j* library)
5. the Spring Security library (using the *spring-security-pac4j* library)
6. the Ratpack JVM toolkit (using the *ratpack-pac4j* module)
7. the Vertx framework (using the *vertx-pac4j* module)
8. the Undertow web server (using the *undertow-pac4j* module)
9. the Spark Java framework (using the *spark-pac4j* module).


### Open source

It's available under the Apache 2 license.


## The "big picture"

<img src="https://pac4j.github.io/pac4j/img/pac4j.png" />


## Technical description

This Maven project is composed of 9 modules:

#### pac4j-core

This is the core module of the project with the core classes/interfaces:

* the *Client* interface is the **main API of the project** as it defines the mechanism that all clients must follow: redirect(WebContext,boolean, boolean), getCredentials(WebContext) and getUserProfile(Credentials,WebContext)
* the *Credentials* class is the base class for all credentials
* the *UserProfile* class is the base class for all user profiles (it is associated with attributes definition and converters)
* the *CommonProfile* class inherits from the *UserProfile* class and implements all the common getters that profiles must have (getFirstName(), getEmail()...)
* the *WebContext* interface represents a web context which can be implemented in a J2E or another environment.

#### pac4j-oauth

This module is dedicated to OAuth client support, it's the successor of the <b>scribe-up</b> library:

* the *FacebookClient*, *TwitterClient*... classes are the clients for all the providers: Facebook, Twitter...
* the *OAuthCredentials* class is the credentials for OAuth support
* the *FacebookProfile*, *TwitterProfile*... classes are the associated profiles, returned by the clients.

This module is based on the **pac4j-core** module, the [scribe-java](https://github.com/fernandezpablo85/scribe-java) library for OAuth protocol support, the [Jackson](https://github.com/FasterXML/jackson-core) library for JSON parsing and the [commons-lang3](http://commons.apache.org/lang/) library.

#### pac4j-cas

This module is dedicated to CAS client support:

* the *CasClient* class is the client for CAS server (the *CasProxyReceptor* is dedicated to CAS proxy support)
* the *CasCredentials* class is the credentials for CAS support
* the *CasProfile* class is the user profile returned by the *CasClient*.

This module is based on the **pac4j-core** module and the [Jasig CAS client](https://github.com/Jasig/java-cas-client).

#### pac4j-http

This module is dedicated to HTTP protocol support:

* the *FormClient* & *BasicAuthClient* classes are the client for form and basic auth authentications
* the *UsernamePasswordCredentials* class is the username/password credentials in HTTP support
* the *HttpProfile* class is the user profile returned by the *FormClient* and *BasicAuthClient*.

This module is based on the **pac4j-core** module and the [commons-codec](http://commons.apache.org/codec/) library.

#### pac4j-openid

This module is dedicated to OpenID protocol support:

* the *YahooOpenIdClient* is the client for Yahoo
* the *OpenIdCredentials* class is the credentials for OpenID support
* the *YahooOpenIdProfile* class is the associated profile, returned by the client.

This module is based on the **pac4j-core** module and the [openid4java](http://code.google.com/p/openid4java/) library.

#### pac4j-saml

This module is dedicated to SAML support:

* the *Saml2Client* class is the client for integrating with a SAML2 compliant Identity Provider
* the *Saml2Credentials* class is the credentials for SAML2 support
* the *Saml2Profile* class is the user profile returned by the *Saml2Client*.

This module is based on the **pac4j-core** module and the [OpenSAML library](https://wiki.shibboleth.net/confluence/display/OpenSAML/Home).

In case you use the library against Microsoft ADFS (Active Directory Federation Services), a SAML Identity Provider server, please have a look into
file README-ADFS.txt for details on how to setup your client.

#### pac4j-gae

This module is dedicated to Gae connexion login mechanism support:

* the *GaeUserServiceClient* is the client for Gae
* the *GaeUserServiceCredentials* class is the credentials for gae support
* the *GaeUserServiceProfile* class is the associated profile, returned by the client.

This module is based on the **pac4j-core** module and the [Google App Engine API](http://appengine.google.com) library.

#### pac4j-oidc

This module is dedicated to OpenID Connect 1.0 support:

* the *OidcClient* class is the client for integrating with an OpenID Connect compliant Provider
* the *OidcCredentials* class is the credentials for OpenID Connect support
* the *OidcProfile* class is the user profile returned by the *OidcClient*.

This module is based on the **pac4j-core** module and the excellent [Nimbus OAuth 2.0 SDK with OpenID Connect extensions](http://connect2id.com/products/nimbus-oauth-openid-connect-sdk).

#### pac4j-test-cas

This module is made to test CAS support in pac4j.

Learn more by browsing the [Javadoc](https://pac4j.github.io/pac4j/apidocs/pac4j/index.html).


## Providers supported

<table>
<tr><th>Provider</th><th>Protocol</th><th>Maven dependency</th><th>Client class</th><th>Profile class</th></tr>
<tr><td>CAS server</td><td>CAS</td><td>pac4j-cas</td><td>CasClient & CasProxyReceptor</td><td>CasProfile</td></tr>
<tr><td>CAS server using OAuth Wrapper</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>CasOAuthWrapperClient</td><td>CasOAuthWrapperProfile</td></tr>
<tr><td>DropBox</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>DropBoxClient</td><td>DropBoxProfile</td></tr>
<tr><td>Facebook</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FacebookClient</td><td>FacebookProfile</td></tr>
<tr><td>GitHub</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>GitHubClient</td><td>GitHubProfile</td></tr>
<tr><td>Google</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>Google2Client</td><td>Google2Profile</td></tr>
<tr><td>LinkedIn</td><td>OAuth 1.0 & 2.0</td><td>pac4j-oauth</td><td>LinkedInClient & LinkedIn2Client</td><td>LinkedInProfile & LinkedIn2Profile</td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>TwitterClient</td><td>TwitterProfile</td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WindowsLiveClient</td><td>WindowsLiveProfile</td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WordPressClient</td><td>WordPressProfile</td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>YahooClient</td><td>YahooProfile</td></tr>
<tr><td>PayPal</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>PayPalClient</td><td>PayPalProfile</td></tr>
<tr><td>Vk</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>VkClient</td><td>VkProfile</td></tr>
<tr><td>Foursquare</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FoursquareClient</td><td>FoursquareProfile</td></tr>
<tr><td>Bitbucket</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>BitbucketClient</td><td>BitbucketProfile</td></tr>
<tr><td>ORCiD</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>OrcidClient</td><td>OrcidProfile</td></tr>
<tr><td>Strava</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>StravaClient</td><td>StravaProfile</td></tr>
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>Yahoo</td><td>OpenID</td><td>pac4j-openid</td><td>YahooOpenIdClient</td><td>YahooOpenIdProfile</td></tr>
<tr><td>SAML Identity Provider</td><td>SAML 2.0</td><td>pac4j-saml</td><td>Saml2Client</td><td>Saml2Profile</td></tr>
<tr><td>Google App Engine User Service</td><td>Gae User Service Mechanism</td><td>pac4j-gae</td><td>GaeUserServiceClient</td><td>GaeUserServiceProfile</td></tr>
<tr><td>OpenID Connect Provider</td><td>OpenID Connect 1.0</td><td>pac4j-oidc</td><td>OidcClient</td><td>OidcProfile</td></tr>
</table>


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

To use form authentication in a web application, you should use the *org.pac4j.http.client.FormClient* class:

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

To use basic auth authentication in a web application, you should use the *org.pac4j.http.client.BasicAuthClient* class the same way:

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


## Libraries built with pac4j

Even if you can use **pac4j** on its own, this library is used to be integrated with:

1. the [cas-server-support-pac4j](https://wiki.jasig.org/pages/viewpage.action?pageId=57577635) module to add multi-protocols client support to the [CAS server](http://www.jasig.org/cas)
2. the [play-pac4j](https://github.com/pac4j/play-pac4j) library to add multi-protocols client support to the [Play 2.x framework](http://www.playframework.org/) in Java and Scala
2. the [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) library to add multi-protocols client support to the [J2E environment](http://docs.oracle.com/javaee/)
3. the [buji-pac4j](https://github.com/bujiio/buji-pac4j) library to add multi-protocols client support to the [Apache Shiro project](http://shiro.apache.org)
4. the [spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j) library to add multi-protocols client support to [Spring Security](http://static.springsource.org/spring-security/site/)
5. the [ratpack-pac4j](https://github.com/ratpack/ratpack/tree/master/ratpack-pac4j) module to add multi-protocols client support to [Ratpack](http://www.ratpack.io/)
6. the [vertx-pac4j](https://github.com/pac4j/vertx-pac4j) module to add multi-protocols client support to [Vertx](http://vertx.io/)
7. the [undertow-pac4j](https://github.com/pac4j/undertow-pac4j) module to add multi-protocols client support to [Undertow](http://undertow.io/)
 
<table>
<tr><th>Integration library</th><th>Protocol(s) supported</th><th>Based on</th><th>Demo webapp</th></tr>
<tr><td>cas-server-support-pac4j 4.0.0</td><td>OAuth / CAS / OpenID</td><td>pac4j 1.4.1</td><td><a href="https://github.com/leleuj/cas-pac4j-oauth-demo">cas-pac4-oauth-demo</a></td></tr>
<tr><td>play-pac4j 1.4.0 / 1.2.3 / 1.1.5</td><td>OAuth / CAS / OpenID Connect / HTTP / SAML / GAE</td><td>pac4j 1.7.0</td><td><a href="https://github.com/pac4j/play-pac4j-java-demo">play-pac4j-java-demo</a><br /><a href="https://github.com/pac4j/play-pac4j-scala-demo">play-pac4j-scala-demo</a></td></tr>
<tr><td>j2e-pac4j 1.1.0</td><td>OAuth / CAS / OpenID / HTTP / SAML / GAE / OpenID Connect</td><td>pac4j 1.7.0</td><td><a href="https://github.com/pac4j/j2e-pac4j-demo">j2e-pac4j-demo</a></td></tr>
<tr><td>buji-pac4j 1.3.1</td><td>OAuth / CAS / OpenID / HTTP / SAML / GAE / OpenID Connect</td><td>pac4j 1.7.0</td><td><a href="https://github.com/pac4j/buji-pac4j-demo">buji-pac4j-demo</a></td></tr>
<tr><td>spring-security-pac4j 1.2.5</td><td>OAuth / CAS / OpenID / HTTP / SAML / GAE / OpenID Connect</td><td>pac4j 1.7.0</td><td><a href="https://github.com/pac4j/spring-security-pac4j-demo">spring-security-pac4j-demo</a></td></tr>
<tr><td>ratpack 0.9.7</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.5.1</td><td><a href="https://github.com/pac4j/ratpack-pac4j-demo">ratpack-pac4j-demo</a></td></tr>
<tr><td>vertx-pac4j 1.1.0</td><td>OAuth / CAS / OpenID / HTTP / SAML / GAE / OpenID Connect</td><td>pac4j 1.7.0</td><td><a href="https://github.com/pac4j/vertx-pac4j-demo">vertx-pac4j-demo</a></td></tr>
</table>


## Versions

The current version **1.7.1-SNAPSHOT** is under development.  
The build is done on Travis: [https://travis-ci.org/pac4j/pac4j](https://travis-ci.org/pac4j/pac4j).
The generated artifacts are available on the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j) as a Maven dependency.

The last released version is the **1.7.0**:

    <dependency>
        <groupId>org.pac4j</groupId>
        <artifactId>pac4j-core</artifactId>
        <version>1.7.0</version>
    </dependency>

See the [release notes](https://github.com/pac4j/pac4j/wiki/Versions).



## Testing

pac4j is tested by more than 400:
- unit and bench tests launched by *mvn test*
- integration tests (authentication processes are fully simulated using the [HtmlUnit](http://htmlunit.sourceforge.net/) library) launched by *mvn verify*.


## Bugs / Features tracking

Bugs and new features can be tracked using Github issues.


## Contact

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
