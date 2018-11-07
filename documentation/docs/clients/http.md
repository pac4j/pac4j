---
layout: doc
title: HTTP
---

*pac4j* allows you to login using HTTP mechanims (like basic auth or form posting).

The HTTP clients require to define an [Authenticator](../authenticators.html) to handle the credentials validation.

Except the `X509Client` with its default `X509Authenticator` whichs extracts an identifier from the subjectDN of the X509 certificate.


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
| username/password sent via a form posting | [`FormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/FormClient.java)  (indirect client)<br />[`DirectFormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/DirectFormClient.java) (direct client) |
| username/password sent via basic auth | [`IndirectBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/IndirectBasicAuthClient.java) (indirect client)<br />[`DirectBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/DirectBasicAuthClient.java) (direct client) |
| value sent as a cookie | [`CookieClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/CookieClient.java) (direct client) |
| value sent as a HTTP header | [`HeaderClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/HeaderClient.java) (direct client) |
| value sent as a HTTP parameter | [`ParameterClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/ParameterClient.java) (direct client) |
| IP address | [`IpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/IpClient.java) (direct client) |
| X509 certificate | [`X509Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/X509Client.java) (direct client) |
{:.table-striped}

**Examples:**

```java
// REST authentication with JWT token passed in the url as the "token" parameter
ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(salt));
parameterClient.setSupportGetRequest(true);
parameterClient.setSupportPostRequest(false);

// if the 'Authorization' header is passed with the 'Basic token' value
HeaderClient client = new HeaderClient("Authorization", "Basic ", (credentials, ctx) -> {
    String token = ((TokenCredentials) credentials).getToken();
    // check the token and create a profile
    if ("goodToken".equals(token)) {
        CommonProfile profile = new CommonProfile();
        profile.setId("myId");
        // save in the credentials to be passed to the default AuthenticatorProfileCreator
        credentials.setUserProfile(profile);
    }
});
```
