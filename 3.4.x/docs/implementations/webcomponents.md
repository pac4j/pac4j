---
layout: idoc
title: Implementations comparison for the web components&#58;
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
        <th>The <code class="highlighter-rouge">WebContext</code></th>
        <th>The <code class="highlighter-rouge">SessionStore</code></th>
        <th>The <code class="highlighter-rouge">HTTPActionAdapater</code></th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2EContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ENoHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>j2e-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2EContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ENoHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2EContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ShiroSessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ENoHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2EContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2ENoHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlayWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlayCacheSessionStore</code> to use the Play Cache, <code class="highlighter-rouge">PlayCookieSessionStore</code> to use the Play session cookie</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlaytHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">VertxWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">VertxSessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">SparkWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2SessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">Pac4jContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">J2SessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">RatpackWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">RatpackSessionStore</code></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">UndertowWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">UndertowSessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">UndertowNopHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
</table>
