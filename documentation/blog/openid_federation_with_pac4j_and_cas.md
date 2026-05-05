---
layout: blog
title: OpenID Federation with pac4j and CAS
author: Jérôme LELEU
date: June 2026
---

In previous posts, we have presented the OpenID (Connect) Federation protocol with pac4j and the Connect2id server: [(Part 1)](/blog/openid_federation_with_pac4j_and_connect2id.html) + [(Part 2)](/blog/more_openid_federation_with_pac4j_and_connect2id.html).

From our previous setup, we have 3 components:
- a client, which is called the Relying Party (RP) in OIDC, and we use pac4j
- a server, which is called the OpenID Provider (OP) in OIDC, and we use the Connect2id server
- a trust anchor (TA in short) and we use a simulated one.

As the **pac4j** library is a first-class citizen of the CAS server (it is used for authentication delegation and the security of the OAuth/OIDC server support), let's bring the CAS server into action.

In this article, we will replace the simulated trust anchor by the CAS server.


## 1) Setup a CAS server as a trust anchor

The pac4j application runs on `localhost:8081` and the Connect2id server is on `127.0.0.1:8080`.

So let's choose `localhost:8082` to run the CAS server.

Let's start from this basic Maven overlay: [https://github.com/casinthecloud/cas-overlay-demo](https://github.com/casinthecloud/cas-overlay-demo).

For the version, it must be at least `8.0.0-RC5`.

We can remove the useless `cas-server-support-json-service-registry` dependency and add the new `cas-server-support-oidc-federation` module dedicated to the federation support.

To make things easier, let's turn the CAS server into a standalone (Tomcat embedded) JAR using the `spring-boot-maven-plugin`.

