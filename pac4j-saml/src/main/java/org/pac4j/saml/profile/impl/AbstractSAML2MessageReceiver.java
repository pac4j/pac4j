package org.pac4j.saml.profile.impl;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2MessageReceiver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.transport.AbstractPac4jDecoder;

import java.util.Optional;

/**
 * Receives the SAML2 messages.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2MessageReceiver implements SAML2MessageReceiver {

    protected SAML2ResponseValidator validator;

    public AbstractSAML2MessageReceiver(final SAML2ResponseValidator validator) {
        this.validator = validator;
    }

    @Override
    public Credentials receiveMessage(final SAML2MessageContext context) {
        final var peerContext = context.getSAMLPeerEntityContext();
        final var webContext = context.getWebContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        context.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        final var decoder = getDecoder(webContext);

        final var decodedCtx = prepareDecodedContext(context, decoder);

        return this.validator.validate(decodedCtx);
    }

    protected SAML2MessageContext prepareDecodedContext(final SAML2MessageContext context, final AbstractPac4jDecoder decoder) {
        final var decodedCtx = new SAML2MessageContext();
        decodedCtx.setMessageContext(decoder.getMessageContext());
        final var message = (SAMLObject) decoder.getMessageContext().getMessage();
        if (message == null) {
            throw new SAMLException("Response from the context cannot be null");
        }
        decodedCtx.getMessageContext().setMessage(message);
        context.getMessageContext().setMessage(message);
        decodedCtx.setSAMLMessageStore(context.getSAMLMessageStore());

        final var bindingContext = prepareBindingContext(context, decoder, decodedCtx);

        if (decodedCtx.getMessageContext().getMessage() instanceof StatusResponseType) {
            final var response = (StatusResponseType) decodedCtx.getMessageContext().getMessage();
            getEndpoint(context, response).ifPresent(e -> decodedCtx.getSAMLEndpointContext().setEndpoint(e));
        }

        final var metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        preparePeerEntityContext(decoder, decodedCtx, bindingContext, metadata);
        prepareSelfEntityContext(context, decodedCtx);

        decodedCtx.getProfileRequestContext().setProfileId(getProfileUri());
        decodedCtx.getSAMLSelfMetadataContext().setRoleDescriptor(context.getSPSSODescriptor());
        decodedCtx.setWebContext(context.getWebContext());
        decodedCtx.setSessionStore(context.getSessionStore());
        return decodedCtx;
    }

    protected void prepareSelfEntityContext(final SAML2MessageContext context, final SAML2MessageContext decodedCtx) {
        decodedCtx.getSAMLSelfEntityContext().setEntityId(context.getSAMLSelfEntityContext().getEntityId());
        decodedCtx.getSAMLSelfEndpointContext().setEndpoint(context.getSAMLSelfEndpointContext().getEndpoint());
        decodedCtx.getSAMLSelfEntityContext().setRole(context.getSAMLSelfEntityContext().getRole());
    }

    protected void preparePeerEntityContext(final AbstractPac4jDecoder decoder,
                                            final SAML2MessageContext decodedCtx,
                                            final SAMLBindingContext bindingContext,
                                            final EntityDescriptor metadata) {
        final var decodedPeerContext = decoder.getMessageContext().getSubcontext(SAMLPeerEntityContext.class);
        CommonHelper.assertNotNull("SAMLPeerEntityContext", bindingContext);

        decodedCtx.getSAMLPeerEntityContext().setEntityId(metadata.getEntityID());
        decodedCtx.getSAMLPeerEntityContext().setAuthenticated(decodedPeerContext != null && decodedPeerContext.isAuthenticated());
    }

    protected SAMLBindingContext prepareBindingContext(final SAML2MessageContext context,
                                                       final AbstractPac4jDecoder decoder,
                                                       final SAML2MessageContext decodedCtx) {
        final var bindingContext = decoder.getMessageContext().getSubcontext(SAMLBindingContext.class);
        CommonHelper.assertNotNull("SAMLBindingContext", bindingContext);
        decodedCtx.getSAMLBindingContext().setBindingDescriptor(bindingContext.getBindingDescriptor());
        decodedCtx.getSAMLBindingContext().setBindingUri(bindingContext.getBindingUri());
        decodedCtx.getSAMLBindingContext().setHasBindingSignature(bindingContext.hasBindingSignature());
        decodedCtx.getSAMLBindingContext().setIntendedDestinationEndpointURIRequired(bindingContext
            .isIntendedDestinationEndpointURIRequired());
        final var relayState = bindingContext.getRelayState();
        decodedCtx.getSAMLBindingContext().setRelayState(relayState);
        context.getSAMLBindingContext().setRelayState(relayState);
        return bindingContext;
    }

    public void setValidator(final SAML2ResponseValidator validator) {
        this.validator = validator;
    }

    protected abstract Optional<Endpoint> getEndpoint(SAML2MessageContext context, StatusResponseType response);

    protected abstract AbstractPac4jDecoder getDecoder(WebContext webContext);

    protected abstract String getProfileUri();
}
