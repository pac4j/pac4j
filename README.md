## What is pac4j ?

**pac4j** is a Profile & Authentication Client for Java (it's a global rebuilding of the *scribe-up* library). It targets all the protocols supporting the following mechanism :

1. From the client application, redirect the user to the "provider" for authentication (HTTP 302)
2. After successful authentication, redirect back the user from the "provider" to the client application (HTTP 302) and get the user credentials
3. With these credentials, get the profile of the authenticated user (direct call from the client application to the "provider").

It has a **very simple and unified API** to support these 4 protocols on client side : 

1. OAuth (1.0 & 2.0)
2. CAS (1.0, 2.0, SAML, logout & proxy)
3. HTTP (form & basic auth authentications)
4. OpenID.

There are 5 libraries implementing **pac4j** for the following environments :

1. the CAS server (using the *cas-server-support-pac4j* library)
2. the Play 2.x framework (using the *play-pac4j_java*, *play-pac4j_scala2.9* and *play-pac4j_scala2.10* libraries)
3. any basic J2E environment (using the *j2e-pac4j* library)
4. the Apache Shiro library (using the *buji-pac4j* library)
5. the Spring Security library (using the *spring-security-pac4j* library).

It's available under the Apache 2 license.


## The "big picture"

<img src="http://www.pac4j.org/img/pac4j.png" />


## Technical description

This Maven project is composed of 6 modules :
<ol>
<li><b>pac4j-core</b> : this is the core module of the project with the core classes/interfaces :
<ul>
<li>the <i>Client</i> interface is the <b>main API of the project</b> as it defines the mechanism that all clients must follow : getRedirectionUrl(WebContext), getCredentials(WebContext) and getUserProfile(Credentials)</li>
<li>the <i>Credentials</i> class is the base class for all credentials</li>
<li>the <i>UserProfile</i> class is the base class for all user profiles (it is associated with attributes definition and converters)</li>
<li>the <i>CommonProfile</i> class inherits from the <i>UserProfile</i> class and implements all the common getters that profiles must have (getFirstName(), getEmail()...)</li>
<li>the <i>WebContext</i> interface represents a web context which can be implemented in a J2E or another environment.</li>
</ul>
<br />
</li>
<li><b>pac4j-oauth</b> : this module is dedicated to OAuth client support, it's the successor of the <b>scribe-up</b> library :
<ul>
<li>the <i>FacebookClient</i>, <i>TwitterClient</i>... classes are the clients for all the providers : Facebook, Twitter...</li>
<li>the <i>OAuthCredentials</i> class is the credentials for OAuth support</li>
<li>the <i>FacebookProfile</i>, <i>TwitterProfile</i>... classes are the associated profiles, returned by the clients.</li>
</ul>
<br />
This module is based on the <b>pac4j-core</b> module, the <a href="https://github.com/fernandezpablo85/scribe-java">scribe-java</a> library for OAuth protocol support, the <a href="https://github.com/FasterXML/jackson-core">Jackson</a> library for JSON parsing and the <a href="http://commons.apache.org/lang/">commons-lang3</a> library.
<br />
<br />
</li>
<li><b>pac4j-cas</b> : this module is dedicated to CAS client support :
<ul>
<li>the <i>CasClient</i> class is the client for CAS server (the <i>CasProxyReceptor</i> is dedicated to CAS proxy support)</li>
<li>the <i>CasCredentials</i> class is the credentials for CAS support</li>
<li>the <i>CasProfile</i> class is the user profile returned by the <i>CasClient</i>.</li>
</ul>
<br />
This module is based on the <b>pac4j-core</b> module and the <a href="https://github.com/Jasig/java-cas-client">Jasig CAS client</a>.
<br />
<br />
</li>
<li><b>pac4j-http</b> : this module is dedicated to HTTP protocol support :
<ul>
<li>the <i>FormClient</i> & <i>BasicAuthClient</i> classes are the client for form and basic auth authentications</li>
<li>the <i>UsernamePasswordCredentials</i> class is the username/password credentials in HTTP support</li>
<li>the <i>HttpProfile</i> class is the user profile returned by the <i>FormClient</i> and <i>BasicAuthClient</i>.</li>
</ul>
<br />
This module is based on the <b>pac4j-core</b> module and the <a href="http://commons.apache.org/codec/">commons-codec</a> library.
<br />
<br />
</li>
<li><b>pac4j-openid</b> : this module is dedicated to OpenID protocol support :
<ul>
<li>the <i>MyOpenIdClient</i> class is dedicated to MyOpenId</li>
<li>the <i>OpenIdCredentials</i> class is the credentials for OpenID support</li>
<li>the <i>MyOpenIdProfile</i> class is the user profile for MyOpenId.</li>
</ul>
<br />
This module is based on the <b>pac4j-core</b> module and the <a href="http://code.google.com/p/openid4java/">openid4java</a> library.
<br />
<br />
</li>
<li><b>pac4j-test-cas</b> : this module is made to test CAS support in pac4j.</li>
</ol>

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
<tr><td>LinkedIn</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>LinkedInClient</td><td>LinkedInProfile</td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>TwitterClient</td><td>TwitterProfile</td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WindowsLiveClient</td><td>WindowsLiveProfile</td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WordPressClient</td><td>WordPressProfile</td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>YahooClient</td><td>YahooProfile</td></tr>
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>MyOpenId</td><td>OpenID</td><td>pac4j-openid</td><td>MyOpenIdClient</td><td>MyOpenIdProfile</td></tr>
</table>


<h2>Code sample</h2>

<h3>Maven dependencies</h3>

First, you have define the right dependency : pac4j-oauth for OAuth support or/and pac4j-cas for CAS support or/and pac4j-http for HTTP support or/and pac4j-openid for OpenID support.
For example :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;org.pac4j&lt;/groupId&gt;
    &lt;artifactId&gt;pac4j-oauth&lt;/artifactId&gt;
    &lt;version&gt;1.4.0&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

As the pac4j snapshots libraries are stored in the <a href="https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/">Sonatype snapshots repository</a>, this repository may be added in the Maven <i>pom.xml</i> file :
<pre><code>&lt;repository&gt;
			&lt;id&gt;sonatype-nexus-snapshots&lt;/id&gt;
			&lt;name&gt;Sonatype Nexus Snapshots&lt;/name&gt;
			&lt;url&gt;https://oss.sonatype.org/content/repositories/snapshots&lt;/url&gt;
			&lt;releases&gt;
				&lt;enabled&gt;false&lt;/enabled&gt;
			&lt;/releases&gt;
			&lt;snapshots&gt;
				&lt;enabled&gt;true&lt;/enabled&gt;
			&lt;/snapshots&gt;
&lt;/repository&gt;</code></pre>

<h3>OAuth support</h3>

If you want to authenticate and get the user profile from Facebook, you have to use the <i>org.pac4j.oauth.client.FacebookClient</i> :
<pre><code>// declare the client (use default scope and fields)
FacebookClient client = new FacebookClient(MY_KEY, MY_SECRET);
// define the client application callback url
client.setCallbackUrl("http://myserver/myapp/callbackUrl");
// send the user to Facebook for authentication and permissions
response.sendRedirect(client.getRedirectionUrl(new J2EContext(request, response)));</code></pre>
...after successfull authentication, in the client application, on the callback url (for Facebook)...
<pre><code>// get OAuth credentials
OAuthCredentials credentials = client.getCredentials(new J2EContext(request, response)));
// get the facebook profile
FacebookProfile facebookProfile = client.getUserProfile(credentials);
System.out.println("Hello : " + facebookProfile.getDisplayName() + " born the " + facebookProfile.getBirthday());</code></pre>

