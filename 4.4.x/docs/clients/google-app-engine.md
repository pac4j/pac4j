---
layout: doc
title: Google App Engine
---

*pac4j* allows you to login using the Google App Engine UserService.

## 1) Dependency

You need to use the following module: `pac4j-gae`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-gae</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `GaeUserServiceClient`

The only available client is the [`GaeUserServiceClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-gae/src/main/java/org/pac4j/gae/client/GaeUserServiceClient.java) which must be used in your Google App Engine web applications.

**Example:**

```java
GaeUserServiceClient client = new GaeUserServiceClient();
client.setCallbackUrl("http://localhost:8080/callback");
```
