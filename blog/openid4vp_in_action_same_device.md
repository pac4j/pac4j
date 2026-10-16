---
layout: blog
title: "EUDI wallets with OpenID4VP in action (1/2): on the same device"
author: Jérôme LELEU
date: November 2026
tags: [guide]
draft: true
seo_title: "OpenID4VP in action with pac4j (1/2): EUDI wallet login on the same device | pac4j"
description: "Authenticate a user with an EUDI-style digital wallet on their phone: a Spring Boot application, pac4j, an SD-JWT VC PID and the Paradym demo wallet, step by step, with the protocol logs."
---

The European Digital Identity wallet is coming, and applications accepting it will need to talk to it. This is the first of two posts showing how to do that with pac4j. Here, the wallet runs on the same phone as the browser; in the [second post](/blog/openid4vp_in_action_another_device.html), it runs on a phone while the browser is on a computer.


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

This first post walks through a complete example on the same device: the user is on their phone, clicks a link, the wallet opens, and an **SD-JWT VC** PID comes back. The [second post](/blog/openid4vp_in_action_another_device.html) does the same on another device, with a QR code and an **mdoc** PID.


## 4) A young protocol: test with demo wallets

OpenID4VP 1.0 was finalized in 2025, and wallet implementations are still evolving. Demo wallets let you test the integration while preparing for national EUDI deployments; expect differences between wallets and versions.

