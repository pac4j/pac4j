package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider wrapping another trust engine provider to suppress all signature validation errors and only log them.
 *
 * @since 3.8.0
 */
public class LogOnlySignatureTrustEngineProvider implements SAML2SignatureTrustEngineProvider {

    private static final Logger log = LoggerFactory.getLogger(LogOnlySignatureTrustEngineProvider.class);

    private final SAML2SignatureTrustEngineProvider wrapped;

    public LogOnlySignatureTrustEngineProvider(final SAML2SignatureTrustEngineProvider wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public SignatureTrustEngine build() {
        return new LogOnlySignatureTrustEngine(wrapped.build());
    }
    
    private static class LogOnlySignatureTrustEngine implements TrustedCredentialTrustEngine<Signature>, SignatureTrustEngine {
        private SignatureTrustEngine wrapped;
        
        public LogOnlySignatureTrustEngine(SignatureTrustEngine wrapped) {
            log.error("SIGNATURE VALIDATION DISABLED, DO NOT USE THIS ON PRODUCTION");
            this.wrapped = wrapped;
        }
        
        @Override
        public CredentialResolver getCredentialResolver() {
            return ((TrustedCredentialTrustEngine<?>) wrapped).getCredentialResolver();
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
