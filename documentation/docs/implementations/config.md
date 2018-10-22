---
layout: idoc
title: Implementations comparison for configuration&#58;
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

When using dependency injection, the configuration can be defined via source code or XML context files (Spring).

<table class="centered">
    <tr>
        <th>Implementation</th>
        <th>The configuration can be defined by properties<br />(YAML or properties file)</th>
        <th>The configuration can be defined by source code</th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>j2e-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />using the <code class="highlighter-rouge">ConfigFactory</code></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using .ini files</td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td><img src="/img/green_check.png" /></td>
        <td><img src="/img/red_cross.png" /><br />it was possible in older versions of CAS and by customization</td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td><img src="/img/green_check.png" /><br />using Dropwizard YAML configuration files</td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td><img src="/img/green_check.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /></td>
    </tr>
</table>
