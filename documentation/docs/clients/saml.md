---
layout: doc
title: SAML
---

*pac4j* allows you to login with any SAML identity provider using the SAML v2.0 protocol.

It has been tested with various SAML 2 providers: Okta, testshib.org, the CAS SAML wrapper, ...

## 1) Dependency

You need to use the following module: `pac4j-saml`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-saml</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) Basic configuration

The [`SAML2Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/client/SAML2Client.java) must be used to login with a SAML 2 identity provider.

First, you need to generate a keystore for all signature and encryption operations:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -keystore samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

Then, you must define a [`SAML2ClientConfiguration`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/client/SAML2ClientConfiguration.java):

```java
SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks", "pac4j-demo-passwd", "pac4j-demo-passwd", "resource:testshib-providers.xml");
```

The first parameter (`keystorePath`) should point to your keystore:

- using the `resource:` prefix, the following file path will be searched in as a resources stream (`CommonHelper.class.getResourceAsStream`)
- using the `classpath:` prefix, the following file path will be searched in as a resources stream (`Thread.currentThread().getContextClassLoader().getResourceAsStream`)
- using `file:` prefix means the keystore is accessible on the following file path
- it can also be an url (starting with `http:` or `https:`)
- otherwise, it is considered to be a file path.

The second parameter (`keystorePassword`) is the value of the `-storepass` option for the keystore generation while the third parameter (`privateKeyPassword`) is the value of the `-keypass` option.

The fourth parameter (`identityProviderMetadataPath`) should point to your IdP metadata, assuming you can use the same syntax than for the keystore.

Finally, you need to declare the `SAML2Client` based on the previous configuration:

```java
Saml2Client client = new Saml2Client(cfg);
```

After a successful authentication, a [`SAML2Profile`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/profile/SAML2Profile.java) is returned.

## 3) Additional configuration:

Once you have an authenticated web session on the Identity Provider, usually it won't prompt you again to enter your credentials and it will automatically generate a new assertion for you. By default, the SAML client will accept assertions based on a previous authentication for one hour. If you want to change this behaviour, set the `maximumAuthenticationLifetime parameter:

```java
// lifetime in seconds
client.setMaximumAuthenticationLifetime(600);
```

By default, the entity ID of your application (the Service Provider) will be equals to the [callback url](/docs/clients.html#the-callback-url). This can lead to problems with some IDP because of the query string not being accepted (like ADFS v2.0). So you can force your own entity ID with the `serviceProviderEntityId` parameter:

```java
// custom SP entity ID
cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
```

To configure the supported algorithms and digest methods for the initial authentication request, specify what is supported via the configuration object:

```java
cfg.setBlackListedSignatureSigningAlgorithms(...);
cfg.setSignatureAlgorithms(...);
cfg.setSignatureReferenceDigestMethods(...);
cfg.setSignatureCanonicalizationAlgorithm(...);
```

By default, assertions must be signed, but this may be disabled using:

```java
cfg.setWantsAssertionsSigned(false);
```

The final result will be determined based on the IdP metadata and the configuration above. The IdP metadata will always be chosen in favor of the *pac4j* configuration, so if you need to purely rely on *pac4j*, you need to modify the metadata. 

You can generate the SP metadata in two ways:
- either programmatically using the `SAML2Client`: `String spMetadata = client.getServiceProviderMetadataResolver().getMetadata();`
- or by defining the appropriate configuration: `cfg.setServiceProviderMetadataPath("/tmp/sp-metadata.xml");`

## 4) ADFS subtilities

You must follow these rules to successfully authenticate using Microsoft ADFS 2.0 / 3.0.

### a) Entity ID

You must always specify an explicit Entity ID that does not contain any question mark. By default, *pac4j* uses the same Entity ID as the AssertionConsumerService location, which contains the client's name as a parameter after a question mark. Unfortunately ADFS does not work well with such IDs and starts an infinite redirection loop when A SAML message with such a message arrives.

This property is supported since *pac4j* v1.6.0. Don't forget to change your metadata accordingly!

### b) Maximum authentication time

*pac4j* has the default maximum time set to 1 hour while ADFS has it set to 8 hours. Therefore it can happen that ADFS sends an assertion which is still valid on ADFS side but evaluated as invalid on *pac4j* side.

You can see the following error message: `org.pac4j.saml.exceptions.SAMLException: Authentication issue instant is too old or in the future`

There are two possibilities how to make the values equal:

- change the value in ADFS management console in the trust properties dialog
- change the value on *pac4j* side using the `setMaximumAuthenticationLifetime` method.

### c) Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files

You must install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files into your JRE/JDK running *pac4j*. If you don't do it, you may encounter errors like this:

```
ERROR [org.opensaml.xml.encryption.Decrypter] - <Error decrypting the encrypted data element>
org.apache.xml.security.encryption.XMLEncryptionException: Illegal key size
ERROR [org.opensaml.xml.encryption.Decrypter] - <Failed to decrypt EncryptedData using either EncryptedData KeyInfoCredentialResolver or EncryptedKeyResolver + EncryptedKey KeyInfoCredentialResolver>
ERROR [org.opensaml.saml2.encryption.Decrypter] - <SAML Decrypter encountered an error decrypting element content>
```

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files can be downloaded from Oracle's Java Download site.
