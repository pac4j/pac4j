package org.pac4j.saml.profile.impl;

import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.EndpointURLSchemeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2MessageSender;
import org.pac4j.saml.transport.Pac4jHTTPPostEncoder;
import org.pac4j.saml.transport.Pac4jHTTPPostSimpleSignEncoder;
import org.pac4j.saml.transport.Pac4jHTTPRedirectDeflateEncoder;
import org.pac4j.saml.util.SAML2Utils;
import org.pac4j.saml.util.VelocityEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common message sender.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2MessageSender<T extends SAMLObject> implements SAML2MessageSender<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SignatureSigningParametersProvider signatureSigningParametersProvider;
    protected final String destinationBindingType;
    protected final boolean signErrorResponses;
    protected final boolean isRequestSigned;

    public AbstractSAML2MessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                      final String destinationBindingType,
                                      final boolean signErrorResponses,
                                      final boolean isRequestSigned) {
        this.signatureSigningParametersProvider = signatureSigningParametersProvider;
        this.destinationBindingType = destinationBindingType;
        this.signErrorResponses = signErrorResponses;
        this.isRequestSigned = isRequestSigned;
    }

    @Override
    public void sendMessage(final SAML2MessageContext context,
                            final T request,
                            final Object relayState) {

        val spDescriptor = context.getSPSSODescriptor();
        val idpssoDescriptor = context.getIDPSSODescriptor();

        val acsService = context.getSPAssertionConsumerService();

        val encoder = getMessageEncoder(spDescriptor, idpssoDescriptor, context);

        val outboundContext = new SAML2MessageContext();
        outboundContext.setMessageContext(context.getMessageContext());
        outboundContext.getProfileRequestContext().setProfileId(outboundContext.getProfileRequestContext().getProfileId());

        outboundContext.getProfileRequestContext().setInboundMessageContext(
            context.getProfileRequestContext().getInboundMessageContext());
        outboundContext.getProfileRequestContext().setOutboundMessageContext(
            context.getProfileRequestContext().getOutboundMessageContext());

        outboundContext.getMessageContext().setMessage(request);
        outboundContext.getSAMLEndpointContext().setEndpoint(acsService);
        outboundContext.getSAMLPeerEndpointContext().setEndpoint(getEndpoint(outboundContext));

        outboundContext.getSAMLPeerEntityContext().setRole(outboundContext.getSAMLPeerEntityContext().getRole());
        outboundContext.getSAMLPeerEntityContext().setEntityId(outboundContext.getSAMLPeerEntityContext().getEntityId());
        outboundContext.getSAMLProtocolContext().setProtocol(outboundContext.getSAMLProtocolContext().getProtocol());
        outboundContext.getSecurityParametersContext()
            .setSignatureSigningParameters(this.signatureSigningParametersProvider.build(spDescriptor));

        if (relayState != null) {
            outboundContext.getSAMLBindingContext().setRelayState(relayState.toString());
        }

        try {
            val messageContext = outboundContext.getMessageContext();
            invokeOutboundMessageHandlers(spDescriptor, idpssoDescriptor, messageContext);

            encoder.setMessageContext(messageContext);
            encoder.initialize();
            encoder.prepareContext();
            encoder.encode();

            storeMessage(context, request);
            SAML2Utils.logProtocolMessage(request);
        } catch (final MessageEncodingException e) {
            throw new SAMLException("Error encoding saml message", e);
        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing saml encoder", e);
        }
    }

    protected void storeMessage(final SAML2MessageContext context, final T request) {
        val messageStorage = context.getSamlMessageStore();
        if (messageStorage != null) {
            if (request instanceof RequestAbstractType requestAbstractType) {
                messageStorage.set(requestAbstractType.getID(), request);
            } else if (request instanceof StatusResponseType statusResponseType) {
                messageStorage.set(statusResponseType.getID(), request);
            }
        }
    }

    protected abstract Endpoint getEndpoint(SAML2MessageContext context);

    protected void invokeOutboundMessageHandlers(final SPSSODescriptor spDescriptor,
                                                       final IDPSSODescriptor idpssoDescriptor,
                                                       final MessageContext messageContext) {
        try {
            val handlerEnd =
                new EndpointURLSchemeSecurityHandler();
            handlerEnd.initialize();
            handlerEnd.invoke(messageContext);

            val handlerDest =
                new SAMLOutboundDestinationHandler();
            handlerDest.initialize();
            handlerDest.invoke(messageContext);

            if (!destinationBindingType.equals(SAMLConstants.SAML2_REDIRECT_BINDING_URI) &&
                    mustSignRequest(spDescriptor, idpssoDescriptor)) {
                logger.debug("Signing SAML2 outbound context...");
                val handler = new
                    SAMLOutboundProtocolMessageSigningHandler();
                handler.setSignErrorResponses(this.signErrorResponses);
                handler.invoke(messageContext);
            }
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    protected boolean mustSignRequest(final SPSSODescriptor spDescriptor, final IDPSSODescriptor idpssoDescriptor) {
        return isRequestSigned;
    }

    private MessageEncoder getMessageEncoder(final SPSSODescriptor spDescriptor,
            final IDPSSODescriptor idpssoDescriptor,
            final SAML2MessageContext ctx) {

        val adapter = ctx.getProfileRequestContextOutboundMessageTransportResponse();

        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {
            val velocityEngine = VelocityEngineFactory.getEngine();
            val encoder = new Pac4jHTTPPostEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(destinationBindingType)) {
            val velocityEngine = VelocityEngineFactory.getEngine();
            val encoder = new Pac4jHTTPPostSimpleSignEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            return new Pac4jHTTPRedirectDeflateEncoder(adapter, mustSignRequest(spDescriptor, idpssoDescriptor));

        } else {
            throw new UnsupportedOperationException("Binding type - "
                + destinationBindingType + " is not supported");
        }
    }
}
