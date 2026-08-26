---
layout: homeblog
title: <i class="fa fa-info-circle" aria-hidden="true"></i> Blog&#58;
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
