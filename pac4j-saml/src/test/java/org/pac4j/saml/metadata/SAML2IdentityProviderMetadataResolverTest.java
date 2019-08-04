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
        SAML2Configuration configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
    }

    @Test
    public void resolveMetadataEntityId() throws Exception {
        MetadataResolver resolver = metadataResolver.resolve();
        CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion("mmoayyed.example.net"));
        final EntityDescriptor entity = resolver.resolveSingle(criteria);
        assertEquals("mmoayyed.example.net", entity.getEntityID());
    }

    @Test
    public void resolveMetadataDocumentAsString() {
        final String metadata = metadataResolver.getMetadata();
        assertNotNull(metadata);
    }
}
