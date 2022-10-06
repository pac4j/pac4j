---
layout: doc
title: CAS
---

*pac4j* allows you to login with a CAS server in various ways:

1) using the **CAS login page** (for a web site): when accessing a protected web site, the user will be redirected to the CAS login page to enter his credentials before being granted access to the web site

2) using **proxy tickets** (for a web service): if the user is already authenticated by CAS in the web application (use case 1), the web application can request a proxy ticket and use it to call the web service which is protected by CAS

3) using the **CAS REST API** (for a web service): a standalone/mobile application can call a web service by providing the CAS user credentials (these credentials will be directly checked via the CAS REST API).

It supports all CAS protocol versions (v1.0, v2.0 and v3.0).

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> The CAS server can also act as a SAML IdP or as an OpenID Connect provider. In that case, you must not use the CAS support, but the appropriate SAML or OpenID Connect supports.</div>

## 0) Dependency

You need to use the following module: `pac4j-cas` (CAS client v3.x, JDK 11) or `pac4j-cas-clientv4` (CAS client v4.x, JDK 17).

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-cas</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

---

## 1) CAS login page (web site)

### a) Application configuration

To login with a CAS server, the indirect [`CasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasClient.java) must be defined (and optionally a [`CasProxyReceptor`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/CasProxyReceptor.java) to deal with proxies). Your web application protected by the `CasClient` will thus participate in the SSO.

The CAS configuration must be defined in a [`CasConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/config/CasConfiguration.java) object.

**Example:**

```java
CasClient casClient = new CasClient(new CasConfiguration("https://mycasserver/login"));
```

`https://mycasserver/login` is the login URL of your CAS server.

