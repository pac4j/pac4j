---
layout: blog
title: More OpenID Federation with pac4j and Connect2id
author: Jérôme LELEU
date: 2026
---

I strongly recommend that you read the first article about the [OpenID Federation protocol](/blog/openid_federation_with_pac4j_and_connect2id.html).

This new article dives deeper into the OpenID Federation support in pac4j and Connect2id.

You should download and use the latest versions of both software (at least version 6.5.0 for pac4j).


# 1) Let's log in (again)

## a) Calling the login page

<div class="text-center">
  <img alt="Before login" src="/img/blog/s1_26/before_login.png" />
</div>

As we have previously seen, the first login generates several logs on both sides (client = RP = pac4j + server = OP = connect2id).

Several HTTP calls are required to check the JWKS and the entity statements and establish the trust chains.

This could be a performance issue if these HTTP calls were made for each login attempt, though on the second try, the logs are much less verbose before displaying the login page:

_The pac4j logs:_

```
DEBUG o.p.o.r.OidcRedirectionActionBuilder     : Request Object claim names: [iss, aud, iat, exp, jti, scope, response_type,
 redirect_uri, state, code_challenge_method, client_id, code_challenge, response_mode]
DEBUG o.p.o.r.OidcRedirectionActionBuilder     : Authz parameter names: [response_type, request, client_id, scope]
DEBUG o.p.o.r.OidcRedirectionActionBuilder     : Authentication request URL: http://127.0.0.1:8080/c2id-login
 ?response_type=code&request=eyJr...hULhwg&client_id=http%3A%2F%2Flocalhost%3A8081&scope=openid%20profile%20email
```

_The Connect2id logs:_

```
INFO AUTHZ-SESSION - [OP2101] Created new auth session: sid=FKEttMylh3MVBAtu59F7CXx5m4BTGXZ2_DKowRh_8eg
 client_id=http://localhost:8081 scope=[openid, profile, email]
```

Hopefully, trust chains have been cached depending on the expiration time and the whole plumbing has not been triggered a second time.


## b) After typing in the login and password

<div class="text-center">
  <img alt="After login" src="/img/blog/s1_26/after_login.png" />
</div>

After a successful login, we get the following logs:

_On the Connect2id side:_

```
INFO SESSION-STORE - [SS0201] Added new session: sub=alice ctx=web sid_key=MhNnqmimFgizG5ALmnp-tg
INFO AUTHZ-SESSION - [OP2103] Created new consent session: sid=FKEttMylh3MVBAtu59F7CXx5m4BTGXZ2_DKowRh_8eg subject=alice
 client_id=http://localhost:8081
INFO AUTHZ-SESSION - [OP2108] Created authZ response: subject=alice client_id=http://localhost:8081 response_type=[code]
INFO TOKEN - HTTP POST request: ip=127.0.0.1 path=/c2id/token
INFO TOKEN - [OP6204] Authenticated: client_id=http://localhost:8081 method=private_key_jwt client_auth_id=NgAiEAADRhMrDiZp
INFO AUTHZ-STORE - [AS0280] Issued access token: sub=alice act=null client_id=http://localhost:8081 scope=[openid]
INFO TOKEN - [OP6225] Success response: client_id=http://localhost:8081 grant=code tokens=[access,id]
INFO USERINFO - HTTP GET request: ip=127.0.0.1 path=/c2id/userinfo
INFO AUTHZ-STORE - [AS0213] Inspected valid SELF_CONTAINED Bearer access token: sub=alice act=null client_id
 =http://localhost:8081 iat=1775213922: eyJraWQiOiJQUlJ6Iiwid...
INFO USERINFO - [OP7307] Received valid UserInfo request: sub=alice claims=null ia_id=aac191d2-dcc5-4837-8545-692e204bcc07
```

This is "fairly" obvious:

1. A POST call is performed on the <code>/c2id/token</code> endpoint using the <code>private_key_jwt</code> client authentication method (this is what we have configured on the pac4j side)
2. A GET call is performed on the <code>/c2id/userinfo</code> endpoint using the <code>access_token</code> as bearer (= HTTP header).

This is the regular OIDC login process <u>even if the flow has started in a federation way</u>.


_On the pac4j side:_

