package org.pac4j.saml.logout.impl;

import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageSender;

/**
 * Sender for SAML logout requests.
 * 
 * @author Matthieu Taggiasco
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutRequestMessageSender extends AbstractSAML2MessageSender<LogoutRequest> {

    public SAML2LogoutRequestMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                           final String destinationBindingType,
                                           final boolean signErrorResponses,
                                           final boolean isRequestSigned) {
        super(signatureSigningParametersProvider, destinationBindingType, signErrorResponses, isRequestSigned);
    }
}
