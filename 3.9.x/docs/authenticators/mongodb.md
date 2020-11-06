---
layout: doc
title: MongoDB
---

*pac4j* allows you to validate username/password and create, update and delete users on a MongoDB database.

## 1) Dependency

You need to use the following module: `pac4j-mongodb`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-mongodb</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `MongoProfileService`

The [`MongoProfileService`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/profile/service/MongoProfileService.java) supersedes the deprecated `MongoAuthenticator` to:

- validate a username/password on a MongoDB database (it can be defined for HTTP clients which deal with `UsernamePasswordCredentials`)
- create, update or delete a user in the MongoDB database.

It works with a [`MongoProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/profile/MongoProfile.java).

It is built from a `com.mongodb.MongoClient`.

**Example:**

```java
MongoClient mongoClient = new MongoClient(server, port);
MongoProfileService mongoProfileService = new MongoProfileService(mongoClient);
```

The users are managed in a `users` database in a `users` collection, but both can be changed via the `setUsersDatabase(String)` and `setUsersCollection(String)` methods.
As well as the `id`, `username` and `password` attribute names using the `setIdAttribute`, `setUsernameAttribute` and `setPasswordAttribute` methods.

The attributes of the user profile can be managed in the MongoDB collection in two ways:

- either each attribute is explicitly saved in a specific attribute and all these attributes are defined as a list of names separated by commas via the `setAttributes` method (it's the legacy mode already existing in version 1.9)
- or the whole user profile is serialized and saved in the `serializedprofile` attribute.

This `MongoProfileService` supports the use of a specific [`PasswordEncoder`](../authenticators.html#passwordencoder) to encode the passwords in the MongoDB database.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Starting with v3.9.0 in the 3.x stream, v4.2.0 in the 4.x stream and v5.0, the <code>serializedprofile</code> is written in JSON instead of using the Java serialization.</div>
