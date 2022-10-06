---
layout: doc
title: Spring Boot Integration
---

## 0) Dependency

You need to use the following module: `pac4j-springboot` (Spring 5, Boot v2, JDK 11) or `pac4j-springbootv3` (Spring 6, Boot v3, JDK 17).

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-springboot</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 1) Auto-Configuration

The following auto-configuration classes are provided:

- `ConfigAutoConfiguration`

This auto-configuration will inject a Spring Bean of type `Config` into the application runtime
that can be used as such:

```java
@Autowired
private Config config;
```

The `Config` object may carry built instances of pac4j clients, etc using
the strategy and properties defined by the `PropertiesConfigFactory`, bootstrapped from the following settings:

- `pac4j.callbackUrl=`
- `pac4j.properties.[path-to-property]=[value]`

For example, to create a `TwitterClient` instance:

```properties
pac4j.properties.twitter.id=id
pac4j.properties.twitter.secret=secret
```
