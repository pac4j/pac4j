package org.pac4j.saml.metadata;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.xml.XMLParserException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.junit.Assert.*;

@SuppressWarnings("PMD.TooManyStaticImports")
public class SAML2IdentityProviderMetadataResolverTest {

    private SAML2IdentityProviderMetadataResolver metadataResolver;

    private static SSLContext disabledSslContext() throws Exception {
        var trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        var sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
    }

    @Before
    public void setUp() {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
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
        metadataResolver.init();
        var metadata = metadataResolver.getMetadata();
        assertNotNull(metadata);
    }

    @Test
    @Ignore
    public void resolveMetadataOverUrlWithHostnameVerifierFromConfig() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new UrlResource("https://self-signed.badssl.com"));
        configuration.setHostnameVerifier((s, sslSession) -> true);
        configuration.setSslSocketFactory(disabledSslContext().getSocketFactory());
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        try {
            metadataResolver.init();
        } catch (final TechnicalException e) {
            assertEquals(XMLParserException.class, e.getCause().getClass());
        }
    }

    @Test
    @Ignore
    public void resolveMetadataOverUrlWithHostnameVerifier() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new UrlResource("https://self-signed.badssl.com"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        try {
            metadataResolver.init();
        } catch (final TechnicalException e) {
            assertEquals(SSLHandshakeException.class, e.getCause().getClass());
        }

        metadataResolver.setHostnameVerifier((s, sslSession) -> true);
        metadataResolver.setSslSocketFactory(disabledSslContext().getSocketFactory());
        try {
            metadataResolver.init();
        } catch (final TechnicalException e) {
            assertEquals(XMLParserException.class, e.getCause().getClass());
        }
    }

    @Test
    public void resolveMetadataOverUrl() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new UrlResource("https://sso.union.edu/idp/shibboleth"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);

        var resolver = metadataResolver.resolve();
        assertNotNull(resolver);

        assertFalse(metadataResolver.hasChanged());
        assertEquals(0, metadataResolver.getLastModified());
        assertNotNull(metadataResolver.load());

        var addr = new InetSocketAddress("unknown.example.com", 8080);
        var proxy = new Proxy(Proxy.Type.HTTP, addr);
        metadataResolver.setProxy(proxy);
        assertThrows(TechnicalException.class, () -> metadataResolver.internalLoad());
    }

    @Test
    public void resolveMetadataFromByteArray() throws Exception {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(
                new ByteArrayResource(new ClassPathResource("idp-metadata.xml").getInputStream().readAllBytes()));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);

        var resolver = metadataResolver.resolve();
        assertNotNull(resolver);

        assertFalse(metadataResolver.hasChanged());
        assertNotNull(metadataResolver.load());
    }

    @Test
    public void resolveExpiringMetadata() {
        var configuration = new SAML2Configuration();
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("expired-idp-metadata.xml"));
        metadataResolver = new SAML2IdentityProviderMetadataResolver(configuration);
        metadataResolver.init();
        assertNull(metadataResolver.getEntityDescriptorElement());
    }
}
