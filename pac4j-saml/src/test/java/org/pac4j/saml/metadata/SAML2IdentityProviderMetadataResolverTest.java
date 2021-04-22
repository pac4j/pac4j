package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.pac4j.core.exception.TechnicalException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.net.InetSocketAddress;
import java.net.Proxy;

import static org.junit.Assert.*;

public class SAML2IdentityProviderMetadataResolverTest {

    private SAML2IdentityProviderMetadataResolver metadataResolver;

    @Before
    public void setUp() {
        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
    }

    @Test
    public void resolveMetadataEntityId() throws Exception {
        final MetadataResolver resolver = metadataResolver.resolve();
        final CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion("mmoayyed.example.net"));
        final EntityDescriptor entity = resolver.resolveSingle(criteria);
        assertNotNull(entity);
        assertEquals(entity.getEntityID(), "mmoayyed.example.net");
    }

    @Test
    public void resolveMetadataDocumentAsString() {
        final String metadata = metadataResolver.getMetadata();
        assertNotNull(metadata);
    }

    @Test
    public void resolveMetadataOverUrl() throws Exception {
        SAML2Configuration configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new UrlResource("https://sso.union.edu/idp/shibboleth"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();

        MetadataResolver resolver = metadataResolver.resolve();
        assertNotNull(resolver);

        assertFalse(metadataResolver.hasChanged());
        assertEquals(0, metadataResolver.getLastModified());
        assertNotNull(metadataResolver.resolve(true));

        InetSocketAddress addr = new InetSocketAddress("unknown.example.com", 8080);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        metadataResolver.setProxy(proxy);
        assertThrows(TechnicalException.class, () -> metadataResolver.resolve(true));
    }

    @Test
    public void resolveExpiringMetadata() throws Exception {
        SAML2Configuration configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("expired-idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
        assertNull(metadataResolver.getEntityDescriptorElement());
    }
}
