package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.util.SAML2Utils;

/**
 * Provider returning well configured {@link SignatureTrustEngine} instances.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class ExplicitSignatureTrustEngineProvider implements SAML2SignatureTrustEngineProvider {

    private final SAML2MetadataResolver idpMetadataResolver;

    private final SAML2MetadataResolver spMetadataResolver;

    public ExplicitSignatureTrustEngineProvider(final SAML2MetadataResolver idpMetadataResolver,
                                                final SAML2MetadataResolver spMetadataResolver) {
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
    }

    @Override
    public SignatureTrustEngine build() {
        final var metadataCredentialResolver = new MetadataCredentialResolver();
        final MetadataResolver metadataResolver = SAML2Utils.buildChainingMetadataResolver(idpMetadataResolver, spMetadataResolver);
        final var roleResolver = new PredicateRoleDescriptorResolver(metadataResolver);

        final var keyResolver =
                DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();

        metadataCredentialResolver.setKeyInfoCredentialResolver(keyResolver);
        metadataCredentialResolver.setRoleDescriptorResolver(roleResolver);

        try {
            metadataCredentialResolver.initialize();
            roleResolver.initialize();
        } catch (final ComponentInitializationException e) {
            throw new SAMLException(e);
        }

        return new ExplicitKeySignatureTrustEngine(metadataCredentialResolver, keyResolver);
    }
}
