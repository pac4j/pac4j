<p align="center">
  <img src="http://www.pac4j.org/img/logo.png" />
</p>

- [What is pac4j ?](#what-is-pac4j--)
- [The "big picture"](#the-big-picture)
- [Sequence diagram (example: CAS)](#sequence-diagram-example-cas)
- [Technical description](#technical-description)
    - [pac4j-core](#pac4j-core)
    - [pac4j-oauth](#pac4j-oauth)
    - [pac4j-cas](#pac4j-cas)
    - [pac4j-http](#pac4j-http)
    - [pac4j-openid](#pac4j-openid)    
    - [pac4j-saml](#pac4j-saml)
    - [pac4j-test-cas](#pac4j-test-cas)
- [Providers supported](#providers-supported)
- [Code sample](#code-sample)
    - [Maven dependencies](#maven-dependencies)
    - [OAuth support](#oauth-support)
    - [CAS support](#cas-support)
    - [HTTP support](#http-support)
    - [OpenID support](#openid-support)
    - [SAML support](#saml-support)
    - [Multiple clients](#multiple-clients)
    - [Error handling](#error-handling)
    - [Authorizations](#authorizations)
- [Libraries built with pac4j](#libraries-built-with-pac4j)
- [Versions](#versions)
- [Testing](#testing)
- [Bugs / Features tracking](#bugs--features-tracking)
- [Contact](#contact)


## What is pac4j ? [![Build Status](https://travis-ci.org/leleuj/pac4j.png?branch=master)](https://travis-ci.org/leleuj/pac4j) 

**pac4j** is a Profile & Authentication Client for Java (it's a global rebuilding of the *scribe-up* library). It targets all the protocols supporting the following mechanism:

1. From the client application, redirect the user to the "provider" for authentication (HTTP 302)
2. After successful authentication, redirect back the user from the "provider" to the client application (HTTP 302) and get the user credentials
3. With these credentials, get the profile of the authenticated user (direct call from the client application to the "provider").

It has a **very simple and unified API** to support these 5 protocols on client side: 

1. OAuth (1.0 & 2.0)
2. CAS (1.0, 2.0, SAML, logout & proxy)
3. HTTP (form & basic auth authentications)
4. OpenID
5. SAML (2.0) (*still experimental*)

There are 6 libraries implementing **pac4j** for the following environments:

1. the CAS server (using the *cas-server-support-pac4j* library)
2. the Play 2.x framework (using the *play-pac4j_java* and *play-pac4j_scala* libraries)
3. any basic J2E environment (using the *j2e-pac4j* library)
4. the Apache Shiro library (using the *buji-pac4j* library)
5. the Spring Security library (using the *spring-security-pac4j* library)
6. the Ratpack JVM toolkit (using the *ratpack-pac4j* module).

It's available under the Apache 2 license.


## The "big picture"

<img src="http://www.pac4j.org/img/pac4j.png" />


## Sequence diagram (example: CAS)

<img src="http://www.pac4j.org/img/sequence_diagram.jpg" />


## Technical description

This Maven project is composed of 6 modules:

#### pac4j-core

This is the core module of the project with the core classes/interfaces:

* the *Client* interface is the **main API of the project** as it defines the mechanism that all clients must follow: redirect(WebContext,boolean,boolean), getCredentials(WebContext) and getUserProfile(Credentials,WebContext)
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

* the *GoogleOpenIdClient* is the client for Google
* the *OpenIdCredentials* class is the credentials for OpenID support
* the *GoogleOpenIdProfile* class is the associated profile, returned by the client.

This module is based on the **pac4j-core** module and the [openid4java](http://code.google.com/p/openid4java/) library.

#### pac4j-saml

This module is dedicated to SAML support:

* the *Saml2Client* class is the client for integrating with a SAML2 compliant Identity Provider
* the *Saml2Credentials* class is the credentials for SAML2 support
* the *Saml2Profile* class is the user profile returned by the *Saml2Client*.

This module is based on the **pac4j-core** module and the [OpenSAML library](https://wiki.shibboleth.net/confluence/display/OpenSAML/Home).

#### pac4j-test-cas

This module is made to test CAS support in pac4j.

Learn more by browsing the [Javadoc](http://www.pac4j.org/apidocs/pac4j/index.html).


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
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>Google</td><td>OpenID</td><td>pac4j-openid</td><td>GoogleOpenIdClient</td><td>GoogleOpenIdProfile</td></tr>
<tr><td>SAML Identity Provider</td><td>SAML 2.0</td><td>pac4j-saml</td><td>Saml2Client</td><td>Saml2Profile</td></tr>
</table>


## Code sample

### Maven dependencies

First, you have define the right dependency: pac4j-oauth for OAuth support or/and pac4j-cas for CAS support or/and pac4j-http for HTTP support or/and pac4j-openid for OpenID support or/and pac4j-saml for SAML support.
For example:

    <dependency>
      <groupId>org.pac4j</groupId>
      <artifactId>pac4j-oauth</artifactId>
      <version>1.5.0</version>
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

To use Google and OpenID for authentication, you should use the *org.pac4j.openid.client.GoogleOpenIdClient* class:

    // declare the client
    GoogleOpenIdClient client = new GoogleOpenIdClient();
    client.setCallbackUrl("/callbackUrl");
    // send the user to Google for authentication
    WebContext context = new J2EContext(request, response);
    // we assume the user identifier is in the "openIdUser" request parameter
    client.redirect(context, false, false);

...after successful authentication, in the client application, on the callback url...

    // get the OpenID credentials
    OpenIdCredentials credentials = client.getCredentials(context);
    // get the GooglOpenID profile
    GoogleOpenIdProfile profile = client.getUserProfile(credentials, context);
    System.out.println("Hello: " + profile.getDisplayName());

### SAML support

For integrating an application with a SAML2 Identity Provider server, you should use the *org.pac4j.saml.client.Saml2Client*:

    //Generate a keystore for all signature and encryption stuff:
    keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -keystore samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
    
    // declare the client
    Saml2Client client = new Saml2Client();
    // configure keystore
    client.setKeystorePath("samlKeystore.jks");
    client.setKeystorePassword("pac4j-demo-passwd");
    client.setPrivateKeyPassword("pac4j-demo-passwd");
    // configure a file containing the Identity Provider metadata 
    client.setIdpMetadataPath("testshib-providers.xml");

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

    Clients clients = new Clients("http://server/app/callbackUrl", fbClient, casClient, formClient googleOpenIdClient, samlClient);
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
2. the [play-pac4j](https://github.com/leleuj/play-pac4j) library to add multi-protocols client support to the [Play 2.x framework](http://www.playframework.org/) in Java and Scala
2. the [j2e-pac4j](https://github.com/leleuj/j2e-pac4j) library to add multi-protocols client support to the [J2E environment](http://docs.oracle.com/javaee/)
3. the [buji-pac4j](https://github.com/bujiio/buji-pac4j) library to add multi-protocols client support to the [Apache Shiro project](http://shiro.apache.org)
4. the [spring-security-pac4j](https://github.com/leleuj/spring-security-pac4j) library to add multi-protocols client support to [Spring Security](http://static.springsource.org/spring-security/site/)
5. the [ratpack-pac4j](https://github.com/ratpack/ratpack/tree/master/ratpack-pac4j) module to add multi-protocols client support to [Ratpack](http://www.ratpack.io/)

<table>
<tr><th>Integration library</th><th>Protocol(s) supported</th><th>Based on</th><th>Demo webapp</th></tr>
<tr><td>cas-server-support-pac4j 4.0.0-RC4</td><td>OAuth / CAS / OpenID</td><td>pac4j 1.4.1</td><td><a href="https://github.com/leleuj/cas-pac4j-oauth-demo">cas-pac4-oauth-demo</a></td></tr>
<tr><td>cas-server-support-oauth 3.5.2</td><td>OAuth</td><td>scribe-up 1.2.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo-3.5.x">cas-oauth-demo-3.5.x</a></td></tr>
<tr><td>play-pac4j 1.2.0 / 1.1.2</td><td>OAuth / CAS / OpenID / HTTP / SAML</td><td>pac4j 1.5.0</td><td><a href="https://github.com/leleuj/play-pac4j-java-demo">play-pac4j-java-demo</a><br /><a href="https://github.com/leleuj/play-pac4j-scala-demo">play-pac4j-scala-demo</a></td></tr>
<tr><td>j2e-pac4j 1.0.2</td><td>OAuth / CAS / OpenID / HTTP / SAML</td><td>pac4j 1.5.0</td><td><a href="https://github.com/leleuj/j2e-pac4j-demo">j2e-pac4j-demo</a></td></tr>
<tr><td>buji-pac4j 1.2.2</td><td>OAuth / CAS / OpenID / HTTP / SAML</td><td>pac4j 1.5.0</td><td><a href="https://github.com/leleuj/buji-pac4j-demo">buji-pac4j-demo</a></td></tr>
<tr><td>spring-security-pac4j 1.2.2</td><td>OAuth / CAS / OpenID / HTTP / SAML</td><td>pac4j 1.5.0</td><td><a href="https://github.com/leleuj/spring-security-pac4j-demo">spring-security-pac4j-demo</a></td></tr>
<tr><td>ratpack 0.9.3</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.4.1</td><td><a href="https://github.com/leleuj/ratpack-pac4j-demo">ratpack-pac4j-demo</a></td></tr>
</table>


## Versions

The current version **1.5.1-SNAPSHOT** is under development.  
The build is done on Travis: [https://travis-ci.org/leleuj/pac4j](https://travis-ci.org/leleuj/pac4j).   
The generated artifacts are available on the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j) as a Maven dependency.

The last released version is the **1.5.0**:

    <dependency>
        <groupId>org.pac4j</groupId>
        <artifactId>pac4j-core</artifactId>
        <version>1.5.0</version>
    </dependency>

See the [release notes](https://github.com/leleuj/pac4j/wiki/Versions).



## Testing

pac4j is tested by more than 400 unit, bench and integration tests (authentication processes are completely simulated using the [HtmlUnit](http://htmlunit.sourceforge.net/) library).

To launch the tests, the **nr** Maven profile should be used. For example:

    mvn clean install -Pnr

Use the **js** Maven profile for Javadoc and sources generation.


## Bugs / Features tracking

Bugs and new features can now be tracked using [JIRA](https://pac4jos.atlassian.net/secure/Dashboard.jspa?selectPageId=10100).


## Contact

If you have any question, please use the following mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

