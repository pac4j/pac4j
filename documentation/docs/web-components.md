---
layout: doc
title: Web components&#58;
seo_title: "Security filters, callbacks and logout components | pac4j"
description: "Explore the web components a pac4j integration provides: security filters, authentication callbacks and logout endpoints backed by shared security logic."
---

A *pac4j* implementation must implement the necessary web components based on the *pac4j* logics:

- the [security filter](security-filter.html) to secure a URL, based on the `SecurityLogic`
- the [callback endpoint](callback-endpoint.html) to finish the login process for `IndirectClient` in web applications. Based on the `CallbackLogic`
- the [logout endpoint](logout-endpoint.html) to handle the local and central logouts, based on the `LogoutLogic`.

See [How to implement <i>pac4j</i> for a new framework/tool](how-to-implement-pac4j-for-a-new-framework.html) to get more technical details.
