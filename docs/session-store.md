---
layout: doc
title: Session store
---

The [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) is an abstraction do deal with the HTTP request and response but also with the web session, via the `getSessionIdentifier`, `getSessionAttribute` and `setSessionAttribute` methods.

These methods can be backed by a [`SessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/SessionStore.java). This interface defines the proper implementation for the web session thanks to the following methods: `getOrCreateSessionId`, `get` and `set`.

The [`J2EContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/J2EContext.java) currently uses the [`J2ESessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/J2ESessionStore.java). In Play, we have a specific cache-based [`PlayCacheStore`](https://github.com/pac4j/play-pac4j/blob/master/src/main/java/org/pac4j/play/store/PlayCacheStore.java) as well as in Knox, which has a cookie-based [`KnoxSessionStore`](https://github.com/apache/knox/blob/master/gateway-provider-security-pac4j/src/main/java/org/apache/hadoop/gateway/pac4j/session/KnoxSessionStore.java).


In the future, we could have session store implementations for Memcached or Redis.
