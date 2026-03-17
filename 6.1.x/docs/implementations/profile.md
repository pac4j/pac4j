---
layout: idoc
title: Implementations comparison for the profile management&#58;
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
        <th>The default profile manager</th>
        <th>The profile manager is automatically available</th>
        <th>One profile is automatically available</th>
        <th>All profiles are automatically available</th>
    </tr>
    <tr>
        <td>spring-webmvc-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/green_check.png" /><br />by injection thanks to the <code class="highlighter-rouge">ComponentConfig</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jee-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/green_check.png" /><br />by injection thanks to the <code class="highlighter-rouge">Pac4jProducer</code></td>
        <td><img src="/img/green_check.png" /><br />via the <code class="highlighter-rouge">getUserPrincipal()</code> method in the <code class="highlighter-rouge">HttpServletRequest</code></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>buji-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ShiroProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />via the regular <code class="highlighter-rouge">SecurityUtils.getSubject()</code></td>
        <td><img src="/img/green_check.png" /><br />via the regular <code class="highlighter-rouge">SecurityUtils.getSubject()</code></td>
    </tr>
    <tr>
        <td>spring-security-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">SpringProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />via the regular <code class="highlighter-rouge">SecurityContextHolder.getContext().getAuthentication()</code></td>
        <td><img src="/img/green_check.png" /><br />via the regular <code class="highlighter-rouge">SecurityContextHolder.getContext().getAuthentication()</code></td>
    </tr>
    <tr>
        <td>play-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />in the templates via the <code class="highlighter-rouge">Pac4jScalaTemplateHelper</code></td>
        <td><img src="/img/green_check.png" /><br />in the templates via the <code class="highlighter-rouge">Pac4jScalaTemplateHelper</code></td>
    </tr>
    <tr>
        <td>vertx-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">VertxProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>spark-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>javalin-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>ratpack-pac4j</td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>pippo-pac4j</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
    <tr>
        <td>undertow-pac4j</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">UndertowProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/green_check.png" /><br />From the <code class="highlighter-rouge">securityContext.getAuthenticatedAccount()</code></td>
        <td><img src="/img/green_check.png" /><br />From the <code class="highlighter-rouge">securityContext.getAuthenticatedAccount()</code></td>
    </tr>
    <tr>
        <td>CAS</td>
        <td><img src="/img/green_check.png" /><br /><code class="highlighter-rouge">ProfileManager</code></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
        <td><img src="/img/red_cross.png" /></td>
    </tr>
    <tr>
        <td>jax-rs-pac4j</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
    <tr>
        <td>dropwizard-pac4j</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
    <tr>
        <td>lagom-pac4j</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
    <tr>
        <td>Knox</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
    <tr>
        <td>jooby-pac4j2</td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
        <td bgcolor="#eeeeee"></td>
    </tr>
</table>
