---
layout: doc
title: Backward compatibility&#58;
---

## 1) Versioning & backward compatibility

Since version 2, the *pac4j* project has adopted the [semver](http://semver.org/) versioning, to make things clearer.

Given a version X.Y.Z:

- when X changes, it's a major version with breaking changes (example: 2.x, 3.x, and so on)
- when Y changes, it's a minor version with no compilation/runtime breaking changes (for example, you can upgrade from 2.5.2 to 2.7.0 without any worry)
- when Z changes, it's a bug fix(es) version with no compilation/runtime breaking change (no upgrade issue either).

Generally, *pac4j* implementations follow the semver versioning as well.

Notice that, while `pac4j-*` modules are backward compatible, you should always add the `pac4j-javaee` or `pac4j-jakartaee` or `pac4j-core` dependency in the same version as the upgraded `pac4j-*` dependency.


## 2) Maintenance

### a) Core project

Community efforts are focused on the latest stream (the **master** branch**).

For older streams, contributions may be accepted and bug/security fixes may be backported.

### b) Implementations

The "major" implementations are:

- *jee-pac4j*
- *buji-pac4j*
- *spring-webmvc-pac4j*
- *spring-security-pac4j*
- *play-pac4j*
- *CAS*

They should be upgraded to the latest *pac4j* stream.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> If this free Open Source maintenance policy is not sufficient, contact the <a href="/commercial-support.html">commercial support</a>.</div>
