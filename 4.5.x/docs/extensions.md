---
layout: doc
title: Third-party extensions&#58;
---

There are extensions to *pac4j* developed by third parties. The extensions provide features not available in the core *pac4j* distribution that may be useful for a specific set of users having specific needs. At the moment, the following extensions are known:

### IDC Extensions

[IDC Extensions to PAC4J](https://github.com/jkacer/pac4j-extensions) is a project developed internally by IDC and published as open source.

It provides the following modules:
- *Database configuration of SAML clients* - This module allows you to configure a set of SAML2 clients using a relational database, such as Oracle DB. You need not change your PAC4J static configuration (e.g. a Spring XML file) to make configuration changes to the application. You just add a new row to a database table or modify an existing row and then restart your application. You can also implement a reload mechanism that will allow you to make configuration changes even without restarting the application.
