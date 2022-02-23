---
layout: doc
title: Session store
---

While the [`WebContext`](web-context.html) is related to the HTTP request and response, the [`SessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/SessionStore.java) is an abstraction to deal with the web session.

It has the following methods:

- `getSessionId`: gets or creates the session identifier and initializes the session with it if necessary
- `get`: gets the attribute from the session
- `set`: sets the attribute in the session
- `destroySession`: destroys the underlying web session
- `getTrackableSession`: get the native session as a trackable object (for back-channel logout)
- `buildFromTrackableSession`: builds a new session store from a trackable session (for back-channel logout)
- `renewSession`: renews the native session by copying all data to a new one.

Its implementations are different depending on the *pac4*j implementations.

For example, the `JEEContext` currently uses the [`JEESessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-jee/src/main/java/org/pac4j/core/context/session/JEESessionStore.java) which relies on the JEE session. In Play, we have a specific cache-based [`PlayCacheSessionStore`](https://github.com/pac4j/play-pac4j/blob/master/shared/src/main/java/org/pac4j/play/store/PlayCacheSessionStore.java) as well as in Knox, which has a cookie-based `KnoxSessionStore`.
