---
layout: doc
title: Stormpath
---

*pac4j* allows you to validate username / password on the Stormpath cloud provider.

## 1) Dependency

You need to use the following module: `pac4j-stormpath`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-stormpath</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `StormpathAuthenticator`

The [`StormpathAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-stormpath/src/main/java/org/pac4j/stormpath/credentials/authenticator/StormpathAuthenticator.java) validates username / password on Stormpath.

It can be defined for HTTP clients which deal with `UsernamePasswordCredentials`.

After a successful credentials validation, it "returns" a [`StormpathProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-stormpath/src/main/java/org/pac4j/stormpath/profile/StormpathProfile.java).

**Example:**

```java
StormpathAuthenticator authenticator = new StormpathAuthenticator(accessId, secretKey, applicationId);
```
