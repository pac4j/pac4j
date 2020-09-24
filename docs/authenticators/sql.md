---
layout: doc
title: Relational database
---

*pac4j* allows you to validate username/password and create, update and delete users on a SQL database.

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

## 2) `DbProfileService`

The [`DbProfileService`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/profile/service/DbProfileService.java) allows you to:

- validate a username/password on a relational database (it can be defined for HTTP clients which deal with `UsernamePasswordCredentials`)
- create, update or delete a user in the database.

It works with a [`DbProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/profile/DbProfile.java).

It is built from a `javax.sql.DataSource`.

**Example:**

```java
DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test", dbuser, dbpwd);
DbProfileService dbProfileService = new DbProfileService(dataSource);
```

The `users` table in the database must be created with the following script:

```sql
CREATE TABLE users
(
  id varchar(255),
  username varchar(255),
  password varchar(255),
  linkedid varchar(255),
  serializedprofile varchar(10000)
);

ALTER TABLE users
	ADD PRIMARY KEY (id),
	ADD KEY username (username),
	ADD KEY linkedid (linkedid);
```

The name of the table in the database can be changed via the `setUsersTable` method. As well as the `id`, `username` and `password` columns using the `setIdAttribute`, `setUsernameAttribute` and `setPasswordAttribute` methods.

The attributes of the user profile can be managed in the database in two ways:

- either each attribute is explicitly saved in a specific column and all these columns are defined as a list of column names separated by commas via the `setAttributes` method (it's the legacy mode existing since version 1.9)
- or the whole user profile is serialized and saved in the `serializedprofile` column.

This `DbProfileService` supports the use of a specific [`PasswordEncoder`](../authenticators.html#passwordencoder) to encode the passwords in the database.
