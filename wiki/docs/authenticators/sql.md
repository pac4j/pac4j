---
layout: doc
title: Relational database
---

*pac4j* allows you to validate username / password on a SQL database.

## 1) Dependency

You need to use the following module: `pac4j-sql`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-sql</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `DbAuthenticator`

The [`DbAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/credentials/authenticator/DbAuthenticator.java) validates username / password on a relational database. It is built from a `javax.sql.DataSource`.

It can be defined for HTTP clients which deal with `UsernamePasswordCredentials`.

After a successful credentials validation, it "returns" a [`DbProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/profile/DbProfile.java).

**Example:**

```java
DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test", dbuser, dbpwd);
DbAuthenticator authenticator = new DbAuthenticator(dataSource);
```

The `users` table in the database must be created with the following script (for Oracle):

```sql
CREATE TABLE users
(
  username varchar(255),
  password varchar(255)
);
```

To define attributes for the user profile, the appropriate columns must be added to the table (like `first_name` and `last_name`) and the `DbAuthenticator` must be configured accordingly with the `setAttributes(String attributes)` method (like `authenticator.setAttributes("firt_name,last_name");`).

In fact, you can even adapt to a new / existing structure for the `users` table by changing the query which is performed on the database, using the `setStartQuery` and `setEndQuery` methods.

The query is built as: `startQuery + "," + attributes + endQuery` and by default, `startQuery` is "select username, password" and `endQuery` is " from users where username = _username_;".

This `DbAuthenticator` supports the use of a specific [`PasswordEncoder`](/docs/authenticators.html#passwordencoder).
