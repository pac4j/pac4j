---
layout: doc
title: HTTP
---

### HTTP clients (pac4j-http module)

You can use the following clients depending on the way the credentials are passed to the HTTP request:

[Form (indirect)](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/FormClient.java) - [Basic auth (indirect)](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/indirect/IndirectBasicAuthClient.java) - [Basic auth (direct)](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/DirectBasicAuthClient.java) - [IP](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/IpClient.java) - [Cookie](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/CookieClient.java) - [Header](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/HeaderClient.java) - [Parameter](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/ParameterClient.java)

They require an [`Authenticator`](https://github.com/pac4j/pac4j/wiki/Authenticators) (and optionally a `ProfileCreator`):
