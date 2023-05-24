package org.pac4j.saml.sso.impl;

import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageSender;

/**
 * <p>SAML2WebSSOMessageSender class.</p>
 *
 * @author Misagh Moayyed
 */
@Slf4j
public class SAML2WebSSOMessageSender extends AbstractSAML2MessageSender<AuthnRequest> {

    /**
     * <p>Constructor for SAML2WebSSOMessageSender.</p>
     *
     * @param signatureSigningParametersProvider a {@link SignatureSigningParametersProvider} object
     * @param destinationBindingType a {@link String} object
     * @param signErrorResponses a boolean
     * @param isAuthnRequestSigned a boolean
     */
    public SAML2WebSSOMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                    final String destinationBindingType,
                                    final boolean signErrorResponses,
                                    final boolean isAuthnRequestSigned) {
        super(signatureSigningParametersProvider, destinationBindingType, signErrorResponses, isAuthnRequestSigned);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean mustSignRequest(final SPSSODescriptor spDescriptor, final IDPSSODescriptor idpssoDescriptor) {
        var signOutboundContext = false;
        if (this.isRequestSigned) {
            LOGGER.debug("Requests are expected to be always signed before submission");
            signOutboundContext = true;
        } else if (spDescriptor.isAuthnRequestsSigned()) {
            LOGGER.debug("The service provider metadata indicates that authn requests are signed");
            signOutboundContext = true;
        } else if (idpssoDescriptor.getWantAuthnRequestsSigned()) {
            LOGGER.debug("The identity provider metadata indicates that authn requests may be signed");
            signOutboundContext = true;
        }
        return signOutboundContext;
    }

    /** {@inheritDoc} */
    @Override
    protected Endpoint getEndpoint(final SAML2MessageContext context) {
        return context.getIDPSingleSignOnService(destinationBindingType);
    }
}
