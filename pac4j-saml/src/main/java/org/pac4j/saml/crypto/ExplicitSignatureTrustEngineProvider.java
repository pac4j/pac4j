package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.pac4j.saml.exceptions.SAMLException;

/**
 * Provider returning well configured {@link SignatureTrustEngine} instances.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class ExplicitSignatureTrustEngineProvider implements SAML2SignatureTrustEngineProvider {

    private final MetadataResolver metadataResolver;

    public ExplicitSignatureTrustEngineProvider(final MetadataResolver metadataResolver) {
        this.metadataResolver = metadataResolver;
    }

    @Override
    public SignatureTrustEngine build() {
        final MetadataCredentialResolver metadataCredentialResolver = new MetadataCredentialResolver();
        final PredicateRoleDescriptorResolver roleResolver = new PredicateRoleDescriptorResolver(metadataResolver);

        final KeyInfoCredentialResolver keyResolver =
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
