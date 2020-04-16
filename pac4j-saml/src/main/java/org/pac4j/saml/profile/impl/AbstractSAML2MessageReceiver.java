package org.pac4j.saml.profile.impl;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
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

/**
 * Receives the SAML2 messages.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2MessageReceiver implements SAML2MessageReceiver {

    protected final SAML2ResponseValidator validator;

    public AbstractSAML2MessageReceiver(final SAML2ResponseValidator validator) {
        this.validator = validator;
    }

    @Override
    public Credentials receiveMessage(final SAML2MessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();
        final WebContext webContext = context.getWebContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        context.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        final AbstractPac4jDecoder decoder = getDecoder(webContext);

        final SAML2MessageContext decodedCtx = new SAML2MessageContext();
        decodedCtx.setMessageContext(decoder.getMessageContext());
        final SAMLObject message = (SAMLObject) decoder.getMessageContext().getMessage();
        decodedCtx.getMessageContext().setMessage(message);
        context.getMessageContext().setMessage(message);
        decodedCtx.setSAMLMessageStore(context.getSAMLMessageStore());

        final SAMLBindingContext bindingContext = decoder.getMessageContext().getSubcontext(SAMLBindingContext.class);
        CommonHelper.assertNotNull("SAMLBindingContext", bindingContext);
        decodedCtx.getSAMLBindingContext().setBindingDescriptor(bindingContext.getBindingDescriptor());
        decodedCtx.getSAMLBindingContext().setBindingUri(bindingContext.getBindingUri());
        decodedCtx.getSAMLBindingContext().setHasBindingSignature(bindingContext.hasBindingSignature());
        decodedCtx.getSAMLBindingContext().setIntendedDestinationEndpointURIRequired(bindingContext
            .isIntendedDestinationEndpointURIRequired());
        final String relayState = bindingContext.getRelayState();
        decodedCtx.getSAMLBindingContext().setRelayState(relayState);
        context.getSAMLBindingContext().setRelayState(relayState);

        final StatusResponseType response = (StatusResponseType) decodedCtx.getMessageContext().getMessage();
        if (response == null) {
            throw new SAMLException("Response from the context cannot be null");
        }
        final AssertionConsumerService acsService = context.getSPAssertionConsumerService(response);
        decodedCtx.getSAMLEndpointContext().setEndpoint(acsService);

        final EntityDescriptor metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        final SAMLPeerEntityContext decodedPeerContext = decoder.getMessageContext().getSubcontext(SAMLPeerEntityContext.class);
        CommonHelper.assertNotNull("SAMLPeerEntityContext", bindingContext);

        decodedCtx.getSAMLPeerEntityContext().setEntityId(metadata.getEntityID());
        decodedCtx.getSAMLPeerEntityContext().setAuthenticated(decodedPeerContext != null && decodedPeerContext.isAuthenticated());

        decodedCtx.getSAMLSelfEntityContext().setEntityId(context.getSAMLSelfEntityContext().getEntityId());
        decodedCtx.getSAMLSelfEndpointContext().setEndpoint(context.getSAMLSelfEndpointContext().getEndpoint());
        decodedCtx.getSAMLSelfEntityContext().setRole(context.getSAMLSelfEntityContext().getRole());

        decodedCtx.getProfileRequestContext().setProfileId(getProfileUri());

        decodedCtx.getSAMLSelfMetadataContext().setRoleDescriptor(context.getSPSSODescriptor());

        decodedCtx.setWebContext(context.getWebContext());

        return this.validator.validate(decodedCtx);
    }

    protected abstract AbstractPac4jDecoder getDecoder(WebContext webContext);

    protected abstract String getProfileUri();
}
