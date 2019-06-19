---
layout: doc
title: SAML
---

*pac4j* allows you to login with any SAML identity provider using the SAML v2.0 protocol.

It has been tested with various SAML 2 providers: Okta, testshib.org, CAS SAML2 IdP, Shibboleth v3.4...

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

The [`SAML2Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/client/SAML2Client.java) 
must be used to login with a SAML 2 identity provider.

First, if you don't have one, you need to generate a keystore for all signature and encryption operations:

```bash
keytool -genkeypair -alias pac4j-demo -keypass pac4j-demo-passwd -keystore samlKeystore.jks -storepass pac4j-demo-passwd -keyalg RSA -keysize 2048 -validity 3650
```

Alternatively, you can also let pac4j create the keystore for you. If the keystore resource does not exist and is writable, *pac4j* will attempt to generate a keystore and produce the relevant key pairs inside it.

Then, you must define a [`SAML2Configuration`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/config/SAML2Configuration.java):

```java
SAML2Configuration cfg = new SAML2Configuration(new ClassPathResource("samlKeystore.jks"),
                                        "pac4j-demo-passwd",
                                        "pac4j-demo-passwd",
                                        new ClassPathResource("testshib-providers.xml"));
```

The first parameter (`keystoreResource`) is the keystore defined as a Spring resource using:
- the `org.springframework.core.io.FileSystemResource` class for disk files
- the `org.springframework.core.io.ClassPathResource` class for classpath files
- the `org.springframework.core.io.UrlResource` class for URLs.

The second parameter (`keystorePassword`) is the value of the `-storepass` option for the keystore generation while the third parameter (`privateKeyPassword`) is the value of the `-keypass` option.

The fourth parameter (`identityProviderMetadataResource`) should point to your IdP metadata, assuming you can use the same kind of definition than for the keystore.

Or you can also use the "prefix mechanism" to define the `Resource`:

```java
SAML2Configuration cfg = new SAML2Configuration("resource:samlKeystore.jks",
                                        "pac4j-demo-passwd",
                                        "pac4j-demo-passwd",
                                        "resource:testshib-providers.xml");
```

These are the available prefixes:

- the `resource:` or the `classpath:` prefixes creates a `ClassPathResource` component
- the `http:` or the `https:` prefixes creates a `UrlResource` component
- the `file:` prefix or no prefix at all creates a `FileSystemResource` component.

Or you can even use the empty constructor and the appropriate setters:
- the `setKeystoreResource`, `setKeystoreResourceFilepath`, `setKeystoreResourceClasspath`, `setKeystoreResourceUrl` or `setKeystorePath` methods to define the keystore
- the `setKeystorePassword` method to define the keystore password
- the `setPrivateKeyPassword` method to set the private password of the keystore
- the `setIdentityProviderMetadataResource`, `setIdentityProviderMetadataResourceFilepath`, `setIdentityProviderMetadataResourceClasspath`, `setIdentityProviderMetadataResourceUrl` or `setIdentityProviderMetadataPath` methods to define the identity provider metadata.

Finally, you need to declare the `SAML2Client` based on the previous configuration:

```java
Saml2Client client = new Saml2Client(cfg);
```

After a successful authentication, a [`SAML2Profile`](https://github.com/pac4j/pac4j/blob/master/pac4j-saml/src/main/java/org/pac4j/saml/profile/SAML2Profile.java) is returned.

## 3) Additional configuration:

You can control aspects of the authentication request such as forced and/or passive authentication as such:

```java
cfg.setForceAuth(true);
cfg.setPassive(true);
```

You can define the binding type for the authentication request via the `setAuthnRequestBindingType` method and the binding type for the SP logout request via the `setSpLogoutRequestBindingType` method:

```java
cfg.setAuthnRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
// or cfg.setAuthnRequestBindingType(SAMLConstants.SAML2_POST_BINDING_URI);
// or cfg.setAuthnRequestBindingType(SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI);
```

Notice that the SP metadata will define the POST binding for the authentication response and for the IdP logout request.

Once you have an authenticated web session on the Identity Provider, usually it won't prompt you again to enter your credentials and it will automatically generate a new assertion for you. By default, the SAML client will accept assertions based on a previous authentication for one hour. If you want to change this behavior, set the `maximumAuthenticationLifetime` parameter:

```java
// lifetime in seconds
client.setMaximumAuthenticationLifetime(600);
```

By default, the entity ID of your application (the Service Provider) will be equals to the [callback url](clients.html#the-callback-url). 
But you can force your own entity ID with the `serviceProviderEntityId` parameter:

```java
// custom SP entity ID
cfg.setServiceProviderEntityId("http://localhost:8080/callback?extraParameter");
```

To allow the authentication request sent to the identity provider to specify an attribute consuming index:

```java
cfg.setAttributeConsumingServiceIndex(1);
```

To allow the authentication request sent to the identity provider to specify an assertion consumer service index:

```java
cfg.setAssertionConsumerServiceIndex(1);
```

This will also guide pac4j to pick the ACS url from the metadata indicated by this index.

To configure the supported algorithms and digest methods for the initial authentication request, specify what is supported via the configuration object:

```java
cfg.setBlackListedSignatureSigningAlgorithms(...);
cfg.setSignatureAlgorithms(...);
cfg.setSignatureReferenceDigestMethods(...);
cfg.setSignatureCanonicalizationAlgorithm(...);
```

The SAML client always requires assertions to be signed either directly or via the response that contains them. 
When the assertions need to be processed separate of the response, you can request them to be signed directly using:
 
```java
cfg.setWantsAssertionsSigned(true);
```

You may also want to enable signing of the authentication requests using:

```java
cfg.setAuthnRequestSigned(true);
```

The final result will be determined based on the IdP metadata and the configuration above. 
The IdP metadata will always be chosen in favor of the *pac4j* configuration, so if you need to purely rely on *pac4j*, you need to modify the metadata. 

You can generate the SP metadata in two ways:
- either programmatically using the `SAML2Client`: `String spMetadata = client.getServiceProviderMetadataResolver().getMetadata();`
- or by defining the appropriate configuration: `cfg.setServiceProviderMetadata(new FileSystemResource("/tmp/sp-metadata.xml"));`


## 4) Logout

The SAML support handles the HTTP-POST and the HTTP-Redirect bindings for logout requests/responses (and the SOAP binding for incoming logout requests).

The `SAML2Client` can participate in the central logout and send a logout request to the IdP.
The binding of this request is controlled by the `spLogoutRequestBindingType` property and
the request can be signed using the `spLogoutRequestSigned` property of the `SAML2Configuration`.

When calling the IdP, the SAML *pac4j* application locally removes the user profiles and optionally destroys the web session based on the [`DefaultLogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/handler/DefaultLogoutHandler.java).
You may use your own logout handler by implementing the [`LogoutHandler`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/logout/handler/LogoutHandler.java) interface
and define it in the SAML configuration.

