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

### a) Application configuration

To login with a CAS server, the indirect [`CasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasClient.java) must be defined (and optionally a [`CasProxyReceptor`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasProxyReceptor.java) to deal with proxies). Your web application protected by the `CasClient` will thus participate in the SSO.

Since *pac4j* v1.9.2, the CAS configuration must be defined in a [`CasConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/config/CasConfiguration.java) object instead of directly in the `CasClient`.

**Example:**

```java
CasClient casClient = new CasClient(new CasConfiguration("https://mycasserver/login"));
```

The `https://mycasserver/login` url is the login url of your CAS server.

After a sucessful login with a CAS server, a [`CasProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProfile.java) is returned.

The `CasConfiguration` can be built with the CAS login url and / or with the CAS prefix url (when different urls are required):

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setPrefixUrl("http://internal-cas-url");
```

You can also define the CAS protocol you want to support (`CasProtocol.CAS30` by default):

```java
config.setProtocol(CasProtocol.CAS20);
```

### b) CAS configuration

Assuming your callback url is `http://localhost:8080/callback`, the CAS server will be called via `https://mycasserver/login?service=http://localhost:8080/callback?client_name=CasClient`.

So you must define in the CAS services registry the appropriate CAS service matching this url: `http://localhost:8080/callback?client_name=CasClient` and with the appropriate configuration: which attributes to return? Does it support proxies?

Read the [CAS documentation](https://apereo.github.io/cas/4.2.x/installation/Service-Management.html) for that.

### c) Proxy support

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

### d) Logout configuration

To handle CAS logout requests, by default (J2E web context), the `CasSingleSignOutHandler` is defined: it must be used in conjonction with the J2E `SingleSignOutHttpSessionListener` and the `renewSession` flag must be disabled in the "callback filter" in that case.

**In the `web.xml` file:**

```xml
<listener>
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>
```

Though, a specific [`CasLogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/logout/CasLogoutHandler.java) can be specified in other frameworks. 

### e) In a stateless way

In fact, you can even login with the CAS login page using a **direct** [`DirectCasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasClient.java). No session will be created and thus no logout will be necessary.


---

## 3) Proxy tickets (web service)

If you want to call a web service from a web application using the CAS identity, the proxy mode must be used as explained above.

The generated proxy tickets must be sent to the web services and the web services must be properly protected by the direct [`DirectCasProxyClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasProxyClient.java). It requires a `CasConfiguration`.


**Example:**

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setProtocol(CasProtocol.CAS30_PROXY);
DirectCasProxyClient directCasProxyClient = new DirectCasProxyClient(config, "http://localhost:8080/webservices");
```

After generating a proxy ticket (like `PT-1`), the web service will be called on a url similar to: `http://localhost:8080/webservices/myoperation?ticket=PT-1`. 

The `DirectCasProxyClient` will validate the proxy ticket and the service url (defined in the constructor: `http://localhost:8080/webservices`) on the CAS server to get the identity of the user.

This requires to define the appropriate CAS service (matching the `http://localhost:8080/webservices` url) on the CAS server side.

This `DirectCasProxyClient` internally relies on the [`CasAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/credentials/authenticator/CasAuthenticator.java). See how to [deal with performance issues](/docs/authenticators.html#deal-with-performance-issues).

---

## 4) CAS REST API (web service)

The CAS server can be called via a [REST API](https://apereo.github.io/cas/4.2.x/protocol/REST-Protocol.html) if the feature is enabled.

The [`CasRestFormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestFormClient.java) and [`CasRestBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestBasicAuthClient.java) are direct clients which can be used to interact with the REST API of a CAS server:

- if the username / password of the user are sent via basic authentication, the `CasRestBasicAuthClient` will get them and validate them via the CAS REST API
- if the credentials are sent via form parameters, the `CasRestFormClient` will receive them and validate them via the CAS REST API.

**Example:**

```java
CasRestFormClient casRestClient = new CasRestFormClient("https://mycasserver/");
```

These direct clients internally rely on the [`CasRestAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/credentials/authenticator/CasRestAuthenticator.java). See how to [deal with performance issues](/docs/authenticators.html#deal-with-performance-issues).
