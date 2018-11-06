package org.pac4j.saml.logout.impl;

import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageSender;

/**
 * Sender for SAML logout responses.
 * 
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SAML2LogoutResponseMessageSender extends AbstractSAML2MessageSender<LogoutResponse> {

    public SAML2LogoutResponseMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                            final String destinationBindingType) {
        super(signatureSigningParametersProvider, destinationBindingType, true, false);
    }

    protected Endpoint getEndpoint(final SAML2MessageContext context) {
        return context.getIDPSingleLogoutService(destinationBindingType);
    }
}
