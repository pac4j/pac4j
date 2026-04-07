---
layout: ddoc
title: Tests strategy&#58;
---

*pac4j* has two different kinds of tests:

## 1) Unit tests

Unit tests are run on each `mvn clean test` build and unit tests classes are suffixed by `Tests`.

## 2) Manual tests

Manual tests rely on evolving UI and thus can be often broken (Facebook login for example). They are available in `*.run` package and prefixed by `Run`: they must be launched manually (like any Java application).
