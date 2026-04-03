---
layout: blog
title: More OpenID Federation with pac4j and Connect2id
author: Jérôme LELEU
date: 2026
---

I strongly recommend that you read the first article about the [OpenID Federation protocol](/blog/openid_federation_with_pac4j_and_connect2id.html).

This new article dives deeper into the OpenID Federation support in pac4j and Connect2id.

You should download and use the latest versions of both software (at least version 6.5.0 for pac4j).


## 1) Let's log in (again)

### a) Calling the login page

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


### b) After typing in the login and password

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

The logs are straightforward on the pac4j side as well: we see the sucessful authentication, the token and the userprofile calls.

### c) After the login process

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


## 2) Let's log in with explicit registration

And this is a feature supported by the OpenID Federation protocol: the explicit registration of the OIDC client.

This must be of course supported by the OIDC server and this is the case of the Connect2id server.

pac4j supports both modes depending on the OIDC server, so the configuration must only be updated on the Connect2id server.

Stop the server (`tomcat/bin/shutdown.sh`), edit the `tomcat/webapps/c2id/WEB-INF/oidcProvider.properties` file:

```properties
op.federation.clientRegistrationTypes=explicit
```

and restart the server (`tomcat/bin/startup.sh`).
