---
layout: doc
title: JWT
---

*pac4j* allows you to validate JSON Web Token.

## 1) Dependency

You need to use the following module: `pac4j-jwt`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-jwt</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) `JwtAuthenticator`

The [`JwtAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/credentials/authenticator/JwtAuthenticator.java) validates JWT tokens produced by the [`JwtGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/profile/JwtGenerator.java) or by any other means.

It can be defined for HTTP clients which deal with `TokenCredentials`.

It supports plain text, signed and / or encrypted JWT tokens.

To handle these use cases:

- several [`SignatureConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/SignatureConfiguration.java) can be defined: `MacSignatureConfiguration`, `RSASignatureConfiguration` or `ECSignatureConfiguration` 
- an [`EncryptionConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/EncryptionConfiguration.java) like the `DirectEncryptionConfiguration` can be defined as well.

**Example**:

```java
JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
jwtAuthenticator.addSignatureConfiguration(new MacSignatureConfiguration(KEY2));
KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
KeyPair rsaKeyPair = keyGen.generateKeyPair();
jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(rsaKeyPair));
jwtAuthenticator.setEncryptionConfiguration(new DirectEncryptionConfiguration(SECRET));
jwtAuthenticator.validate(new TokenCredentials(token, "myclient"));
```

## 3) `JwtGenerator`

To generate a JWT, a `JwtGenerator` can be defined with a `SignatureConfiguration` and `EncryptionConfiguration`.

**Example:**

```java
JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(SECRET), new DirectEncryptionConfiguration(SECRET));
String token = generator.generate(facebookProfile);
```
