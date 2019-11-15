---
layout: doc
title: Backward compatibility&#58;
---

## 1) Versioning & backward compatibility

Since version 2, the `pac4j` project has adopted the [semver](http://semver.org/) versioning, to make things clearer.

Given a version X.Y.Z:

- when X changes, it's a major version with breaking changes (example: 2.x, 3.x, and so on)
- when Y changes, it's a minor version with no compilation/runtime breaking changes (for example, you can upgrade from 2.5.2 to 2.7.0 without any worry)
- when Z changes, it's a bug fix(es) version with no compilation/runtime breaking change (no upgrade issue either).

Generally, *pac4j* implementations will adopt the semver versioning when upgrading to `pac4j` version 2, if they don't already have.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Notice that, while <code>pac4j-*</code> modules are backward compatible, you should always add the <code>pac4j-core</code> dependency in the same version as the upgraded <code>pac4j-*</code> dependency.</div>


## 2) Maintenance

Only one stable and released stream of `pac4j` is maintained at the same time since version 3.