```
DEBUG o.p.o.c.e.OidcCredentialsExtractor       : Authentication response successful
DEBUG o.p.o.c.e.OidcCredentialsExtractor       : Request state: d508c0f0ad/response state: d508c0f0ad
DEBUG org.pac4j.oidc.client.OidcClient         : clean authentication attempt from session
DEBUG o.p.o.c.authenticator.OidcAuthenticator  : Token response: status=200, content={"access_token":"eyJ...bng","token_type":"Bearer","expires_in":600}
DEBUG o.p.o.c.authenticator.OidcAuthenticator  : Token response successful
DEBUG org.pac4j.oidc.client.OidcClient         : clean authentication attempt from session
DEBUG org.pac4j.oidc.client.OidcClient         : Credentials validation took: 32 ms
DEBUG org.pac4j.oidc.client.OidcClient         : credentials : OidcCredentials(code=sjtox4...NQw, accessToken=...
DEBUG org.pac4j.oidc.profile.OidcProfile       : adding => key: access_token / value: eyJh...MDB9 / class java.lang.String
DEBUG org.pac4j.oidc.profile.OidcProfile       : adding => key: expiration / value: 1775214522541 / class java.lang.Long
DEBUG org.pac4j.oidc.profile.OidcProfile       : adding => key: id_token / value: eyJ...bng / class java.lang.String
DEBUG o.p.oidc.profile.creator.TokenValidator  : Trying IDToken validator, issuer: http://127.0.0.1:8080/c2id, type: null, JWS:
DEBUG o.p.oidc.profile.creator.TokenValidator  : Validated: {"iss":"http:\/\/127.0.0.1:8080\/c2id","sub":"alice",
 "aud":"http:\/\/localhost:8081","exp":1775214522,"iat":1775213922,"amr":["pwd"]}
DEBUG o.p.o.p.creator.OidcProfileCreator       : User info response: status=200, content={"sub":"alice","groups":["admin","audit"]}
DEBUG o.p.oidc.profile.OidcProfileDefinition   : converted to => key: sub / value: alice / class java.lang.String
DEBUG org.pac4j.oidc.profile.OidcProfile       : adding => key: sub / value: alice / class java.lang.String
...
DEBUG org.pac4j.oidc.profile.OidcProfile       : adding => key: token_expiration_advance / value: 0 / class java.lang.Integer
DEBUG org.pac4j.oidc.client.OidcClient         : profile: Optional[OidcProfile(super=AbstractJwtProfile(super=CommonProfile(
 super=BasicUserProfile(logger=Logger[org.pac4j.oidc.profile.OidcProfile], id=alice, attributes={access_token=eyJ...MDB9,
 token_expiration_advance=0, sub=alice, aud=[http://localhost:8081], amr=[pwd], id_token=eyJr...Hsbng,
 iss=http://127.0.0.1:8080/c2id, groups=[admin, audit], expiration=1775214522541, exp=Fri Apr 03 13:08:42 CEST 2026,
```

The logs are straightforward on the pac4j side as well: we see the successful authentication, the token and the userprofile calls.

## c) After the login process

Even though we’re not doing anything, new logs keep appearing for the Connect2id server:

```
INFO AUTHZ-STORE - [AS0228] Revoking multiple authzs: client_id=http://localhost:8081
INFO CLIENT-REG - [OP5184] Deleted client: client_id=http://localhost:8081 num_revoked_authz=1
INFO FED-REG - [OP8041] Reaped 1 expired federation clients
```

These logs are related to the fact that we have performed an automatic registration. The logs indicate that the temporarily created client is deleted.

Indeed, as Connect2id does not know the pac4j client, it has **temporarily** registered this client and after some time, the registered client is cleaned.

While this is a very convenient mechanism, it can impact server performance.

Therefore, it would be wise to consider explicitly and permanently registering our OIDC pac4j client.


# 2) Let's log in with explicit registration

## a) The client identifier

And this is a feature supported by the OpenID Federation protocol:

> the explicit registration of the OIDC client.

This must be of course supported by the OIDC server and this is the case of the Connect2id server.

pac4j supports both modes depending on the OIDC server, so the configuration must only be updated on the Connect2id server.

Stop the server (`tomcat/bin/shutdown.sh`), edit the `tomcat/webapps/c2id/WEB-INF/oidcProvider.properties` file:

```properties
op.federation.clientRegistrationTypes=explicit
```

and restart the server (`tomcat/bin/startup.sh`).

_On the pac4j side:_

