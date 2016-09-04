---
layout: doc
title: Authenticators&#58;
---

[HTTP](/docs/clients/http.html) [clients](/docs/clients.html) require an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) to validate the credentials.

This `Authenticator` interface has only one method: `void validate(C credentials, WebContext context) throws HttpAction;`.

[`Credentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java) can be of two kinds:

- username / password are [`UsernamePasswordCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/UsernamePasswordCredentials.java)
- tokens or identifiers are [`TokenCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/TokenCredentials.java).

The [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/HttpAction.java) allows you to interrupt the credentials validation and trigger a specific HTTP action (like a temporary redirection).

You can use various `Authenticator` for many identity systems:

- [LDAP](/docs/authenticators/ldap.html)
- [SQL](/docs/authenticators/sql.html)
- [JWT](/docs/authenticators/jwt.html)
- [MongoDB](/docs/authenticators/mongodb.html)
- [Stormpath](/docs/authenticators/stormpath.html)
- [IP address](/docs/authenticators/ip.html)

## 1) Deal with performance issues

For direct HTTP clients, credentials are passed and validated for each request, which may lead to performance issues (too many calls to the underlying identity system). So the use of a cache is highly recommended.

This can be done using the [`LocalCachingAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/LocalCachingAuthenticator.java) class (available in the `pac4j-core` module) which caches the resulted user profile depending on the provided credentials and can thus spare credentials validation on the identity system.

**Example:**

```java
LocalCachingAuthenticator authent = new LocalCachingAuthenticator(new JwtAuthenticator(secret), 10000, 15, TimeUnit.MINUTES);
```

<div class="alert alert-danger"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Notice that this <code>LocalCachingAuthenticator</code> requires the additionnal <i>guava</i> dependency.</div>

## 2) `PasswordEncoder`

For the `Authenticator<UsernamePasswordCredentials>` types of authenticators, the root implementation: [`AbstractUsernamePasswordAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/AbstractUsernamePasswordAuthenticator.java) allows you to define a [`PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/PasswordEncoder.java) with the `setPasswordEncoder(passwordEncoder)` method: in that case, it will encode the password before the credentials check against the identity system.

By default, no encoding is performed ([`NopPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/NopPasswordEncoder.java)), but you can use the default implementation: [`BasicSaltedSha512PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/BasicSaltedSha512PasswordEncoder.java) or create your own.

## 3) `ProfileCreator`

In fact, in the HTTP clients, you can also define the way the user profile is created via a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) in addition to the way of validating credentials (`Authenticator`).

In practice:

- all the available `Authenticator` create a user profile when validating credentials and save it in the current `Credentials`
- all the clients are configured by default with the [`AuthenticatorProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/AuthenticatorProfileCreator.java) which retrieves the user profile from the current `Credentials` and returns it.

So it works out of the box, even if providing a specific `ProfileCreator` is perfectly feasible.
