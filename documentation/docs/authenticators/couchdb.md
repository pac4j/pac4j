---
layout: doc
title: CouchDB
---

*pac4j* allows you to validate username/password and create, update and delete users on a CouchDB database.

## 1) Dependency

You need to use the following module: `pac4j-couch`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-couch</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `CouchProfileService`

The [`CouchProfileService`](https://github.com/pac4j/pac4j/blob/master/pac4j-couch/src/main/java/org/pac4j/couch/profile/service/CouchProfileService.java) allows you to:

- validate a username/password on a CouchDB database (it can be defined as the `Authenticator` for HTTP clients which deal with `UsernamePasswordCredentials`)
- create, update or delete a user in the CouchDB database.

It works with a [`CouchProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-couch/src/main/java/org/pac4j/couch/profile/CouchProfile.java).

It is built from a `org.ektorp.CouchDbConnector`.

**Example:**

```java
HttpClient httpClient = new StdHttpClient.Builder().url(couchDbUrl).build();
CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
CouchDbConnector couchDbConnector = dbInstance.createConnector("users", true);
CouchProfileService couchProfileService = new CouchProfileService(couchDbConnector);
```

The choice of the database name is irrelevant to `CouchProfileService`. The database containing the users must contain the following design document:

```json
{
	"_id": "_design/pac4j",
	"language": "javascript",
	"views": {
		"by_username": {
			"map": "function(doc) {if (doc.username) emit(doc.username, doc);}"
		},
		"by_linkedid": {
			"map": "function(doc) {if (doc.linkedid) emit(doc.linkedid, doc);}"
		}
	}
}
```

The `id`, `username` and `password` attribute names can be changed using the `setIdAttribute`, `setUsernameAttribute` and `setPasswordAttribute` methods. By default, the `id` attribute is CouchDB's `_id` attribute. If you change the `username` or `linkedid` attribute, please change the design document accordingly. You can also get/set the ObjectMapper used to serialize the JSON data from CouchDB with `getObjectMapper()` and `setObjectMapper()`, the default one is simply `new ObjectMapper()`.

The attributes of the user profile can be managed in the CouchDB collection in two ways:

- either each attribute is explicitly saved in a specific attribute and all these attributes are defined as a list of names separated by commas via the `setAttributes` method (it's the legacy mode existing since version 1.9)
- or the whole user profile is serialized and saved in the `serializedprofile` attribute.

This `CouchProfileService` supports the use of a specific [`PasswordEncoder`](authenticators.html#passwordencoder) to encode the passwords in the CouchDB database.
