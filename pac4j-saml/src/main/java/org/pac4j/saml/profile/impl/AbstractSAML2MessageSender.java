package org.pac4j.saml.profile.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
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
    protected final Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");
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

        final var spDescriptor = context.getSPSSODescriptor();
        final var idpssoDescriptor = context.getIDPSSODescriptor();

        final var acsService = context.getSPAssertionConsumerService();

        final var encoder = getMessageEncoder(spDescriptor, idpssoDescriptor, context);

        final var outboundContext = new SAML2MessageContext();
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
            final var messageContext = outboundContext.getMessageContext();
            invokeOutboundMessageHandlers(spDescriptor, idpssoDescriptor, messageContext);

            encoder.setMessageContext(messageContext);
            encoder.initialize();
            encoder.prepareContext();
            encoder.encode();

            storeMessage(context, request);
            logProtocolMessage(request);
        } catch (final MarshallingException e) {
            throw new SAMLException("Error marshalling saml message", e);
        } catch (final MessageEncodingException e) {
            throw new SAMLException("Error encoding saml message", e);
        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing saml encoder", e);
        }
    }

    protected void storeMessage(final SAML2MessageContext context, final T request) {
        final var messageStorage = context.getSAMLMessageStore();
        if (messageStorage != null) {
            if (request instanceof RequestAbstractType) {
                messageStorage.set(((RequestAbstractType) request).getID(), request);
            } else if (request instanceof StatusResponseType) {
                messageStorage.set(((StatusResponseType) request).getID(), request);
            }
        }
    }

    protected void logProtocolMessage(final XMLObject object) throws MarshallingException {
        if (protocolMessageLog.isDebugEnabled()) {
            final var requestXml = SerializeSupport.nodeToString(XMLObjectSupport.marshall(object));
            protocolMessageLog.debug(requestXml);
        }
    }

    protected abstract Endpoint getEndpoint(SAML2MessageContext context);

    protected void invokeOutboundMessageHandlers(final SPSSODescriptor spDescriptor,
                                                       final IDPSSODescriptor idpssoDescriptor,
                                                       final MessageContext messageContext) {
        try {
            final var handlerEnd =
                new EndpointURLSchemeSecurityHandler();
            handlerEnd.initialize();
            handlerEnd.invoke(messageContext);

            final var handlerDest =
                new SAMLOutboundDestinationHandler();
            handlerDest.initialize();
            handlerDest.invoke(messageContext);

            if (!destinationBindingType.equals(SAMLConstants.SAML2_REDIRECT_BINDING_URI) &&
                    mustSignRequest(spDescriptor, idpssoDescriptor)) {
                logger.debug("Signing SAML2 outbound context...");
                final var handler = new
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

        final var adapter = ctx.getProfileRequestContextOutboundMessageTransportResponse();

        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {
            final var velocityEngine = VelocityEngineFactory.getEngine();
            final var encoder = new Pac4jHTTPPostEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(destinationBindingType)) {
            final var velocityEngine = VelocityEngineFactory.getEngine();
            final var encoder = new Pac4jHTTPPostSimpleSignEncoder(adapter);
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
