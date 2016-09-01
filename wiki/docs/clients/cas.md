---
layout: doc
title: CAS
---

*pac4j* allows you to login with a CAS server in various ways:

- using the CAS login page (for a web application)
- using proxy tickets (for a web service)
- using the CAS REST API (for a web service)

It supports all CAS protocol versions (v1.0, v2.0 and v3.0).

<div class="alert alert-danger"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> The CAS server can also act as a SAML IdP, as an OpenID Connect provider or as an OpenID provider. In that case, you must not use the CAS support, but the appropriate SAML, OpenID Connect or OpenID supports.</div>

## 1) Dependency

You need to use the following module: `pac4j-cas`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

---

## 2) CAS login page (web application)

### a) Configuration

To login with a CAS server, the indirect [`CasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasClient.java) must be defined (and optionally a [`CasProxyReceptor`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasProxyReceptor.java) to deal with proxies). Your web application protected by the `CasClient` will thus participate in the SSO.

Since *pac4j* v1.9.2, the CAS configuration must be defined in a [`CasConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/config/CasConfiguration.java) object instead of directly in the `CasClient`.

**Example:**

```java
CasClient casClient = new CasClient(new CasConfiguration("https://mycasserver/login"));
```

The `https://mycasserver/login` url is the login url of your CAS server.

After a sucessful login with a CAS server, a [`CasProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProfile.java) is returned.

The `CasConfiguration` can be built with the CAS login url and with the CAS prefix url (when different urls are required):

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setPrefixUrl("http://internal-cas-url");
```

You can also define the CAS protocol you want to support (`CasProtocol.CAS30` by default):

```java
config.setProtocol(CasProtocol.CAS20);
```

### b) Proxy support

For proxy support, the `CasProxyReceptor` class must be used , defined on the same or new callback url (via the [security configuration](/docs/config.html)) and declared in the `CasConfiguration`:

```java
CasProxyReceptor casProxy = new CasProxyReceptor(); 
config.setProxyReceptor(casProxy);
// config.setAcceptAnyProxy(false);
// config.setAllowedProxyChains(proxies);
```

In this case, a [`CasProxyProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProxyProfile.java) is returned after a successful authentication and it can be used to get proxy tickets for other CAS services:

```java
CasProxyProfile casProxyProfile = (CasProxyProfile) casProfile;
String proxyTicket = casProxyProfile.getProxyTicketFor(anotherCasServiceUrl);
```

### c) Logout configuration

To handle CAS logout requests, by default (J2E web context), the `CasSingleSignOutHandler` is defined: it must be used in conjonction with the J2E `SingleSignOutHttpSessionListener` and the `renewSession` flag must be disabled in the "callback filter" in that case.

**In the `web.xml` file:**

```xml
<listener>
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>
```

Though, a specific [`CasLogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/logout/CasLogoutHandler.java) can be specified in other frameworks. 

---

## 3) Proxy tickets (web service)






 To validate proxy tickets (web services), the [`DirectCasProxyClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasProxyClient.java) must be used. Both requires a `CasConfiguration`.



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
