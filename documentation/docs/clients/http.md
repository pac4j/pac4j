---
layout: doc
title: HTTP
---

*pac4j* allows you to login using HTTP mechanims (like basic auth or form posting).

The HTTP clients require to define an [Authenticator](/docs/authenticators.html) to handle the credentials validation.

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

## 2) Clients

You can use the following clients depending on what are the credentials and how they are passed in the HTTP request:

| Credentials | Client |
|-------------|--------|
| username / password sent via a form posting | [`FormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/FormClient.java)  (indirect client)<br />[`DirectFormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/DirectFormClient.java) (direct client) |
| username / password sent via basic auth | [`IndirectBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/IndirectBasicAuthClient.java) (indirect client)<br />[`DirectBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/DirectBasicAuthClient.java) (direct client) |
| value sent as a cookie | [`CookieClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/CookieClient.java) (direct client) |
| value sent as a HTTP header | [`HeaderClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/HeaderClient.java) (direct client) |
| value sent as a HTTP parameter | [`ParameterClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/ParameterClient.java) (direct client) |
| IP address | [`IpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/IpClient.java) (direct client) |
{:.table-striped}

**Example:**

```java
// REST authentication with JWT token passed in the url as the "token" parameter
ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(salt));
parameterClient.setSupportGetRequest(true);
parameterClient.setSupportPostRequest(false);
```
