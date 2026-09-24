---
layout: blog
title: Security advisory for pac4j-core, pac4j-oidc and pac4j-saml
author: Jérôme LELEU
date: August 24, 2026
tags: [sec]
seo_title: "Security advisory: core, OIDC and SAML hardening | pac4j"
description: "Read the pac4j 6.5.6 security advisory covering fixes and hardening in pac4j-core, pac4j-oidc and pac4j-saml."
---

A few security fixes/hardenings have been applied in version `6.5.6`.

To stay safe, you SHOULD upgrade:

- the `pac4j-core` dependency
- the `pac4j-oidc` dependency if you use the OIDC protocol
- the `pac4j-saml` dependency if you use the SAML protocol.

No additional details will be shared in this post.

These vulnerabilities were discovered by **[Joshua Rogers](https://joshua.hu) of [AISLE Research](https://aisle.com)**.

{% include security_warning.html %}
