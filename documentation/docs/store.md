---
layout: doc
title: Store
---

In some cases, a cache mechanism is required. In *pac4j*, this is defined by the [`Store`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/Store.java) concept.

It has the following methods:

- `get`: gets a value from the store
- `set`: sets a value in the store
- `remove`: removes a value (by its key) from the store.

It has the following implementations, and you may provide your own if necessary:

- the [`GuavaStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/GuavaStore.java): entries expire after a given timeout and the number of entries is capped. It requires the optional `guava` dependency
- the [`ConcurrentMapStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/ConcurrentMapStore.java): entries expire after a given timeout as well, but the store has no maximum size. It requires no dependency at all.

```java
val store = new ConcurrentMapStore<String, String>(5, TimeUnit.MINUTES);
```

When the stored values carry their own expiration date, extend the [`AbstractConcurrentMapStore`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/store/AbstractConcurrentMapStore.java) and read the date from the value: there is then a single lifetime to define instead of two which must agree.

```java
public class MyStore extends AbstractConcurrentMapStore<String, MyValue> {

    @Override
    protected Instant expiresAt(final MyValue value) {
        return value.getExpiresAt();
    }
}
```

The expiration date is computed once, when the value is stored: changing it afterwards is only taken into account on the next `set`. Returning `null` keeps the entry forever.

Entries are dropped when they expire: when they are read and when another value is stored. Neither of these two stores runs a background thread.
