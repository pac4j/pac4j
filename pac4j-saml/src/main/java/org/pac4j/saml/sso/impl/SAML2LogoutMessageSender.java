package org.pac4j.saml.sso.impl;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.EndpointURLSchemeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.sso.SAML2MessageSender;
import org.pac4j.saml.storage.SAMLMessageStorage;
import org.pac4j.saml.transport.Pac4jHTTPPostEncoder;
import org.pac4j.saml.transport.Pac4jHTTPRedirectDeflateEncoder;
import org.pac4j.saml.transport.Pac4jSAMLResponse;
import org.pac4j.saml.util.VelocityEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * Sender for SAML logout messages
 * 
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */
public class SAML2LogoutMessageSender implements SAML2MessageSender<LogoutRequest> {

    private final static Logger logger = LoggerFactory.getLogger(SAML2LogoutMessageSender.class);

    private final SignatureSigningParametersProvider signatureSigningParametersProvider;
    private final String destinationBindingType;
    private final boolean signErrorResponses;
    private final boolean forceSignRedirectBindingLogoutRequest;

    public SAML2LogoutMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
            final String destinationBindingType,
            final boolean signErrorResponses,
            final boolean forceSignRedirectBindingLogoutRequest) {
        this.signatureSigningParametersProvider = signatureSigningParametersProvider;
        this.destinationBindingType = destinationBindingType;
        this.signErrorResponses = signErrorResponses;
        this.forceSignRedirectBindingLogoutRequest = forceSignRedirectBindingLogoutRequest;
    }

    @Override
    public void sendMessage(final SAML2MessageContext context,
                            final LogoutRequest logoutRequest,
                            final Object relayState) {

        final SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        final IDPSSODescriptor idpssoDescriptor = context.getIDPSSODescriptor();

        final SingleLogoutService ssoLogoutService = context.getIDPSingleLogoutService(destinationBindingType);
        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();

        final MessageEncoder encoder = getMessageEncoder(context);

        final SAML2MessageContext outboundContext = new SAML2MessageContext(context);
        outboundContext.getProfileRequestContext().setProfileId(context.getProfileRequestContext().getProfileId());

        outboundContext.getProfileRequestContext().setInboundMessageContext(
                context.getProfileRequestContext().getInboundMessageContext());
        outboundContext.getProfileRequestContext().setOutboundMessageContext(
                context.getProfileRequestContext().getOutboundMessageContext());

        outboundContext.setMessage(logoutRequest);
        outboundContext.getSAMLEndpointContext().setEndpoint(acsService);
        outboundContext.getSAMLPeerEndpointContext().setEndpoint(ssoLogoutService);

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

            final SAMLMessageStorage messageStorage = context.getSAMLMessageStorage();
            if (messageStorage != null) {
                messageStorage.storeMessage(logoutRequest.getID(), logoutRequest);
            }

        } catch (final MessageEncodingException e) {
            throw new SAMLException("Error encoding saml message", e);
        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing saml encoder", e);
        }
    }


    protected final void invokeOutboundMessageHandlers(final SPSSODescriptor spDescriptor,
                                                       final IDPSSODescriptor idpssoDescriptor,
                                                       final SAML2MessageContext outboundContext) {
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

    /**
     * Build the WebSSO handler for sending and receiving SAML2 messages.
     * @param ctx
     * @return the encoder instance
     */
    private MessageEncoder getMessageEncoder(final SAML2MessageContext ctx) {

        final Pac4jSAMLResponse adapter = ctx.getProfileRequestContextOutboundMessageTransportResponse();

        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {

            final VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            final Pac4jHTTPPostEncoder encoder = new Pac4jHTTPPostEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {

            final Pac4jHTTPRedirectDeflateEncoder encoder =
                    new Pac4jHTTPRedirectDeflateEncoder(adapter, forceSignRedirectBindingLogoutRequest);
            return encoder;
        }

        throw new UnsupportedOperationException("Binding type - "
                + destinationBindingType + " is not supported");
    }
}
