package org.pac4j.saml.sso.impl;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageSender;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageSender extends AbstractSAML2MessageSender<AuthnRequest> {

    public SAML2WebSSOMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                    final String destinationBindingType,
                                    final boolean signErrorResponses,
                                    final boolean isAuthnRequestSigned) {
        super(signatureSigningParametersProvider, destinationBindingType, signErrorResponses, isAuthnRequestSigned);
    }

    @Override
    protected boolean mustSignRequest(final SPSSODescriptor spDescriptor, final IDPSSODescriptor idpssoDescriptor) {
        boolean signOutboundContext = false;
        if (this.isRequestSigned) {
            logger.debug("Requests are expected to be always signed before submission");
            signOutboundContext = true;
        } else if (spDescriptor.isAuthnRequestsSigned()) {
            logger.debug("The service provider metadata indicates that authn requests are signed");
            signOutboundContext = true;
        } else if (idpssoDescriptor.getWantAuthnRequestsSigned()) {
            logger.debug("The identity provider metadata indicates that authn requests may be signed");
            signOutboundContext = true;
        }
        return signOutboundContext;
    }

    protected Endpoint getEndpoint(final SAML2MessageContext context) {
        return context.getIDPSingleSignOnService(destinationBindingType);
    }
}