<h3>CAS support</h3>

For integrating an application with a CAS server, you should use the <i>org.pac4j.cas.client.CasClient</i> :
<pre><code>// declare the client
CasClient client = new CasClient();
// define the client application callback url
client.setCallbackUrl("http://myserver/myapp/callbackUrl");
// send the user to the CAS server for authentication
response.sendRedirect(client.getRedirectionUrl(new J2EContext(request, response)));</code></pre>
...after successfull authentication, in the client application, on the callback url...
<pre><code>// get CAS credentials
CasCredentials credentials = client.getCredentials(new J2EContext(request, response)));
// get the CAS profile
CasProfile casProfile = client.getUserProfile(credentials);
System.out.println("Hello : " + casProfile.getAttribute("anAttribute"));</code></pre>

For proxy support, the <i>org.pac4j.cas.client.CasProxyReceptor</i> class must be used (on the same or new callback url) and declared with the <i>CasClient</i> class :
<pre><code>casClient.setCasProxyReceptor(new CasProxyReceptor());
// casClient.setAcceptAnyProxy(false);
// casClient.setAllowedProxyChains(proxies);</code></pre>
In this case, the <i>org.pac4j.cas.profile.CasProxyProfile</i> must be used to get proxy tickets for other CAS services :
<pre><code>CasProxyProfile casProxyProfile = (CasProxyProfile) casProfile;
String proxyTicket = casProxyProfile.getProxyTicketFor(anotherCasService);</code></pre>


