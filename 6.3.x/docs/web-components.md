---
layout: doc
title: Web components&#58;
---

A *pac4j* implementation must implement the necessary web components based on the *pac4j* logics:

- the [security filer](security-filter.html) to secure an URL, based on the `SecurityLogic`
- the [callback endpoint](callback-endpoint.html) to finish the login process for `IndirectClient` in web applications. Based on the `CallbackLogic`
- the [logout endpoint](logout-endpoint.html) to handle the local and central logouts, based on the `LogoutLogic`.

See [How to implement <i>pac4j</i> for a new framework/tool](how-to-implement-pac4j-for-a-new-framework.html) to get more technical details.
