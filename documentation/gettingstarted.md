---
layout: guide
title: Get started with Java application security
seo_title: "Java authentication tutorials: OIDC, CAS and SAML | pac4j"
description: "Secure Java applications with pac4j: OIDC, SAML and CAS tutorials for Spring Boot, Jakarta EE, Play, JAX-RS, Vert.x, Shiro and other frameworks."
---

**pac4j is a Java security framework** that provides authentication and authorization for web applications and web services. Secure your application with OpenID Connect (OIDC), CAS, SAML and other authentication mechanisms. Start with one of these Spring Boot examples:

<ul class="blog-index">
{% assign protocols = "oidc,cas,saml" | split: "," %}
{% for protocol in protocols %}
    <li>
        <a href="/how-to-secure-a-java-application-with-{{ protocol }}.html">How to secure a Java application with {{ protocol | upcase }} (using Spring Boot)</a>
        <img class="guide-logo" src="/img/logo-spring-webmvc.png" alt="" width="48" height="40" />
        <span class="tag tag-{{ protocol }}">{{ site.data.blogtags[protocol] }}</span>
    </li>
{% endfor %}
</ul>

Choose an integration for your application. The guides below use OIDC, SAML or CAS to explain the framework-specific setup, including bridges for applications that already use Spring Security or Shiro:

<ul class="blog-index">
    <li>
        <a href="/how-to-secure-a-jakarta-ee-application-with-oidc.html">How to secure a Jakarta EE application with OIDC (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-j2e.png" alt="" width="48" height="40" />
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-play-application-with-saml.html">How to secure a Play application with SAML (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-play.png" alt="" width="48" height="40" />
        <span class="tag tag-saml">{{ site.data.blogtags.saml }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-spring-security-application-with-oidc.html">How to secure a Spring Security application with OIDC (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-spring-security.png" alt="" width="48" height="40" />
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-shiro-application-with-cas.html">How to secure a Shiro application with CAS (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-shiro.png" alt="" width="48" height="40" />
        <span class="tag tag-cas">{{ site.data.blogtags.cas }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-spring-webflux-application-with-oidc.html">How to secure a Spring WebFlux application with OIDC (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-spring-webflux.png" alt="" width="48" height="40" />
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-jax-rs-application-with-oidc.html">How to secure a JAX-RS application with OIDC (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-jaxrs.png" alt="" width="48" height="40" />
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-vertx-application-with-cas.html">How to secure a Vert.x application with CAS (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-vertx.png" alt="" width="48" height="40" />
        <span class="tag tag-cas">{{ site.data.blogtags.cas }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-javalin-application-with-saml.html">How to secure a Javalin application with SAML (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-javalin.png" alt="" width="48" height="40" />
        <span class="tag tag-saml">{{ site.data.blogtags.saml }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-spark-java-application-with-oidc.html">How to secure a Spark Java application with OIDC (using pac4j)</a>
        <img class="guide-logo" src="/img/logo-spark.png" alt="" width="48" height="40" />
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
</ul>

## The grand tour

For a more complete introduction to pac4j's concepts and security model, follow the **Grand tour** below.

<div class="video-embed">
<iframe src="https://docs.google.com/presentation/d/1ScGtdqRBUpGYA915sn6L3CXOB3axzJa1H3rj7i27MZs/embed?start=false&loop=false&delayms=60000" allowfullscreen="true" mozallowfullscreen="true" webkitallowfullscreen="true"></iframe>
</div>
