---
layout: blog
title: Understanding the callback endpoint
author: Jérôme LELEU
date: June 2018
---

This is one of the most important concepts in **pac4j** and unfortunately, there are still many questions about it on the [pac4j-users mailing list](https://groups.google.com/forum/?fromgroups#!forum/pac4j-users). So let's focus on this topic.

# Overview

**pac4j** splits the authentication processes (we say clients) into two kinds:

- for web services (direct clients)
- for UI (indirect clients).

For most situations, it makes things much easier compared to other security frameworks and edge use cases can still be addressed with simple customizations.

On one side, when it comes to web services, credentials are passed on each HTTP request and the authentication process is performed for each HTTP request, although a cache mechanism can be used to avoid any performance bottleneck.  
The authenticated user only lives during the HTTP request. Thus, we can talk about a *stateless* mode.

On the other side, for the UI authentication process, the credentials are never passed to the application, they are filled in at the identity server level. In fact, in case of the basic auth and login form authentication processes, the identity server is the application, but it's more an exception than the general rule.  
So, credentials are filled in at the identity provider level and the application and the identity server must communicate via the appropriate protocol like OAuth, CAS, SAML or OpenID Connect in order to make the authentication process works.  
When a non-authenticated user calls an URL protected by an indirect client, the user is redirected to the related identity server for login. He fills in his credentials and if the credentials are valid, the user is redirected back to the application and the URL on which he is redirected back is the callback endpoint.  
The authentication process happens only once for the user session, the authenticated user lives during the whole web session (until expiration or explicit logout). Thus, we can talk about a *stateful* mode.


# Configuration

So the callback endpoint is the URL which receives the responses from the identity providers after a successful login. It is generally defined via the `Config` component:

```java
Config config = new Config("http://localhost:8080/callback", saml2Client, facebookClient);
```

or via the `Clients` component:

```java
Clients clients = new Clients("http://localhost:8080/callback", saml2Client, facebookClient);
```

And its value is `/callback` in most **pac4j** demos (although you can change it to whatever you want).

Technically speaking, in all the **pac4j** implementations (*j2e-pac4j*, *play-pac4j*, etc.), there is a `CallbackController` or a `CallbackFilter` which relies on the `DefaultCallbackLogic` component (from the [core pac4j project](https://github.com/pac4j/pac4j)) to handle callbacks.

This `CallbackController` or `CallbackFilter` must, of course, be defined on the same URL(s) in the web framework.

*For example*, with the *j2e-pac4j* implementation (in the *web.xml* file):

```xml
<filter>
    <filter-name>callbackFilter</filter-name>
    <filter-class>org.pac4j.j2e.filter.CallbackFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>callbackFilter</filter-name>
    <url-pattern>/callback</url-pattern>
</filter-mapping>
```

When reaching the callback endpoint, we are still in the middle of the authentication process and the user is still not authenticated, so **the callback endpoint must NOT be secured, it must always be accessible to anonymous users**.

Each authentication process (client) could have a different callback URL, but **in pac4j, all authentication mechanisms are expected to use the same callback endpoint** (except the CAS proxy endpoint) **with a specific parameter: `client_name` to distinguish them**.


# **pac4j** v2

You can define for each indirect client a specific callback URL, but even in that case, the `client_name` parameter would be added during the initialization of the *pac4j* framework.

*Example:*

```java
saml2Client.setCallbackUrl("http://app/samlCallback");
new Clients("http://app/callback", facebookClient, saml2Client);
```

will be initialized as:

- `http://app/samlCallback?client_name=SAML2Client` for the callback URL of the `SAML2Client`
- `http://app/callback?client_name=FacebookClient` for the callback URL of the `FacebookClient`.

This is great, but in some cases, query strings are not very well supported by the protocols or identity providers, so you can block the addition of the `client_name` parameter using the `setIncludeClientNameInCallbackUrl` method.
Which leads to a new issue as the callback endpoint can no longer find out which client is calling back, so the only option is to have only one client on the callback endpoint and define it as the default client at the `Clients` level: `clients.setDefaultClient(saml2Client);`

It works, but it's a bit tricky!


# **pac4j** v3

Hopefully, this is resolved in **pac4j** v3.

In version 3, there is a new component: the `CallbackUrlResolver` in addition to the `callbackUrl` property. This component computes the specific client callback URL every time the information is needed.

This is a difference with **pac4j** v2 where the client callback URLs were computed at the initialization of the framework and not every time they were needed. And this has a side effect for the SAML support:
the service provider entity identifier is by default the callback URL, which has the `client_name` parameter in **pac4j** v2, but not in **pac4j** v3 (which is a good thing as query strings in SAML entityIds are not well supported).

So, in version 3, with the default `QueryParameterCallbackUrlResolver`, both versions work the same way. But you can switch from the `QueryParameterCallbackUrlResolver` to the `PathParameterCallbackUrlResolver`.
In that case, no `client_name` parameter is added, but the client name is added at the end of the client callback URL.

*Example:*

```java
saml2Client.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
new Clients("http://app/callback", facebookClient, saml2Client);
```

will be computed as:

- `http://app/callback/SAML2Client` for the callback URL of the `SAML2Client`
- `http://app/callback?client_name=FacebookClient` for the callback URL of the `FacebookClient`.

You can even use the `NoParameterCallbackUrlResolver` to block the addition of the `client_name` parameter. In that case, you need define a default client at the callback endpoint which is done *this time* at the callback level: `callbackController.setClient("SAML2Client");`

Which may be not useful as **pac4j** v3 tries to be smarter than in its previous versions and will choose the appropriate client if only one client is defined for the callback endpoint (with v2, it fails).

I hope it makes things clear.