```
DEBUG o.p.o.m.r.FederationClientRegister       : Registration endpoint exists and only explicit registration by OP (and RP)
 -> performing explicit registration
 INFO .f.e.DefaultEntityConfigurationGenerator : Generating entity configuration for: http://localhost:8081
DEBUG o.p.o.m.r.FederationClientRegister       : Received response registration: eyJraW...mkaMxZQ
 WARN o.p.o.m.r.FederationClientRegister       : /!\ ================================================
 WARN o.p.o.m.r.FederationClientRegister       : /!\ Explicit registration of the client 'http://localhost:8081' returns
  id: [t4j746kwjax6s]. This information won't be repeated. You MUST add this value to your configuration before the next
   application startup!
 WARN o.p.o.m.r.FederationClientRegister       : /!\ ================================================
```

_On the Connect2id side:_

```
INFO FED-REG - [OP8014] Registered entity http://localhost:8081 as explicit client with client_id=xkqolxvshcjv6 exp=1783005293
INFO FED-REG - [OP8019] Explicit registration response statement for entity http://localhost:8081: {sub=http://localhost:8081,
 aud=[http://localhost:8081], metadata={openid_relying_party={client_registration_types=[explicit, automatic],
 token_endpoint_auth_signing_alg=RS256, grant_types=[authorization_code], jwks={keys=[{kty=RSA, e=AQAB, use=sig, kid=...
```

The explicit registration is duly taken into account by Connect2id which generates a specific `client_id` for the OIDC client, returns it to pac4j to be displayed in its logs.

Let's follow the instruction given in the logs and add this `client_id` in the pac4j configuration:

```java
@Bean
public Config config() {
    final var config = new OidcConfiguration();

    // the new clientId!
    config.setClientId("xkqolxvshcjv6");

    final var rpJwks = config.getRpJwks();
    rpJwks.setJwksPath("file:./metadata/rpjwks.jwks");
    rpJwks.setKid("defaultjwks0426");
    config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
    final var privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
    config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);

    config.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);

    final var federation = config.getFederation();

    federation.setTargetOp("http://127.0.0.1:8080/c2id");
    final var trust = new OidcTrustAnchorProperties();
    trust.setIssuer("http://localhost:8081/trustanchor");
    trust.setJwksPath("classpath:trustanchor.jwks");
    federation.getTrustAnchors().add(trust);

    federation.getJwks().setJwksPath("file:./metadata/oidcfede.jwks");
    federation.getJwks().setKid("mykeyoidcfede26");
    federation.setContactName("New RP test");
    federation.setContactEmails(List.of("jerome@casinthecloud.com"));

    federation.setEntityId("http://localhost:8081");

    return new Config(baseUri + "/callback", new OidcClient(config));
}
```

Here is the complete configuration to refresh memories (and not only the added `client_id`).

We restart the Spring Boot application and try a new login process.

This time, no registration happens and Connect2id directly recognizes the provided `client_id`:

```
INFO AUTHZ-SESSION - [OP2101] Created new auth session: sid=reJ...58w client_id=xkqolxvshcjv6 scope=[openid, profile, email]
```

## b) The client secret

At this point in the article, you should wonder why we only have a `client_id` and no `client_secret`.

In fact, we don't need a secret as we use the `private_key_jwt` client authentication method: the credential is the private key, not the secret.

As this pac4j configuration is revealed in its entity statement, the Connect2id server is aware of that setting and **accordingly** decides to only return a `client_id` for this OIDC client.

