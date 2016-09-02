---
layout: doc
title: IP address validation
---

*pac4j* allows you to validate incoming IP address.

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

## 2) `IpRegexpAuthenticator`

The [`IpRegexpAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/authorizer/IpRegexpAuthorizer.java) allows you to check that a given IP address is valid. It is generally defined for an [`IpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/IpClient.java).

After a successful credentials validation, it "returns" an [`IpProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/profile/IpProfile.java).

**Example:**

```java
IpClient ipClient = new IpClient(new IpRegexpAuthenticator("10\\..*"));
```