<h3>HTTP support</h3>

To use form authentication in a web application, you should use the <i>org.pac4j.http.client.FormClient</i> class :
<pre><code>// declare the client
FormClient client = new FormClient("/myloginurl", new MyUsernamePasswordAuthenticator());
client.setCallbackUrl("http://myserver/myapp/callbackUrl");
// send the user to the form for authentication
response.sendRedirect(client.getRedirectionUrl(new J2EContext(request, response)));</code></pre>
...after successfull authentication...
<pre><code>// get username/password credentials
UsernamePasswordCredentials credentials = client.getCredentials(new J2EContext(request, response)));
// get the HTTP profile
HttpProfile httpProfile = client.getUserProfile(credentials);
System.out.println("Hello : " + httpProfile.getUsername());</code></pre>

To use basic auth authentication in a web application, you should use the <i>org.pac4j.http.client.BasicAuthClient</i> class :
<pre><code>// declare the client
BasicAuthClient client = new BasicAuthClient(new MyUsernamePasswordAuthenticator(), new UsernameProfileCreator());</code></pre>

<h3>OpenID support</h3>

To use myopenid.com for authentication, you should use the <i>org.pac4j.openid.client.MyOpenIdClient</i> class :
<pre><code>// declare the client
MyOpenIdClient client = new MyOpenIdClient();
client.setCallbackUrl("/callbackUrl");
// send the user to myopenid.com for authentication
// we assume the user identifier is in the "openIdUser" request parameter
response.sendRedirect(client.getRedirectionUrl(new J2EContext(request, response)));</code></pre>
...after successfull authentication...
<pre><code>// get the OpenID credentials
OpenIdCredentials credentials = client.getCredentials(new J2EContext(request, response)));
// get the myOpenID profile
MyOpenIdProfile profile = client.getUserProfile(credentials);
System.out.println("Hello : " + profile.getDisplayName());</code></pre>

<h3>Multiple clients</h3>

If you use multiple clients, you can use more generic objects. All profiles inherit from the <i>org.pac4j.core.profile.CommonProfile</i> class :
<pre><code>// get credentials
Credentials credentials = client.getCredentials(new J2EContext(request, response)));
// get the common profile
CommonProfile commonProfile = client.getUserProfile(credentials);
System.out.println("Hello : " + commonProfile.getFirstName());</code></pre>

If you want to interact more with the OAuth providers (like Facebook), you can retrieve the access token from the (OAuth) profiles :
<pre><code>OAuthProfile oauthProfile = (OAuthProfile) commonProfile;
String accessToken = oauthProfile.getAccessToken();
// or
String accesstoken = facebookProfile.getAccessToken();</code></pre>

You can also group all clients on a single callback url by using the <i>org.pac4j.core.client.Clients</i> class :
<pre><code>Clients clients = new Clients("http://server/app/callbackUrl", fbClient, casClient, formClient myOpenIdClient);
// on the callback url, retrieve the right client
Client client = clients.findClient(new J2EContext(request, response)));</code></pre>

<h3>Error handling</h3>

