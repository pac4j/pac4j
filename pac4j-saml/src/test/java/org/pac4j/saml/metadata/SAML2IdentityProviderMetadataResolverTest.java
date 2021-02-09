package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;

public class SAML2IdentityProviderMetadataResolverTest {

    private SAML2IdentityProviderMetadataResolver metadataResolver;

    @Before
    public void setUp() {
        final var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
    }

    @Test
    public void resolveMetadataEntityId() throws Exception {
        final var resolver = metadataResolver.resolve();
        final var criteria = new CriteriaSet(new EntityIdCriterion("mmoayyed.example.net"));
        final var entity = resolver.resolveSingle(criteria);
        assertEquals(entity.getEntityID(), "mmoayyed.example.net");
    }

    @Test
    public void resolveMetadataDocumentAsString() {
        final var metadata = metadataResolver.getMetadata();
        assertNotNull(metadata);
    }
}