A very good one is the **Paradym wallet** by [Animo](https://animo.id), available for Android and iOS, and its [playground](https://playground.paradym.id/) which plays both the issuer and the verifier:

1. install the Paradym wallet on your phone
2. open [playground.paradym.id](https://playground.paradym.id/) on your computer and, on the **Issuance** page, choose **PID SD-JWT**, the card this post uses (**PID mDoc** is the one of the second post): the page shows a QR code, scan it with the Paradym wallet, accept the offer, and the card appears in the wallet
3. keep the **Trust Information** page of the playground at hand: it shows the certificate of the playground issuer, which your application must trust.

The cards are Emma Charlotte van der Berg's, a fictitious Dutch citizen: given name, family name, birth date, address...

Issue the card from this playground and no other: a PID issued elsewhere, by the older [playground.animo.id](https://playground.animo.id/) for instance, is signed by another certificate and the truststore of section 7 refuses it. If the wallet already holds PID cards from another source, delete them, or at least pick the right one when the wallet asks which card to present.

<!-- screenshot: the Issuance page of the playground, and the cards in the Paradym wallet -->


## 5) ngrok: a real URL for the wallet

The wallet talks to your application over HTTPS from the phone: it fetches the request object and posts its response. Although the browser and wallet run on the same phone, the application runs on your development computer. `localhost` on the phone points to the phone itself, so it cannot reach that application.

The simplest way during development is [ngrok](https://ngrok.com/), which exposes your local port on a public HTTPS address:

```bash
ngrok http 8080
```

It prints a public URL such as `https://abcd-1234.ngrok-free.dev`: this is the base URL of your application for the rest of this post. The [free plan](https://ngrok.com/docs/pricing-limits/free-plan-limits) includes a development domain assigned to your account. Copy the URL ngrok prints into the property below. If the browser shows an ngrok warning page, choose **Visit Site** to continue.


## 6) Create the Spring Boot project

This is a plain Spring Boot web application, with [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) as the pac4j implementation. Java 17 or later and Maven are enough.

```bash
mkdir -p wallet-same-device/src/main/java/org/pac4j/demo/wallet
mkdir -p wallet-same-device/src/main/resources wallet-same-device/metadata
cd wallet-same-device
```

The `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.8</version>
        <relativePath/>
    </parent>
    <groupId>org.pac4j.demo</groupId>
    <artifactId>wallet-same-device</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <pac4j.version>6.6.0</pac4j.version>
        <kotlin.version>2.4.21</kotlin.version>
        <kotlin-coroutines.version>1.11.0</kotlin-coroutines.version>
        <kotlin-serialization.version>1.11.1-waltid-20260811</kotlin-serialization.version>
        <jackson-2-bom.version>2.22.3</jackson-2-bom.version>
        <commons-lang3.version>3.21.0</commons-lang3.version>
        <slf4j.version>2.0.20</slf4j.version>
        <commons-logging.version>1.3.5</commons-logging.version>
        <jspecify.version>1.0.0</jspecify.version>
    </properties>

    <!-- the Kotlin Serialization variant used by pac4j is published by walt.id -->
    <repositories>
        <repository>
            <id>waltid-releases</id>
            <url>https://maven.waltid.dev/releases</url>
            <snapshots><enabled>false</enabled></snapshots>
        </repository>
    </repositories>

    <dependencyManagement>
        <dependencies>
            <!-- align pac4j-core and shared dependencies with pac4j-openid4vp -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-parent</artifactId>
                <version>${pac4j.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- spring-webmvc-pac4j otherwise pulls an older Jakarta EE adapter -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-jakartaee</artifactId>
                <version>${pac4j.version}</version>
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
            <version>8.0.4</version>
        </dependency>
        <!-- the OpenID4VP support: the application is a verifier -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>pac4j-openid4vp</artifactId>
            <version>${pac4j.version}</version>
        </dependency>
        <!-- the SD-JWT VC verification, an optional library of pac4j -->
        <dependency>
            <groupId>eu.europa.ec.eudi</groupId>
            <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
            <version>0.20.2</version>
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

SD-JWT VC verification relies on the [EUDI SD-JWT library](https://github.com/eu-digital-identity-wallet/eudi-lib-jvm-sdjwt-kt), which pac4j declares as optional. This example uses version `0.20.2`, matching pac4j. The imported `pac4j-parent` dependency management aligns `pac4j-core` and shared libraries. The explicit management entry for `pac4j-jakartaee` also aligns the adapter pulled in by `spring-webmvc-pac4j`; setting a `pac4j.version` property alone would not override that adapter's transitive version.

The runtime versions match pac4j's build: Kotlin `2.4.21`, Coroutines `1.11.0` and Serialization `1.11.1-waltid-20260811`. The Serialization variant is published in the walt.id repository, required here even though this demo does not use mdoc. Spring Boot 4.0.8 supplies Spring Framework 7.0.9, matching pac4j; the properties also align Jackson 2, Commons Lang, SLF4J, Commons Logging and JSpecify. Jackson 3 remains managed by Boot for its own web stack.

Boot's default Kotlin dependencies would otherwise override pac4j's versions. In particular, Coroutines 1.10.2 lacks `BuildersKt.runBlockingK$default`, called by the current pac4j adapter, and causes a `NoSuchMethodError` when the wallet posts its response.

To check the resolved versions, run `mvn dependency:tree`. For this unreleased pac4j version, install the current snapshot locally and run Maven with `-Dpac4j.version=6.6.0-SNAPSHOT`; keep all pac4j modules on the same version.

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

Two keystores go in the `metadata` directory. Run the `keytool` commands below from the root of the project, next to the `pom.xml`: the paths are relative to it, like the ones in the configuration.

**The verifier key.** `EudiWalletClient` signs requests and identifies the verifier by its certificate hash (`x509_hash`). Production EUDI deployments use a *relying party access certificate* issued by the ecosystem. This demo uses a self-signed certificate with an EC P-256 key, accepted by the tested Paradym wallet; self-signed verifier certificates are outside the HAIP requirements:

```bash
keytool -genkeypair -alias rp -keyalg EC -groupname secp256r1 -sigalg SHA256withECDSA \
  -dname "CN=pac4j demo verifier,O=pac4j" -validity 365 \
  -keystore metadata/verifier.p12 -storetype PKCS12 -storepass changeit
```

**The issuers you trust.** No credential is accepted unless its issuer is trusted, and pac4j trusts nobody by default. Copy the *Issuer Certificate* from the [Trust Information](https://playground.paradym.id/info) page of the playground into a `paradym-issuer.pem` file, and import it into a truststore:

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
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.openid4vp.client.EudiWalletClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.dcql.EudiPidQuery;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.openid4vp.verifier.trust.CertificateTrustedIssuer;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.UUID;

import static org.pac4j.core.profile.definition.CommonProfileDefinition.FAMILY_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.GIVEN_NAME;

@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public Config config() {
        // the request: signed with the key of the verifier, asking for two attributes of the PID as an SD-JWT VC
        final var configuration = new OpenId4VpConfiguration()
            .setKeystore(new KeystoreProperties("./metadata/verifier.p12")
                .setKeyStoreType("PKCS12")
                .setKeyStoreAlias("rp")
                .setKeystorePassword("changeit")
                .setPrivateKeyPassword("changeit"))
            .setDcqlQuery(EudiPidQuery.sdJwtVc(GIVEN_NAME, FAMILY_NAME))
            // Mandatory when using the same browser
            .setUseRedirectUri(true);

        // the response: an SD-JWT VC is accepted if its certificate chain ends in the truststore
        final var issuers = new CertificateTrustedIssuer(new KeystoreProperties("./metadata/pid-issuers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"));
        // certificate revocation checking is disabled to simplify this demo; keep it enabled in production
        issuers.setCertificateRevocationEnabled(false);
        // the playground PID references a status list: this demo logs the reference and accepts the credential,
        // a real status checker must retrieve the list, verify it and check the entry of the credential
        configuration.addCredentialVerifier(new SdJwtVcVerifier().addTrustedIssuer(issuers)
            .setStatusChecker(credential -> LOGGER.info("credential status not checked: {}", credential.getClaims().get("status"))));

        // the demo PID carries no stable identifier: a new profile identifier at each login
        configuration.setProfileIdResolver(credentials -> UUID.randomUUID().toString());

        final var client = new EudiWalletClient(configuration);
        // keep the client name in the callback path to work around URL decoding in the tested wallet
        client.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
        return new Config(baseUrl + "/callback", client);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        addSecurity(registry, "EudiWalletClient").addPathPatterns("/protected/**");
    }
}
```

A few configuration details:

- `EudiWalletClient` pins the HAIP choices: the request is signed and identifies the verifier by `x509_hash`, the hash of the certificate found in the keystore, and the response comes back encrypted (`direct_post.jwt`). These settings are applied by the client; they do not constitute complete HAIP conformance validation.
- `SdJwtVcVerifier` verifies the expected format. With this trust configuration, the wallet's `x5c` certificate chain must lead to a certificate in your truststore, in addition to passing the signature, validity, disclosure and holder-binding checks.
- The profile identifier is yours to choose: `EudiWalletClient` has no default and refuses to initialize until you configure a resolver. If the PID provider issues a stable, unique and non-reassigned `personal_administrative_number`, request it in DCQL and use `ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER)`. That claim is not guaranteed for every PID, and this demo does not ask for it, hence the random identifier: it identifies a login, not a persistent user account.
- The callback URL is the regular pac4j one, and the wallet uses it too: it fetches the request object there and posts its response there, without any browser session.
- `setUseRedirectUri(true)` enables the automatic return to the browser. After accepting the wallet response, pac4j returns its callback URL as `redirect_uri`, with a fresh `response_code`. The wallet opens that URL in the browser; pac4j checks the code against the transaction of the browser session, saves the profile and redirects to the protected page originally requested. The option defaults to `false`; this same-device example enables it explicitly.

The playground PID carries a `status` claim, a reference to an [OAuth Token Status List](https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/): the list where the issuer marks the credentials it has revoked or suspended. pac4j refuses a credential carrying a status unless a *status checker* is configured, because only the application can decide how to read the list, and it never fetches it on its own. The checker of this demo only logs the reference and accepts the credential, so revocation is not checked here. A real checker retrieves the status list token, verifies its signature with the same trust as the credential and checks the entry at the index of the credential: that deserves a post of its own. See [credential status](/docs/clients/openid4vp-sd-jwt-vc-verifier.html#7-credential-status). Disabling certificate revocation does not bypass this check.


## 9) The DCQL query

The query says what your application wants. `EudiPidQuery.sdJwtVc(GIVEN_NAME, FAMILY_NAME)` builds it in one line for the PID; this is what the wallet receives:

```json
{
  "credentials": [
    {
      "id": "pid",
      "format": "dc+sd-jwt",
      "meta": { "vct_values": ["urn:eudi:pid:1"] },
      "claims": [
        { "path": ["given_name"] },
        { "path": ["family_name"] }
      ]
    }
  ]
}
```

One credential query, identified as `pid`, for an SD-JWT VC whose type is the EUDI PID, and the two claims to disclose. The same can be written by hand, in JSON when it comes from a configuration file:

```java
configuration.setDcqlQuery("""
    {"credentials":[{"id":"pid","format":"dc+sd-jwt","meta":{"vct_values":["urn:eudi:pid:1"]},
     "claims":[{"path":["given_name"]},{"path":["family_name"]}]}]}""");
```

Or in Java, with access to expected values, alternatives (`claim_sets`, `credential_sets`) and trusted authorities:

```java
configuration.setDcqlQuery(new DcqlQuery()
    .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
        .setVctValues("urn:eudi:pid:1")
        .addClaim("given_name")
        .addClaim("family_name")));
```

Whatever the wallet presents is checked against the query again by pac4j: the format, the type, the claims. The constraints are not left to the wallet.


## 10) The pages

Two pages are enough: a public home page, and a protected page which shows the profile. Both come from a single Spring MVC controller, `WalletController`, to create next to the two previous classes:

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WalletController {

    @Autowired
    private ProfileManager profileManager;

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "<h1>Home</h1>"
            + "<p>Start in your phone's default browser, using its regular profile (not private browsing). "
            + "The wallet must return to the same browser and profile to complete authentication.</p>"
            + "<p><a href='/protected/index'>Open the protected page: the wallet opens</a></p>";
    }

    @GetMapping("/protected/index")
    @ResponseBody
    public String protectedPage() {
        return "<h1>Protected page</h1><p>" + HtmlUtils.htmlEscape(profileManager.getProfiles().toString()) + "</p>"
            + "<p><a href='/logout'>Logout</a></p>";
    }
}
```

The home page needs only the link which starts authentication. The callback is handled by pac4j: with `setUseRedirectUri(true)`, the wallet returns the browser there automatically, then pac4j restores the original request. No second link, polling script or application code relaying the `response_code` is needed.

Run the application, with ngrok running next to it:

```bash
mvn spring-boot:run
```


## 11) The flow, step by step

Everything happens on the phone. **First open the Paradym wallet and unlock it**, then leave it in the background: the flow fails if the wallet is not already running when the browser hands it the `openid4vp://` link. Then open the ngrok URL **in the phone's default browser, using its regular profile (not private browsing)** and click the link on the home page: `Open the protected page: the wallet opens`.

The wallet may open the return URL in the phone's default browser. Starting in another browser, a private browsing session or an embedded browser can therefore lose the original session: the callback cannot complete authentication and the demo returns to the home page. Use the same browser and profile throughout the flow.

The six screens below show the home page where login starts, the request to review, the warning about the unverified verifier, the PIN code, the wallet success screen and the return to the protected page in the browser, where the profile is displayed. The steps below follow the exchange through to that final page.

<div class="screens">
  <img alt="The application home page: open the protected page to start the wallet login" src="/img/blog/2026_wallet/mobilebeforelogin.jpg" />
  <span class="screens-arrow" aria-hidden="true">&rarr;</span>
  <img alt="Paradym: review the request" src="/img/blog/2026_wallet/sdjwtvc_request.jpg" />
  <span class="screens-arrow" aria-hidden="true">&rarr;</span>
  <img alt="Paradym: organization not verified" src="/img/blog/2026_wallet/organization.jpg" />
  <span class="screens-arrow" aria-hidden="true">&rarr;</span>
  <img alt="Paradym: enter the PIN code" src="/img/blog/2026_wallet/pincode.jpg" />
  <span class="screens-arrow" aria-hidden="true">&rarr;</span>
  <img alt="Paradym: success, the information has been shared" src="/img/blog/2026_wallet/success.jpg" />
  <span class="screens-arrow" aria-hidden="true">&rarr;</span>
  <img alt="Back in the application: the protected page displays Emma Charlotte van der Berg's profile after the automatic wallet return" src="/img/blog/2026_wallet/mobilepostlogin.jpg" />
</div>

**1. The browser asks for the protected page.** pac4j finds no profile, creates a *transaction* and redirects to an `openid4vp://` URL. The browser hands this scheme to the operating system, which opens the wallet.

**2. The wallet fetches the request.** The URL only carries the identifier of the verifier and a `request_uri`: the wallet calls it, on the callback URL of your application, and receives the signed request object, with the DCQL query and the public key to encrypt its response.

**3. The user consents.** The wallet shows who is asking, the hash of your certificate for now, and what is asked: the PID, given name and family name. The user picks the card and confirms with their PIN.

**4. The wallet posts its response.** An encrypted JWT, containing the SD-JWT VC with the two disclosures and a key binding JWT proving the wallet holds the key of the credential, posted on the callback URL. pac4j decrypts, verifies and keeps the result for the browser session which started the transaction. Its HTTP 200 JSON response contains a `redirect_uri` pointing to `/callback/EudiWalletClient?response_code=...`. This is the browser return URL, not the `response_uri` where the wallet just posted the presentation.

**5. The browser returns automatically.** The wallet opens the `redirect_uri` in the default browser. Because the flow started in that browser and profile, its session cookie accompanies the callback. pac4j checks the `response_code` against that session's pending transaction, retrieves the profile already validated at step 4, consumes the transaction and stores the profile in the session. It then redirects to `/protected/index`, the URL saved at step 1: the protected page shows Emma Charlotte van der Berg. The code is handled entirely by pac4j and is not carried to the final protected URL.

The `response_code` is a fresh random value required by [OpenID4VP section 8.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.2). The callback requires both this code and the session which started the transaction; a missing or wrong code is refused, another session cannot claim the presentation, and a completed transaction cannot be consumed again. Keep the return in the original browser: a different browser or an isolated webview does not have its session cookie. The [second post](/blog/openid4vp_in_action_another_device.html) uses polling instead, because the wallet is on the phone while the browser waiting for the answer is on the computer.


## 12) What the logs say

The two loggers enabled in `application.properties` give two views of the flow. The `PROTOCOL_MESSAGE.OPENID4VP` logger prints the messages themselves, as they were sent or received, without any interpretation: one line per message, starting with `>>>` when the application sent it, `<<<` when it received it, followed by the other party, the browser or the wallet. The `org.pac4j.openid4vp` logger tells what pac4j did with them: the transaction created, the request object built, the response decrypted and verified, the profile built. The excerpts below are abbreviated; the final JSON response and browser return illustrate the new `setUseRedirectUri(true)` configuration.

The redirection, step 1: a transaction is created, expiring five minutes later, and the browser is sent to the wallet URL. Note the `request_uri`, on the callback URL with the client name in its path, and the identifier of the verifier, the hash of its certificate:

```
EudiWalletClient : applying EUDI wallet settings for client EudiWalletClient: X509_HASH, DIRECT_POST_JWT, PID profile definition
OpenId4VpConfiguration : OpenID4VP configuration checked: 1 credential queries, verifier formats=[SD_JWT_VC, MSO_MDOC], signed requests=true
VpTransaction : transaction 51fef2…: - -> CREATED (client EudiWalletClient, response mode direct_post.jwt, expires at 2026-10-12T09:19:39Z)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> browser wallet URL: openid4vp://?client_id=x509_hash%3ApntH00p6r9h1QcHOvJAVv90dkHvm5Jhx_GBAKcJH5CE&request_uri=https%3A%2F%2Fabcd-1234.ngrok-free.dev%2Fcallback%2FEudiWalletClient%3Fvp_tx%3D51fef2…&request_uri_method=post
```

The first two lines appear once, at the first request: the client initializes lazily. The configuration comes with a default verifier per format; section 8 replaced the SD-JWT VC one with the trusted issuers and the mdoc one stays, hence the two formats listed.

Step 2: the wallet fetches the request object, which is built for it and signed:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] <<< wallet GET vp_tx=51fef2…
OpenId4VpRequestObjectBuilder : client metadata built for transaction 51fef2…: formats=[dc+sd-jwt, mso_mdoc], response encryption methods=[A128GCM, A256GCM]
OpenId4VpRequestObjectBuilder : request object signed for transaction 51fef2… with ES256
VpTransaction : transaction 51fef2…: CREATED -> REQUEST_RETRIEVED (GET of the request URI)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> wallet 200 eyJ4NWMiOlsiTUlJQmNq…
```

These demo traces show a GET even though the wallet URL advertises `request_uri_method=post`. pac4j accepts both methods; a wallet following the advertised method uses POST and can include its metadata and a wallet nonce.

The request object is a JWT whose header carries your certificate (`x5c`, with the `rp` alias as `kid`) and whose payload is the request. Decoded, its payload reads:

```json
{
  "client_id": "x509_hash:pntH00p6r9h1QcHOvJAVv90dkHvm5Jhx_GBAKcJH5CE",
  "response_type": "vp_token",
  "response_mode": "direct_post.jwt",
  "response_uri": "https://abcd-1234.ngrok-free.dev/callback/EudiWalletClient?vp_tx=51fef2…",
  "nonce": "281fec06efb9492b95b45c09a537e624",
  "state": "7b99f8b2372b42d6b819942add896494…",
  "dcql_query": { "credentials": [ { "id": "pid", "format": "dc+sd-jwt", "meta": { "vct_values": ["urn:eudi:pid:1"] },
                  "claims": [ { "path": ["given_name"] }, { "path": ["family_name"] } ] } ] },
  "client_metadata": { "jwks": { "keys": [ { "kty": "EC", "use": "enc", "crv": "P-256", "alg": "ECDH-ES", "kid": "2f75d54c…", "x": "…", "y": "…" } ] },
                       "encrypted_response_enc_values_supported": ["A128GCM", "A256GCM"],
                       "vp_formats_supported": { "dc+sd-jwt": { "sd-jwt_alg_values": ["ES256"], "kb-jwt_alg_values": ["ES256"] },
                                                 "mso_mdoc": { "issuerauth_alg_values": [-7], "deviceauth_alg_values": [-7] } } },
  "aud": "https://self-issued.me/v2", "iat": 1791796479, "exp": 1791796779
}
```

Step 4: the wallet posts its response, a JWE encrypted with the ephemeral key published in the request. pac4j decrypts it, verifies the SD-JWT VC, its chain, its disclosures and its key binding, hands the status reference to the checker of section 8, and builds the profile:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] <<< wallet POST vp_tx=51fef2…&response=eyJraWQiOiIyZjc1ZDU0Yy…
OpenId4VpAuthenticator : response decrypted and authentication tag verified for transaction 51fef2…: alg=ECDH-ES, enc=A128GCM, members=[vp_token, state]
CertificateTrustedIssuer : issuer truststore loaded: 1 trust anchors
CertificateTrustedIssuer : certificate revocation lists loaded: 0
SecurityConfig : credential status not checked: {status_list={idx=18, uri=https://metadata.paradym.id/7f22b55a…/status-lists/86852c16…}}
SdJwtVcVerifier : SD-JWT VC verified for transaction 51fef2…: holder binding=true
OpenId4VpAuthenticator : presentation 1/1 verified for transaction 51fef2…, query pid: format=SD_JWT_VC, verifier=SdJwtVcVerifier
OpenId4VpAuthenticator : wallet response validated for transaction 51fef2…: 1 credential queries answered
EudiPidProfile : adding => key: issuing_country / value: NL / class java.lang.String
EudiPidProfile : adding => key: vct / value: urn:eudi:pid:1 / class java.lang.String
EudiPidProfile : adding => key: status / value: {status_list={idx=18, uri=https://metadata.paradym.id/7f22b55a…/status-lists/86852c16…}} / class …
EudiPidProfile : adding => key: family_name / value: van der Berg / class java.lang.String
EudiPidProfile : adding => key: given_name / value: Emma Charlotte / class java.lang.String
OpenId4VpAuthenticator : profile built for transaction 51fef2…: profile class=EudiPidProfile, definition=EudiPidProfileDefinition
VpTransaction : transaction 51fef2…: REQUEST_RETRIEVED -> RESPONSE_RECEIVED (presentation validated)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> wallet 200 {"redirect_uri":"https://abcd-1234.ngrok-free.dev/callback/EudiWalletClient?response_code=8c731e…"}
```

The profile gets more than the two requested claims: the issuer also signs, outside any disclosure, the `vct`, the issuing country and authority, the validity dates, the holder key (`cnf`) and the status reference, and the wallet cannot withhold them. The two names are the only disclosed claims.

Step 5: the wallet opens the returned callback URL in the browser. pac4j checks the `response_code` using the transaction identifier held in the browser session; the return URL carries no `vp_tx`. It then picks up the validated profile, without verifying it twice, and the regular callback redirects to the saved `/protected/index` URL:

```
VpTransaction : transaction 51fef2…: RESPONSE_RECEIVED -> CONSUMED (the browser came back)
OpenId4VpAuthenticator : transaction 51fef2…: reusing the profile validated when the wallet posted its response, nothing is verified again
```

Note that the logs show the credentials as they travel, tokens and the return `response_code` included: enable them to diagnose, not permanently in production.


## 13) Limitations and what comes next

A few things to know before going further:

- **The return needs the original browser session.** `setUseRedirectUri(true)` implements the OpenID4VP redirect, but the wallet must open it in the browser that started the login. Opening another browser or an isolated webview does not transfer that session.
- **A refusal was silent** with the tested Paradym wallet: when the user declined or had no matching card, nothing was posted, and the transaction expired after five minutes. Wallets that post an error let pac4j report it to the browser.
- **Certificate revocation** is disabled in this demo. In production, keep it enabled and supply current CRLs or configure the Java PKIX provider to retrieve revocation data. This setting does not disable credential status checking: credentials carrying status information still need a status checker, and the one of this demo only logs the status reference, as seen in section 8.
- Transaction data, the `verifier_attestation` and `openid_federation` prefixes, and the automatic retrieval of status lists are not supported yet.

This integration is introduced in **pac4j v6.6.0**. The full documentation starts at [OpenID for Verifiable Presentations](/docs/clients/openid4vp.html), and the [second post](/blog/openid4vp_in_action_another_device.html) takes the wallet to another device, with a QR code and an mdoc PID.
