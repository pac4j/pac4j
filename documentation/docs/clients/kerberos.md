---
layout: doc
title: Kerberos
---

*pac4j* allows you to login using the Keberos authentication mechanism (also known as SPNEGO or Microsoft HTTP Negotiate).

The Kerberos clients require to define an [Authenticator](../authenticators.html) to handle the credentials validation.
 Most likely all you need is to use the existing `KerberosAuthenticator` with a `SunJaasKerberosTicketValidator` which will do all the heavy-lifting of the Kerberos ticket validation.

## 1) Dependency

You need to use the following module: `pac4j-kerberos`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-kerberos</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) Clients

You can use the following clients depending on how they are passed in the HTTP request:

| Behaviour wanted | Client |
|-------------|--------|
| **Web Browser** (Firefox/Safari/IE)<br/> after ticket validation, it stores user profile in the session| [`IndirectKerberosClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-kerberos/src/main/java/org/pac4j/kerberos/client/indirect/IndirectKerberosClient.java)<br>(upon failure it sends a `HTTP 401` with a `WWW-Authenticate: Negotiate` header asking the browser to provide the Kerberos/SPNEGO credentials) |
| **Stateless Web service** | [`DirectKerberosClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-kerberos/src/main/java/org/pac4j/kerberos/client/direct/DirectKerberosClient.java) <br/>credentials are expected to be already provided as a request's HTTP header:<br/>`Authentication: Negotiate SomeBase64EncKerberosTicket`<br/> (it will not send any headers to indicate expected mechanism) |
{:.table-striped}

**Example:**

```java
import org.pac4j.kerberos.client.indirect.DirectKerberosClient;
import org.pac4j.kerberos.client.indirect.IndirectKerberosClient;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.SunJaasKerberosTicketValidator;
import org.pac4j.kerberos.profile.KerberosProfile;
import org.springframework.core.io.FileSystemResource;

SunJaasKerberosTicketValidator validator = new SunJaasKerberosTicketValidator();
// HTTP/fully-qualified-domain-name@DOMAIN
validator.setServicePrincipal("HTTP/www.mydomain.myrealm.lt@MYREALM.LT");
// the keytab file must contain the keys for the service principal, and should be protected
validator.setKeyTabLocation(new FileSystemResource("/private/security/http-keytab"));
// validator.setDebug(true);

IndirectKerberosClient client = new IndirectKerberosClient(new KerberosAuthenticator(validator));
client.setCallbackUrl("/force-kerberos-login"); // required only for indirect client
```

## 3) Common caveats with Kerberos (in JVM)

Some common problems/caveats:
- make sure the `keytab` file contains the service principal, and the name matches exactly
  * for web the `HTTP/` prefix is mandatory and must be uppercase
- Likely you need an Oracle Java (JDK/ Java SE etc) here.
  * Most encryption mechanisms need the `Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files` to be downloaded and copied over in a proper dir of your Java installation.
- the browser URL must match the fully-qualified-domain-name as specified in `setServicePrincipal`.
  * client needs to run `kinit`
  * P.S. to test your app when developing locally, one can add a fake domain name in `/etc/hosts` pointing to localhost:
```
127.0.0.1 www.mydomain.myrealm.lt
```
