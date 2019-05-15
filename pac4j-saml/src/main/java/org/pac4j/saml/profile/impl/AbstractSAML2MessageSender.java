package org.pac4j.saml.profile.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.EndpointURLSchemeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2MessageSender;
import org.pac4j.saml.store.SAMLMessageStore;
import org.pac4j.saml.transport.Pac4jHTTPPostEncoder;
import org.pac4j.saml.transport.Pac4jHTTPPostSimpleSignEncoder;
import org.pac4j.saml.transport.Pac4jHTTPRedirectDeflateEncoder;
import org.pac4j.saml.transport.Pac4jSAMLResponse;
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

        final SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        final IDPSSODescriptor idpssoDescriptor = context.getIDPSSODescriptor();

        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();

        final MessageEncoder encoder = getMessageEncoder(context);

        final SAML2MessageContext outboundContext = new SAML2MessageContext(context);
        outboundContext.getProfileRequestContext().setProfileId(context.getProfileRequestContext().getProfileId());

        outboundContext.getProfileRequestContext().setInboundMessageContext(
            context.getProfileRequestContext().getInboundMessageContext());
        outboundContext.getProfileRequestContext().setOutboundMessageContext(
            context.getProfileRequestContext().getOutboundMessageContext());

        outboundContext.setMessage(request);
        outboundContext.getSAMLEndpointContext().setEndpoint(acsService);
        outboundContext.getSAMLPeerEndpointContext().setEndpoint(getEndpoint(context));

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

            final SAMLMessageStore messageStorage = context.getSAMLMessageStore();
            if (messageStorage != null) {
                if (request instanceof RequestAbstractType) {
                    messageStorage.set(((RequestAbstractType) request).getID(), request);
                } else if (request instanceof StatusResponseType) {
                    messageStorage.set(((StatusResponseType) request).getID(), request);
                }
            }

        } catch (final MessageEncodingException e) {
            throw new SAMLException("Error encoding saml message", e);
        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing saml encoder", e);
        }
    }

    protected abstract Endpoint getEndpoint(SAML2MessageContext context);

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

            if (mustSignRequest(spDescriptor, idpssoDescriptor)) {
                logger.debug("Signing SAML2 outbound context...");
                final SAMLOutboundProtocolMessageSigningHandler handler = new
                    SAMLOutboundProtocolMessageSigningHandler();
                handler.setSignErrorResponses(this.signErrorResponses);
                handler.invoke(outboundContext);
            }
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    protected boolean mustSignRequest(final SPSSODescriptor spDescriptor, final IDPSSODescriptor idpssoDescriptor) {
        return isRequestSigned;
    }

    private MessageEncoder getMessageEncoder(final SAML2MessageContext ctx) {

        final Pac4jSAMLResponse adapter = ctx.getProfileRequestContextOutboundMessageTransportResponse();

        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {
            final VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            final Pac4jHTTPPostEncoder encoder = new Pac4jHTTPPostEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(destinationBindingType)) {
            final VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            final Pac4jHTTPPostSimpleSignEncoder encoder = new Pac4jHTTPPostSimpleSignEncoder(adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;

        } else if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            return new Pac4jHTTPRedirectDeflateEncoder(adapter, this.isRequestSigned);

        } else {
            throw new UnsupportedOperationException("Binding type - "
                + destinationBindingType + " is not supported");
        }
    }
}
