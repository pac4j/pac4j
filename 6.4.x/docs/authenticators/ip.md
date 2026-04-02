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

The IP address is retrieved via the `context.getRemoteAddr()` method. Though, on some infrastructure, the IP address is available in an HTTP header (like `X-Forwarded-For` or `x-real-ip`). So you can define the HTTP headers (one or more) from which you preferably want to retrieve the IP address. You can set the proxy IP, so pac4j can check if the remote address of the request is from your proxy server before searching in headers.

**Examples:**

```java
IpClient ipClient = new IpClient(new IpRegexpAuthenticator("10\\..*"));

IpExtractor ipHeaderExtractor = new IpExtractor(ipClient.getName());
ipHeaderExtractor.setAlternateIpHeaders("X-Forwarded-For", "x-real-ip");
ipHeaderExtractor.setProxyIp("127.0.0.1");
ipClient.setCredentialsExtractor(ipHeaderExtractor);
```
