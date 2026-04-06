---
layout: blog
title: What's new in pac4j v6.4 and v6.5?
author: Jérôme LELEU
date: 2026
---

## 1) Removal of the "old" modules

The `pac4j-gae` module dedicated to the old Google App Engine authentication mechanism has been deprecated in v6.4.0 and removed in v6.5.0.

The `pac4j-couch` module to support CouchDB as an identity storage has been deprecated in v6.4.0 and removed in v6.5.0.


## 2) Configuration via Java

To address any Java environment, pac4j configurations must be defined via Java code to benefit from discoverability via code completion.

The `pac4j-config` and `pac4j-springboot` modules which allow a properties configuration (no discovery available, documentation must be read) have been deprecated in v6.4.0.

The `pac4j-springboot` module is removed in version 6.5.0 and the `pac4j-config` module will be removed in version 7.

Client and configuration classes offer:
- easy constructors:

```java
val cfg = new SAML2Configuration(new ClassPathResource("samlKeystore.jks"),
    "pac4j-demo-passwd",
    "pac4j-demo-passwd",
    new ClassPathResource("metadata-okta.xml"));
```

- chainable setters:

```java
val config = new OidcConfiguration()
    .setDiscoveryURI("https://casserverpac4j.herokuapp.com/oidc/.well-known/openid-configuration")
    .setClientId("myclient")
    .setSecret("mysecret")
    .setAllowUnsignedIdTokens(true);
```

- and smart copiers (`.withXXX`) for quick configuration:

```java
config.withSecurityLogic(new DefaultSecurityLogic().setLoadProfilesFromSession(false));
```


## 3) Build

The build process has been simplified (30% faster):
- reworked/cleaned dependencies/plugins
- 3 modules have been removed
- no more `test-jar` components: the few test classes are in the `org.pac4j.test` package of the `pac4j-core` module
- OSGi and shading phases have been removed.

OSGi support and shading have been removed due to lack of usage and to simplify the Maven configuration.

<u>If you rely on OSGi or need any shaded JARs, please report your use case as feedback is still welcome.</u>


## 4) Improvement of the OpenID support

### a) JWKS

For the OpenID Connect protocol, we generally don't need JWKS.

For those who don't know, a JWKS is a file which stores a key (public or private) in the JSON format.

For example (private key):

```json
{
   "keys":[
      {
         "p":"-4uskkVXsQ_pyMlYcCF-...wctk5aLuGk2KzImA7eccQhqWDOv0sMm98",
         "kty":"RSA",
         "q":"3kg3SErpqux-tYEpKOh-U...CeXB5BjpUy9BPQPh0_zMMbFgErM",
         "d":"UT_QS2eUEc3AcNXZrl7WYjyh...fgd7-RlvyvGw8g4gWNH3ZFXUO2_CFT_YrUIDKl1LYw",
         "e":"AQAB",
         "use":"sig",
         "kid":"mykey",
         "qi":"Lp-0TpZ3oyi8hX1glWQPUI1VpNn92oTHoWLF...Lq4Xb8GwWEDqT7_IcmIDH2Mzp8tHM_xtaEXlo4afg",
         "dp":"xcakAL2UnRqt7bihWEh9YjOEzEKfAvwVGwMb...DPjjhZut0P-xxeyB3R-fx22WwkK1B6Pd18JHE",
         "dq":"JByJVqZM9ea74xLbs_ipZKnxucIdtgB0wDic...9tkmcLtDXmqiP8",
         "n":"2moVQWw9fDRr891E9GWiCory0wA0QokXV9AQF...7rYW6T4U6sFoj1x2NmeROp2HM0KvhhI8_qO1_6t_Wk2_2aq7Q"
      }
   ]
}
```

To authenticate on the _token_ endpoint, we generally use the `client_secret_basic` or the `client_secret_post` and no key is required.

Yet, there is the `private_key_jwt` client authentication method which requires a private key via the deprecated `PrivateKeyJWTClientAuthnMethodConfig`.

As JWKS become mandatory for the federation support and the Request Object signing, JWKS have become first-class citizens in pac4j.

A JWKS can be defined in configuration via `JwksProperties`:

```java
public class JwksProperties {

    private Resource jwksResource;

    private String kid;

    public JwksProperties setJwksPath(final String path) { ... }
}
```

is properly handled thanks to the new `org.pac4j.core.util.JwkHelper`:

- `loadJwkFromOrCreateJwks`: load or create a JWKS from a `JwkProperties`
- `saveJwkPrivate`: save the private information of the key
- `saveJwkPublic`: save the public information of the key.

and is a default configuration of the `OidcConfiguration` as the `rpJwks` property:

```java
final var rpJwks = config.getRpJwks();
rpJwks.setJwksPath("file:./metadata/rpjwks.jwks");
rpJwks.setKid("mykey");
```

It can be re-used for the `private_key_jwt` client authentication method via the `PrivateKeyJwtClientAuthnMethodConfig` component:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
final var privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```


### b) Request Object signing

You can enable the request object signing by setting the wanted algorithm:

```java
oidConfig.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);
```

The key used for the signing is the one stored in the `rpJwks` property.


### c) Pushed Authorization Request (aka PAR)

You can enable PAR via the following setting:

```java
oidConfig.setPushedAuthorizationRequest(true);
```


## 5) Support of OpenID Federation

[OpenID Connect Federation support](https://openid.net/specs/openid-federation-1_0.html) is configured via the `OidcFederationProperties` which centralizes both RP entity statement generation and OP resolution through federation.
The federation is enabled when the property `federation.targetOp` is set.

At RP level, you must configure signing material for the federation endpoint using either a keystore or a JWKS (JWKS is preferred if both are present).
If the configured file resource does not exist, pac4j can generate it automatically.
To expose the RP entity configuration, you use an `EntityConfigurationGenerator` (default provided if not overridden), typically via a `/.well-known/openid-federation` endpoint.

The federation properties provide metadata options like entity ID, token validity, application type, response/grant types, scopes, contacts, and trust anchors.
In federation mode, you should not configure a `discoveryURI` property; instead, set the `targetOp` and one or more trust anchors (`issuer` + `jwksResource`).
Metadata resolution is blocking on first access, then refreshed asynchronously when trust chains expire.

For client registration, if `clientId` is blank, pac4j supports both federation registration styles and checks OP capabilities.
It prefers automatic registration (sending entity statement as `client_assertion`); otherwise it falls back to explicit registration via `federation_registration_endpoint`, and the returned `client_id` must be persisted manually for next startup.

See this [guide](/docs/clients/openid-connect-federation.html) for more details.

## 6) Learn more

Read the [release notes](../docs/release-notes.html) for a thorough presentation of the changes.
