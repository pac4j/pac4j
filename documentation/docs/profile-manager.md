---
layout: doc
title: Profile manager
---

The profile manager is meant to deal with the user profile: it can be used to save or restore it.

The profile manager is instantiated from the `WebContext` and the `SessionStore`.

## 1) Retrieval

You can use the `getProfile()` method to return only one profile and the `getProfiles()` method to retrieve all profiles.

The returned profiles are of type `UserProfile`, but they should be at least cast as `CommonProfile` to retrieve the most common attributes that all profiles share
or to their real types like a `FacebookProfile` in case of a Facebook authentication.

You may also use the `getProfile(class)` if you already know the type of the user profile.

```java
CasProfile profile = manager.getProfile(CasProfile.class).get();
```

## 2) Custom profile managers

By default, the profile manager is the [`ProfileMamager`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/ProfileManager.java) component.

In some *pac4j* implementations, there are specific profile managers: `UndertowProfileManager`, `ShiroProfileManager`, etc.

A custom profile manager can be instantiated via the following factory:

- `setProfileManagerFactory(final ProfileManagerFactory factory)`.

It can be set at components level (like for the logics) or at the `Config` level.
