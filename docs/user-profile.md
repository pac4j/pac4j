---
layout: doc
title: User profile
---

When the user is successfully authenticated by *pac4j*, his data are retrieved from the identity provider and a user profile is built. His profile has:

- an identifier (`getId()`)
- attributes (`getAttributes()`, `getAttribute(name)`)
- authentication-related attributes (`getAuthenticationAttributes()`, `getAuthenticationAttribute(name)`)
- roles (`getRoles()`)
- a client name (`getClientName()`)
- a remember-me nature (`isRemembered()`)
- a linked identifier (`getLinkedId()`)

In fact, the root class of the profiles hierarchy is the [`BasicUserProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/BasicUserProfile.java). It implements the [`UserProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/UserProfile.java) interface.

This is for specific use cases when you want a minimal user profile.

In the *pac4j* environment, the first user profile which must be considered is the [`CommonProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/CommonProfile.java) which defines the most common methods available in most profiles.

User profiles are managed via the [profile manager](profile-manager.html).

## 1) Identifier

Each user profile must have a unique identifier. Thus, when building the user profile, the *pac4j* clients use for the profile identifier a value enforcing uniqueness from the identity provider.

This works well accross the profiles provided from the same identity provider, though this can become a problem when using multiple identity providers.
We could have a collision between the identifiers chosen from the identity provider. To avoid that issue, there is a "typed identifier" adding the profile class name before the profile identifier.

**Example:**

```java
profile.getId() // 00001
profile.getTypedId() // org.pac4j.oauth.profile.facebook.FacebookProfile#00001
```

As the identifier must be a `String`, you may use the `ProfileHelper.sanitizeIdentifier` method to convert other Java types and remove the "typed" part of the identifier.


## 2) Attributes

User profiles have attributes, populated from the data retrieved from the identity provider (after conversion).
Multiple attributes with the same name and value of collection type can be (optionally) merged into a single attribute.
In particular it can be useful for identity providers that return roles in different single-element collections.


## 3) Authentication-related attributes

Some identity providers will include attributes related to the authentication itself, such as authentication method,
time period for which the authentication is valid, or metadata about the identity provider.  These attributes are stored
seperately from the user's attributes.


## 4) Roles

Roles can be added to the user profile via the `addRole(role)` and `addRoles(roles)` methods.

They are generally computed in an [`AuthorizationGenerator`](clients.html#2-compute-roles).


## 5) Client name

During the login process, the name of the client is saved into the user profile via the `setClientName(name)` method and can be retrieved later on via the `getClientName()` method.


## 6) Remember-me

A user profile can be defined as remember-me as opposed to fully authenticated via the `setRemembered(boolean)` method. The `isRemembered()` method returns if the user profile is remembered.


## 7) Common methods of the `CommonProfile`

The `CommonProfile` has the following methods:

| Method | Type | Returns |
|--------|------|---------|
| `getEmail()` | `String` | The `email` attribute |
| `getFirstName()` | `String` | The `first_name` attribute |
| `getFamilyName()` | `String` | The `family_name` attribute |
| `getDisplayName()` | `String` | The `display_name` attribute |
| `getUsername()` | `String` | The `username` attribute |
| `getGender()` | [`Gender`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/Gender.java) | The `gender` attribute |
| `getLocale()` | `Locale` | The `locale` attribute |
| `getPictureUrl()` | `URI` | The `picture_url` attribute |
| `getProfileUrl()` | `URI` | The `profile_url` attribute |
| `getLocation()` | `String` | The `location` attribute |
| `asPrincipal()` | `Principal` | An object containing the name of the current authenticated user |
| `isExpired()` | `boolean` | false if the current profile is expired |
{:.striped}

## 8) Profile definition

The profile class and attributes are defined via [`ProfileDefinition`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/definition/ProfileDefinition.java) implementations.

The `setProfileFactory` method allows you to define the instance class to return for the user profile while the `primary` and `secondary` methods allow you to define attributes with their specific converters.

Many attribute converters already exists: [`BooleanConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/BooleanConverter.java), [`ColorConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/ColorConverter.java)... Check the [org.pac4j.core.profile.converter](https://github.com/pac4j/pac4j/tree/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter) package.

As a result, the `newProfile` method returns a new class instance while the `convertAndAdd` methods convert the attributes if there is an associated converter and adds them to the profile.

The `newProfile` method may also return the related profile of a `typedId` if this one is specified as the first parameter and if the `setRestoreProfileFromTypedId(true)` method has been called.

## 9) Profile hierarchy

In fact, most clients never return a `CommonProfile`, but specific profiles like the `FacebookProfile`, the `OidcProfile`... which:

- (partially) override the common methods of the `CommonProfile` with specific implementations
- add their specific getters for their specific attributes.


## 10) Linked identifier

Each user profile may have a linked identifier, it's the identifier of another user profile. This way, both user profiles are linked and it allows you to authenticate via an account for a user
and load the linked user defined in the first user, especially by using the [`LoadLinkedUserAuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/LoadLinkedUserAuthorizationGenerator.java).
