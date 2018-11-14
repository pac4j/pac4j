---
layout: idoc
title: Implementations comparison for the default logics&#58;
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
        <th>The default <code class="highlighter-rouge">SecurityLogic</code></th>
        <th>The default <code class="highlighter-rouge">CallbackLogic</code></th>
        <th>The default <code class="highlighter-rouge">LogoutLogic</code></th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>j2e-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ShiroSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ShiroCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
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
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code></td>
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
