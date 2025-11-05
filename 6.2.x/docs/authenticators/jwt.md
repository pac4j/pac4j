---
layout: doc
title: JWT
---

*pac4j* allows you to validate JSON web tokens.

The JWT support is based on the excellent [Nimbus JOSE JWT library](http://connect2id.com/products/nimbus-jose-jwt) and you should consider reading this [algorithm selection guide](http://connect2id.com/products/nimbus-jose-jwt/algorithm-selection-guide).

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

The [`JwtAuthenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/credentials/authenticator/JwtAuthenticator.java) validates JWT tokens produced by the [`JwtGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/profile/JwtGenerator.java) or by other systems.

It can be defined for HTTP clients which deal with `TokenCredentials`.

It supports plain text, signed and/or encrypted JWT tokens.

In all cases, the `JwtAuthenticator` requires the JWT to have a subject (`sub` claim) unless you have defined an `identifierGenerator` (of type `ValueGenerator`) to generate an identifier. Otherwise it will throw an exception.

If the provided JWT has an expiration date, then `JwtAuthenticator` may also be configured to only accept JWTs that pass a date criteria that is compared against the JWT expiration date, via `JwtAuthenticator#setExpirationTime()`

<div class="warning"><i class="fa fa-exclamation-triangle fa-2x" aria-hidden="true"></i> Notice that for security reasons, plain text JWT tokens will be accepted ONLY if no signature configuration is defined. If one or more signature configurations are defined, the JWT tokens are expected to be signed accordingly.</div>

### a) Signature

To handle signed JWT, you must define one or more [`SignatureConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/signature/SignatureConfiguration.java) with the `addSignatureConfiguration` method.

Three signature configurations are available: with a secret (`SecretSignatureConfiguration`), using an RSA key pair (`RSASignatureConfiguration`) or using an elliptic-curve key pair (`ECSignatureConfiguration`).

To verify a signed JWT, the defined signature configurations will be tried successfully (if the algorithm of the JWT matches the one supported by the signature configuration).

### b) Encryption

To handle encrypted JWT, you must define one or more [`EncryptionConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/config/encryption/EncryptionConfiguration.java) with the `addEncryptionConfiguration` method.

Like for signature configurations, three encryption configurations are available: with a secret (`SecretEncryptionConfiguration`), using an RSA key pair (`RSAEncryptionConfiguration`) or using an elliptic-curve key pair (`ECEncryptionConfiguration`).

To decrypt an encrypted JWT, the defined encryption configurations will be tried successfully (if the algorithm of the JWT matches the one supported by the encryption configuration).

**Example**:

```java
JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();

# define two signature configurations (one based on the KEY2 secret and the other one based on a generated RSA key pair)
jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(KEY2));
KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
KeyPair rsaKeyPair = keyGen.generateKeyPair();
jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(rsaKeyPair));

# define two encryption configurations (one based on the SECRET secret and the other one based on a generated elliptic curve key pair)
jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(SECRET));
KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
KeyPair ecKeyPair = keyGen.generateKeyPair();
ECEncryptionConfiguration encConfig = new ECEncryptionConfiguration(ecKeyPair);
encConfig.setAlgorithm(JWEAlgorithm.ECDH_ES_A128KW);
encConfig.setMethod(EncryptionMethod.A192CBC_HS384);
jwtAuthenticator.addEncryptionConfiguration(encConfig);

jwtAuthenticator.validate(new TokenCredentials(token, "myclient"));
```

The `JwtAuthenticator` also offers two convenient methods to handle JWT:

- `CommonProfile validateToken(final String token)` validates a token and directly returns a *pac4j* user profile
- `Map<String, Object> validateTokenAndGetClaims(final String token)` validates a token and directly returns a set of claims/attributes, this method is completely agnostic from *pac4j* profiles.


### c) User profiles

- if the provided JWT has been generated from a *pac4j* profile (like `FacebookProfile` for example) using the `JwtGenerator`, the `JwtAuthenticator` will re-create the same profile
- if the provided JWT has been created with any other mean, the `JwtAuthenticator` will create a [`JwtProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-jwt/src/main/java/org/pac4j/jwt/profile/JwtProfile.java).


## 3) `JwtGenerator`

To generate a plain text, signed and/or encrypted JWT, a `JwtGenerator` can be defined with a `SignatureConfiguration` or/and `EncryptionConfiguration`.

**Example:**

```java
JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(SECRET), new SecretEncryptionConfiguration(SECRET));
String token = generator.generate(facebookProfile);
```

JWTs may also be generated with an assigned expiration time:

```java
generator.setExpirationTime(new Date());
```

## 4) JWK

If your configuration is available as a JSON JWK, you can use the methods of the [`JWKHelper`](https://github.com/pac4j/pac4j/tree/master/pac4j-jwt/src/main/java/org/pac4j/jwt/util/JWKHelper.java) to:

- retrieve the secret from the JSON using the `buildSecretFromJwk` method
- build the RSA key from the JSON using the `buildRSAKeyPairFromJwk` method
- build the elliptic curve key from the JSON using the `buildECKeyPairFromJwk` method.

Then, you'll be able to build the appropriate signature or encryption configuration.
