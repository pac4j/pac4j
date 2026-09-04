package org.pac4j.openid4vp.config;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.util.TestsHelper;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.time.Period;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the client identifier prefixes which derive something from the signing key: a hash for
 * {@code x509_hash}, a key identifier for {@code decentralized_identifier}.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class ClientIdPrefixTests {

    /** A throwaway PKCS#12 holding an EC key and its self-signed certificate. */
    private static final String KEYSTORE =
        "MIIDowIBAzCCA2kGCSqGSIb3DQEHAaCCA1oEggNWMIIDUjCCAi8GCSqGSIb3DQEHBqCCAiAwggIcAgEAMIICFQYJKoZIhvcNAQcB"
        + "MBwGCiqGSIb3DQEMAQYwDgQIJgT+Y1MJEwECAggAgIIB6IaF+/0xZ16QPESfMU/B5AQR7gOGHWtE8jPaj+VEl48Z2XR2RhUoA+VK"
        + "UvAq0nuue4cnLANhL8wSvaopnyzET300mXl4Sha4ClU9lKyD9P2uZmhqHSq9y3WfzcSUZjwCxbp7FieT1SI9NuWKnIOyS5jTNKr8"
        + "zxxO/dON+e9g7SqyFaVWwmfoCAJgP/YIQDgEX/7XmJIasr0m/0TYnOCRjdRTo3aSeuCoqp4G8UdHMpgKF5sClcw7HS2zNPozyBBz"
        + "pf8ckis87ckSPtiPxF8sCmJJMxcscee7ZIYHApWAZKGkb3Dfd7rkewHHBS9MuApT47OzT4wAxpUK5H9cnB2niV3QTg1TqReI1mUz"
        + "FWiiKHWjnNH6uT92+/OgrP3WkGyLeJczZKxDJR+of+tFREClW/GioL8SpTC4wN7qPGCUI1pWgnnX/TLTcyUv58Zfi2xL5m6ONFIp"
        + "9LDpWi90Nx6hBSVyVFZhmrhn9XzpQJqM/KbaplbXyOT3KxYRofxIkYasdqX9XMvCx/2siWqpaClGLLzEFWWqcAf+BCAP7908ON3r"
        + "iI4W4sleZf10h5h8yXd85dNb0nvxChlf9SEnOrbWV7hfRdtBmX4dODXMCVGK1L+jZP8+TiPDhuzlJgnwOB83jWonDH6jcgiaMIIB"
        + "GwYJKoZIhvcNAQcBoIIBDASCAQgwggEEMIIBAAYLKoZIhvcNAQwKAQKggbQwgbEwHAYKKoZIhvcNAQwBAzAOBAhbjmWFYzlytgIC"
        + "CAAEgZAn/6XejDXC3Bxu+rmDGLY8MwEjxIYHT89AW34Rdi7tYGV3aof//W7XDnrqjYEkry7YYpahDgCllZ/NJfesUtSxCUFJQaWe"
        + "gLujwuPs67g7Dg9q6UCWbPwaHuxiBxbyhZ8gNaYU3gqDMJXUHhz42b3Yp5VGtqAH54BGge6uderrxc5wf93fzGrNmJ2xHqJ0CbEx"
        + "OjATBgkqhkiG9w0BCRQxBh4EAHIAcDAjBgkqhkiG9w0BCRUxFgQUoOKuotV/mOPpHaklm/XdY6fvje4wMTAhMAkGBSsOAwIaBQAE"
        + "FPbJd2Ja3EDr5D9dQedv5OAcH759BAivguXQR7uaJAICCAA=";

    @TempDir
    private Path directory;

    private OpenId4VpConfiguration configuration(final ClientIdPrefix prefix) {
        val configuration = new OpenId4VpConfiguration();
        configuration.setClientIdPrefix(prefix)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}");
        configuration.addCredentialVerifier(new SdJwtVcVerifier());
        return configuration;
    }

    private KeystoreProperties keystore() throws Exception {
        val file = directory.resolve("rp.p12");
        Files.write(file, Base64.getDecoder().decode(KEYSTORE));
        val keystore = new KeystoreProperties()
            .setKeystorePath(file.toString())
            .setKeystorePassword("changeit")
            .setPrivateKeyPassword("changeit")
            .setKeyStoreAlias("rp")
            .setKeyStoreType("PKCS12")
            // required by the generator even when the keystore exists and nothing is generated
            .setCertificatePrefix("rp-cert")
            .setCertificateExpirationPeriod(Period.ofYears(1));
        keystore.setKeystoreGenerator(new FileSystemKeystoreGenerator(keystore));
        return keystore;
    }

    @Test
    void testTheX509HashIsComputedFromTheCertificate() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_HASH);
        configuration.setClientId("whatever-was-typed");
        configuration.setKeystore(keystore());
        configuration.init();

        // the specification: the base64url-encoded SHA-256 hash of the DER-encoded certificate
        val keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(Base64.getDecoder().decode(KEYSTORE)), "changeit".toCharArray());
        val expected = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(MessageDigest.getInstance("SHA-256").digest(keyStore.getCertificate("rp").getEncoded()));

        assertEquals(expected, configuration.getClientId());
        assertEquals("x509_hash:" + expected, configuration.computeClientId());
    }

    @Test
    void testTheX509SanDnsIdentifierMustBeASubjectAlternativeName() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_SAN_DNS);
        configuration.setClientId("verifier.example.org");
        configuration.setKeystore(keystore());
        configuration.init();

        assertEquals("x509_san_dns:verifier.example.org", configuration.computeClientId());
    }

    @Test
    void testAnX509SanDnsIdentifierAbsentFromTheCertificateIsRefused() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_SAN_DNS);
        configuration.setClientId("other.example.org");
        configuration.setKeystore(keystore());

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the client identifier 'other.example.org' must be a dNSName subject alternative name of the leaf "
                + "certificate for the x509_san_dns client identifier prefix, but it holds: [verifier.example.org]");
    }

    @Test
    void testADecentralizedIdentifierNeedsAKeyIdentifier() {
        val configuration = configuration(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setClientId("did:example:123");
        // a key created without any identifier: the wallet could not find it in the DID document
        configuration.setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()));

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the signing key must carry a key identifier for the decentralized_identifier client identifier prefix: "
                + "the wallet looks the key up in the DID document by the kid of the request object");
    }

    @Test
    void testADecentralizedIdentifierWithAKeyIdentifier() {
        val configuration = configuration(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setClientId("did:example:123");
        configuration.setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.init();

        assertEquals("key-1", configuration.getRequestObjectSigningKey().getKeyID());
        assertEquals("decentralized_identifier:did:example:123", configuration.computeClientId());
    }
}
