package org.pac4j.saml.logout.impl;

import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.saml.context.SAML2MessageContext;
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

    /**
     * <p>Constructor for SAML2LogoutRequestMessageSender.</p>
     *
     * @param signatureSigningParametersProvider a {@link SignatureSigningParametersProvider} object
     * @param destinationBindingType a {@link String} object
     * @param signErrorResponses a boolean
     * @param isRequestSigned a boolean
     */
    public SAML2LogoutRequestMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                           final String destinationBindingType,
                                           final boolean signErrorResponses,
                                           final boolean isRequestSigned) {
        super(signatureSigningParametersProvider, destinationBindingType, signErrorResponses, isRequestSigned);
    }

    /** {@inheritDoc} */
    @Override
    protected Endpoint getEndpoint(final SAML2MessageContext context) {
        return context.getIDPSingleLogoutService(destinationBindingType);
    }
}
