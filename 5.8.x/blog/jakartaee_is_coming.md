---
layout: blog
title: JakartaEE is coming
author: Jérôme LELEU
date: April 2022
---

For a long time, the Java Enterprise Edition has lived its life under the umbrella of Sun Microsystems then Oracle.

But the project is now hosted by the Eclipse Foundation.

While the transition started smoothly, things have come to a breaking turn when the `javax.servlet` package has been moved to the `jakarta.servlet` package.

Several voices have raised to request the support of JakartaEE in `pac4j` and this post explains the roadmap on this.

In fact, several `pac4j` implementations exist for various JEE frameworks so the main JEE components exist in the `pac4j` core project instead of inside each of the implementations.

## pac4j v5.2: 1/2

A first step was made in version 5.2 of pac4j where the JEE components (= JavaEE, based on `javax.servlet`) have been moved from the `pac4j-core` dependency into the `pac4j-jee` dependency.

This was not really a breaking change as they remain in the same `org.pac4j.core` package; this was just a matter of switching from one dependency to another dependency.

Apart from the JakartaEE roadmap, it really was a good change to be able to differentiate between the `pac4j` JEE implementations (based on `pac4j-jee`) and the `pac4j` none-JEE implementations (based on `pac4j-core`).

## pac4j v5.4: 2/2

With pac4j v5.4 comes the second (and last) step to support JakartaEE.

The `pac4j-jee` dependency is now deprecated and will be removed in version 5.5.x.

It has been replaced by two new dependencies:
- `pac4j-javaee` (JavaEE) based on `javax.servlet`
- `pac4j-jakartaee` (JakartaEE) based on `jakarta.servlet`.

In these new dependencies, the JEE components are now in the `org.pac4j.jee` package, which is a breaking change but won't be one when you switch from JavaEE (`pac4j-javaee`) to JakartaEE (`pac4j-jakartaee`).

As it's a breaking change, new major versions of the JEE implementations have been released:
- `spring-security-pac4j` v7.0.0
- `spring-webmvc-pac4j` v6.0.0
- `buji-pac4j` v7.0.0
- `jee-pac4j` v7.0.0

They have moved from `pac4j-jee` to `pac4j-javaee`.

The `jee-pac4j` project has even been split into two new modules:
- `javaee-pac4j` (based on `pac4j-javaee`)
- `jakartaee-pac4j` (based on `pac4j-jakartaee`).

While the `pac4j` JakartaEE support is only used once currently, it will be used more and more in the future (Spring 6).
