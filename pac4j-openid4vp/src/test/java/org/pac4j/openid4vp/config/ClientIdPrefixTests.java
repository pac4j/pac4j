package org.pac4j.openid4vp.config;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
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

    /**
     * A leaf issued by an intermediate authority, itself issued by a self-signed root: three certificates,
     * leaf first, in the keystore entry.
     */
    private static final String INTERMEDIATE_KEYSTORE =
        "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"
        + "8GU+joC3QwICCAA=";

    @TempDir
    private Path directory;

    private OpenId4VpConfiguration configuration(final ClientIdPrefix prefix) {
        val configuration = new OpenId4VpConfiguration();
        configuration.setClientIdPrefix(prefix)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\",\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]}}]}");
        return configuration;
    }

    private KeystoreProperties keystore() throws Exception {
        return keystore(KEYSTORE);
    }

    private KeystoreProperties keystore(final String encoded) throws Exception {
        val file = directory.resolve("rp.p12");
        Files.write(file, Base64.getDecoder().decode(encoded));
        return new KeystoreProperties(file.toString())
            .setKeystorePassword("changeit")
            .setPrivateKeyPassword("changeit")
            .setKeyStoreAlias("rp")
            .setKeyStoreType("PKCS12");
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
    void testTheIntermediateAuthorityStaysInTheChain() throws Exception {
        val configuration = configuration(ClientIdPrefix.X509_HASH);
        configuration.setKeystore(keystore(INTERMEDIATE_KEYSTORE));
        configuration.init();

        // the wallet needs the intermediate to build the chain up to its anchor: leaf then intermediate, root dropped
        val published = configuration.getRequestObjectSigningKey().getX509CertChain();
        assertEquals(2, published.size());
        val leaf = com.nimbusds.jose.util.X509CertUtils.parse(published.get(0).decode());
        val intermediate = com.nimbusds.jose.util.X509CertUtils.parse(published.get(1).decode());
        assertEquals("CN=verifier.example.org", leaf.getSubjectX500Principal().getName());
        assertEquals("CN=Test Intermediate CA", leaf.getIssuerX500Principal().getName());
        assertEquals("CN=Test Intermediate CA", intermediate.getSubjectX500Principal().getName());
        assertEquals("CN=Test CA", intermediate.getIssuerX500Principal().getName());
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
        configuration.setJwks(new JwksProperties(directory.resolve("keys.jwks").toString()));

        TestsHelper.expectException(configuration::init, TechnicalException.class,
            "the signing key must carry a key identifier for the decentralized_identifier client identifier prefix: "
                + "the wallet looks the key up in the DID document by the kid of the request object");
    }

    @Test
    void testADecentralizedIdentifierWithAKeyIdentifier() {
        val configuration = configuration(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setClientId("did:example:123");
        configuration.setJwks(new JwksProperties(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.init();

        assertEquals("key-1", configuration.getRequestObjectSigningKey().getKeyID());
        assertEquals("decentralized_identifier:did:example:123", configuration.computeClientId());
    }
}