When called by the IdP, the SAML *pac4j* application also removes the user profiles based on the logout handler and returns a logout response with a binding defined by the `spLogoutResponseBindingType` property (in the `SAML2Configuration`).


## 5) Authentication Attributes

The following authentication attributes are populated by this client:

- The entityID of the IdP (`getAuthenticationAttribute("issuerId")` or `SAML2Profile.getIssuerId()`)
- The authentication method(s) asserted by the IdP (`getAuthenticationAttribute("authnContext")` or `SAML2Profile.getAuthnContexts()`)
- The NotBefore SAML Condition (`getAuthenticationAttribute("notBefore")` or `SAML2Profile.getNotBefore()`)
- The NotOnOrAfter SAML Condition (`getAuthenticationAttribute("notOnOrAfter")` or `SAML2Profile.getNotOnOrAfter()`)
- the session index.


## 6) ADFS subtilities

You must follow these rules to successfully authenticate using Microsoft ADFS 2.0/3.0.

### a) Maximum authentication time

*pac4j* has the default maximum time set to 1 hour while ADFS has it set to 8 hours. Therefore it can happen that ADFS 
sends an assertion which is still valid on ADFS side but evaluated as invalid on the *pac4j* side.

You can see the following error message: `org.pac4j.saml.exceptions.SAMLException: Authentication issue instant is too old or in the future`

