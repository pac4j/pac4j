---
layout: homeblog
title: <i class="fa fa-info-circle" aria-hidden="true"></i> Blog&#58;
seo_title: "Java security blog and authentication tutorials | pac4j"
description: "Explore Java authentication tutorials and articles on OIDC, SAML, CAS, JWT and JWKS, plus pac4j release news and security advisories."
---

{%- comment -%}
  This index is generated from the posts themselves: every page using the "blog"
  layout and NOT flagged "draft: true" shows up here, newest first.
  A draft stays reachable at its own URL for proofreading, but is listed neither
  here nor in /feed.xml. To publish it, just remove its "draft: true" line.
  Tag labels live in _data/blogtags.yml.
{%- endcomment -%}
{%- capture keys -%}
{%- for p in site.pages -%}
{%- if p.dir == "/blog/" and p.layout == "blog" and p.draft != true and p.title and p.date -%}
{{ p.date | date: "%Y%m%d" }}|{{ p.url }}~
{%- endif -%}
{%- endfor -%}
{%- endcapture -%}
{%- assign sorted = keys | split: "~" | sort | reverse -%}

## Step-by-step guides

<ul class="blog-index">
{% assign protocols = "oidc,cas,saml" | split: "," %}
{% for protocol in protocols %}
    <li>
        <a href="/how-to-secure-a-java-application-with-{{ protocol }}.html">How to secure a Java application with {{ protocol | upcase }} (using Spring Boot)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-{{ protocol }}">{{ site.data.blogtags[protocol] }}</span>
    </li>
{% endfor %}
    <li>
        <a href="/how-to-secure-a-jakarta-ee-application-with-oidc.html">How to secure a Jakarta EE application with OIDC (using pac4j)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-play-application-with-saml.html">How to secure a Play application with SAML (using pac4j)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-saml">{{ site.data.blogtags.saml }}</span>
    </li>
    <li>
        <span class="blog-group">
            <a href="/how-to-secure-a-spring-security-application-with-oidc.html">How to secure a Spring Security application with OIDC (using pac4j)</a>
            <a href="/how-to-secure-a-shiro-application-with-cas.html">How to secure a Shiro application with CAS (using pac4j)</a>
        </span>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
        <span class="tag tag-cas">{{ site.data.blogtags.cas }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-spring-webflux-application-with-oidc.html">How to secure a Spring WebFlux application with OIDC (using pac4j)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-jax-rs-application-with-oidc.html">How to secure a JAX-RS application with OIDC (using pac4j)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
    <li>
        <a href="/how-to-secure-a-vertx-application-with-cas.html">How to secure a Vert.x application with CAS (using pac4j)</a>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-cas">{{ site.data.blogtags.cas }}</span>
    </li>
    <li>
        <span class="blog-group">
            <a href="/how-to-secure-a-javalin-application-with-saml.html">How to secure a Javalin application with SAML (using pac4j)</a>
            <a href="/how-to-secure-a-spark-java-application-with-oidc.html">How to secure a Spark Java application with OIDC (using pac4j)</a>
        </span>
        <span class="tag tag-tuto">{{ site.data.blogtags.tuto }}</span>
        <span class="tag tag-saml">{{ site.data.blogtags.saml }}</span>
        <span class="tag tag-oidc">{{ site.data.blogtags.oidc }}</span>
    </li>
</ul>

## Latest articles

<ul class="blog-index">
{%- for key in sorted -%}
{%- assign purl = key | split: "|" | last -%}
{%- for p in site.pages -%}
{%- if p.url == purl %}
    <li>
        <time>{{ p.date | date: "%B %Y" }}</time>
        <a href="{{ p.url }}">{{ p.title | replace: "&colon;", ":" | replace: "&#58;", ":" }}</a>
        {%- for t in p.tags %}
        <span class="tag tag-{{ t }}">{{ site.data.blogtags[t] | default: t }}</span>
        {%- endfor %}
    </li>
{%- endif -%}
{%- endfor -%}
{%- endfor %}
</ul>
