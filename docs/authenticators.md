---
layout: doc
title: Authenticators&#58;
---

[HTTP](clients/http.html) [clients](clients.html) require an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) to validate the credentials.

This `Authenticator` interface has only one method: `void validate(Credentials credentials, WebContext context)`.

[`Credentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java) can be of two kinds:

- username/password are [`UsernamePasswordCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/UsernamePasswordCredentials.java)
- tokens or identifiers are [`TokenCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/TokenCredentials.java).

The [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/http/HttpAction.java) allows you to interrupt the credentials validation and trigger a specific HTTP action (like a temporary redirection).

You can use various `Authenticator` for many identity mechanisms:

- [LDAP](authenticators/ldap.html)
- [SQL](authenticators/sql.html)
- [JWT](authenticators/jwt.html)
- [MongoDB](authenticators/mongodb.html)
- [CouchDB](authenticators/couchdb.html)
- [IP address](authenticators/ip.html)
- [REST API](authenticators/rest.html)


## 1) Dealing with performance issues

For direct HTTP clients, credentials are passed and validated for each request, which may lead to performance issues (too many calls to the underlying identity system). So the use of a cache is highly recommended.

This can be done using the [`LocalCachingAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/LocalCachingAuthenticator.java) class (available in the `pac4j-core` module) which caches the resulted user profile depending on the provided credentials and can thus spare credentials validation on the identity system.

**Example:**

```java
LocalCachingAuthenticator authent = new LocalCachingAuthenticator(new JwtAuthenticator(secret), 10000, 15, TimeUnit.MINUTES);
```

By default, the `LocalCachingAuthenticator` uses Guava as its internal [`Store`](store.html) but you can provide your own store via the `setStore` method.

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Notice that this <code>LocalCachingAuthenticator</code> requires the additionnal <i>guava</i> dependency.</div>


## 2) `PasswordEncoder`

Regarding the IP address authenticator, there is no need for password protection. Regarding the LDAP authenticator, the password protection is handled by the system itself.

But for the MongoDB and SQL authenticators, the password protection must be handled explicitly by the [`PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/PasswordEncoder.java)
which can encode plaintext passwords into encrypted passwords as well as check if a plaintext password matches with an already encoded password.

The password encoder must be defined for these two authenticators via constructors or via the `setPasswordEncoder(passwordEncoder)` method.

Three `PasswordEncoder` implementations are available:

- a wrapper for the Spring Security Crypto [`PasswordEncoder`](https://github.com/spring-projects/spring-security/blob/master/crypto/src/main/java/org/springframework/security/crypto/password/PasswordEncoder.java): the [`SpringSecurityPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/SpringSecurityPasswordEncoder.java)
- a wrapper for the Apache Shiro [`PasswordService`](https://shiro.apache.org/static/1.4.0/apidocs/org/apache/shiro/authc/credential/PasswordService.html):  the [`ShiroPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/ShiroPasswordEncoder.java)
- one based on the jBCrypt library:  the [`JBCryptPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/JBCryptPasswordEncoder.java).

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Notice that the <code>SpringSecurityPasswordEncoder</code> requires the additionnal <i>spring-security-crypto</i> dependency, the <code>ShiroPasswordEncoder</code> the <i>shiro-core</i> dependency and the <code>JBCryptPasswordEncoder</code> the <i>jBCrypt</i> dependency.</div>


## 3) `ProfileCreator`

In fact, in the HTTP clients, you can also define the way the user profile is created via a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) in addition to the way of validating credentials (`Authenticator`).

In practice:

- all the available `Authenticator` create a specific user profile when validating credentials and save it in the current `Credentials`
- all the clients are configured by default with the [`AuthenticatorProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/AuthenticatorProfileCreator.java) which retrieves the user profile from the current `Credentials` and returns it.

So it works out of the box, even if providing a specific `ProfileCreator` is possible.
