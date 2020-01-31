---
layout: doc
title: Spring Boot Integration
---

## 0) Dependency

You need to use the following module: `pac4j-springboot`.

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

### Facebook

- `FacebookClientAutoConfiguration`

| Property                                              |
|-------------------------------------------------------|
| `pac4j.facebook.id`                                   |
| `pac4j.facebook.secret`                               |
| `pac4j.facebook.fields`                               |
| `pac4j.facebook.scope`                                |

### Twitter

- `TwitterClientAutoConfiguration`

| Property                                             |
|------------------------------------------------------|
| `pac4j.twitter.id`                                   |
| `pac4j.twitter.secret`                               |

