---
layout: doc
title: Authentication flows
---

## 1) UI authentication (stateful / indirect client)

`pac4j` supports **UI authentication, that is stateful / indirect client**: the user is redirected to the external identity provider, logs in and is finally redirected back to the application.

![External/stateful authentication flow](http://www.pac4j.org/img/pac4j-stateful.png)

### CAS specific stateful authentication flow:

![CAS specific stateful authentication flow](http://www.pac4j.org/img/sequence_diagram.jpg)

---

## 2) Web services authentication (stateless / direct client)

`pac4j` also supports **web services authentication, that is stateless / direct client**: credentials are passed with the HTTP request and an `Authenticator` must be defined to validate the credentials (a specific `ProfileCreator` can also be defined to get the user profile from another source).

![Stateless authentication flow](http://www.pac4j.org/img/pac4j-stateless.png)

