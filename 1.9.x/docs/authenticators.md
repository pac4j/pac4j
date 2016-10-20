---
layout: doc
title: Authenticators&#58;
---

[HTTP](clients/http.html) [clients](clients.html) require an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) to validate the credentials.

This `Authenticator` interface has only one method: `void validate(C credentials, WebContext context) throws HttpAction;`.

[`Credentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java) can be of two kinds:

- username / password are [`UsernamePasswordCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/UsernamePasswordCredentials.java)
- tokens or identifiers are [`TokenCredentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/TokenCredentials.java).

The [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/HttpAction.java) allows you to interrupt the credentials validation and trigger a specific HTTP action (like a temporary redirection).

You can use various `Authenticator` for many identity systems:

- [LDAP](authenticators/ldap.html)
- [SQL](authenticators/sql.html)
- [JWT](authenticators/jwt.html)
- [MongoDB](authenticators/mongodb.html)
- [Stormpath](authenticators/stormpath.html)
- [IP address](authenticators/ip.html)

## 1) Deal with performance issues

For direct HTTP clients, credentials are passed and validated for each request, which may lead to performance issues (too many calls to the underlying identity system). So the use of a cache is highly recommended.

This can be done using the [`LocalCachingAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/LocalCachingAuthenticator.java) class (available in the `pac4j-core` module) which caches the resulted user profile depending on the provided credentials and can thus spare credentials validation on the identity system.

**Example:**

```java
LocalCachingAuthenticator authent = new LocalCachingAuthenticator(new JwtAuthenticator(secret), 10000, 15, TimeUnit.MINUTES);
```

<div class="alert alert-danger"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Notice that this <code>LocalCachingAuthenticator</code> requires the additionnal <i>guava</i> dependency.</div>

## 2) `PasswordEncoder`

For the `Authenticator<UsernamePasswordCredentials>` types of authenticators, the root implementation: [`AbstractUsernamePasswordAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/AbstractUsernamePasswordAuthenticator.java) allows you to define a [`PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/PasswordEncoder.java) with the `setPasswordEncoder(passwordEncoder)` method.

The [`PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/PasswordEncoder.java) can encode plaintext passwords into crypted passwords as well as check if a plaintext password matches with an already encoded password.
The latter is especially used in database [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java)s such as [`MongoAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/credentials/authenticator/MongoAuthenticator.java) or [`DbAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/credentials/authenticator/DbAuthenticator.java).

By default, no encoding is performed ([`NopPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/NopPasswordEncoder.java)), but you can use one of the default implementations: [`BasicSaltedSha512PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/BasicSaltedSha512PasswordEncoder.java), [`JBCryptPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/JBCryptPasswordEncoder.java) or create your own.
Wrappers for Spring Security Crypto [`PasswordEncoder`](https://github.com/spring-projects/spring-security/blob/master/crypto/src/main/java/org/springframework/security/crypto/password/PasswordEncoder.java) ([`SpringSecurityPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/SpringSecurityPasswordEncoder.java)) and Apache Shiro [`PasswordService`](https://shiro.apache.org/static/1.2.5/apidocs/org/apache/shiro/authc/credential/PasswordService.html) ([`ShiroPasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/ShiroPasswordEncoder.java)) are also available.

<div class="alert alert-danger"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Notice that the <code>SpringSecurityPasswordEncoder</code> requires the additionnal <i>spring-security-crypto</i> dependency, the <code>ShiroPasswordEncoder</code> the <i>shiro-core</i> dependency, the <code>JBCryptPasswordEncoder</code> the <i>jBCrypt</i> dependency and the <code>BasicSaltedSha512PasswordEncoder</code> the <i>commons-codec</i> dependency.</div>

## 3) `ProfileCreator`

In fact, in the HTTP clients, you can also define the way the user profile is created via a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) in addition to the way of validating credentials (`Authenticator`).

In practice:

- all the available `Authenticator` create a specific user profile when validating credentials and save it in the current `Credentials`
- all the clients are configured by default with the [`AuthenticatorProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/AuthenticatorProfileCreator.java) which retrieves the user profile from the current `Credentials` and returns it.

So it works out of the box, even if providing a specific `ProfileCreator` is perfectly feasible.

Notice that you can change the returned profile from the `AuthenticatorProfileCreator` by using the `setProfileFactory` method to build the appropriate profile.
