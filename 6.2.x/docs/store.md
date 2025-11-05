---
layout: doc
title: Store
---

In some cases, a cache mechanism is required. In *pac4j*, this is defined by the [`Store`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/Store.java) concept.

It has the following methods:

- `get`: gets a value from the store
- `set`: sets a value in the store
- `remove`: removes a value (by its key) from the store.

It has only one default implementation using Guava: the [`GuavaStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/GuavaStore.java). But you may provide your own if necessary.
