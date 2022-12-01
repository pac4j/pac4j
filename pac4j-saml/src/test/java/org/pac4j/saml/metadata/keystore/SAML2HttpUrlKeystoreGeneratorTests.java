package org.pac4j.saml.metadata.keystore;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;

/**
 * This is {@link SAML2HttpUrlKeystoreGeneratorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2HttpUrlKeystoreGeneratorTests {
    @Test
    public void verifyKeystoreGeneration() throws Exception {
        final ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        val wireMockServer = new WireMockServer(8085);
        try {
            wireMockServer.stubFor(
                post(urlPathEqualTo("/keystore"))
                    .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", ContentType.TEXT_PLAIN.getMimeType())));

            val restBody = IOUtils.toString(
                new ClassPathResource("dummy-keystore.txt").getInputStream(), StandardCharsets.UTF_8);

            wireMockServer.stubFor(
                get(urlPathEqualTo("/keystore"))
                    .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", ContentType.TEXT_PLAIN.getMimeType())
                        .withBody(restBody)));
            wireMockServer.start();

            val configuration = new SAML2Configuration();
            configuration.setCertificateSignatureAlg("SHA256withRSA");
            configuration.setForceKeystoreGeneration(true);
            configuration.setKeystoreResourceUrl("http://localhost:8085/keystore");
            configuration.setKeystorePassword("pac4j");
            configuration.setPrivateKeyPassword("pac4j");
            configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
            configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
            configuration.init();

            final CredentialProvider provider = new KeyStoreCredentialProvider(configuration);
            assertNotNull(provider.getCredentialResolver());
            assertNotNull(provider.getCredential());
            assertNotNull(provider.getKeyInfo());
        } finally {
            wireMockServer.stop();
        }
    }
}
