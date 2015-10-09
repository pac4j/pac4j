/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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

    private final static Logger logger = LoggerFactory.getLogger(SAML2WebSSOProfileHandler.class);


    private final SignatureSigningParametersProvider signatureSigningParametersProvider;
    private final String destinationBindingType;
    private final boolean signErrorResponses;

    public SAML2WebSSOMessageSender(final SignatureSigningParametersProvider signatureSigningParametersProvider,
                                    final String destinationBindingType,
                                    final boolean signErrorResponses) {
        this.signatureSigningParametersProvider = signatureSigningParametersProvider;
        this.destinationBindingType = destinationBindingType;
        this.signErrorResponses = signErrorResponses;
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

        invokeOutboundMessageHandlers(spDescriptor, idpssoDescriptor, outboundContext);

        try {
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
            final Pac4jHTTPPostEncoder encoder = new Pac4jHTTPPostEncoder(ctx.getWebContext(), adapter);
            encoder.setVelocityEngine(velocityEngine);
            return encoder;
        }

        if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            final Pac4jHTTPRedirectDeflateEncoder encoder =
                    new Pac4jHTTPRedirectDeflateEncoder(ctx.getWebContext(), adapter);
            return encoder;
        }

        throw new UnsupportedOperationException("Binding type - "
                + destinationBindingType + " is not supported");

    }
}
