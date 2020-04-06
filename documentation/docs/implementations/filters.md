---
layout: idoc
title: Implementations comparison for the filter and endpoints&#58;
---

[<i class="fa fa-long-arrow-left fa-2x" aria-hidden="true"></i> Categories](./comparison.html)

<style>
    table {
        margin-top: 20px
    }
    table img {
        border: 0
    }
</style>

<table class="centered">
    <tr>
        <th>Implementation</th>
        <th>The security filter applies at a URL level</th>
        <th>The security filter applies at a method level</th>
        <th>The callback endpoint</th>
        <th>The logout endpoint</th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityInterceptor</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackController</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutController</code></td>
    </tr>
    <tr>
        <td>jee-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityFilter</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackFilter</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutFilter</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityFilter</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackFilter</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutFilter</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityFilter</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackFilter</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutFilter</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityFilter</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@Secure</code> annotation or the <code>Security</code> trait</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackController</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutController</code></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityHandler</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackHandler</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutHandler</code></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityFilter</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackRoute</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutRoute</code></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityHandler</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackHandler</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutHandler</code></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">RatpackPac4j.login</code> and <code class="highlighter-rouge">RatpackPac4j.requireAuth</code> methods</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">RatpackPac4j.authenticator</code> method</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">RatpackPac4j.logout()</code> method</td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">SecurityHandler</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">CallbackHandler</code></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">LogoutHandler</code></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>lagom-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
</table>
