package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This is {@link SAML2MetadataGeneratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class SAML2MetadataGeneratorTests {

    @Test
    public void verifyOperation() throws Exception {
        final SAML2MetadataGenerator generator = new SAML2MetadataGenerator("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        generator.setSignMetadata(true);
        generator.setWantAssertionSigned(true);
        generator.setAuthnRequestSigned(true);
        generator.setAssertionConsumerServiceUrl("https://www.example.org");
        generator.setEntityId("https://example.com/saml");

        final List attributes = new ArrayList<>();
        attributes.add(new SAML2ServiceProvicerRequestedAttribute("mail", "email-address"));
        generator.setRequestedAttributes(attributes);

        final SAML2ClientConfiguration configuration = new SAML2ClientConfiguration();
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);

        configuration.setServiceProviderEntityId("pac4j.org");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        final KeyStoreCredentialProvider credentialProvider = new KeyStoreCredentialProvider(configuration);
        generator.setCredentialProvider(credentialProvider);
        final MetadataResolver metadataResolver = generator.buildMetadataResolver();
        assertNotNull(metadataResolver);

        final List<KeyInfoProvider> keyInfoProviderList = new ArrayList<>();
        keyInfoProviderList.add(new RSAKeyValueProvider());
        keyInfoProviderList.add(new DSAKeyValueProvider());
        keyInfoProviderList.add(new DEREncodedKeyValueProvider());
        keyInfoProviderList.add(new InlineX509DataProvider());

        final Credential credential = credentialProvider.getCredential();
        final StaticCredentialResolver resolver = new StaticCredentialResolver(credential);
        final BasicProviderKeyInfoCredentialResolver keyInfoResolver = new BasicProviderKeyInfoCredentialResolver(keyInfoProviderList);
        final ExplicitKeySignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(resolver, keyInfoResolver);

        final SignatureValidationFilter signatureValidationFilter = new SignatureValidationFilter(trustEngine);
        signatureValidationFilter.setRequireSignedRoot(false);

        final CriteriaSet criteria = new CriteriaSet();
        criteria.add(new EntityIdCriterion(configuration.getServiceProviderEntityId()));
        final EntityDescriptor descriptor = metadataResolver.resolveSingle(criteria);
        signatureValidationFilter.filter(descriptor);
    }
}
