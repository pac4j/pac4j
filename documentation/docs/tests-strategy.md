---
layout: ddoc
title: Tests strategy&#58;
---

*pac4j* has three different kinds of tests:

## 1) Unit tests

Unit tests are run on each `mvn clean test` build and unit tests classes are suffixed by `Tests`.

## 2) Integration tests

Integration tests are disabled by default, so they won't run with `mvn clean install` (or `mvn clean verify`), you need the `forceIT` Maven profile to run them: `mvn -PforceIT clean install`. Integration tests classes are suffixed by `IT`.

## 3) Manual tests

Manual tests are like integration tests, except they rely on evolving UI and thus can be often broken (Facebook login for example). They are available in `*.run` package and prefixed by `Run`: they must be launched manually (like any Java application).
