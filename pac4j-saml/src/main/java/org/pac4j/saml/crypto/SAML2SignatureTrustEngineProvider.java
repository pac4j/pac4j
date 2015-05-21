package org.pac4j.saml.crypto;

import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Builds the signature trust engine.
 * @author Misagh Moayyed
 */
public interface SAML2SignatureTrustEngineProvider {
    SignatureTrustEngine build();
}
