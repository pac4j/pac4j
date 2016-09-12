---
layout: doc
title: Backward compatibility&#58;
---

Living projects need changes and sometimes these are breaking ones. Breaking changes are generally available in major versions.

`pac4j` is a whole project in itself and all *pac4j* implementations are also projects in themselves which depend on `pac4j` (in fact, they have a dependency on the `pac4j-core` library). And this generates a huge number of constraints and work to keep a full backward compatibility.

That's why the *pac4j* project has a specific definition when it comes to backward compatibility. We want to go fast and we make compromises to achieve this goal.


## 1) Versioning

We don't use [semver](http://semver.org/). For a version X.Y.Z, semver says X for major versions (breaking changes), Y for minor versions and Z for bug fixes.

In *pac4j*, Z is for minor updates / bug fixes and Y (and optionally X) is for major versions. The version 1.9.1 of the `pac4j` project would be the **9.1.0** version if we used semver.

We don't need big numbers to feel production-ready ;-)


## 2) Backward compatibility

So for *pac4j*, backward compatibility means that **all `pac4j` versions of the same stream can be integrated with the same *pac4j* implementation version**.



For example, if `play-pac4j` v2.4.0 has a dependency on `pac4j` v1.9.0 (`pac4j-core` v1.9.0), you'll be able to keep this version 2.4.0 of `play-pac4j` and use `pac4j` (`pac4j-core`, `pac4j-oauth`, `pac4j-cas`...) version 1.9.1, 1.9.2, 1.9.3... but not the version 2.0.0 or 1.8.8.


## 3) Potential breaking changes

This backward compatibility in terms of integration provides warranties. But there are downsides:

### a) The compilation of components may fail

Although the integration will never be broken, `pac4j` components may have changed in a way that makes compilation fail. It should be straightforward to find the issue and fix it.

For example, between `pac4j` v1.9.0 and v1.9.1, the signature of the `validate` method of the `Authenticator` interface has changed from [`void validate(T credentials) throws HttpAction;`](https://github.com/pac4j/pac4j/blob/pac4j-1.9.0/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java#L22) to [`void validate(C credentials, WebContext context) throws HttpAction;`](https://github.com/pac4j/pac4j/blob/pac4j-1.9.1/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java#L24).

### b) All `pac4j-*` libraries should have the same version

If you use the `play-pac4j` v2.4.0 (which has a dependency on `pac4j-core` v1.9.0) with the `pac4j-oauth` library v1.9.1, you may encounter "MethodError" or "LinkageError". So you should upgrade to the `pac4j-core` v1.9.1 dependency as well.

And if you use the `play-pac4j` v2.4.1 (which has a dependency on `pac4j-core` v1.9.1) with the `pac4j-oauth` library v1.9.0, you may also have issues. So you should use the `pac4j-oauth` v1.9.1 library.
