---
layout: blog
title: Security advisory for pac4j-core and pac4j-ldap
author: Jérôme LELEU
date: April 2026
---

A security vulnerability affecting the CSRF support in the `pac4j-core` module has been identified and fixed.

To stay safe, you SHOULD upgrade:

- If you use the 5.x line: upgrade to **5.7.10** (or newer)
- If you use the 6.x line: upgrade to **6.4.1** (or newer)

Another security vulnerability affecting the `LdapProfileService` in the `pac4j-ldap` module has been identified and fixed.

To stay safe, you MUST upgrade:

- If you use the 4.x line: upgrade to **4.5.10** (or newer)
- If you use the 5.x line: upgrade to **5.7.10** (or newer)
- If you use the 6.x line: upgrade to **6.4.1** (or newer)

No additional details will be shared in this post.

These vulnerabilities were discovered by **Bartlomiej Dmitruk, striga.ai**.
