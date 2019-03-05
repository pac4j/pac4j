---
layout: doc
title: REST API
---

*pac4j* allows you to validate users via a REST API.

## 1) Dependency

You need to use the following module: `pac4j-http`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-http</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `RestAuthenticator`

The [`RestAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/credentials/authenticator/RestAuthenticator.java) validates the provided username/password by POSTing them as a basic authentication to an URL which must return:
 
- a 200 HTTP response with a user profile as a JSON if the username/password credentials are valid

- any other HTTP status code (preferably 401) if the username/password credentials are not valid.

In case of a successful authentication, a [`RestProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/profile/RestProfile.java) is returned.

**Example of a correct server response:**

```json
{
  "id": "1234",
  "attributes": {
    "firstName": "Jerome"
  }
}
```

**Example of a client:**

```java
RestAuthenticator authenticator = new RestAuthenticator("http://rest-api-url");
DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(authenticator);
```