After a successful login with a CAS server, a [`CasProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/profile/CasProfile.java) is returned.

If an authentication delegation occurred in CAS and no proxy configuration is defined, the returned profile will be the original profile after the authentication delegation.

The `CasConfiguration` can be built with the CAS login URL and/or with the CAS prefix URL (when different URLs are required):

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setPrefixUrl("http://internal-cas-url");
```

You can define the CAS protocol you want to use (`CasProtocol.CAS30` by default):

```java
config.setProtocol(CasProtocol.CAS20);
```

You can also set various parameters:

| Method | Usage |
|--------|-------|
| `setEncoding(String)` |  Define the encoding used for parsing the CAS responses |
| `setRenew(boolean)` |  Define if the `renew` parameter will be used |
| `setGateway(boolean)` |  Define if the `gateway` parameter will be used |
| `setTimeTolerance(long)` |  Define the time tolerance for the SAML ticket validation (`CasProtocol.SAML`) |
| `setCallbackUrlResolver(CallbackUrlResolver)` |  Define a specific `CallbackUrlResolver` (by default, the `CallbackUrlResolver` of the `CasClient` is used) |
| `setDefaultTicketValidator(TicketValidator)` | Define the default `TicketValidator` to use |
{:.striped}


Furthermore, `renew` or `gateway` authentication requests can also be controlled on a per-request basis based on the presence of HTTP attributes defined in `RedirectionActionBuilder#ATTRIBUTE_FORCE_AUTHN` and `RedirectionActionBuilder#ATTRIBUTE_PASSIVE`.

### b) CAS configuration

Assuming your callback URL is `http://localhost:8080/callback`, the CAS server will be called by default via `https://mycasserver/login?service=http://localhost:8080/callback?client_name=CasClient`.

So you must define in the CAS services registry the appropriate CAS service matching this URL: `http://localhost:8080/callback?client_name=CasClient` and with the appropriate configuration: which attributes to return? Does it support proxies?

Read the [CAS documentation](https://apereo.github.io/cas/6.2.x/services/Service-Management.html) for that.


### c) Proxy support

For proxy support, the `CasProxyReceptor` component must be used, defined on the same or a new callback URL (via the [security configuration](../config.html)) and declared in the `CasConfiguration`:

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

To correlate proxy information, the `CasProxyReceptor` uses an internal [`Store`](../store.html) that you can change via the `setStore` method (by default, Guava is used).


### d) Logout configuration

To handle CAS logout requests, a [`DefaultLogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/handler/DefaultLogoutHandler.java) is automatically created. Unless you specify your own implementation of the [`LogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/handler/LogoutHandler.java) interface.

The `DefaultLogoutHandler`:

- relies on the capabilities of the `SessionStore` (`destroySession`, `getTrackableSession` and `buildFromTrackableSession`  methods)
- stores data in a [`Store`](../store.html) that you can change via the `setStore` method (by default, Guava is used).


### e) In a stateless way

In fact, you can even login with the CAS login page using a **direct** [`DirectCasClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasClient.java). No callback URL will be involved: the requested URL will be called back after the CAS login. No session will be created and thus no logout will be necessary.


---

## 2) Proxy tickets (web service)

If you want to call a web service from a web application using the CAS identity, the proxy mode must be used as explained above.

The generated proxy tickets must be sent to the web services and the web services must be properly protected by the direct [`DirectCasProxyClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/direct/DirectCasProxyClient.java). It requires a `CasConfiguration`.

**Example:**

```java
CasConfiguration config = new CasConfiguration();
config.setLoginUrl("https://casserverpac4j.herokuapp.com/login");
config.setProtocol(CasProtocol.CAS30_PROXY);
DirectCasProxyClient directCasProxyClient = new DirectCasProxyClient(config, "http://localhost:8080/webservices");
```

After generating a proxy ticket (like `PT-1`), the web service will be called on a URL similar to: `http://localhost:8080/webservices/myoperation?ticket=PT-1`.

You can define a list of URL patterns using the `setAllowedProxies(List<String>)` method on the `CasConfiguration` object which applications are allowed to act as an proxy for this application.

The `DirectCasProxyClient` will validate the proxy ticket and the service URL (defined in the constructor: `http://localhost:8080/webservices`) on the CAS server to get the identity of the user.

This requires to define the appropriate CAS service (matching the `http://localhost:8080/webservices` URL) on the CAS server side.

This `DirectCasProxyClient` internally relies on the [`CasAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/credentials/authenticator/CasAuthenticator.java). See how to [deal with performance issues](../authenticators.html#1-dealing-with-performance-issues).

---

## 3) CAS REST API (web service)

The CAS server can be called via a [REST API](https://apereo.github.io/cas/6.2.x/protocol/REST-Protocol.html) if the feature is enabled.

The [`CasRestFormClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestFormClient.java) and [`CasRestBasicAuthClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/client/rest/CasRestBasicAuthClient.java) are direct clients which can be used to interact with the REST API of a CAS server:

- if the username/password of the user are sent via basic authentication, the `CasRestBasicAuthClient` will get them and validate them via the CAS REST API
- if the credentials are sent via form parameters, the `CasRestFormClient` will receive them and validate them via the CAS REST API.

**Example:**

```java
CasConfiguration casConfig = new CasConfiguration("https://mycasserver/login");
CasRestFormClient casRestClient = new CasRestFormClient(casConfig);
```

These direct clients internally rely on the [`CasRestAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-cas/src/main/java/org/pac4j/cas/credentials/authenticator/CasRestAuthenticator.java). See how to [deal with performance issues](../authenticators.html#1-dealing-with-performance-issues).

After a successful authentication via the `CasRestBasicAuthClient`/`CasRestFormClient`, a `CasRestProfile` will be created.

This profile has no attributes as it was built by validating the CAS credentials on the REST API. You must request a service ticket and validate it to get a `CasProfile` with attributes (as the default protocol used is CAS v3.0).

Indeed, with the `CasRestProfile`, you'll be able to:

- request service tickets: `TokenCredentials tokenCredentials = casRestClient.requestServiceTicket(serviceUrl, casRestProfile, context)`
- validate them: `CasProfile casProfile = casRestClient.validateServiceTicket(serviceUrl, tokenCredentials, context)`
- or destroy the previous authentication: `casRestClient.destroyTicketGrantingTicket(casRestProfile, context)`.
