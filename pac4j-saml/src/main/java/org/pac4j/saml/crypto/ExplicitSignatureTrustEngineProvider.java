package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider returning well configured {@link SignatureTrustEngine} instances.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class ExplicitSignatureTrustEngineProvider implements SAML2SignatureTrustEngineProvider {
    
    private static final Logger log = LoggerFactory.getLogger(ExplicitSignatureTrustEngineProvider.class);
    
    private final MetadataResolver metadataResolver;
    
    private final boolean signatureValidationDisabled;

    public ExplicitSignatureTrustEngineProvider(final MetadataResolver metadataResolver) {
        this(metadataResolver, false);
    }
    
    public ExplicitSignatureTrustEngineProvider(final MetadataResolver metadataResolver, final boolean signatureValidationDisabled) {
        this.metadataResolver = metadataResolver;
        this.signatureValidationDisabled = signatureValidationDisabled;
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

        ExplicitKeySignatureTrustEngine ret = new ExplicitKeySignatureTrustEngine(metadataCredentialResolver, keyResolver);
        return signatureValidationDisabled ? new LogOnlySignatureTrustEngine(ret) : ret;
    }
    
    private static class LogOnlySignatureTrustEngine implements TrustedCredentialTrustEngine<Signature>, SignatureTrustEngine {
        private ExplicitKeySignatureTrustEngine wrapped;
        
        public LogOnlySignatureTrustEngine(ExplicitKeySignatureTrustEngine wrapped) {
            log.error("SIGNATURE VALIDATION DISABLED, DO NOT USE THIS ON PRODUCTION");
            this.wrapped = wrapped;
        }
        
        @Override
        public CredentialResolver getCredentialResolver() {
            return wrapped.getCredentialResolver();
        }
        
        @Override
        public KeyInfoCredentialResolver getKeyInfoResolver() {
            return wrapped.getKeyInfoResolver();
        }
        
        @Override
        public boolean validate(Signature token, CriteriaSet trustBasisCriteria) throws SecurityException {
            try {
                if (!wrapped.validate(token, trustBasisCriteria)) {
                    log.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria);
                }
            } catch (SecurityException e) {
                log.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria + ", cause: "
                        + e.getMessage(), e);
            }
            return true;
        }


        @Override
        public boolean validate(byte[] signature, byte[] content, String algorithmURI, CriteriaSet trustBasisCriteria,
                Credential candidateCredential) throws SecurityException {
            try {
                if (!wrapped.validate(signature, content, algorithmURI, trustBasisCriteria, candidateCredential)) {
                    log.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria);
                }
            } catch (SecurityException e) {
                log.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria + ", cause: "
                        + e.getMessage(), e);
            }
            return true;
        }
    }
}