There are two possibilities how to make the values equal:

- change the value in ADFS management console in the trust properties dialog
- change the value on *pac4j* side using the `setMaximumAuthenticationLifetime` method.

### b) Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files

You must install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files into your JRE/JDK 
running *pac4j*. If you don't do it, you may encounter errors like this:

```
ERROR [org.opensaml.xml.encryption.Decrypter] - <Error decrypting the encrypted data element>
org.apache.xml.security.encryption.XMLEncryptionException: Illegal key size
ERROR [org.opensaml.xml.encryption.Decrypter] - <Failed to decrypt EncryptedData using either EncryptedData KeyInfoCredentialResolver or EncryptedKeyResolver + EncryptedKey KeyInfoCredentialResolver>
ERROR [org.opensaml.saml2.encryption.Decrypter] - <SAML Decrypter encountered an error decrypting element content>
```

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files can be downloaded from Oracle's Java Download site.

### c) Disable Name Qualifier for format urn:oasis:names:tc:SAML:2.0:nameid-format:entity

ADFS 3.0 does not accept NameQualifier when using urn:oasis:names:tc:SAML:2.0:nameid-format:entity. In the `SAML2Configuration`, you can use setUseNameQualifier to disable the NameQualifier from SAML Request.


# Integration with various IdPs

## SimpleSAMPphp

SimpleSAMLphp is a commonly used IdP. To integrate PAC4J with SimpleSAMLphp use the following steps as a start. Let's assume a *standard* simpleSAMLphp install.

### DemoConfigFactory.java

```java
final SAML2Configuration cfg = new SAML2Configuration("resource:samlKeystore.jks",
 "pac4j-demo-passwd",
 "pac4j-demo-passwd",
 "resource:idp-metadata.xml"); //the id-metadata.xml contains IdP metadata, you will have to create this
 cfg.setMaximumAuthenticationLifetime(3600);
 cfg.setServiceProviderEntityId("test.pac4j"); //the entityId of you client (the SP), you will usualy change this
 cfg.setServiceProviderMetadataPath(new File("sp-metadata.xml").getAbsolutePath()); //the metadata of the SP, no changes required usually
 final SAML2Client saml2Client = new SAML2Client(cfg);
```

### SimpleSAMLphp config

Please note that pac4j requires the binding `urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST` for both SingleSignOn and SingleLogout services while simpleSAMLphp is by default installed using only `urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect`. It is required to add the bindings to the **metadata/saml20-idp-hosted.php** file:

```php
'SingleSignOnServiceBinding' => array('urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect', 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST'),
'SingleLogoutServiceBinding' => array('urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect', 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST'),
```

It is also required to register the EntityID of your SP into the file **metadata/saml20-sp-remote.php**

```php
$metadata['test.pac4j'] = array(
 'AssertionConsumerService' => 'http://localhost:8080/callback?client_name=SAML2Client',
...
```

### Metadata

SimpleSAMLphp exposes his IdP metadata on `http://idp-domain/simplesamlphp/saml2/idp/metadata.php?output=xhtml`. You can wrap this file in an additional `<md:EntitiesDescriptor ...` tag to generate the **idp-metadata.xml** file.

