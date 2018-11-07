package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.EndpointURLSchemeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
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

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageSender implements SAML2MessageSender<AuthnRequest> {

    private static final Logger logger = LoggerFactory.getLogger(SAML2WebSSOProfileHandler.class);

    private final SignatureSigningParametersProvider signatureSigningParametersProvider;
    private final String destinationBindingType;
    private final boolean isAuthnRequestSigned;

    public SAML2WebSSOMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                    final String destinationBindingType,
                                    final boolean isAuthnRequestSigned) {
        this.signatureSigningParametersProvider = signatureSigningParametersProvider;
        this.destinationBindingType = destinationBindingType;
        this.isAuthnRequestSigned = isAuthnRequestSigned;
    }

    @Override
    public void sendMessage(final SAML2MessageContext context,
                            final AuthnRequest authnRequest,
                            final Object relayState) {

        final SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        final IDPSSODescriptor idpssoDescriptor = context.getIDPSSODescriptor();

        final SingleSignOnService ssoService = context.getIDPSingleSignOnService(destinationBindingType);
        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();

        final MessageEncoder encoder = getMessageEncoder(context);

        final SAML2MessageContext outboundContext = new SAML2MessageContext(context);
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

        try {
            invokeOutboundMessageHandlers(spDescriptor, idpssoDescriptor, outboundContext);

            encoder.setMessageContext(outboundContext);
            encoder.initialize();
            encoder.prepareContext();
            encoder.encode();

            final SAMLMessageStorage messageStorage = context.getSAMLMessageStorage();
            if (messageStorage != null) {
                messageStorage.storeMessage(authnRequest.getID(), authnRequest);
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

            boolean signOutboundContext = false;

            if (this.isAuthnRequestSigned) {
                logger.debug("Authn requests are expected to be always signed before submission");
                signOutboundContext = true;
            } else if (spDescriptor.isAuthnRequestsSigned()) {
                logger.debug("The service provider metadata indicates that authn requests are signed");
                signOutboundContext = true;
            } else if (idpssoDescriptor.getWantAuthnRequestsSigned()) {
                logger.debug("The identity provider metadata indicates that authn requests may be signed");
                signOutboundContext = true;
            }

            if (signOutboundContext) {
                logger.debug("Signing SAML2 outbound context...");
                final SAMLOutboundProtocolMessageSigningHandler handler = new
                    SAMLOutboundProtocolMessageSigningHandler();
                handler.invoke(outboundContext);
            }
        } catch (final Exception e) {
            throw new SAMLException(e);
        }

    }

    /**
     * Build the WebSSO handler for sending and receiving SAML2 messages.
     *
     * @param ctx the ctx
     * @return the encoder instance
     */
    private MessageEncoder getMessageEncoder(final SAML2MessageContext ctx) {

        final Pac4jSAMLResponse adapter = ctx.getProfileRequestContextOutboundMessageTransportResponse();

        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {

            final VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            final Pac4jHTTPPostEncoder encoder = new Pac4jHTTPPostEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;
        }

        if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            return new Pac4jHTTPRedirectDeflateEncoder(adapter, isAuthnRequestSigned);
        }

        throw new UnsupportedOperationException("Binding type - "
            + destinationBindingType + " is not supported");
    }
}
