---
layout: doc
title: User profile
---

When the user is successfully authenticated by *pac4j*, his data are retrieved from the identity provider and a user profile is built. His profile has:

- an identifier (`getId()`)
- attributes (`getAttributes()`, (`getAttribute(name)`)
- roles (`getRoles()`)
- permissions (`getPermissions()`)
- a client name (`getClientName()`)
- a remember-me nature (`isRemembered()`)

In fact, the root class of the profiles hierarchy is the [`UserProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/UserProfile.java). Though, it's an abstract class which is never referenced and used directly.

The first user profile which must be considered is the [`CommonProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/CommonProfile.java) which defines the most common methods available in most profiles.

## 1) Identifier

Each user profile must have a unique identifier. Thus, when building the user profile, the *pac4j* clients use for the profile identifier a value enforcing uniqueness from the identity provider.

This works well accross the profiles provided from the same identity provider, though this can become a problem when using multiple identity providers. We could have a collision between the identifiers chosen from the identity provider. To avoid that issue, there is a "typed identifier" adding the profile name before the profile identifier.

Notice that with *pac4j* v1.9, the typed identifier has changed and now uses the full class name as prefix.

**Example:**

```java
profile.getId() // 00001
profile.getOldTypedId() // FacebookProfile#00001 with pac4j v1.9.x / does not exist before
profile.getTypedId() // org.pac4j.oauth.profile.facebook.FacebookProfile#00001 with pac4j v1.9.x / FacebookProfile#00001 before
```

## 2) Attributes

User profiles have attributes, populated from the data retrieved from the identity provider.

Any attribute name is accepted, though user profiles may have [`AttributesDefinition`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/AttributesDefinition.java) to:

- separate attributes in primary ones and secondary ones to facilitate the parsing of all the attributes returned by the identity provider
- define [`AttributeConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/AttributeConverter.java) associated with attributes names in order to transform simple strings in dates, enumerations...

The `AttributesDefinition` used by the profile must be returned via the `getAttributesDefinition()` method.

**Example:**

```java
public class FacebookProfile extends OAuth20Profile {

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new FacebookAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    ...
}
```

```java
public class FacebookAttributesDefinition extends AttributesDefinition {
    
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";

	...
    
    public FacebookAttributesDefinition() {
        Arrays.stream(new String[] {
            NAME, FIRST_NAME, MIDDLE_NAME, LAST_NAME, LINK, THIRD_PARTY_ID, BIO, EMAIL, POLITICAL, QUOTES,
            RELIGION, WEBSITE
        }).forEach(a -> primary(a, Converters.STRING));
        primary(TIMEZONE, Converters.INTEGER);
        primary(VERIFIED, Converters.BOOLEAN);
        final JsonListConverter multiObjectConverter = new JsonListConverter(FacebookObject.class, FacebookObject[].class);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
        primary(UPDATED_TIME, Converters.DATE_TZ_GENERAL);
        primary(BIRTHDAY, new FormattedDateConverter("MM/dd/yyyy"));
        primary(RELATIONSHIP_STATUS, new FacebookRelationshipStatusConverter());
        primary(LANGUAGES, multiObjectConverter);

        ...

        secondary(FRIENDS, multiObjectConverter);
        secondary(MOVIES, multiInfoConverter);
        secondary(MUSIC, multiInfoConverter);
        secondary(BOOKS, multiInfoConverter);
    }
}
```

Many attribute converters already exists: [`BooleanConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/BooleanConverter.java), [`ColorConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/ColorConverter.java)... Check the [org.pac4j.core.profile.converter](https://github.com/pac4j/pac4j/tree/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter) package.


## 3) Roles and permissions

Roles and permissions can be added to the user profile via the `addRole(role)`, `addRoles(roles)`, `addPermission(permission)` and `addPermissions(permissions)` methods.

They are generally computed in an [`AuthorizationGenerator`](/docs/clients.html#compute-roles-and-permissions).


## 4) Client name

During the login process, the name of the client is saved into the user profile via the `setClientName(name)` method and can be retrieved later on via the `getClientName()` method.


## 5) Remember-me

A user profile can be defined as remember-me as opposed to fully authenticated via the `setRemembered(boolean)` method. The `isRemembered()` method returns if the user profile is remembered.


## 6) Common methods of the `CommonProfile`

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
| `getPictureUrl()` | `String` | The `picture_url` attribute |
| `getProfileUrl()` | `String` | The `profile_url` attribute |
| `getLocation()` | `String` | The `location` attribute |


## 7) Profile subclassing

In fact, most clients never return a `CommonProfile`, but specific profiles like the `FacebookProfile`, the `OidcProfile`... which:

- have their specific attributes definition
- (partially) override the common methods of the `CommonProfile` with specific implementations
- add their specific getters for their specific attributes.
