package org.pac4j.saml.crypto;

import lombok.extern.slf4j.Slf4j;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Provider wrapping another trust engine provider to suppress all signature validation errors and only log them.
 *
 * @since 3.8.0
 * @author bidou
 */
@Slf4j
public class LogOnlySignatureTrustEngineProvider implements SAML2SignatureTrustEngineProvider {

    private final SAML2SignatureTrustEngineProvider wrapped;

    /**
     * <p>Constructor for LogOnlySignatureTrustEngineProvider.</p>
     *
     * @param wrapped a {@link SAML2SignatureTrustEngineProvider} object
     */
    public LogOnlySignatureTrustEngineProvider(final SAML2SignatureTrustEngineProvider wrapped) {
        this.wrapped = wrapped;
    }

    /** {@inheritDoc} */
    @Override
    public SignatureTrustEngine build() {
        return new LogOnlySignatureTrustEngine(wrapped.build());
    }

    private static class LogOnlySignatureTrustEngine implements TrustedCredentialTrustEngine<Signature>, SignatureTrustEngine {
        private final SignatureTrustEngine wrapped;

        public LogOnlySignatureTrustEngine(final SignatureTrustEngine wrapped) {
            LOGGER.error("SIGNATURE VALIDATION DISABLED, DO NOT USE THIS ON PRODUCTION");
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
        public boolean validate(final Signature token, final CriteriaSet trustBasisCriteria) throws SecurityException {
            try {
                if (!wrapped.validate(token, trustBasisCriteria)) {
                    LOGGER.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria);
                }
            } catch (final SecurityException e) {
                LOGGER.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria + ", cause: "
                        + e.getMessage(), e);
            }
            return true;
        }


        @Override
        public boolean validate(final byte[] signature, final byte[] content, final String algorithmURI,
                                final CriteriaSet trustBasisCriteria,
                                final Credential candidateCredential) throws SecurityException {
            try {
                if (!wrapped.validate(signature, content, algorithmURI, trustBasisCriteria, candidateCredential)) {
                    LOGGER.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria);
                }
            } catch (final SecurityException e) {
                LOGGER.error("Signature validation failed, continuing anyway. Criteria: " + trustBasisCriteria + ", cause: "
                        + e.getMessage(), e);
            }
            return true;
        }
    }
}