Let's go further and replace this configuration in pac4j:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
final var privateKeyJwtConfig = new PrivateKeyJwtClientAuthnMethodConfig(rpJwks);
config.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);
```

by:

```java
config.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
```

to use the `client_secret_basic` authentication (and not the `private_key_jwt`).

We also remove the previous `client_id`:

```java
config.setClientId("xkqolxvshcjv6");
```

and completely change the contact name:

```java
federation.setContactName("New RP test");
```

Restart the Spring Boot application and try to log in.

This time, we call the Connect2id server with explicit registration and no configured client id/secret and a `client_secret_basic` authentication method.

And we get a new error from pac4j:

```
org.pac4j.oidc.exceptions.OidcException: Client secret export file is required
```

This seems definitely a weird one, but it's not. Let me explain: the received `client_id` is output in the logs, though it would not be safe to output the `client_secret` in the logs as well.

So the received `client_secret` is planned to be saved on the disk, on a file defined by the `secretExportFile` property.

Let's define it in the configuration:

```java
federation.setSecretExportFile("./mysecret.tmp");
```

and try again. It works!

The generated `client_id` and `client_secret` have been added on-the-fly to the OIDC configuration and have been used to perform the `client_secret_basic` authentication method.

The `client_id` is in the logs: `Explicit registration of the client 'http://localhost:8081' returns id: [wzcyln5hdtmck]`.

The `client_secret` is in the defined file: `The received secret has been saved into the file: ./mysecret.tmp`. Its value is: `U9UaF99rUopAXnWqXqkWBj_RsOZXfM1Efs67N4KweHo`.

These seem to be the right settings as the login process has worked, but we'd like to check that on the Connect2id side.

Let's seek in the Connect2id configuration file `oidcProvider.properties` for the property: `op.reg.apiAccessTokenSHA256`. I find:

```properties
# Evaluation note: Use token value ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6
op.reg.apiAccessTokenSHA256=cca68b8b82bcf0b96cb826199429e50cd95a042f8e8891d1ac56ab135d096633
```

Let's try to use the Connect2id API to list the existing clients with this key:

```shell
curl -X GET http://127.0.0.1:8080/c2id/clients -H "Authorization: Bearer ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6"
```

We get two clients:

```json
[
    {
        "client_registration_types":[
            "explicit",
            "automatic"
        ],
        "grant_types":[
            "authorization_code"
        ],
        "jwks":{
            "keys":[
                {
                    "kty":"RSA",
                    "e":"AQAB",
                    "use":"sig",
                    "kid":"defaultjwks0326",
                    "n":"2moV...aq7Q"
                }
            ]
        },
        "subject_type":"public",
        "application_type":"web",
        "registration_client_uri":"http:\/\/127.0.0.1:8080\/c2id\/clients\/wzcyln5hdtmck",
        "redirect_uris":[
            "http:\/\/localhost:8081\/callback?client_name=OidcClient"
        ],
        "registration_access_token":"0QKFCVfBe5PqVFwgjEaRousXHKEa9My0-IMMMCTPB20.YSxpLHI",
        "token_endpoint_auth_method":"client_secret_basic",
        "client_id":"wzcyln5hdtmck",
        "client_secret_expires_at":0,
        "request_object_signing_alg":"RS256",
        "client_id_issued_at":1775836027,
        "client_secret":"U9UaF99rUopAXnWqXqkWBj_RsOZXfM1Efs67N4KweHo",
        "client_name":"New RP test",
        "contacts":[
            "jerome@casinthecloud.com"
        ],
        "response_types":[
            "code"
        ],
        "id_token_signed_response_alg":"RS256"
    },
    {
        "token_endpoint_auth_signing_alg":"RS256",
        "grant_types":[
            "authorization_code"
        ],
        "jwks":{
            "keys":[
                {
                    "kty":"RSA",
                    "e":"AQAB",
                    "use":"sig",
                    "kid":"defaultjwks0326",
                    "n":"2moV...aq7Q"
                }
            ]
        },
        "subject_type":"public",
        "application_type":"web",
        "registration_client_uri":"http:\/\/127.0.0.1:8080\/c2id\/clients\/xkqolxvshcjv6",
        "redirect_uris":[
            "http:\/\/localhost:8081\/callback?client_name=OidcClient"
        ],
        "registration_access_token":"7ilSWZGsH4wOyehETCNJQz0My8NO-6efLiV1ED2HcN0.YSxpLHI",
        "token_endpoint_auth_method":"private_key_jwt",
        "client_id":"xkqolxvshcjv6",
        "request_object_signing_alg":"RS256",
        "client_id_issued_at":1775747499,
        "client_name":"C2ID Test RP (Localhost)",
        "contacts":[
            "jerome@casinthecloud.com"
        ],
        "response_types":[
            "code"
        ],
        "id_token_signed_response_alg":"RS256"
    }
]
```

The second one has only the right `client_id`, no `client_secret` and is defined with `private_key_jwt`.

The first one has the right `client_id` and `client_secret` and is defined with `client_secret_basic`.

Notice the appropriate `client_name` property as well.

The Connect2id configuration perfectly matches what was received by pac4j (not that I had any doubts ;-)

The **magic of the federation** continues:

<div class="text-center highlight-blog">

The pac4j RP and the Connect2id OP only know and rely on the trust anchor, they don't know each other.

<br/>

But nonetheless the <b>RP has been able to definitely register itself on the OP!</b>

</div>
