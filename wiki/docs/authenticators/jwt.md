---
layout: doc
title: LDAP
---


## Deal with performance issues


## Understanding the concept

A [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) represents an **authentication mechanism** defined by the following methods:

| Method | Associated sub-component|
|--------|-------------------------|
| `HttpAction redirect(WebContext context) throws HttpAction` (only for indirect clients) | the redirection of the user to the identity provider can be defined via a [`RedirectActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectActionBuilder.java) |
| `C getCredentials(WebContext context) throws HttpAction` | the extraction of the credentials can be done by a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) while the credentials validation is ensured by an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) |
| `U getUserProfile(C credentials, WebContext context) throws HttpAction` | the creation of the authenticated user profile can be performed by a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) |

Clients are generally populated with a default `RedirectActionBuilder`, `CredentialsExtractor` and `ProfileCreator`, but an `Authenticator` must be defined for credentials validation. Other sub-components can of course be changed for different [customizations](https://github.com/pac4j/pac4j/wiki/Customizations).

In fact, the `Authenticator` also creates the user profile during the credentials validation and saves it in the credentials, so that the default `AuthenticatorProfileCreator` defined in clients can retrieve it and does not need to be customized (even if this is perfectly feasible).

---

### Available authenticators:

#### `LdapAuthenticator` (pac4j-ldap module)

The [`LdapAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-ldap/src/main/java/org/pac4j/ldap/credentials/authenticator/LdapAuthenticator.java) can be used for LDAP authentication, for clients which deals with `UsernamePasswordCredentials`. It is based on the great [Ldpative](http://www.ldaptive.org/) library and built with a `org.ldaptive.auth.Authenticator`.

**Example**:

```java
LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(ldaptiveAuthenticator);
```

==

#### `JwtAuthenticator` (pac4j-jwt module)

The [`JwtAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/credentials/authenticator/JwtAuthenticator.java) validates JWT tokens produced by the [`JwtGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/profile/JwtGenerator.java) or by any other means.

It supports plain text or signed (and encrypted) JWT tokens. To handle that use cases, [`SignatureConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/SignatureConfiguration.java) can be defined: `MacSignatureConfiguration`, `RSASignatureConfiguration` and `ECSignatureConfiguration` as well as [`EncryptionConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/EncryptionConfiguration.java) like the `DirectEncryptionConfiguration`.

**Example**:

```java
JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
jwtAuthenticator.addSignatureConfiguration(new MacSignatureConfiguration(KEY2));
KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
KeyPair rsaKeyPair = keyGen.generateKeyPair();
jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(rsaKeyPair));
jwtAuthenticator.setEncryptionConfiguration(new DirectEncryptionConfiguration(SECRET));
```

==

#### `DbAuthenticator` (pac4j-sql module)

The [`DbAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-sql/src/main/java/org/pac4j/sql/credentials/authenticator/DbAuthenticator.java) validates username / password on a relational database. It is built with a `javax.sql.DataSource`.

**Example:**

```java
DbAuthenticator authenticator = new DbAuthenticator(dataSource);
```

==

#### `MongoAuthenticator` (pac4j-mongo module)

The [`MongoAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-mongo/src/main/java/org/pac4j/mongo/credentials/authenticator/MongoAuthenticator.java) validates username / password on a MongoDB database. It is built with a `com.mongodb.MongoClient`.

**Example:**

```java
MongoAuthenticator authenticator = new MongoAuthenticator(mongoClient);
```

==

#### `IpRegexpAuthenticator` (pac4j-http module)

The [`IpRegexpAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/authorization/authorizer/IpRegexpAuthorizer.java) allows you to check that a given IP address is valid. It is generally defined for an [`IpClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-http/src/main/java/org/pac4j/http/client/direct/IpClient.java).

==

#### `StormpathAuthenticator` (pac4j-stormpath module)

The [[`StormpathAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-stormpath/src/main/java/org/pac4j/stormpath/credentials/authenticator/StormpathAuthenticator.java) validates username / password on the Strompath cloud provider.

**Example:**

```java
StormpathAuthenticator authenticator = new StormpathAuthenticator(accessId, secretKey, applicationId);
```

==

#### `LocalCachingAuthenticator` (pac4j-core module)

There is also a specific [`LocalCachingAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/LocalCachingAuthenticator.java) class available which does not actually perform any credentials validation, but caches the resulted user profile depending on the provided credentials.

For direct clients, credentials are passed and validated for each request, which may lead to performance issues so the use of a cache is generally recommended and this can be done using the `LocalCachingAuthenticator`:

**Example:**

```java
authenticator = new LocalCachingAuthenticator(new JwtAuthenticator(secret), 10000, 15, TimeUnit.MINUTES);
```

==

#### Password encoders:

For the `Authenticator<UsernamePasswordCredentials>` types of authenticators, the root implementation: [`AbstractUsernamePasswordAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/AbstractUsernamePasswordAuthenticator.java) allows you to define a `PasswordEncoder` which may be used in the authenticators: the password will be encoded before being checked against the source of the credentials.

By default, no encoding is performed (`NopPasswordEncoder`), but you can use the default implementation: [`BasicSaltedSha512PasswordEncoder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/password/BasicSaltedSha512PasswordEncoder.java) or create your own.

---

### Available profile creators:

Currently, the [`AuthenticatorProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/AuthenticatorProfileCreator.java) is the only available profile creator.
