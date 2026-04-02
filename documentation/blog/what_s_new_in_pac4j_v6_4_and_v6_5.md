---
layout: blog
title: What's new in pac4j v6.4 and v6.5?
author: Jérôme LELEU
date: 2026
---

### 1) Removal of the "old" modules

The `pac4j-gae` module dedicated to the old Google App Engine authentication mechanism has been deprecated in v6.4.0 and removed in v6.5.0.

The `pac4j-couch` module to support CouchDB as an identity storage has been deprecated in v6.4.0 and removed in v6.5.0.


### 2) Configuration via Java

To address any Java environment, pac4j configurations must be defined via Java code to benefit from discoverability via code completion.

The `pac4j-config` and `pac4j-springboot` modules which allow a properties configuration (no discovery available, documentation must be read) have been deprecated in v6.4.0 and removed in v6.5.0.

Client and configuration classes offer easy constructors and chainable setters for quick configuration.


### 3) Build

The build process has been simplified (30% faster):
- 4 modules have been removed
- no more `test-jar` components: the few test classes are in the `org.pac4j.test` package of the `pac4j-core` module
- OSGi and shading phases have been removed.

OSGi support and shading have been removed due to lack of usage and to simplify the Maven configuration.

If you rely on OSGi or need any shaded JARs, please report your use case as feedback is still welcome.


### 4) Improvement of the OpenID support

#### a) Technically

#### b) Functionnaly


### 5) Support of OpenID Federation


### 6) Learn more

Read the [release notes](../docs/release-notes.html) for a thorough presentation of the changes.
