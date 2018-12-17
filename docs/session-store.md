---
layout: doc
title: Session store
---

The [`WebContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/WebContext.java) is an abstraction do deal with the HTTP request and response.
 
To specifically deal with the session, it relies on a [`SessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/SessionStore.java) which may be available via the `getSessionStore` method.

The `SessionStore` has the following methods:

- `getOrCreateSessionId`: gets or creates the session identifier and initializes the session with it if necessary
- ` get`: gets the attribute from the session
- `set`: sets the attribute in the session
- `destroySession`: destroys the underlying web session
- `getTrackableSession`: get the native session as a trackable object (for back-channel logout)
- `buildFromTrackableSession`: builds a new session store from a trackable session (for back-channel logout)
- `renewSession`: renews the native session by copying all data to a new one.

For example, the [`JEEContext`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/JEEContext.java) currently uses the [`JEESessionStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/context/session/JEESessionStore.java) which relies on the JEE session. In Play, we have a specific cache-based [`PlayCacheSessionStore`](https://github.com/pac4j/play-pac4j/blob/master/src/main/java/org/pac4j/play/store/PlayCacheSessionStore.java) as well as in Knox, which has a cookie-based [`KnoxSessionStore`](https://github.com/apache/knox/blob/master/gateway-provider-security-pac4j/src/main/java/org/apache/hadoop/gateway/pac4j/session/KnoxSessionStore.java).

The [`ProfileStorageDecision`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/strategy/ProfileStorageDecision.java) defines the decisions related to the profile, whether we must read it from and save it into the web session. It is used by the [`DefaultSecurityLogic`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/DefaultSecurityLogic.java):

- by default, the [`DefaultProfileStorageDecision`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/strategy/DefaultProfileStorageDecision.java) is set, which is appropriate for a web application which only uses indirect clients or direct clients
- for a web application using both indirect and direct clients and mixing authentications, the [`AlwaysUseSessionProfileStorageDecision`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/engine/strategy/AlwaysUseSessionProfileStorageDecision.java) should be used.
