---
layout: idoc
title: <i class="fa fa-user" aria-hidden="true"></i> All available <i>pac4j</i> implementations&#58;
---

<style>
    .implem-block {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: flex-start;
        gap: 16px 24px;
        margin: 16px 0 24px;
        padding: 18px 12px;
        border: 1px solid var(--border);
        border-radius: var(--radius);
    }
    .implem-block .spacer { flex: 0 0 20%; }
    .implem {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 0 0 auto;
        text-align: center;
    }
    .implem-block .implem img { height: 120px; width: auto; border: 0; }
    .implem h1 { font-size: 1.05rem; font-weight: 700; margin: 6px 0 0; line-height: 1.25; }
    .implem h1 small { font-size: .8em; }
    @media (max-width: 560px) {
        .implem-block { flex-direction: column; align-items: center; gap: 10px; }
        .implem-block .spacer { display: none; }
        .implem-block .implem img { height: 100px; max-width: 100%; }
    }
    @media (max-width: 480px) {
        .implem-block .implem img { height: 80px; }
    }
    @media (max-width: 360px) {
        .implem-block .implem img { height: 60px; }
    }
</style>

<h2>All <i>pac4j</i> implementations offer <a href="docs/implementations/comparison.html"><b>similar</b> features</a>:</h2>

<div class="implem-block">
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/spring-webmvc-pac4j"><img height="100" src="/img/logo-spring-webmvc.png" /></a><a target="_blank" href="https://github.com/pac4j/spring-webmvc-pac4j"><h1>Spring Web MVC<br /><small>(Spring Boot)</small></h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/jee-pac4j"><img height="100" src="/img/logo-j2e.png" /></a><a target="_blank" href="https://github.com/pac4j/jee-pac4j"><h1>Jakarta EE<br /><small>(Servlet)</small></h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/spring-webflux-pac4j"><img height="100" src="/img/logo-spring-webflux.png" /></a><a target="_blank" href="https://github.com/pac4j/spring-webflux-pac4j"><h1>Spring Webflux<br /><small>(Spring Boot)</small></h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/bujiio/buji-pac4j"><img height="100" src="/img/logo-shiro.png" /></a><a target="_blank" href="https://github.com/bujiio/buji-pac4j"><h1>Shiro</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/spring-security-pac4j"><img height="100" src="/img/logo-spring-security.png" /></a><a target="_blank" href="https://github.com/pac4j/spring-security-pac4j"><h1>Spring Security<br /><small>(Spring Boot)</small></h1></a></div>
</div>

<div class="implem-block">
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/play-pac4j"><img height="100" src="/img/logo-play.png" /></a><a target="_blank" href="https://github.com/pac4j/play-pac4j"><h1>Play 2.x/3.x</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/vertx-pac4j"><img height="100" src="/img/logo-vertx.png" /></a><a target="_blank" href="https://github.com/pac4j/vertx-pac4j"><h1>Vertx</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/spark-pac4j"><img height="100" src="/img/logo-spark.png" /></a><a target="_blank" href="https://github.com/pac4j/spark-pac4j"><h1>Spark Java</h1></a></div>
    <div class="implem"><a target="_blank" href="https://ratpack.io/manual/current/pac4j.html#pac4j"><img height="100" src="/img/logo-ratpack.png" /></a><a target="_blank" href="https://ratpack.io/manual/current/pac4j.html#pac4j"><h1>Ratpack</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/jax-rs-pac4j"><img height="100" src="/img/logo-jaxrs.png" /></a><a target="_blank" href="https://github.com/pac4j/jax-rs-pac4j"><h1>JAX-RS</h1></a></div>
</div>

<div class="implem-block">
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/dropwizard-pac4j"><img height="100" src="/img/logo-dropwizard.png" /></a><a target="_blank" href="https://github.com/pac4j/dropwizard-pac4j"><h1>Dropwizard</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/javalin-pac4j"><img height="100" src="/img/logo-javalin.png" /></a><a target="_blank" href="https://github.com/pac4j/javalin-pac4j"><h1>Javalin</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/undertow-pac4j"><img height="100" src="/img/logo-undertow.png" /></a><a target="_blank" href="https://github.com/pac4j/undertow-pac4j"><h1>Undertow</h1></a></div>
    <div class="implem"><a target="_blank" href="https://jooby.io/modules/pac4j"><img height="100" src="/img/logo-jooby.png" /></a><a target="_blank" href="https://jooby.io/modules/pac4j"><h1>Jooby</h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/http4s-pac4j"><img height="100" src="/img/logo-http4s.png" /></a><a target="_blank" href="https://github.com/pac4j/http4s-pac4j"><h1>http4s</h1></a></div>
</div>

<h2>Also available, on an older <i>pac4j</i> line:</h2>

<div class="implem-block">
    <div class="implem"><a target="_blank" href="https://github.com/pac4j/lagom-pac4j"><img height="100" src="/img/logo-lagom.png" /></a><a target="_blank" href="https://github.com/pac4j/lagom-pac4j"><h1>Lagom<br /><small>(pac4j 3.x)</small></h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/pippo-java/pippo/tree/master/pippo-security-parent/pippo-pac4j"><img height="100" src="/img/logo-pippo.png" /></a><a target="_blank" href="https://github.com/pippo-java/pippo/tree/master/pippo-security-parent/pippo-pac4j"><h1>Pippo<br /><small>(pac4j 2.x)</small></h1></a></div>
    <div class="implem"><a target="_blank" href="https://github.com/StackVista/akka-http-pac4j"><img height="100" src="/img/logo-akkahttp.png" /></a><a target="_blank" href="https://github.com/StackVista/akka-http-pac4j"><h1>Akka HTTP<br /><small>(pac4j 5.x, archived)</small></h1></a></div>
</div>

<h2>Products that embed <i>pac4j</i>:</h2>

<div class="implem-block">
    <div class="implem"><a target="_blank" href="https://apereo.github.io/cas/7.3.x/integration/Delegate-Authentication.html"><img height="100" src="/img/logo-cas.png" /></a><a target="_blank" href="https://apereo.github.io/cas/7.3.x/integration/Delegate-Authentication.html"><h1>CAS server</h1></a></div>
    <div class="implem"><a target="_blank" href="https://syncope.apache.org"><img height="100" src="/img/logo-syncope.png" /></a><a target="_blank" href="https://syncope.apache.org"><h1>Syncope</h1></a></div>
    <div class="implem"><a target="_blank" href="https://knox.apache.org/books/knox-2-1-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect"><img height="100" src="/img/logo-knox.png" /></a><a target="_blank" href="https://knox.apache.org/books/knox-2-1-0/user-guide.html#Pac4j+Provider+-+CAS+/+OAuth+/+SAML+/+OpenID+Connect"><h1>Knox</h1></a></div>
</div>
