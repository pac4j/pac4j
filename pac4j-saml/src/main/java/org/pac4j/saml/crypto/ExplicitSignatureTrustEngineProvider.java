package org.pac4j.saml.crypto;

import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
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

    /**
     * <p>Constructor for ExplicitSignatureTrustEngineProvider.</p>
     *
     * @param idpMetadataResolver a {@link SAML2MetadataResolver} object
     * @param spMetadataResolver a {@link SAML2MetadataResolver} object
     */
    public ExplicitSignatureTrustEngineProvider(final SAML2MetadataResolver idpMetadataResolver,
                                                final SAML2MetadataResolver spMetadataResolver) {
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
    }

    /** {@inheritDoc} */
    @Override
    public SignatureTrustEngine build() {
        val metadataCredentialResolver = new MetadataCredentialResolver();
        final MetadataResolver metadataResolver = SAML2Utils.buildChainingMetadataResolver(idpMetadataResolver, spMetadataResolver);
        val roleResolver = new PredicateRoleDescriptorResolver(metadataResolver);

        val keyResolver =
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
