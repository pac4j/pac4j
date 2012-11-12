<h2>What is scribe-up ?</h2>

<b>scribe-up</b> ("up" for User Profile) is a web OAuth client to :
<ol>
<li>delegate authentication and permissions to an OAuth provider (i.e. the user is redirected to the OAuth provider to log in)</li>
<li>(in the application) retrieve the profile of the authorized user after successfull authentication and permissions acceptation (at the OAuth provider).</li>
</ol>

It's available under the Apache 2 license and based on : <a href="https://github.com/fernandezpablo85/scribe-java">scribe-java</a> (for OAuth protocol) and <a href="https://github.com/FasterXML/jackson-core">Jackson</a> (for JSON parsing).

<h2>OAuth providers supported</h2>

<table>
<tr><td>Web site</td><td>Protocol</td><td>Provider</td><td>Profile</td></tr>
<tr><td>sites using OAuth Wrapper for CAS server</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/CasOAuthWrapperProvider.html">CasOAuthWrapperProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/casoauthwrapper/CasOAuthWrapperProfile.html">CasOAuthWrapperProfile</a></td></tr>
<tr><td>DropBox</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/DropBoxProvider.html">DropBoxProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/dropbox/DropBoxProfile.html">DropBoxProfile</a></td></tr>
<tr><td>Facebook</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/FacebookProvider.html">FacebookProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/facebook/FacebookProfile.html">FacebookProfile</a></td></tr>
<tr><td>Github</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/GitHubProvider.html">GitHubProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/github/GitHubProfile.html">GitHubProfile</a></td></tr>
<tr><td>Google</td><td>OAuth 1.0 & 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/GoogleProvider.html">GoogleProvider</a> & <a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/Google2Provider.html">Google2Provider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/google/GoogleProfile.html">GoogleProfile</a> & <a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/google2/Google2Profile.html">Google2Profile</a></td></tr>
<tr><td>LinkedIn</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/LinkedInProvider.html">LinkedInProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/linkedin/LinkedInProfile.html">LinkedInProfile</a></td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/TwitterProvider.html">TwitterProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/twitter/TwitterProfile.html">TwitterProfile</a></td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/WindowsLiveProvider.html">WindowsLiveProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/windowslive/WindowsLiveProfile.html">WindowsLiveProfile</a></td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/WordPressProvider.html">WordPressProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/wordpress/WordPressProfile.html">WordPressProfile</a></td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/YahooProvider.html">YahooProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/yahoo/YahooProfile.html">YahooProfile</a></td></tr>
</table>

Follow the guide to <a href="https://github.com/leleuj/scribe-up/wiki/Extend-or-add-a-new-provider">extend or add a new provider</a>.

<h2>Code sample</h2>

Suppose you want to authenticate and get the user profile from Facebook :
<pre><code>// declare the provider (use default scope and fields)
FacebookProvider provider = new FacebookProvider();
provider.setKey(MY_KEY);
provider.setSecret(MY_SECRET);
provider.setCallbackUrl("http://myserver/myapp/callbackUrl");
// send the user to Facebook for authentication and permissions
response.sendRedirect(provider.getAuthorizationUrl(new HttpUserSession(session)));</code></pre>
...after successfull authentication, on the callback url for Facebook...
<pre><code>// get OAuth credentials
OAuthCredential credential = provider.getCredential(new HttpUserSession(request), request.getParameterMap());
// get the user profile
UserProfile userProfile = provider.getUserProfile(credential);
// get the facebook profile
FacebookProfile facebookProfile = (FacebookProfile) userProfile;
System.out.println("Hello : " + facebookProfile.getDisplayName() + " born the " + facebookProfile.getBirthday());</code></pre>
If the user can be authenticated by several OAuth providers, use the common profile instead :
<pre><code>CommonProfile commonProfile = (CommonProfile) userProfile;
System.out.println("Hello : " + commonProfile.getDisplayName() + " at " + commonProfile.getEmail());</code></pre>
If you want to interact more with the OAuth provider, you can retrieve the access token from the (OAuth) profile :
<pre><code>OAuthProfile oauthProfile = (OAuthProfile) userProfile;
String accessToken = oauthProfile.getAccessToken();
// or
String accesstoken = facebookProfile.getAccessToken();</code></pre>

