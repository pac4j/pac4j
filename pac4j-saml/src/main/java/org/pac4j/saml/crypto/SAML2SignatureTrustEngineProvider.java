package org.pac4j.saml.crypto;

import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Builds the signature trust engine.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
@FunctionalInterface
public interface SAML2SignatureTrustEngineProvider {
    /**
     * <p>build.</p>
     *
     * @return a {@link SignatureTrustEngine} object
     */
    SignatureTrustEngine build();
}
