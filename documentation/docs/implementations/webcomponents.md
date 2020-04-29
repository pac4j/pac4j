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
        <th>The default <code class="highlighter-rouge">WebContext</code></th>
        <th>The default <code class="highlighter-rouge">SessionStore</code></th>
        <th>The default <code class="highlighter-rouge">HttpActionAdapter</code></th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>jee-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ShiroSessionStore.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEHttpActionAdapter.INSTANCE</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlayWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlayCacheSessionStore</code> to use the Play Cache, <code class="highlighter-rouge">PlayCookieSessionStore</code> to use the Play session cookie</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">PlayHttpActionAdapter.INSTANCE</code></td>
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
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">SparkHttpActionAdapter</code></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JavalinWebContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JavalinHttpActionAdapter</code></td>
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
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEContext</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEESessionStore.INSTANCE</code> or <code class="highlighter-rouge">DistributedJEESessionStore</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">JEEHttpActionAdapter.INSTANCE</code></td>
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
        <td>lagom-pac4j</td>
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
