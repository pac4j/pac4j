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

    /** The same, with the leaf issued by a test authority whose self-signed certificate closes the chain. */
    private static final String CHAINED_KEYSTORE =
        "MIIFCwIBAzCCBNEGCSqGSIb3DQEHAaCCBMIEggS+MIIEujCCA5cGCSqGSIb3DQEHBqCCA4gwggOEAgEAMIIDfQYJKoZIhvcNAQcB"
        + "MBwGCiqGSIb3DQEMAQYwDgQICm3HYwfPVbECAggAgIIDULiEnMaL6p/CohljYg1D1JaBR4dF+nnRNwVkfuzuVb8hENiNfqBl+64k"
        + "EK2FedPJIIZvFtj4dsK3J5XaHjh9Mdcdyl/DR2KeTgT1OCM8MgAV75NF2WdvEzRmJ21PYzGk2L0AUl0/a3W4e9hpJVE7W2f5jWM6"
        + "K9NIZp937ABgdV1r9K4KB5x9FH2H7uKCIjoL9WV1DFtnEjw7jUDSsKz1k61oOjGg+8J9Lb5E7n0+3W39xTR0GkvJEZBt+QH+Oekh"
        + "YFj4D/5vjgrrUzJqhxM7aG1lwnM0/mnosh6FoGOmpvMvYOrZ0i06Vm2sZELdc/32Az1Yk7u/PYj28ilWpQNlNOFakiTJtaOH6yhL"
        + "MEu+WJemVTe/xqwSHqrpixmIzG1ioZHpiRgF0peLTxPh+AZfcQfDp7sYUj0pgmTocmhgGUFDvA9GYvbbDc4atYtdP6FQOzMnyMxF"
        + "lPChXvx69J3OgbY5yWE45mSpNZrvuSdZYRW1qcgHzD076WjbSipK1kPPBp/nFT7ay3uhcQbhQ1pBoUVCZ55ZSpSTrq0S5ZoNW3k4"
        + "/3mpqVotw4E0+TYVHjkCEmsiLNTpwM0nGLiXfzq593O6MGzSY60b1RE9KbFEInq46MtIT2cyyK9yuisMkn9KEZ9u7IOtsy4hRtYM"
        + "9zxK+sIvna7EuhlGrDqmoV5I7rJKKIm5st7lG6sR/Ge8yAuGqUC+7pmWzbmzERH14Osvq4VV+w/fLlkO24jJNSkfW57OgVpdomjY"
        + "QAUq+11KKEz9ic+a+NFoJF5NuF1KMXlB33QXDs9crxI8Hk0yCpHeI1k3L6Gu8CK7Zhq6yc98xcWVWE6nnRFUFMTUote+ABmx85AL"
        + "132OpD3AHCqoVMAaC/lEThWuTbFCm7i/UY3vUKeygZDyUHtZNiR4d3L7/1z1YADLzMSf5utuPhpQ/8NbiPKwPneSqvDDt0/9QMlY"
        + "XOF9ZhfhsKZ+TodE/Q/A3f6Gw1R5vEclRKi0GK14ydkCOZLw3eZtWLbLoCRJvNlJY2DxoqzQH5NSMLEUbS7RSQd/mx6yi2UZAtZ6"
        + "NC45ivWbZSPINz2NOe1XcHdgUW+Q7pbq22wheycdAcFq8wAhf61ejmnhZKJzgYw9W+LZCDtSFuLnMIIBGwYJKoZIhvcNAQcBoIIB"
        + "DASCAQgwggEEMIIBAAYLKoZIhvcNAQwKAQKggbQwgbEwHAYKKoZIhvcNAQwBAzAOBAgCnFWqQ+HLxwICCAAEgZB6qkKfPIlPYi5b"
        + "sbzlrOu7CFQAltjwTA1CSyfCgfkD+kX3XmY4t5xarCE9mVS8+pDpXaJuvRD3EuzACDCsIjIY+9h3JL0fnGI0FuDR6HpxfAhBJafe"
        + "xdjeSWup+tv7dhh4kbtH7mlDNWWMCEQQVFxKXcZTemW1b7E3xO+eCFJJhCKNlSECSDxzUg2uyd3XO18xOjATBgkqhkiG9w0BCRQx"
        + "Bh4EAHIAcDAjBgkqhkiG9w0BCRUxFgQUXktk9NUL7+be2Ksspaxh1uq/zAMwMTAhMAkGBSsOAwIaBQAEFAkysqMK/+IQVU+DYwjK"
        + "XaWeFIIcBAieQpBaQBeAvwICCAA=";

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
        return keystore(KEYSTORE);
    }

    private KeystoreProperties keystore(final String encoded) throws Exception {
        val file = directory.resolve("rp.p12");
        Files.write(file, Base64.getDecoder().decode(encoded));
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
    void testTheTrustAnchorIsLeftOutOfTheChain() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_HASH);
        configuration.setKeystore(keystore(CHAINED_KEYSTORE));
        configuration.init();

        // the keystore held the leaf and the authority: only the leaf is published, as HAIP requires
        val published = configuration.getRequestObjectSigningKey().getX509CertChain();
        assertEquals(1, published.size());
        val leaf = com.nimbusds.jose.util.X509CertUtils.parse(published.get(0).decode());
        assertEquals("CN=verifier.example.org", leaf.getSubjectX500Principal().getName());
        assertEquals("CN=Test CA", leaf.getIssuerX500Principal().getName());
    }

    @Test
    void testASelfSignedLeafIsKept() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_HASH);
        configuration.setKeystore(keystore());
        configuration.init();

        // a lone self-signed certificate is the leaf itself, not an anchor to drop
        assertEquals(1, configuration.getRequestObjectSigningKey().getX509CertChain().size());
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
