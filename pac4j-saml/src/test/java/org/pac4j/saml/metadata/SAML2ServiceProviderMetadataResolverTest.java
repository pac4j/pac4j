package org.pac4j.saml.metadata;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.SAML2HttpClientBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SAML2ServiceProviderMetadataResolverTest {

    private static SAML2Configuration initializeConfiguration(final Resource serviceProviderMetadataResource,
                                                              final String keystorePath,
                                                              final boolean forceServiceProviderMetadataGeneration) {
        var httpClient = new SAML2HttpClientBuilder();
        httpClient.setConnectionTimeout(Duration.ofSeconds(1));
        httpClient.setSocketTimeout(Duration.ofSeconds(1));

        val config = new SAML2Configuration();
        config.setHttpClient(httpClient.build());
        config.setKeystorePath(keystorePath);
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.setSignMetadata(true);
        config.setForceKeystoreGeneration(true);
        config.setForceServiceProviderMetadataGeneration(forceServiceProviderMetadataGeneration);
        config.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        config.setServiceProviderMetadataResource(serviceProviderMetadataResource);
        config.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        var attribute =
            new SAML2ServiceProviderRequestedAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "eduPersonPrincipalName");
        attribute.setServiceLang("fr");
        attribute.setServiceName("MySAML2ServiceProvider");
        config.getRequestedServiceProviderAttributes().add(attribute);
        config.init();
        return config;
    }

    @Test
    public void resolveServiceProviderMetadataViaFile() {
        val configuration =
            initializeConfiguration(new FileSystemResource("target/out.xml"), "target/keystore.jks", true);
        final SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        assertNotNull(metadataResolver.resolve());
    }

    @Test
    public void resolveServiceProviderMetadataViaExistingClasspath() {
        val configuration =
            initializeConfiguration(new ClassPathResource("sample-sp-metadata.xml"), "target/keystore.jks", true);
        final SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        assertNotNull(metadataResolver.resolve());
    }

    @Test
    public void resolveServiceProviderMetadataViaExistingClasspathWithMerge() {
        val configuration =
            initializeConfiguration(new FileSystemResource("target/sample-sp-metadata.xml"), "target/keystore.jks", false);
        final SAML2MetadataResolver initialMetadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        assertNotNull(initialMetadataResolver.resolve());
        final SAML2MetadataResolver otherMetadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        assertNotNull(otherMetadataResolver.resolve());
        try {
            EntityDescriptor descriptor = otherMetadataResolver.resolve().resolveSingle(
                new CriteriaSet(new EntityIdCriterion("urn:mace:saml:pac4j.org"))
            );
            var sp = descriptor.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
            assertEquals(4, sp.getSingleLogoutServices().size());
            assertEquals(1, sp.getAssertionConsumerServices().size());
        } catch (ResolverException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void resolveServiceProviderMetadataViaUrl() throws Exception {
        val restBody = IOUtils.toString(
            new ClassPathResource("sample-sp-metadata.xml").getInputStream(), StandardCharsets.UTF_8);
        val wireMockServer = new WireMockServer(8181);
        wireMockServer.stubFor(
            get(urlPathEqualTo("/saml"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", ContentType.APPLICATION_XML.getMimeType())
                    .withBody(restBody)));
        wireMockServer.stubFor(
            post(urlPathEqualTo("/saml"))
                .willReturn(aResponse().withStatus(200)));

        val keystore = IOUtils.toString(
            new ClassPathResource("dummy-keystore.txt").getInputStream(), StandardCharsets.UTF_8);
        wireMockServer.stubFor(
            get(urlPathEqualTo("/keystore"))
                .willReturn(aResponse().withStatus(200).withBody(keystore)));
        wireMockServer.stubFor(
            post(urlPathEqualTo("/keystore"))
                .willReturn(aResponse().withStatus(200)));

        try {
            wireMockServer.start();
            val configuration =
                initializeConfiguration(new FileUrlResource(new URL("http://localhost:8181/saml")),
                    "http://localhost:8181/keystore", true);
            final SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
            assertNotNull(metadataResolver.resolve());
        } finally {
            wireMockServer.stop();
        }
    }
}
