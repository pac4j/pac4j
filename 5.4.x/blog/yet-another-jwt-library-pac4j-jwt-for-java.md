---
layout: blog
title: Yet another JWT library (pac4j-jwt) for Java
author: Jérôme LELEU
date: September 2017
---

<style>
.biggertext {
  font-size: 20px
}
.myred {
  color: #fb015b
}
.mypink {
  color: #d63aff
}
.myblue {
  color: #00b9f1;
}
td {
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 20px;
  padding-bottom: 20px;
}
</style>

_pac4j_ is a security engine for Java which supports authentication/authorization and is available for many frameworks: JEE, Play, Vertx, Spring Security, Shiro and Ratpack to name only a few.

Regarding authentication, one very common use case is to have a user authenticating via a browser (Facebook login for example) and then turn his identity into something usable for calling web services.
This could be an opaque string, but it requires checking the value to get the identity user.
So the best solution would be some self-sufficient information.

It is here that [JWT](https://jwt.io) comes into play: a JSON Web Token is a JSON object which can be signed and/or encrypted and is encoded in base64 format.
It's an [industry standard](https://tools.ietf.org/html/rfc7519). Signature is used to ensure that the JWT has not been tampered. Encryption is used to hide the information held by the JWT.

A JWT consists of three parts: a header, a payload and a signature. Here is an example taken from the [jwt.io](https://jwt.io) website.
The value:

<span class="biggertext"><span class="myred">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9</span>.<span class="mypink">eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9</span>. <span class="myblue">TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ</span></span>

is decoded as:

<table width="70%">
  <tr>
    <th width="33%">Header</th>
    <th width="33%">Payload</th>
    <th width="33%">Signature</th>
  </tr>
    <tr>
      <td class="myred">
      {<br />
       &nbsp; "alg": "HS256",<br />
       &nbsp; "typ": "JWT"<br />
      }
      </td>
      <td class="mypink">
      {<br />
       &nbsp; "sub": "1234567890",<br />
       &nbsp; "name": "John Doe",<br />
       &nbsp; "admin": true<br />
      }
      </td>
      <td class="myblue">To be verified</td>
    </tr>
</table>
<br/>

For Java, there are several good JWT libraries, but I think the best one is the [Nimbus JOSE JWT](https://connect2id.com/products/nimbus-jose-jwt): it may not be the easiest one, but it really has everything you need for JWT support.
You should especially read their [algorithm selection guide](https://connect2id.com/products/nimbus-jose-jwt/algorithm-selection-guide).

Let’s take an example with [HMAC signature](https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac):

```java
JWTClaimsSet claimsSet = new JWTClaimsSet();
claimsSet.setSubject("alice");
claimsSet.setIssuer("https://c2id.com");

JWSSigner signer = new MACSigner(KEY1);
SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
signedJWT.sign(signer);
String jwt = signedJWT.serialize();

signedJWT = SignedJWT.parse(jwt);
JWSVerifier verifier = new MACVerifier(sharedSecret);

assertTrue(signedJWT.verify(verifier));
assertEquals("alice", signedJWT.getJWTClaimsSet().getSubject());
assertEquals("https://c2id.com", signedJWT.getJWTClaimsSet().getIssuer());
```

It's really not too complicated and all your use cases can be handled with a similar logic.
Even if it provides low-level abstractions, it should be more than sufficient in most situations.

In *pac4j* security libraries, things are slightly different and authenticated users have profiles (with identifiers, attributes, roles and permissions).
So we must generate JWTs from these profiles (it's done via the `JwtGenerator`) and verify them during the authentication process (thanks to the `JwtAuthenticator`).
We can also verify JWT created in other applications without using the `JwtGenerator` to address other use cases.

Both the `JwtGenerator` and `JwtAuthenticator` are available in the `pac4j-jwt` module (groupId: `org.pac4j`).
As we want to be able to handle more than just one encryption/signature use case in the `JwtAuthenticator`, we offer higher abstractions to configure signature and encryption: the `SignatureConfiguration` and the `EncryptionConfiguration` classes.

Inspired by the [pac4j JWT documentation](https://www.pac4j.org/docs/authenticators/jwt.html):

```java
JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(KEY1), new SecretEncryptionConfiguration(KEY1));
String token = generator.generate(facebookProfile);

JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(KEY1));
jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(KEY2));
jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(KEY1));
jwtAuthenticator.validate(new TokenCredentials(token, "FacebookClient"));
```

Finally, after adding the `String generate(Map claims)` method to the `JwtGenerator` and the `Map validateTokenAndGetClaims(String token)` method to the `JwtAuthenticator`, we don't have any dependency left on the *pac4j* profile and **you now have a full library for JWT**.
