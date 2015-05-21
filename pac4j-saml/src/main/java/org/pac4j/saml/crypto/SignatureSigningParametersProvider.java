package org.pac4j.saml.crypto;

import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.xmlsec.SignatureSigningParameters;

/**
 * @author Misagh Moayyed
 */
public interface SignatureSigningParametersProvider {

    SignatureSigningParameters build(final SSODescriptor descriptor);
}
