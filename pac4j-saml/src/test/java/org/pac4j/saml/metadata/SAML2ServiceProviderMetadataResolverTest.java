package org.pac4j.saml.metadata;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class SAML2ServiceProviderMetadataResolverTest {

    private static SAML2Configuration initializeConfiguration(final Resource serviceProviderMetadataResource,
                                                              final String keystorePath) {
        final SAML2Configuration config = new SAML2Configuration();
        config.setKeystorePath(keystorePath);
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.setSignMetadata(true);
        config.setForceKeystoreGeneration(true);
        config.setForceServiceProviderMetadataGeneration(true);
        config.setServiceProviderMetadataResource(serviceProviderMetadataResource);
        config.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        config.getRequestedServiceProviderAttributes().add(
            new SAML2ServiceProvicerRequestedAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "eduPersonPrincipalName"));
        config.init();
        return config;
    }

    @Test
    public void resolveServiceProviderMetadataViaFile() {
        final SAML2Configuration configuration =
            initializeConfiguration(new FileSystemResource("target/out.xml"), "target/keystore.jks");
        final SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration,
            "http://localhost", new KeyStoreCredentialProvider(configuration));
        assertNotNull(metadataResolver.resolve());
    }

    @Test
    public void resolveServiceProviderMetadataViaUrl() throws Exception {
        final String restBody = IOUtils.toString(
            new ClassPathResource("sample-sp-metadata.xml").getInputStream(), StandardCharsets.UTF_8);
        final WireMockServer wireMockServer = new WireMockServer(8081);
        wireMockServer.stubFor(
            get(urlPathEqualTo("/saml"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", ContentType.APPLICATION_XML.getMimeType())
                    .withBody(restBody)));

        wireMockServer.stubFor(
            get(urlPathEqualTo("/keystore"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", ContentType.APPLICATION_XML.getMimeType())
                    .withBody(restBody)));
        
        try {
            wireMockServer.start();
            final SAML2Configuration configuration =
                initializeConfiguration(new FileUrlResource(new URL("http://localhost:8081/saml")),
                    "http://localhost:8081/keystore");
            final SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration,
                "http://localhost", new KeyStoreCredentialProvider(configuration));
            assertNotNull(metadataResolver.resolve());
        } finally {
            wireMockServer.stop();
        }
    }
}
