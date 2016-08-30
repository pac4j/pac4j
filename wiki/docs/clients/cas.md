---
layout: doc
title: CAS
---

### Module: `pac4j-cas`

To login with a CAS server, the indirect [`CasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasClient.java) must be defined and optionally a [`CasProxyReceptor`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasProxyReceptor.java) to deal with proxies. To validate proxy tickets (web services), the [`DirectCasProxyClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasProxyClient.java) must be used. Both requires a `CasConfiguration`.

**Example:**

```java
CasClient casClient = new CasClient(new CasConfiguration("https://mycasserver/login"));
```

The [`CasRestFormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestFormClient.java) and [`CasRestBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestBasicAuthClient.java) are direct clients which can be used to interact with the REST API of a CAS server. For performance reasons, a `LocalCachingAuthenticator` should be used to avoid sending the credentials for each request.

**Example:**

```java
CasRestFormClient casRestClient = new CasRestFormClient(new LocalCachingAuthenticator(new CasRestAuthenticator("https://mycasserver/"), 100, 10, TimeUnit.SECONDS));
```

---

After a sucessful login with a CAS server, a [`CasProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProfile.java) is returned.

The `CasConfiguration` can be built with the CAS login url and / or with the CAS prefix url:

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setPrefixUrl("https://casserverpac4j.herokuapp.com");
```

You can also define the CAS protocol you want to support (`CasProtocol.CAS30` by default):

```java
config.setProtocol(CasProtocol.CAS20);
```

For proxy support, the `CasProxyReceptor` class must be used (on the same or new callback url) and declared with the `CasConfiguration` class:

```java
config.setProxyReceptor(new CasProxyReceptor());
// config.setAcceptAnyProxy(false);
// config.setAllowedProxyChains(proxies);
```

In this case, a [`CasProxyProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProxyProfile.java) is returned after a successful authentication and it can be used to get proxy tickets for other CAS services:

```java
CasProxyProfile casProxyProfile = (CasProxyProfile) casProfile;
String proxyTicket = casProxyProfile.getProxyTicketFor(anotherCasService);
```

To handle CAS logout requests, a specific [`CasLogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/logout/CasLogoutHandler.java) can be specified. By default (J2E), the `CasSingleSignOutHandler` is defined: it must be used in conjonction with the J2E `SingleSignOutHttpSessionListener` and the `renewSession`flag must be disabled in the "callback filter" in that case.
