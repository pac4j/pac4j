<h2>What is scribe-up ?</h2>

<b>scribe-up</b> ("up" for User Profile) is a web OAuth client to :
<ol>
<li>delegate authentication and permissions at an OAuth provider</li>
<li>retrieve the profile of the authorized user after successfull authentication and permissions acceptation.</li>
</ol>

It's available under the Apache 2 license and based on : <a href="https://github.com/fernandezpablo85/scribe-java">scribe-java</a> (for OAuth protocol) and <a href="https://github.com/FasterXML/jackson-core">Jackson</a> (for JSON parsing).

<h2>OAuth providers supported</h2>

<table><tr>
<td>DropBox (OAuth 1.0)</td><td>Facebook (OAuth 2.0)</td><td>Github (OAuth 2.0)</td><td>Google (OAuth 1.0 & 2.0)</td><td>LinkedIn (OAuth 1.0)</td><td>Twitter (OAuth 1.0)</td><td>Windows Live (OAuth 2.0)</td><td>WordPress (OAuth 2.0)</td><td>Yahoo (OAuth 1.0)</td>
</tr></table>

Look at the <a href="https://github.com/leleuj/scribe-up/wiki/Description-of-providers-and-profiles">description of the providers and profiles</a> or follow the guide to <a href="https://github.com/leleuj/scribe-up/wiki/Extend-or-add-a-new-provider">extend or add a new provider</a>.

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
// get a facebook profile
FacebookProfile facebookProfile = (FacebookProfile) provider.getUserProfile(credential);
System.out.println("Hello : " + facebookProfile.getDisplayName() + " born the " + facebookProfile.getBirthday());</code></pre>
If the user can be authenticated by several OAuth providers, use the common profile instead :
<pre><code>CommonProfile commonProfile = (CommonProfile) provider.getUserProfile(credential);
System.out.println("Hello : " + commonProfile.getDisplayName() + " at " + commonProfile.getEmail());</code></pre>

For a better understanding of <b>scribe-up</b>, take a look at the <a href="https://github.com/leleuj/scribe-up/wiki/Technical-description">technical description of the project</a> or browse the <a href="http://javadoc.leleuj.cloudbees.net/">Javadoc</a>.

<h2>Libraries built on scribe-up</h2>

Even if you can use <b>scribe-up</b> on its own, this library was created to be the foundation of :
<ol>
<li>the <a href="https://wiki.jasig.org/display/CASUM/OAuth">cas-server-support-oauth</a> module to add OAuth client and server support to the <a href="http://www.jasig.org/cas">CAS server</a></li>
<li>the <a href="https://github.com/bujiio/buji-oauth">buji-oauth</a> library to add OAuth client support to the <a href="http://shiro.apache.org">Apache Shiro project</a></li>
<li>the <a href="https://github.com/leleuj/spring-security-oauth-client">spring-security-oauth-client</a> library to add OAuth client support to <a href="http://static.springsource.org/spring-security/site/">Spring Security</a>.</li>
</ol>

<table>
<tr><th>Library</th><th>Based on scribe-up version</th><th>Demo webapp</th></tr>
<tr><td>cas-server-support-oauth 3.5.0</td><td>1.0.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo-3.5.0">cas-oauth-demo-3.5.0</a></td></tr>
<tr><td>cas-server-support-oauth 3.5.1</td><td>1.1.0</td><td><a href="https://github.com/leleuj/cas-oauth-demo">cas-oauth-demo</a></td></tr>
<tr><td>buji-oauth 1.0.0</td><td>1.2.0</td><td><a href="https://github.com/leleuj/scribe-up-shiro-demo">scribe-up-shiro-demo</a></td></tr>
<tr><td>spring-security-oauth-client 1.0.0</td><td>1.2.0</td><td><a href="https://github.com/leleuj/spring-security-oauth-client-demo">spring-security-oauth-client-demo</a></td></tr>
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

ScribeUP is tested by 199 unit tests and also 10 integration tests on OAuth providers by simulating complete authentication processes (using the <a href="http://htmlunit.sourceforge.net/">HtmlUnit</a> library).

<h2>Contact</h2>

Find me on <a href="http://www.linkedin.com/in/jleleu">LinkedIn</a> or by email : leleuj@gmail.com
