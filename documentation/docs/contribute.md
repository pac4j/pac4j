---
layout: ddoc
title: How to contribute
seo_title: "How to contribute code and documentation | pac4j"
description: "Learn how to contribute features, bug fixes and documentation to pac4j, the Java authentication and authorization framework."
---

## 1) Discuss substantial changes first

If your change is clear and well-scoped, simply open a pull request.

For a substantial or complex change (a new feature, a new module, a change of behavior...), discuss it first on the [pac4j-dev](https://groups.google.com/forum/?fromgroups#!forum/pac4j-dev) Google group, so that we agree on the approach before you invest time in it.

**Never report a vulnerability through a public pull request**: follow the [security policy](https://github.com/pac4j/pac4j/blob/master/SECURITY.md) instead.

## 2) Open a pull request

- Target the `master` branch. If the change must also be backported to a maintenance branch (like `6.5.x`), open a second pull request against that branch.
- One pull request per topic.
- Add a line describing your change for the next version in the [release notes](release-notes.html).
- Update the documentation for any change visible to users (see below).

## 3) Build and test

The project requires Java 17 and Maven. Before submitting, run:

```shell
mvn clean verify
```

It runs Checkstyle, PMD, SpotBugs and the unit tests: the build must pass.

Your code should be easy to read and use Lombok to reduce boilerplate (`@Getter`, `@Setter`, `@Slf4j`, `val`...).

Any change must come with the appropriate [tests](tests-strategy.html): unit test classes are suffixed by `Tests`.

## 4) Documentation

The documentation lives in the `documentation` directory and is published as this website.
You can browse it locally at [http://localhost:4000](http://localhost:4000) by running `bundle exec jekyll serve` in that directory: changes are visible immediately.
