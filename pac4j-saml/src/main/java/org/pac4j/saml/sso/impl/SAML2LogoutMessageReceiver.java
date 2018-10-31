package org.pac4j.saml.sso.impl;

import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.sso.SAML2MessageReceiver;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.util.Configuration;

/**
 * Receives the SAML2 logout messages.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SAML2LogoutMessageReceiver implements SAML2MessageReceiver {

    private static final String SAML2_SLO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout";

    private final SAML2ResponseValidator validator;

    public SAML2LogoutMessageReceiver(final SAML2ResponseValidator validator) {
        this.validator = validator;
    }

    @Override
    public Credentials receiveMessage(final SAML2MessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        context.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);
        final Pac4jHTTPPostDecoder decoder = new Pac4jHTTPPostDecoder(context.getWebContext());
        try {
            decoder.setParserPool(Configuration.getParserPool());
            decoder.initialize();
            decoder.decode();
        } catch (final Exception e) {
            throw new SAMLException("Error decoding SAML message", e);
        }

        final SAML2MessageContext decodedCtx = new SAML2MessageContext(decoder.getMessageContext());
        decodedCtx.setMessage(decoder.getMessageContext().getMessage());
        decodedCtx.setSAMLMessageStorage(context.getSAMLMessageStorage());

        final SAMLBindingContext bindingContext = decodedCtx.getParent()
                .getSubcontext(SAMLBindingContext.class);

        decodedCtx.getSAMLBindingContext().setBindingDescriptor(bindingContext.getBindingDescriptor());
        decodedCtx.getSAMLBindingContext().setBindingUri(bindingContext.getBindingUri());
        decodedCtx.getSAMLBindingContext().setHasBindingSignature(bindingContext.hasBindingSignature());
        decodedCtx.getSAMLBindingContext().setIntendedDestinationEndpointURIRequired(bindingContext
            .isIntendedDestinationEndpointURIRequired());
        decodedCtx.getSAMLBindingContext().setRelayState(bindingContext.getRelayState());

        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();
        decodedCtx.getSAMLEndpointContext().setEndpoint(acsService);

        final EntityDescriptor metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        decodedCtx.getSAMLPeerEntityContext().setEntityId(metadata.getEntityID());

        decodedCtx.getSAMLSelfEntityContext().setEntityId(context.getSAMLSelfEntityContext().getEntityId());
        decodedCtx.getSAMLSelfEndpointContext().setEndpoint(context.getSAMLSelfEndpointContext().getEndpoint());
        decodedCtx.getSAMLSelfEntityContext().setRole(context.getSAMLSelfEntityContext().getRole());

        decodedCtx.getProfileRequestContext().setProfileId(SAML2_SLO_PROFILE_URI);

        decodedCtx.getSAMLSelfMetadataContext().setRoleDescriptor(context.getSPSSODescriptor());

        return this.validator.validate(decodedCtx);
    }
}
