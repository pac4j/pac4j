package org.pac4j.saml.crypto;

import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.xmlsec.SignatureSigningParameters;

/**
 * Provider to allow building of signature parameters.
 * @author Misagh Moayyed
 * @since 1.7
 */
@FunctionalInterface
public interface SignatureSigningParametersProvider {

    SignatureSigningParameters build(final SSODescriptor descriptor);
}
