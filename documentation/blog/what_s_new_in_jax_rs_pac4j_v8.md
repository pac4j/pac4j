---
layout: blog
title: "JAX-RS now supports Jersey 4 and RESTEasy 7 with pac4j"
author: Jérôme LELEU
date: September 21, 2026
tags: [rel, guide]
draft: true
---

**jax-rs-pac4j 8.0.0 is out, with support for Jersey 4 and RESTEasy 7!** Both implement **Jakarta REST 4**, the API formerly known as JAX-RS. Two new modules join the existing Jersey 3 and RESTEasy 6 integrations.

You’ll need Java 17 and pac4j v6.x (at least v6.5.8). Let's get an endpoint running.

## Choose your module

Pick the module for your runtime. All four use the `org.pac4j` group and version `8.0.0`:

| Your runtime | Maven artifact | Jakarta REST |
|--------------|----------------|--------------|
| Jersey 3.1 | `jersey3-pac4j` | 3.1 |
| Jersey 4.0 | **`jersey4-pac4j`** (new) | 4.0 |
| RESTEasy 6.2 | `resteasy6-pac4j` | 3.1 |
| RESTEasy 7.0 | **`resteasy7-pac4j`** (new) | 4.0 |

For Jersey 4 with HTTP Basic authentication, add these dependencies:

```xml
<dependency>
  <groupId>org.pac4j</groupId>
  <artifactId>jersey4-pac4j</artifactId>
  <version>8.0.0</version>
</dependency>
<dependency>
  <groupId>org.pac4j</groupId>
  <artifactId>pac4j-http</artifactId>
  <version>6.5.8</version>
</dependency>
```

For RESTEasy 7, swap `jersey4-pac4j` for `resteasy7-pac4j`. Your application or server supplies the Jersey or RESTEasy runtime.

Remove the old module when upgrading. For example, `jersey3-pac4j` and `jersey4-pac4j` share Java packages, so you can't use both.

## Secure a Jersey 4 endpoint

Our `/me` endpoint will return the logged-in user's ID. First, configure Jersey in a Servlet container, using your application's username/password `authenticator`:

```java
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.jax.rs.servlet.features.Pac4JServletFeature;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;

var config = new Config(new DirectBasicAuthClient(authenticator));

var application = new ResourceConfig(MeResource.class)
    .register(new Pac4JServletFeature(config))
    .register(new Pac4JSecurityFeature())
    .register(new Pac4JValueFactoryProvider.Binder());
```

`Pac4JServletFeature` uses the container's `HttpSession` for sessions. For **Grizzly without Servlet**, use `Pac4JGrizzlyFeature`.

For an **API without sessions**, use `Pac4JJaxRsFeature` with `config.setSessionStoreFactory(NoOpSessionStoreFactory.INSTANCE)`. Remove that setting if you later enable container sessions: an explicit factory takes priority. Either way, the Basic client checks credentials on every request.

Now add the resource. `@Pac4JSecurity` protects it; `@Pac4JProfile` injects the user's profile:

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

@Path("/me")
public class MeResource {
    @GET
    @Pac4JSecurity(clients = "DirectBasicAuthClient")
    public String me(@Pac4JProfile CommonProfile profile) {
        return profile.getId();
    }
}
```

Deploy at the root context and try it with a valid account. `curl` will ask for the password:

```bash
curl --user alice http://localhost:8080/me
```

The same resource works with RESTEasy. Only the registration changes.

## Moving to RESTEasy 6.2 or 7

Check your server's Servlet and CDI versions before switching modules:

| Module | RESTEasy | Servlet | CDI / Weld |
|--------|----------|---------|------------|
| `resteasy6-pac4j` | 6.2 | 6.0 | 4.0 / 5.1 |
| `resteasy7-pac4j` | 7.0 | 6.1 | 4.1 / 6.0 |

Moving from RESTEasy 6.0 to 6.2 also requires Servlet 6.0. Keep CDI and Weld aligned with your server; the [dependency guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Dependencies) lists the versions we test.

With CDI, register `Pac4JSecurityFeature` as a class so CDI can inject its JAX-RS context. Register `Pac4JServletFeature` as an instance with your `config`:

```java
@Override
public Set<Class<?>> getClasses() {
    return Set.of(MeResource.class,
        Pac4JSecurityFeature.class,
        Pac4JProfileInjectorFactory.class);
}

@Override
public Set<Object> getSingletons() {
    return Set.of(new Pac4JServletFeature(config));
}
```

The [configuration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Security-configuration#resteasy-62-and-70-with-servlet-and-cdi) has the complete example, including imports and CDI setup.

We also brought back the Jersey tests, aligned Grizzly dependencies and removed an unused Log4j 1 declaration. **All 214 tests pass**, including a Servlet 6.1 test on Undertow EE.

To upgrade an existing application, follow the [v8 migration guide](https://github.com/pac4j/jax-rs-pac4j/wiki/Migration-guide).

<div class="text-center highlight-blog">
With v8, <b>pac4j's JAX-RS integration is up to date with Jersey 4, RESTEasy 7 and Jakarta REST 4.</b>
<br/>
Upgrade your runtime and keep the pac4j clients, annotations and profiles you already use.
</div>
