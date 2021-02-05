---
layout: doc
title: OpenID
---

*pac4j* allows you to login using the OpenID protocol v1.0 and v2.0.

Notice that this support is deprecated and will be removed in the next major version (v5).

## 1) Dependency

You need to use the following module: `pac4j-openid`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-openid</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `YahooClient`

The only available client is the [`YahooOpenIdClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-openid/src/main/java/org/pac4j/openid/client/YahooOpenIdClient.java) which allows you to login at Yahoo (using the OpenID protocol).

**Example:**

```java
YahooOpenIdClient client = new YahooOpenIdClient();
client.setCallbackUrl("http://localhost:8080/callback");
```
