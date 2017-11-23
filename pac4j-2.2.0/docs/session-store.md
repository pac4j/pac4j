---
layout: doc
title: Session store
---

The [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) is an abstraction do deal with the HTTP request and response but also with the web session, via the `getSessionIdentifier`, `getSessionAttribute` and `setSessionAttribute` methods.
 
To specifically deal with the session, it generally relies on a [`SessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/SessionStore.java) which may be available via the `getSessionStore` method.

The `SessionStore` has the following methods:

- `getOrCreateSessionId`: gets or creates the session identifier and initializes the session with it if necessary
- ` get`: gets the attribute from the session
- `set`: sets the attribute in the session
- `destroySession`: destroys the underlying web session
- `getTrackableSession`: get the native session as a trackable object (for back-channel logout)
- `buildFromTrackableSession`: builds a new session store from a trackable session (for back-channel logout)
- `renewSession`: renews the native session by copying all data to a new one.

For example, the [`J2EContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/J2EContext.java) currently uses the [`J2ESessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/J2ESessionStore.java) which relies on the J2E session. In Play, we have a specific cache-based [`PlayCacheStore`](https://github.com/pac4j/play-pac4j/blob/master/src/main/java/org/pac4j/play/store/PlayCacheStore.java) as well as in Knox, which has a cookie-based [`KnoxSessionStore`](https://github.com/apache/knox/blob/master/gateway-provider-security-pac4j/src/main/java/org/apache/hadoop/gateway/pac4j/session/KnoxSessionStore.java).
