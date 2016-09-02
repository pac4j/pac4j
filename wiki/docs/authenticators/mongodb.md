---
layout: doc
title: MongoDB
---

*pac4j* allows you to validate username / password on a MongoDB collection.

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

## 2) `MongoAuthenticator`

The [`MongoAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/credentials/authenticator/MongoAuthenticator.java) validates username / password on a MongoDB database. It is built from a `com.mongodb.MongoClient`.

It can be defined for HTTP clients which deal with `UsernamePasswordCredentials`.

After a successful credentials validation, it "returns" a [`MongoProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/profile/MongoProfile.java).

**Example:**

```java
MongoClient mongoClient = new MongoClient(server, port);
MongoAuthenticator authenticator = new MongoAuthenticator(mongoClient);
```

The credentials validation is done on the `users` database in a `users` collection, but both can be changed via the `setUsersDatabase(String)` and `setUsersCollection(String)` methods.

The users in the collection are expected to have the following format:

```json
{
	"username": "jleleu",
	"password": "4d81a960ee36c86d5ea1152d77084410b6596dd04cc29e2866ba9ea2c60e22f8",
	"first_name": "Jérôme",
	"last_name": "LELEU"
} 
```

And the `username` and `password` attributes can be changed to other names via the `setUsernameAttribute(String)` and `setPasswordAttribute(String)` methods.

The list of attributes can be defined as a list of attribute names separated by commas via the `setAttributes(String)`. In the previous example, it would be: `mongoAuthenticator.setattributes("first_name, last_name");`.

This `MongoAuthenticator` supports the use of a specific [`PasswordEncoder`](/docs/authenticators.html#passwordencoder).