So we have the following `pom.xml` file:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.casinthecloud</groupId>
    <artifactId>cas-overlay-demo</artifactId>
    <version>8.0.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>org.apereo.cas</groupId>
            <artifactId>cas-server-webapp${tomcat.properties}</artifactId>
            <version>${cas.version}</version>
            <type>war</type>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.apereo.cas</groupId>
            <artifactId>cas-server-support-oidc-federation</artifactId>
            <version>${cas.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.1</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                    <recompressZippedFiles>false</recompressZippedFiles>
                    <archive>
                        <compress>false</compress>
                        <manifestFile>${project.build.directory}/war/work/org.apereo.cas/cas-server-webapp${tomcat.properties}/META-INF/MANIFEST.MF</manifestFile>
                    </archive>
                    <overlays>
                        <overlay>
                            <groupId>org.apereo.cas</groupId>
                            <artifactId>cas-server-webapp${tomcat.properties}</artifactId>
                        </overlay>
                    </overlays>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <release>${java.version}</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>4.1.0-RC1</version>
                <configuration>
                    <mainClass>org.apereo.cas.web.CasWebApplication</mainClass>
                    <excludes>
                        <exclude>
                            <groupId>org.apereo.cas</groupId>
                            <artifactId>cas-server-webapp-tomcat</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
        <finalName>cas</finalName>
    </build>

    <properties>
        <cas.version>8.0.0-RC5</cas.version>
        <java.version>25</java.version>
        <tomcat.properties>-tomcat</tomcat.properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

</project>
```

In the `application.yml`, let's setup the CAS server to act as a trust anchor:

```yml
server.ssl.enabled: false
server.port: 8082

cas.tgc.secure: false
cas.tgc.same-site-policy: Lax

cas.tgc.crypto.enabled: false
cas.webflow.crypto.enabled: false

cas.server.name: http://localhost:${server.port}
cas.server.prefix: ${cas.server.name}/cas
cas.host.name: castest

cas.authn.oidc.federation.role: TRUST_ANCHOR
cas.authn.oidc.core.issuer: ${cas.server.prefix}/oidc
cas.authn.oidc.federation.subordinate-directory: /etc/cas/config/subordinates
```

The configuration is quite easy: we setup the CAS server to run on `localhost:8082` (no SSL, `Lax` policy, no cookie/webflow encryption).

And for the federation part, we define the `TRUST_ANCHOR` role, its OIDC base URL (`issuer`) as well as the file directory in which we will define its subordinates.

The subordinates are the entities for which the CAS server provides trust. They must be defined upfront with their metadata and their federation key(s).

Here, it will be the RP = pac4j client (Spring Boot demo) and the Connect2id server = OP.


## 2) Define its subordinates

To define the subordinates of the trust anchor, we need to retrieve the information from the current RP and OP.

For the RP (pac4j), we call the URL: `http://localhost:8081/.well-known/openid-federation`. An entity statement is returned by the SpringBoot demo and we can decode it via any JWT tool or the `jwt.io` website:

```json
{
  "sub": "http://localhost:8081",
  "metadata": {
    "openid_relying_party": {
      "redirect_uris": [
        "http://localhost:8081/callback?client_name=OidcClient"
      ],
      "application_type": "web",
      "response_types": [
        "code"
      ],
      "grant_types": [
        "authorization_code"
      ],
      "scope": "openid email profile",
      "token_endpoint_auth_method": "private_key_jwt",
      "token_endpoint_auth_signing_alg": "RS256",
      "request_object_signing_alg": "RS256",
      "jwks": {
        "keys": [
          {
            "kty": "RSA",
            "e": "AQAB",
            "use": "sig",
            "kid": "defaultjwks0526",
            "n": "2moVQ...2aq7Q"
          }
        ]
      },
      "client_registration_types": [
        "explicit",
        "automatic"
      ],
      "client_name": "New RP test",
      "contacts": [
        "jerome@casinthecloud.com"
      ]
    }
  },
  "nbf": 1777901081,
  "jwks": {
    "keys": [
      {
        "kty": "RSA",
        "e": "AQAB",
        "use": "sig",
        "kid": "mykeyoidcfede26",
        "n": "nVoec...qELzQ"
      }
    ]
  },
  "iss": "http://localhost:8081",
  "authority_hints": [
    "http://localhost:8081/trustanchor"
  ],
  "exp": 1785677081,
  "iat": 1777901081,
  "jti": "59fad759-60e7-4cc9-a3fe-203e6d02319b"
}
```

The `metadata` and the `keys` from the `jwks` property (not in the `metadata` property) are the ones we use to build the subordinate `rp.json` file (placed in the `/etc/cas/config/subordinates` directory):

```json
{
  "entityId": "http://localhost:8081",
  "metadata": {
    "openid_relying_party": {
      "redirect_uris": [
        "http://localhost:8081/callback?client_name=OidcClient"
      ],
      "application_type": "web",
      "response_types": [
        "code"
      ],
      "grant_types": [
        "authorization_code"
      ],
      "scope": "openid email profile",
      "token_endpoint_auth_method": "private_key_jwt",
      "token_endpoint_auth_signing_alg": "RS256",
      "request_object_signing_alg": "RS256",
      "jwks": {
        "keys": [
          {
            "kty": "RSA",
            "e": "AQAB",
            "use": "sig",
            "kid": "defaultjwks0526",
            "n": "2moVQ...2aq7Q"
          }
        ]
      },
      "client_registration_types": [
        "explicit",
        "automatic"
      ],
      "client_name": "New RP test",
      "contacts": [
        "jerome@casinthecloud.com"
      ]
    }
  },
  "federationKeys": [
    {
      "kty": "RSA",
      "e": "AQAB",
      "use": "sig",
      "kid": "mykeyoidcfede26",
      "n": "nVoec...qELzQ"
    }
  ]
}
```

For the OP, we do something similar and call the URL: `http://127.0.0.1:8080/c2id/.well-known/openid-federation` to get the metadata and the federation keys:

```json
{
  "sub": "http://127.0.0.1:8080/c2id",
  "metadata": {
    "federation_entity": {
      "organization_name": "pac4j_test_c2id"
    },
    "openid_provider": {
      "authorization_endpoint": "http://127.0.0.1:8080/c2id-login",
      "token_endpoint": "http://127.0.0.1:8080/c2id/token",
      "registration_endpoint": "http://127.0.0.1:8080/c2id/clients",
      "introspection_endpoint": "http://127.0.0.1:8080/c2id/token/introspect",
      "revocation_endpoint": "http://127.0.0.1:8080/c2id/token/revoke",
      "pushed_authorization_request_endpoint": "http://127.0.0.1:8080/c2id/par",
      "issuer": "http://127.0.0.1:8080/c2id",
      "jwks_uri": "http://127.0.0.1:8080/c2id/jwks.json",
      "scopes_supported": [
        "openid"
      ],
      "response_types_supported": [
        "code",
        "token",
        "id_token",
        "id_token token",
        "code id_token",
        "code id_token token"
      ],

      ...TRUNCATED...

      "claims_parameter_supported": true,
      "frontchannel_logout_supported": true,
      "frontchannel_logout_session_supported": true,
      "backchannel_logout_supported": true,
      "backchannel_logout_session_supported": true
    }
  },
  "jwks": {
    "keys": [
      {
        "kty": "RSA",
        "e": "AQAB",
        "use": "sig",
        "kid": "bjBN",
        "iat": 1774636985,
        "n": "qoXzz...atFCQ"
      }
    ]
  },
  "iss": "http://127.0.0.1:8080/c2id",
  "authority_hints": [
    "http://localhost:8081/trustanchor"
  ],
  "exp": 1778493319,
  "iat": 1777888519,
  "constraints": {
    "max_path_length": 2
  }
}
```

and create the `op.json` file (in the `/etc/cas/config/subordinates` directory).


## 3) Declare CAS as the trust anchor

We have a trust anchor ready to use, but the RP and the OP don't know it, they still refer to the simulated one.

So let's update their configurations for that.

For the pac4j client, we adjust the `Pac4jConfig` Java configuration:

```java
final var rpJwks = config.getRpJwks();
rpJwks.setJwksPath("file:./metadata/rpjwks.jwks");
rpJwks.setKid("defaultjwks0526");
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
final var privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);

config.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);

val federation = config.getFederation();

federation.setTargetOp("http://127.0.0.1:8080/c2id");
val trust = new OidcTrustAnchorProperties();
trust.setIssuer("http://localhost:8082/cas/oidc");
trust.setJwksPath("classpath:trustanchor.jwks");
federation.getTrustAnchors().add(trust);

federation.getJwks().setJwksPath("file:./metadata/oidcfede.jwks");
federation.getJwks().setKid("mykeyoidcfede26");
federation.setContactName("New RP test");
federation.setContactEmails(List.of("jerome@casinthecloud.com"));

federation.setEntityId("http://localhost:8081");
```

This setup is quite similar to the original one, except that the trust anchor is defined by the `http://localhost:8082/cas/oidc` URL and that we need to update its `trustanchor.jwks` definition file.

For that, we call its federation endpoint (`http://localhost:8082/cas/oidc/.well-known/openid-federation`) and decode its result:

```json
{
  "sub": "http://localhost:8082/cas/oidc",
  "metadata": {
    "federation_entity": {
      "organization_name": "Apereo CAS",
      "federation_fetch_endpoint": "http://localhost:8082/cas/oidc/fetch",
      "contacts": []
    }
  },
  "jwks": {
    "keys": [
      {
        "kty": "RSA",
        "e": "AQAB",
        "use": "sig",
        "kid": "4ddec6ab-b5d2-453a-8036-932f8770c9af",
        "n": "7jXA3...71IGw"
      }
    ]
  },
  "iss": "http://localhost:8082/cas/oidc",
  "exp": 1785628800,
  "iat": 1777852800,
  "constraints": {
    "max_path_length": 1
  }
}
```

The keys must be defined in the `trustanchor.jwks` file in the RP:

```json
{
    "keys": [
      {
        "kty": "RSA",
        "e": "AQAB",
        "use": "sig",
        "kid": "4ddec6ab-b5d2-453a-8036-932f8770c9af",
        "n": "7jXA3...71IGw"
      }
    ]
}
```

After this change, restart the pac4j RP.

For the Connect2id server, things are even easier as only the URL of the trust anchor is required.

In the `./tomcat/webapps/c2id/WEB-INF/oidcProvider.properties`, you need this configuration:


```properties
### Federation ###

# Enables / disables OpenID Federation 1.0. Disabled by default.
op.federation.enable=true

# The configured trust anchors. Leave blank if none.
op.federation.trustAnchors.1=http://localhost:8082/cas/oidc

# Trust anchors or intermediate entities that may issue an entity statement
# about this OpenID provider. Leave blank if none.
op.federation.authorityHints.1=http://localhost:8082/cas/oidc
op.federation.authorityHints.2=
op.federation.authorityHints.3=

# The enabled OpenID Federation 1.0 client registration types. The default
# value is none (for deployments that use manual client registration only).
#
# Supported OpenID Federation 1.0 client registration types:
#
#     * explicit
#     * automatic
#
op.federation.clientRegistrationTypes=automatic
```

Federation is enabled of course. The CAS trust anchor is defined as the first trust anchor and as the first authority hint. The registration is set to `automatic`.

Stop (`./tomcat/bin/shutdown.sh`) and restart (`./tomcat/bin/startup.sh`) the Connect2id server.

To make a proper test, you may need to remove any previous registered `http://localhost:8081` client.

Using your token from the `oidcProvider.properties` file, you can query all existing clients:

```shell
curl -X GET http://127.0.0.1:8080/c2id/clients -H "Authorization: Bearer ztucZ...exmd6"
```

And remove it if it is still registered:

```shell
curl -X DELETE http://127.0.0.1:8080/c2id/clients/http%3A%2F%2Flocalhost%3A8081 -H "Authorization: Bearer ztucZ...exmd6"
```

Notice that for the `client_id` defined as a URL, you may need to update:
- the `setenv.sh` file to add `export JAVA_OPTS="$JAVA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"`
- the `server.xml` file to add `encodedSolidusHandling="decode"` on the `<Connector>` definition.


## 4) Test again

With the RP and OP updated and restarted, and the CAS trust anchor running, you may try to log in by calling `http://localhost:8081` in your browser and clicking on the protected link.

Now it works thanks to the real trust anchor.

If we take a look at the logs of the CAS server:

```
INFO [org.apereo.cas.oidc.federation.web.OidcWellKnownFederationEndpointController] - <Generating federation entity statement>
INFO [org.apereo.cas.oidc.federation.subordinate.OidcFederationSubordinateRepository] - <Loaded [2] subordinates>
INFO [org.apereo.cas.oidc.federation.web.OidcTrustAnchorFetchEndpointController] - <Building entity statement for
 subordinate: [http://127.0.0.1:8080/c2id]>
INFO [org.apereo.cas.oidc.federation.web.OidcWellKnownFederationEndpointController] - <Generating federation entity statement>
INFO [org.apereo.cas.oidc.federation.web.OidcTrustAnchorFetchEndpointController] - <Building entity statement for
 subordinate: [http://localhost:8081]>
```

We see that the federation endpoint has been called twice and the `fetch` endpoint (which returns the trust anchor confidence for the entity) has been called twice as well: one for the OP (`http://127.0.0.1:8080/c2id`) and the other one for the RP (`http://localhost:8081`).

This was fully expected: the trust anchor is used to build the trust chains!

If we do the test again, nothing appears in the CAS trust anchor logs this time as the entity statements are not requested again, they are already known by the RP and the OP, until they expire or get lost (in-memory storage).

<div class="text-center highlight-blog">So the CAS server can act as a real trust anchor <i>between</i> the pac4j RP and the Connect2id OP.</div>