```xml
 <?xml version="1.0"?>
<md:EntitiesDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" xmlns:ds="http://www.w3.org/2000/09/xmldsig#"> 
	<md:EntityDescriptor entityID="http://idp-domain/simplesamlphp/saml2/idp/metadata.php">
	  <md:IDPSSODescriptor protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
	    <md:KeyDescriptor use="signing">
	      <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
	        <ds:X509Data>
	          <ds:X509Certificate>MII...</ds:X509Certificate>
	        </ds:X509Data>
	      </ds:KeyInfo>
	    </md:KeyDescriptor>
	    <md:KeyDescriptor use="encryption">
	      <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
	        <ds:X509Data>
	          <ds:X509Certificate>MII...</ds:X509Certificate>
	        </ds:X509Data>
	      </ds:KeyInfo>
	    </md:KeyDescriptor>
	    <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="http://idp-domain/simplesamlphp/saml2/idp/SingleLogoutService.php"/>
	    <md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="http://idp-domain/simplesamlphp/saml2/idp/SingleLogoutService.php"/>
	    <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>
	    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="http://idp-domain/simplesamlphp/saml2/idp/SSOService.php"/>
	    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="http://idp-domain/simplesamlphp/saml2/idp/SSOService.php"/>
  	  </md:IDPSSODescriptor>
	</md:EntityDescriptor>
</md:EntitiesDescriptor>
```

### Custom OpenSAML bootstrap

Behind the scenes, OpenSAML uses a singleton registry to hold its configuration (builders, marshallers, parsers, etc).
While pac4j ships with generally sane defaults for this configuration
(see `org.pac4j.saml.util.Configuration$DefaultConfigurationManager`), it might be useful for a developer to override
this configuration.

Pac4j uses a Java service provider to find a configuration class and bootstrap the OpenSAML libraries. It will load all
implementations of `org.pac4j.saml.util.Configuration` it can find on the classpath and use the one with the `javax.annotation.Priority` value.

To use a custom configuration, one must add a jar with the following to the classpath:

1. Implementation of `org.pac4j.saml.util.Configuration`. This implementation should have a `javax.annotation.Priority` annotation
denoting the priority. The lowest value is the one that will ultimately be used configuration. The default implementation has
and effective priority of `100`. Generic providers likely should use something like `50`, while end user implementors should
use `1`.  for example:

    ```java
    @Priority(100)
    public static class DefaultConfigurationManager implements ConfigurationManager {
        @Override
        public void configure() {
            XMLObjectProviderRegistry registry;
            synchronized (ConfigurationService.class) {
                registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
                if (registry == null) {
                    registry = new XMLObjectProviderRegistry();
                    ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
                }
            }

            try {
                InitializationService.initialize();
            } catch (final InitializationException e) {
                throw new RuntimeException("Exception initializing OpenSAML", e);
            }

            ParserPool parserPool = initParserPool();
            registry.setParserPool(parserPool);
        }

        private static ParserPool initParserPool() {

            try {
                BasicParserPool parserPool = new BasicParserPool();
                parserPool.setMaxPoolSize(100);
                parserPool.setCoalescing(true);
                parserPool.setIgnoreComments(true);
                parserPool.setNamespaceAware(true);
                parserPool.setExpandEntityReferences(false);
                parserPool.setXincludeAware(false);
                parserPool.setIgnoreElementContentWhitespace(true);

                final Map<String, Object> builderAttributes = new HashMap<String, Object>();
                parserPool.setBuilderAttributes(builderAttributes);

                final Map<String, Boolean> features = new HashMap<>();
                features.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
                features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
                features.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                features.put("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
                features.put("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);

                parserPool.setBuilderFeatures(features);
                parserPool.initialize();
                return parserPool;
            } catch (final ComponentInitializationException e) {
                throw new RuntimeException("Exception initializing parserPool", e);
            }
        }
    }
    ```

1. `/META-INF/services/org.pac4j.saml.util.ConfigurationManager` file. This file should have the fully qualified classname
of the `org.pac4j.saml.util.Configuration` implementation

For more information, see [https://docs.oracle.com/javase/tutorial/ext/basics/spi.html]
