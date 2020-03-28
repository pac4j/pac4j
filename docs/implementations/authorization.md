---
layout: idoc
title: Implementations comparison for the authorization checks&#58;
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
        <th>The roles can be checked at a method level</th>
        <th>The permissions can be checked at a method level</th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@RequireAnyRole</code> and <code class="highlighter-rouge">@RequireAllRoles</code> annotations</td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jee-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br />when using Spring and the <code class="highlighter-rouge">@RequiresRoles</code> annotation</td>
        <td><img src="/img/green_check.png" /><br />when using Spring and the <code class="highlighter-rouge">@RequiresPermissions</code> annotation</td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@Secure</code> and <code class="highlighter-rouge">@PreAuthorize</code> annotations</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@Secure</code> and <code class="highlighter-rouge">@PreAuthorize</code> annotations</td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@Secure</code> annotation or the <code class="highlighter-rouge">Security</code> trait</td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">@Secure</code> annotation or the <code class="highlighter-rouge">Security</code> trait</td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
         <td><img src="/img/red_cross.png" /></td>
         <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>lagom-pac4j</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td></td>
        <td></td>
    </tr>
</table>