All methods of the clients may throw an unchecked <i>org.pac4j.core.exception.TechnicalException</i>, which could be trapped by an appropriate try/catch.
The <i>getCredentials(WebContext)</i> method can throw a checked <i>org.pac4j.core.exception.RequiresHttpAction</i>, exception to require some additionnal HTTP action (redirection, basic auth...)


## Libraries built with pac4j

Even if you can use **pac4j** on its own, this library is used to be integrated with :

1. the [cas-server-support-pac4j](https://wiki.jasig.org/pages/viewpage.action?pageId=57577635) module to add multi-protocols client support to the [CAS server](http://www.jasig.org/cas)
2. the [play-pac4j](https://github.com/leleuj/play-pac4j) library to add multi-protocols client support to the [Play 2.x framework](http://www.playframework.org/) in Java and Scala
2. the [j2e-pac4j](https://github.com/leleuj/j2e-pac4j) library to add multi-protocols client support to the [J2E environment](http://docs.oracle.com/javaee/)
3. the [buji-pac4j](https://github.com/bujiio/buji-pac4j) library to add multi-protocols client support to the [Apache Shiro project](http://shiro.apache.org)
4. the [spring-security-pac4j](https://github.com/leleuj/spring-security-pac4j) library to add multi-protocols client support to [Spring Security](http://static.springsource.org/spring-security/site/)


<table>
<tr><th>Integration library</th><th>Protocol(s) supported</th><th>Based on</th><th>Demo webapp</th></tr>
<tr><td>cas-server-support-pac4j 4.0.0</td><td>OAuth / CAS / OpenID</td><td>pac4j 1.4.0</td><td><a href="https://github.com/leleuj/cas-pac4j-oauth-demo">cas-pac4-oauth-demo</a></td></tr>
<tr><td>cas-server-support-oauth 3.5.2</td><td>OAuth</td><td>scribe-up 1.2.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo-3.5.x">cas-oauth-demo-3.5.x</a></td></tr>
<tr><td>play-pac4j 1.1.0</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.4.0</td><td><a href="https://github.com/leleuj/play-pac4j-java-demo">play-pac4j-java-demo</a><br /><a href="https://github.com/leleuj/play-pac4j-scala-demo">play-pac4j-scala-demo</a></td></tr>
<tr><td>j2e-pac4j 1.0.0-SNAPSHOT</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.4.0</td><td><a href="https://github.com/leleuj/j2e-pac4j-demo">j2e-pac4j-demo</a></td></tr>
<tr><td>buji-pac4j 1.2.0-SNAPSHOT</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.4.0</td><td><a href="https://github.com/leleuj/buji-pac4j-demo">buji-pac4j-demo</a></td></tr>
<tr><td>spring-security-pac4j 1.2.0-SNAPSHOT</td><td>OAuth / CAS / OpenID / HTTP</td><td>pac4j 1.4.0</td><td><a href="https://github.com/leleuj/spring-security-pac4j-demo">spring-security-pac4j-demo</a></td></tr>
</table>


## Versions

The current version : <i>1.4.1-SNAPSHOT</i> is under development, it's available in the <a href="https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/">Sonatype snapshots repository</a>.

The latest release of the <b>pac4j</b> project is the <b>1.4.0</b> version :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;org.pac4j&lt;/groupId&gt;
    &lt;artifactId&gt;pac4j-core&lt;/artifactId&gt;
    &lt;version&gt;1.4.0&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

Learn more about the <a href="https://github.com/leleuj/pac4j/wiki/Versions">different versions</a>.


<h2>Testing</h2>

pac4j is tested by more than 300 unit, bench and integration tests (authentication processes are completely simulated using the <a href="http://htmlunit.sourceforge.net/">HtmlUnit</a> library).
To launch the tests, the <b>nr</b> Maven profile should be used. For example :
<pre><code>mvn clean install -Pnr</code></pre>
Use the <b>js</b> Maven profile for Javadoc and sources generation.


<h2>Contact</h2>

Find me on <a href="http://www.linkedin.com/in/jleleu">LinkedIn</a> or by email : leleuj@gmail.com

