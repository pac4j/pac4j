package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.sso.SAML2MessageReceiver;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.util.Configuration;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageReceiver implements SAML2MessageReceiver {

    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";


    private final SAML2ResponseValidator validator;

    private final CredentialProvider credentialProvider;

    public SAML2WebSSOMessageReceiver(final SAML2ResponseValidator validator,
                                      final CredentialProvider credentialProvider) {
        this.validator = validator;
        this.credentialProvider = credentialProvider;
    }

    @Override
    public Credentials receiveMessage(final SAML2MessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        context.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);
        final HTTPPostDecoder decoder = new HTTPPostDecoder();
        try {

            decoder.setHttpServletRequest(context.getProfileRequestContextInboundMessageTransportRequest().getRequest());
            decoder.setParserPool(Configuration.getParserPool());
            decoder.initialize();

            decoder.decode();

        } catch (MessageDecodingException e) {
            throw new SAMLException("Error decoding saml message", e);
        } catch (ComponentInitializationException e) {
            throw new SAMLException("Error initializing the decoder", e);
        }

        final SAML2MessageContext decodedCtx = new SAML2MessageContext(decoder.getMessageContext());
        decodedCtx.setMessage(decoder.getMessageContext().getMessage());

        final AssertionConsumerService acsService = context.getSPAssertionConsumerService();
        decodedCtx.getSAMLEndpointContext().setEndpoint(acsService);

        final EntityDescriptor metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        decodedCtx.getSAMLPeerEntityContext().setEntityId(metadata.getEntityID());
        decodedCtx.getSAMLSelfEntityContext().setEntityId(context.getSAMLSelfEntityContext().getEntityId());
        decodedCtx.getSAMLSelfEntityContext().setRole(context.getSAMLSelfEntityContext().getRole());
        decodedCtx.getProfileRequestContext().setProfileId(SAML2_WEBSSO_PROFILE_URI);

        return this.validator.validate(decodedCtx);
    }
}
