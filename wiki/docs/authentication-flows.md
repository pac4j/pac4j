---
layout: doc
title: Authentication flows&#58;
---

## 1) UI authentication (stateful / indirect client)

`pac4j` supports **UI authentication, that is stateful / indirect client**: the user is redirected to the external identity provider, logs in and is finally redirected back to the application.

<div class="text-center">

<img alt="External/stateful authentication flow" src="/img/pac4j-stateful.png" />

<h3>CAS specific stateful authentication flow:</h3>

<img alt="CAS specific stateful authentication flow" src="/img/sequence_diagram.jpg" />

</div>

---

## 2) Web services authentication (stateless / direct client)

`pac4j` also supports **web services authentication, that is stateless / direct client**: credentials are passed with the HTTP request and an `Authenticator` must be defined to validate the credentials (a specific `ProfileCreator` can also be defined to get the user profile from another source).

<div class="text-center">

<img src="/img/pac4j-stateless.png" alt="Stateless authentication flow" />

</div>
