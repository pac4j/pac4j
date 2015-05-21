package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.EndpointURLSchemeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.BaseSAML2MessageEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPRedirectDeflateEncoder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.sso.SAML2MessageSender;
import org.pac4j.saml.util.VelocityEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageSender implements SAML2MessageSender<AuthnRequest> {

    private final static Logger logger = LoggerFactory.getLogger(SAML2WebSSOProfileHandler.class);


    private final SignatureSigningParametersProvider signatureSigningParametersProvider;
    private String destinationBindingType;
    private boolean signErrorResponses;

    public SAML2WebSSOMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                    final String destinationBindingType,
                                    final boolean signErrorResponses) {
        this.signatureSigningParametersProvider = signatureSigningParametersProvider;
        this.destinationBindingType = destinationBindingType;
        this.signErrorResponses = signErrorResponses;
    }

    @Override
    public void sendMessage(final ExtendedSAMLMessageContext context,
                            final AuthnRequest authnRequest,
                            final Object relayState) {

        final SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        final IDPSSODescriptor idpssoDescriptor = context.getIDPSSODescriptor();

        final SingleSignOnService ssoService = context.getIDPSingleSignOnService(destinationBindingType);
        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();

        final MessageEncoder encoder = getMessageEncoder(context);

        final ExtendedSAMLMessageContext outboundContext = new ExtendedSAMLMessageContext(context);
        outboundContext.getProfileRequestContext().setProfileId(context.getProfileRequestContext().getProfileId());

        outboundContext.getProfileRequestContext().setInboundMessageContext(
                context.getProfileRequestContext().getInboundMessageContext());
        outboundContext.getProfileRequestContext().setOutboundMessageContext(
                context.getProfileRequestContext().getOutboundMessageContext());

        outboundContext.setMessage(authnRequest);
        outboundContext.getSAMLEndpointContext().setEndpoint(acsService);
        outboundContext.getSAMLPeerEndpointContext().setEndpoint(ssoService);

        outboundContext.getSAMLPeerEntityContext().setRole(context.getSAMLPeerEntityContext().getRole());
        outboundContext.getSAMLPeerEntityContext().setEntityId(context.getSAMLPeerEntityContext().getEntityId());
        outboundContext.getSAMLProtocolContext().setProtocol(context.getSAMLProtocolContext().getProtocol());
        outboundContext.getSecurityParametersContext()
                .setSignatureSigningParameters(this.signatureSigningParametersProvider.build(spDescriptor));

        if (relayState != null) {
            outboundContext.getSAMLBindingContext().setRelayState(relayState.toString());
        }

        invokeOutboundMessageHandlers(spDescriptor, idpssoDescriptor, outboundContext);

        try {
            encoder.setMessageContext(outboundContext);
            encoder.initialize();
            encoder.prepareContext();
            encoder.encode();
        } catch (final MessageEncodingException e) {
            throw new SAMLException("Error encoding saml message", e);
        } catch (ComponentInitializationException e) {
            throw new SAMLException("Error initializing saml encoder", e);
        }

    }

    protected void invokeOutboundMessageHandlers(final SPSSODescriptor spDescriptor,
                                                 final IDPSSODescriptor idpssoDescriptor,
                                                 final ExtendedSAMLMessageContext outboundContext) {

        try {
            final EndpointURLSchemeSecurityHandler handlerEnd =
                    new EndpointURLSchemeSecurityHandler();
            handlerEnd.initialize();
            handlerEnd.invoke(outboundContext);

            final SAMLOutboundDestinationHandler handlerDest =
                    new SAMLOutboundDestinationHandler();
            handlerDest.initialize();
            handlerDest.invoke(outboundContext);

            if (spDescriptor.isAuthnRequestsSigned()) {
                final SAMLOutboundProtocolMessageSigningHandler handler = new
                        SAMLOutboundProtocolMessageSigningHandler();
                handler.setSignErrorResponses(this.signErrorResponses);
                handler.invoke(outboundContext);

            } else if (idpssoDescriptor.getWantAuthnRequestsSigned()) {
                logger.warn("IdP wants authn requests signed, it will perhaps reject your authn requests unless you provide a keystore");
            }
        } catch (final Exception e) {
            throw new SAMLException(e);
        }

    }

    private MessageEncoder getMessageEncoder(final ExtendedSAMLMessageContext ctx) {
        // Build the WebSSO handler for sending and receiving SAML2 messages
        BaseSAML2MessageEncoder encoder;
        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {
            // Get a velocity engine for the HTTP-POST binding (building of an HTML document)
            final VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            encoder = new HTTPPostEncoder();
            ((HTTPPostEncoder) encoder).setVelocityEngine(velocityEngine);
        } else if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            encoder = new HTTPRedirectDeflateEncoder();
        } else {
            throw new UnsupportedOperationException("Binding type - " + destinationBindingType + " is not supported");
        }

        encoder.setHttpServletResponse(ctx.getProfileRequestContextOutboundMessageTransportResponse());
        return encoder;
    }
}
