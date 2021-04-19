package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.pac4j.core.exception.TechnicalException;
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
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
    }

    @Test
    public void resolveMetadataEntityId() throws Exception {
        var resolver = metadataResolver.resolve();
        var criteria = new CriteriaSet(new EntityIdCriterion("mmoayyed.example.net"));
        var entity = resolver.resolveSingle(criteria);
        assertNotNull(entity);
        assertEquals(entity.getEntityID(), "mmoayyed.example.net");
    }

    @Test
    public void resolveMetadataDocumentAsString() {
        var metadata = metadataResolver.getMetadata();
        assertNotNull(metadata);
    }

    @Test
    public void resolveMetadataOverUrl() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new UrlResource("https://sso.union.edu/idp/shibboleth"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();

        var resolver = metadataResolver.resolve();
        assertNotNull(resolver);

        assertFalse(metadataResolver.hasChanged());
        assertEquals(0, metadataResolver.getLastModified());
        assertNotNull(metadataResolver.resolve(true));

        var addr = new InetSocketAddress("unknown.example.com", 8080);
        var proxy = new Proxy(Proxy.Type.HTTP, addr);
        metadataResolver.setProxy(proxy);
        assertThrows(TechnicalException.class, () -> metadataResolver.resolve(true));
    }

    @Test
    public void resolveExpiringMetadata() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("expired-idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
        assertNull(metadataResolver.getEntityDescriptorElement());
    }
}
