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
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>spring-webflux-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>jee-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code> or configurable</td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code> or configurable</td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic</code> or configurable</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic</code> or configurable</td>
    </tr>
    <tr>
        <td>lagom-pac4j</td>
        <td><img src="/img/red_cross.png" /><br />Custom security logic via service composition</td>
        <td><img src="/img/red_cross.png" /><br />No callback logic</td>
        <td><img src="/img/red_cross.png" /><br />No logout logic</td>
    </tr>
    <tr>
        <td>http4s-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code> (via j2e-pac4j)</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code> (via j2e-pac4j)</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code> (via j2e-pac4j)</td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultSecurityLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultCallbackLogic.INSTANCE</code></td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">DefaultLogoutLogic.INSTANCE</code></td>
    </tr>
</table>