For a better understanding of <b>scribe-up</b>, take a look at the <a href="https://github.com/leleuj/scribe-up/wiki/Technical-description">technical description of the project</a> or browse the <a href="http://javadoc.leleuj.cloudbees.net/">Javadoc</a>.

<h2>Libraries built on scribe-up</h2>

Even if you can use <b>scribe-up</b> on its own, this library is used to be the foundation of :
<ol>
<li>the <a href="https://wiki.jasig.org/display/CASUM/OAuth">cas-server-support-oauth</a> module to add OAuth client and server support to the <a href="http://www.jasig.org/cas">CAS server</a></li>
<li>the <a href="https://github.com/bujiio/buji-oauth">buji-oauth</a> library to add OAuth client support to the <a href="http://shiro.apache.org">Apache Shiro project</a></li>
<li>the <a href="https://github.com/leleuj/spring-security-oauth-client">spring-security-oauth-client</a> library to add OAuth client support to <a href="http://static.springsource.org/spring-security/site/">Spring Security</a>.</li>
<li>the <a href="https://github.com/leleuj/play-oauth-client">play-oauth-client</a> library to add OAuth client support to the <a href="http://www.playframework.org/">Play 2.0 framework</a>.</li>
</ol>

<table>
<tr><th>Library</th><th>Based on scribe-up version</th><th>Demo webapp</th></tr>
<tr><td>cas-server-support-oauth 3.5.1</td><td>1.1.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo">cas-oauth-demo</a></td></tr>
<tr><td>cas-server-support-oauth 3.5.0</td><td>1.0.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo-3.5.0">cas-oauth-demo-3.5.0</a></td></tr>
<tr><td>buji-oauth 1.0.0</td><td>1.2.0</td><td><a href="https://github.com/leleuj/scribe-up-shiro-demo">scribe-up-shiro-demo</a></td></tr>
<tr><td>spring-security-oauth-client 1.1.0</td><td>1.3.0</td><td><a href="https://github.com/leleuj/spring-security-oauth-client-demo">spring-security-oauth-client-demo</a></td></tr>
<tr><td>spring-security-oauth-client 1.0.0</td><td>1.2.0</td><td><a href="https://github.com/leleuj/spring-security-oauth-client-demo-1.0.0">spring-security-oauth-client-demo-1.0.0</a></td></tr>
<tr><td>play-oauth-client 1.0.0</td><td>1.3.0</td><td><a href="https://github.com/leleuj/play-oauth-client-demo">play-oauth-client-demo</a></td></tr>
</table>

<h2>Versions</h2>

The current version : <i>1.3.0-SNAPSHOT</i> is under development, it's available on <a href="https://oss.sonatype.org/content/repositories/snapshots/org/scribe/scribe-up/">Sonatype snapshots repository</a> as Maven dependency :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;org.scribe&lt;/groupId&gt;
    &lt;artifactId&gt;scribe-up&lt;/artifactId&gt;
    &lt;version&gt;1.3.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>
The last released version is the <b>1.2.0</b>. Learn more about the <a href="https://github.com/leleuj/scribe-up/wiki/Versions">different versions</a>.

<h2>Testing</h2>

scribe-up is tested by 200 unit tests, 2 bench tests and 11 integration tests on OAuth providers by simulating complete authentication processes (using the <a href="http://htmlunit.sourceforge.net/">HtmlUnit</a> library).

<h2>Contact</h2>

Find me on <a href="http://www.linkedin.com/in/jleleu">LinkedIn</a> or by email : leleuj@gmail.com
