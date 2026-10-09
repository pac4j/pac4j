---
layout: blog
title: "EUDI wallets with OpenID4VP in action (2/2): on another device"
author: Jérôme LELEU
date: November 2026
tags: [guide]
draft: true
seo_title: "OpenID4VP in action with pac4j (2/2): EUDI wallet login by QR code, with an mdoc PID | pac4j"
description: "Authenticate a user on a computer with the EUDI-style wallet of their phone: a Spring Boot application, pac4j, a QR code, an mdoc PID and the Paradym demo wallet, step by step, with the protocol logs."
---

The European Digital Identity wallet is coming, and applications accepting it will need to talk to it. This is the second of two posts showing how to do that with pac4j: in the [first post](/blog/openid4vp_in_action_same_device.html) the wallet ran on the same phone as the browser; here the browser is on a computer, the wallet on a phone, and a QR code bridges the two.


## 1) Why prepare for EUDI wallets?

[Regulation (EU) 2024/1183](https://eur-lex.europa.eu/eli/reg/2024/1183/oj), better known as eIDAS 2.0, entered into force on May 20, 2024. EU member states must offer EUDI wallets by the end of 2026, holding *person identification data* (PID) and attestations such as driving licences or diplomas. [Using a wallet remains voluntary](https://ec.europa.eu/digital-building-blocks/sites/spaces/EUDIGITALIDENTITYWALLET/pages/713526976/FAQ).

Article 5f also requires acceptance by public services requiring electronic identification, certain private services requiring strong authentication, and very large online platforms requiring authentication. Private-sector obligations have conditions and exemptions, including for microenterprises and small enterprises, with a deadline tied to implementing acts, generally described as late 2027.

For applications in scope, wallet authentication is becoming an integration requirement worth preparing for.


## 2) Why it is hard

A wallet login looks simple: the user opens their wallet, sees what your application asks for, consents, and the wallet sends a signed proof. Behind this, the stack is anything but simple. Your application, the *verifier* in the vocabulary of the specifications, has to deal with:

- [OpenID for Verifiable Presentations 1.0](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html), the protocol itself: how the wallet is invoked, how it fetches the request, how it posts the response, with its [Digital Credentials Query Language](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#name-digital-credentials-query-l) (DCQL) to say what you want
- the [High Assurance Interoperability Profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) (HAIP), which pins the options the EUDI wallet uses, and the [Architecture and Reference Framework](https://eudi.dev/2.7.3/architecture-and-reference-framework-main/) (ARF) of the European Commission, with its PID rulebook
- two credential formats: the [SD-JWT VC](https://datatracker.ietf.org/doc/draft-ietf-oauth-sd-jwt-vc/) of the IETF, built on [selective disclosure JWTs](https://datatracker.ietf.org/doc/draft-ietf-oauth-selective-disclosure-jwt/), and the mobile documents (mdoc) of [ISO/IEC 18013-5](https://www.iso.org/standard/69084.html), encoded in CBOR and signed with COSE
- the plumbing underneath: signed request objects ([RFC 9101](https://www.rfc-editor.org/rfc/rfc9101)), JWS, JWE and JWK, X.509 certificate chains and revocation, decentralized identifiers, and the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browsers for the newest flows.

And several ways to identify the verifier, several ways to reach the wallet, several response modes, each with its own validation rules. Implementing this by hand, and keeping it right as the specifications move, is a project in itself.


## 3) pac4j to the rescue

pac4j has one security model for every protocol: a `Client` starts the authentication, a *callback* receives the answer, a `UserProfile` ends up in the session. CAS, SAML, OpenID Connect, OAuth all fit in it, and a digital wallet does too.

The new `pac4j-openid4vp` module, which arrives with pac4j v6.6.0, adds the `EudiWalletClient`: you give it a DCQL query, a keystore and the issuers you trust, and it handles the signed request, the encrypted response, credential verification and the user profile. If you have already used pac4j, the integration will look familiar.

The [first post](/blog/openid4vp_in_action_same_device.html) walked through a same-device flow with an **SD-JWT VC** PID. This one takes the wallet to another device: the user is on a computer, the page shows a QR code, the phone scans it, and an **mdoc** PID comes back, the format of ISO/IEC 18013-5, the one of the mobile driving licences.


## 4) A young protocol: test with demo wallets

OpenID4VP 1.0 was finalized in 2025, and wallet implementations are still evolving. Demo wallets let you test the integration while preparing for national EUDI deployments; expect differences between wallets and versions.

A very good one is the **Paradym wallet** by [Animo](https://animo.id), available for Android and iOS, and its [playground](https://playground.paradym.id/) which plays both the issuer and the verifier:

1. install the Paradym wallet on your phone
2. open [playground.paradym.id](https://playground.paradym.id/) and, on the **Issuance** page, issue the demo cards to your wallet: the PID as mdoc is the one this post uses
3. keep the **Trust Info** page of the playground at hand: it shows the certificate of the playground issuer, which your application must trust.

The cards are Erika Mustermann's, a fictitious Dutch citizen, and the mdoc PID carries, among other attributes, a `personal_administrative_number`: a stable identifier, which the profile below relies on.

<!-- screenshot: the Issuance page of the playground, and the mdoc PID in the Paradym wallet -->


## 5) ngrok: a real URL for the wallet

The wallet talks to your application over HTTPS, from the phone: it fetches the request object and posts its response. The phone is not on your computer, so `localhost` will not do.

The simplest way during development is [ngrok](https://ngrok.com/), which exposes your local port on a public HTTPS address:

```bash
ngrok http 8080
```

It prints a public URL such as `https://abcd-1234.ngrok-free.dev`: this is the base URL of your application for the rest of this post. The [free plan](https://ngrok.com/docs/pricing-limits/free-plan-limits) includes a development domain assigned to your account. Copy the URL ngrok prints into the property below. If the browser shows an ngrok warning page, choose **Visit Site** to continue.


## 6) Create the Spring Boot project

This is a plain Spring Boot web application, with [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) as the pac4j implementation. Java 17 or later and Maven are enough.

```bash
mkdir -p wallet-another-device/src/main/java/org/pac4j/demo/wallet
mkdir -p wallet-another-device/src/main/resources wallet-another-device/metadata
cd wallet-another-device
```

The `pom.xml`. The mdoc verification relies on the [walt.id](https://walt.id) library, which pac4j declares as optional and which walt.id publishes on its own repository:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.4</version>
        <relativePath/>
    </parent>
    <groupId>org.example</groupId>
    <artifactId>wallet-another-device</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <kotlin.version>2.4.20</kotlin.version>
    </properties>

    <!-- walt.id publishes the mdoc library outside Maven Central -->
    <repositories>
        <repository>
            <id>waltid-releases</id>
            <url>https://maven.waltid.dev/releases</url>
            <snapshots><enabled>false</enabled></snapshots>
        </repository>
    </repositories>

    <dependencyManagement>
        <dependencies>
            <!-- keep the datetime API used by walt.id 0.12.0 -->
            <dependency>
                <groupId>org.jetbrains.kotlinx</groupId>
                <artifactId>kotlinx-datetime-jvm</artifactId>
                <version>0.6.1</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- the pac4j implementation for Spring MVC -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>spring-webmvc-pac4j</artifactId>
            <version>8.0.3</version>
        </dependency>
        <!-- the OpenID4VP support: the application is a verifier -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>pac4j-openid4vp</artifactId>
            <version>6.6.0</version>
        </dependency>
        <!-- the mdoc verification, an optional library of pac4j -->
        <dependency>
            <groupId>id.walt.mdoc-credentials</groupId>
            <artifactId>waltid-mdoc-credentials-jvm</artifactId>
            <version>0.12.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

The walt.id library is written in Kotlin. pac4j manages Kotlin versions for its own build; applications must align their runtime dependencies too. This POM pins Kotlin to `2.4.20` and `kotlinx-datetime-jvm` to `0.6.1`; Spring Boot 4.0.4 already manages the matching coroutines (`1.10.2`) and serialization (`1.9.0`) versions. See [Kotlin alignment](/docs/clients/openid4vp-mdoc-verifier.html#12-kotlin-alignment), especially if you also verify SD-JWT VC credentials. Do not upgrade `kotlinx-datetime` to `0.7.x`, which removes APIs used by walt.id 0.12.0.

The `src/main/resources/application.properties` file holds the base URL and enables the logs we will read at the end:

```properties
app.base-url=https://abcd-1234.ngrok-free.dev
logging.level.org.pac4j.openid4vp=DEBUG
logging.level.PROTOCOL_MESSAGE.OPENID4VP=DEBUG
```

And the usual Spring Boot entry point:

```java
package org.pac4j.demo.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
```


## 7) Keys and trust

Two keystores go in the `metadata` directory, the same as in the first post.

**The verifier key.** `EudiWalletClient` signs requests and identifies the verifier by its certificate hash (`x509_hash`). Production EUDI deployments use a *relying party access certificate* issued by the ecosystem. This demo uses a self-signed certificate with an EC P-256 key, accepted by the tested Paradym wallet; self-signed verifier certificates are outside the HAIP requirements:

```bash
keytool -genkeypair -alias rp -keyalg EC -groupname secp256r1 -sigalg SHA256withECDSA \
  -dname "CN=pac4j demo verifier,O=pac4j" -validity 365 \
  -keystore metadata/verifier.p12 -storetype PKCS12 -storepass changeit
```

**The issuers you trust.** No credential is accepted unless its issuer is trusted, and pac4j trusts nobody by default. For an mdoc, the trust anchor is the *issuing authority certificate authority* (IACA) of the PID provider. Copy the *Issuer Certificate* from the [Trust Info](https://playground.paradym.id/info) page of the playground into a `paradym-issuer.pem` file, and import it into a truststore:

```bash
keytool -importcert -alias paradym -file paradym-issuer.pem -noprompt \
  -keystore metadata/pid-issuers.p12 -storetype PKCS12 -storepass changeit
```


## 8) Configure pac4j

The configuration is a `Pac4jSecurityConfig` of `spring-webmvc-pac4j`: the `Config` bean defines the client, and the interceptors say which URLs it protects.

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.openid4vp.client.EudiWalletClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.dcql.EudiPidQuery;
import org.pac4j.openid4vp.verifier.MdocVerifier;
import org.pac4j.openid4vp.verifier.trust.CertificateTrustedIssuer;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Map;

import static org.pac4j.core.profile.definition.CommonProfileDefinition.FAMILY_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.GIVEN_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PERSONAL_ADMINISTRATIVE_NUMBER;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PID_DOCTYPE;

@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public Config config() {
        // the request: signed with the key of the verifier, asking for three attributes of the PID as an mdoc
        final var configuration = new OpenId4VpConfiguration()
            .setKeystore(new KeystoreProperties("./metadata/verifier.p12")
                .setKeyStoreType("PKCS12")
                .setKeyStoreAlias("rp")
                .setKeystorePassword("changeit")
                .setPrivateKeyPassword("changeit"))
            .setDcqlQuery(EudiPidQuery.mdoc(GIVEN_NAME, FAMILY_NAME, PERSONAL_ADMINISTRATIVE_NUMBER));

        // the response: an mdoc is accepted if its certificate chain ends in the truststore
        final var issuers = new CertificateTrustedIssuer(new KeystoreProperties("./metadata/pid-issuers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"));
        // certificate revocation checking is disabled to simplify this demo; keep it enabled in production
        issuers.setCertificateRevocationEnabled(false);
        configuration.addCredentialVerifier(new MdocVerifier().addTrustedIssuer(issuers));

        // the profile identifier: the issuer and the administrative number, read in the namespace of the PID
        configuration.setProfileIdResolver(credentials -> {
            final var pid = credentials.getVerifiedCredentials().get(EudiPidQuery.PID).get(0);
            final var namespace = (Map<?, ?>) pid.getClaims().get(PID_DOCTYPE);
            return pid.getIssuer() + "|" + namespace.get(PERSONAL_ADMINISTRATIVE_NUMBER);
        });

        final var client = new EudiWalletClient(configuration);
        return new Config(baseUrl + "/callback", client);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        addSecurity(registry, "EudiWalletClient").addPathPatterns("/protected/**");
    }
}
```

Compared to the first post, two things change:

- The verifier is `MdocVerifier`, which checks the issuer authentication of the document, its digests, its validity, the signature of the device and the binding of the response to the request, and the chain of the document signer up to your truststore.
- The claims of an mdoc are grouped by **namespace**: for the PID, the namespace is its document type, `eu.europa.ec.eudi.pid.1`. The profile identifier reads the administrative number in that namespace, and the profile exposes an attribute named after the namespace, holding the three claims. `ProfileIdResolver.issuerAndClaim(...)` only reads the top-level claims of an SD-JWT VC, hence the small resolver above.

The rest is the same: `EudiWalletClient` pins the HAIP choices, `x509_hash` and `direct_post.jwt`, and the callback URL serves both the wallet and the browser.

These settings do not constitute complete HAIP conformance validation. This example also assumes a demo document without an MSO `status` object. The playground's [Trust Info](https://playground.paradym.id/info) describes status lists: if the current document carries status information, configure `MdocVerifier.setStatusChecker(...)` to authenticate the status list and check the document's entry. Without it, pac4j rejects the document. See [credential status](/docs/clients/openid4vp-mdoc-verifier.html#7-credential-status). Disabling certificate revocation does not bypass this check.


## 9) The DCQL query

The query says what your application wants. `EudiPidQuery.mdoc(GIVEN_NAME, FAMILY_NAME, PERSONAL_ADMINISTRATIVE_NUMBER)` builds it in one line for the PID; this is what the wallet receives:

```json
{
  "credentials": [
    {
      "id": "pid",
      "format": "mso_mdoc",
      "meta": { "doctype_value": "eu.europa.ec.eudi.pid.1" },
      "claims": [
        { "path": ["eu.europa.ec.eudi.pid.1", "given_name"] },
        { "path": ["eu.europa.ec.eudi.pid.1", "family_name"] },
        { "path": ["eu.europa.ec.eudi.pid.1", "personal_administrative_number"] }
      ]
    }
  ]
}
```

One credential query, identified as `pid`, for a mobile document whose type is the EUDI PID, and three data elements, each addressed by its namespace and its name. The same can be written by hand, in JSON when it comes from a configuration file:

```java
configuration.setDcqlQuery("""
    {"credentials":[{"id":"pid","format":"mso_mdoc","meta":{"doctype_value":"eu.europa.ec.eudi.pid.1"},
     "claims":[{"path":["eu.europa.ec.eudi.pid.1","given_name"]},{"path":["eu.europa.ec.eudi.pid.1","family_name"]},
               {"path":["eu.europa.ec.eudi.pid.1","personal_administrative_number"]}]}]}""");
```

Or in Java, with access to expected values, alternatives (`claim_sets`, `credential_sets`) and trusted authorities:

```java
configuration.setDcqlQuery(new DcqlQuery()
    .addCredential(new CredentialQuery("pid", CredentialFormat.MSO_MDOC)
        .setDoctypeValue("eu.europa.ec.eudi.pid.1")
        .addClaim("eu.europa.ec.eudi.pid.1", "given_name")
        .addClaim("eu.europa.ec.eudi.pid.1", "family_name")
        .addClaim("eu.europa.ec.eudi.pid.1", "personal_administrative_number")));
```

Whatever the wallet presents is checked against the query again by pac4j: the format, the document type, the namespaces and the elements. The constraints are not left to the wallet.


## 10) The pages, with the QR code

In this flow, the wallet is on the phone rather than the computer running the browser. The page gets the wallet URL as data and displays it as a QR code, then waits for the wallet's response. Three pieces do that:

- the protected page itself, as before
- a `/status` endpoint, which reports the transaction state for the browser session: `PENDING`, `RECEIVED` or `EXPIRED`, from `client.getPresentationStatus(...)`. `RECEIVED` includes a wallet error response; the callback then reports that error
- the page with the QR code and its script, which polls the `/status` endpoint every second until the wallet has answered.

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;
import java.util.Map;

@Controller
public class WalletController {

    @Autowired
    private Config config;

    @Autowired
    private WebContext webContext;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ProfileManager profileManager;

    @GetMapping("/protected/index")
    @ResponseBody
    public String protectedPage() {
        return "<h1>Protected page</h1><p>" + HtmlUtils.htmlEscape(profileManager.getProfiles().toString()) + "</p>"
            + "<p><a href='/logout'>Logout</a></p>";
    }

    /** Whether the wallet answered the transaction of this browser session. */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        final var client = (OpenId4VpClient) config.getClients().findClient("EudiWalletClient").orElseThrow();
        final var status = client.getPresentationStatus(new CallContext(webContext, sessionStore));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(Map.of("status", status.name().toLowerCase(Locale.ROOT)));
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return """
            <h1>Log in with your wallet</h1>
            <p>Scan the QR code with your phone's wallet. This page continues automatically when the wallet responds.</p>
            <div id="qr"></div>
            <pre id="log"></pre>
            <script src="https://cdn.jsdelivr.net/npm/qrcode-generator@2.0.4/dist/qrcode.js"></script>
            <script>
              const log = m => document.getElementById('log').textContent += m + '\\n';

              async function start() {
                // the same request a browser would make, but as an AJAX call: pac4j then answers 401,
                // with the wallet URL in the Location header, instead of redirecting the page
                const r = await fetch('/protected/index', {headers: {'X-Requested-With': 'XMLHttpRequest'}});
                if (r.ok) {
                  window.location.assign('/protected/index');
                  return;
                }
                const walletUrl = r.headers.get('Location');
                if (r.status !== 401 || !walletUrl) {
                  throw new Error('Could not start wallet authentication (HTTP ' + r.status + ')');
                }
                log('wallet URL: ' + walletUrl);
                const qr = qrcode(0, 'L');
                qr.addData(walletUrl);
                qr.make();
                document.getElementById('qr').innerHTML = qr.createImgTag(4, 16, 'Scan with your wallet');
                setTimeout(() => poll().catch(reportError), 1000);
              }

              async function poll() {
                const r = await fetch('/status', {cache: 'no-store'});
                if (!r.ok) {
                  throw new Error('Could not read presentation status (HTTP ' + r.status + ')');
                }
                const result = await r.json();
                if (result.status === 'received') {
                  log('the wallet has answered: continuing to the callback');
                  window.location = '/callback?client_name=EudiWalletClient';
                } else if (result.status === 'pending') {
                  setTimeout(() => poll().catch(reportError), 1000);
                } else {
                  log('the transaction expired: reload the page for a new QR code');
                }
              }

              const reportError = error => log(error.message + ': reload the page to retry');
              start().catch(reportError);
            </script>
            """;
    }
}
```

In `start()`, pac4j recognizes the `X-Requested-With` header and returns a 401 with the wallet URL in the `Location` header instead of a redirect. The page renders that URL as a QR code. If the session is already authenticated, it goes directly to the protected page; HTTP or network failures appear in the page's log.

Then `poll()` waits one second between completed status requests until it receives `received` or `expired`. On `received`, it sends the browser to the callback. This limits polling to at most one request per second per page, with a delay of roughly one second plus request time before continuing. Status is read from the transaction store using the identifier in the browser session; the default store is in memory and needs no outgoing network call. The [qrcode-generator](https://github.com/kazuhikoarase/qrcode-generator) library renders the QR code in the browser, without sending its contents to a QR service.

The `WebContext` and `SessionStore` beans come from `spring-webmvc-pac4j`, scoped to the current request; `getPresentationStatus` looks the transaction of the session up and reports whether the wallet has posted its answer.

Run the application, with ngrok running next to it:

```bash
mvn spring-boot:run
```


## 11) The flow, step by step

Open the ngrok URL in the browser of your computer: the QR code appears at once.

**1. The page asks for the protected page, as data.** pac4j finds no profile, creates a *transaction* and answers the `openid4vp://` URL in the `Location` header of a 401. The page draws it as a QR code and starts polling the `/status` endpoint every second.

<!-- screenshot: the page with the QR code -->

**2. The phone scans the QR code.** The Paradym wallet reads the URL, which only carries the identifier of the verifier and a `request_uri`: it calls it, on the callback URL of your application, and receives the signed request object, with the DCQL query and the public key to encrypt its response.

**3. The user consents.** The wallet shows who is asking, the hash of your certificate for now, and what is asked: the PID, given name, family name and administrative number. The user picks the mdoc card and confirms with their PIN.

<!-- screenshot: Paradym, the consent screen with the three attributes -->

**4. The wallet posts its response.** An encrypted JWT, containing the mdoc device response, posted on the callback URL. pac4j decrypts, verifies and keeps the result for the browser session which started the transaction. The wallet shows that the sharing succeeded.

<!-- screenshot: Paradym, the success screen -->

**5. The page continues on its own.** The next poll returns `received`: the page goes to the callback URL, pac4j retrieves the profile already validated when the wallet posted its response, stores it in the session and redirects to the page requested at step 1: the protected page shows Erika Mustermann, with the three claims under the `eu.europa.ec.eudi.pid.1` attribute and an identifier made of the issuer and the administrative number.

<!-- screenshot: the protected page with the profile -->

Nothing to do on the computer between the scan and the result: the whole exchange happened between the phone and the application.


## 12) What the logs say

The two loggers enabled in `application.properties` give two views of the flow. The `PROTOCOL_MESSAGE.OPENID4VP` logger prints the messages themselves, as they were sent or received, without any interpretation: one line per message, starting with `>>>` when the application sent it, `<<<` when it received it, followed by the other party, the browser or the wallet. The `org.pac4j.openid4vp` logger tells what pac4j did with them: the transaction created, the request object built, the response decrypted and verified, the profile built. Here is the flow above, shortened.

Step 1: a transaction is created, expiring five minutes later, and the wallet URL is handed to the page. Note the `request_uri` on the callback URL, and the identifier of the verifier:

```
VpTransaction : transaction 94d5a6…: - -> CREATED (client EudiWalletClient, response mode direct_post.jwt, expires at …)
PROTOCOL_MESSAGE.OPENID4VP : [tx 94d5a6…] >>> browser wallet URL: openid4vp://?client_id=x509_hash%3ALBzQXfZdbZmj-QFQwHjLPhjCeM5AEJTbJ-_FDWifBRI&request_uri=https%3A%2F%2Fabcd-1234.ngrok-free.dev%2Fcallback%3Fclient_name%3DEudiWalletClient%26vp_tx%3D94d5a6…&request_uri_method=post
OpenId4VpClient : AJAX request detected -> returning UnauthorizedAction(super=HttpAction(code=401), content=) for https://abcd-1234.ngrok-free.dev/protected/index
```

Step 2: the wallet fetches the request object, which is built for it and signed:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 94d5a6…] <<< wallet GET vp_tx=94d5a6…
OpenId4VpRequestObjectBuilder : client metadata built for transaction 94d5a6…: formats=[mso_mdoc], response encryption methods=[A128GCM, A256GCM]
OpenId4VpRequestObjectBuilder : request object signed for transaction 94d5a6… with ES256
VpTransaction : transaction 94d5a6…: CREATED -> REQUEST_RETRIEVED (GET of the request URI)
PROTOCOL_MESSAGE.OPENID4VP : [tx 94d5a6…] >>> wallet 200 eyJ4NWMiOlsiTUlJQjdE…
```

These demo traces show a GET even though the wallet URL advertises `request_uri_method=post`. pac4j accepts both methods; a wallet following the advertised method uses POST and can include its metadata and a wallet nonce.

The request object is a JWT whose header carries your certificate (`x5c`) and whose payload is the request. Decoded, its payload reads:

```json
{
  "client_id": "x509_hash:LBzQXfZdbZmj-QFQwHjLPhjCeM5AEJTbJ-_FDWifBRI",
  "response_type": "vp_token",
  "response_mode": "direct_post.jwt",
  "response_uri": "https://abcd-1234.ngrok-free.dev/callback?client_name=EudiWalletClient&vp_tx=94d5a6…",
  "nonce": "fa43378424f1426e9b241c92261a575c",
  "state": "60c58a7619da4e7ea3c52bec41b09740…",
  "dcql_query": { "credentials": [ { "id": "pid", "format": "mso_mdoc", "meta": { "doctype_value": "eu.europa.ec.eudi.pid.1" },
                  "claims": [ { "path": ["eu.europa.ec.eudi.pid.1", "given_name"] }, { "path": ["eu.europa.ec.eudi.pid.1", "family_name"] },
                              { "path": ["eu.europa.ec.eudi.pid.1", "personal_administrative_number"] } ] } ] },
  "client_metadata": { "jwks": { "keys": [ { "kty": "EC", "use": "enc", "crv": "P-256", "alg": "ECDH-ES", "x": "…", "y": "…" } ] },
                       "encrypted_response_enc_values_supported": ["A128GCM", "A256GCM"],
                       "vp_formats_supported": { "mso_mdoc": { "issuerauth_alg_values": [-7], "deviceauth_alg_values": [-7] } } },
  "aud": "https://self-issued.me/v2", "iat": 1791455140, "exp": 1791455440
}
```

The device signature binds the mdoc to this request through a *session transcript*. Its OpenID4VP handover incorporates the response encryption key's JWK thumbprint, the nonce, the verifier's `client_id` and the `response_uri`. pac4j reconstructs this transcript when verifying the presentation, so a response bound to another request is rejected.

Step 4: the wallet posts its response, a JWE encrypted with the ephemeral key published in the request. pac4j decrypts it, verifies the mdoc and builds the profile; the claims come grouped by namespace:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 94d5a6…] <<< wallet POST vp_tx=94d5a6…&response=eyJraWQiOiI5MzVhNzdlNy…
OpenId4VpAuthenticator : response decrypted and authentication tag verified for transaction 94d5a6…: alg=ECDH-ES, enc=A128GCM, members=[vp_token, state]
CertificateTrustedIssuer : issuer truststore loaded: 1 trust anchors
MdocVerifier : mdoc verified for transaction 94d5a6…
OpenId4VpAuthenticator : presentation 1/1 verified for transaction 94d5a6…, query pid: format=MSO_MDOC, verifier=MdocVerifier
OpenId4VpAuthenticator : wallet response validated for transaction 94d5a6…: 1 credential queries answered
VerifiableCredentialProfile : adding => key: eu.europa.ec.eudi.pid.1 / value: {given_name=Erika, family_name=Mustermann, personal_administrative_number=123456782} / class java.util.LinkedHashMap
OpenId4VpAuthenticator : profile built for transaction 94d5a6…: profile class=EudiPidProfile
VpTransaction : transaction 94d5a6…: REQUEST_RETRIEVED -> RESPONSE_RECEIVED (presentation validated)
PROTOCOL_MESSAGE.OPENID4VP : [tx 94d5a6…] >>> wallet 200 {}
```

Step 5: the page polled, saw `received`, and came back to the callback URL: the validated profile is picked up, nothing is verified twice, and the identifier is the one of the resolver:

```
VpTransaction : transaction 94d5a6…: RESPONSE_RECEIVED -> CONSUMED (the browser came back)
OpenId4VpAuthenticator : transaction 94d5a6…: reusing the profile validated when the wallet posted its response, nothing is verified again
OpenId4VpClient : profile: Optional[EudiPidProfile(id=C=NL,CN=credo dcs|123456782, attributes={eu.europa.ec.eudi.pid.1={given_name=Erika, family_name=Mustermann, personal_administrative_number=123456782}}, …)]
```

Note that the logs show the credentials as they travel, tokens included: enable them to diagnose, not permanently in production.


## 13) Limitations and what comes next

A few things to know before going further:

- **The profile identifier** in this demo combines the document signer certificate's subject, `C=NL,CN=credo dcs` in these traces, with the administrative number. For persistent accounts, your PID provider must guarantee that the combination is stable, unique and never reassigned. Certificate subjects can change or be shared, and not every PID provider issues an administrative number. Choose an identifier policy appropriate to your trusted provider.
- **A refusal was silent** with the tested Paradym wallet: when the user declined or had no matching card, nothing was posted, and the page waited until the transaction expired after five minutes. Wallets that post an error let pac4j report it at the callback.
- **Certificate revocation** is disabled in this demo. In production, keep it enabled and supply current CRLs or configure the Java PKIX provider to retrieve revocation data. This setting does not disable credential status checking: credentials carrying status information still need a status checker.
- The device MAC of ISO 18013-5, transaction data, the `verifier_attestation` and `openid_federation` prefixes, and the automatic retrieval of status lists are not supported yet.

This integration is introduced in **pac4j v6.6.0**. The full documentation starts at [OpenID for Verifiable Presentations](/docs/clients/openid4vp.html), and the [first post](/blog/openid4vp_in_action_same_device.html) covers the same flow on a single device, with an SD-JWT VC PID.
