package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.sso.SAML2MessageReceiver;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.util.Configuration;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageReceiver implements SAML2MessageReceiver {

    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    private final Pac4jHTTPPostDecoder decoder;

    private final SAML2ResponseValidator validator;

    private final CredentialProvider credentialProvider;

    public SAML2WebSSOMessageReceiver(final SAML2ResponseValidator validator,
                                      final CredentialProvider credentialProvider) {
        this.decoder = new Pac4jHTTPPostDecoder();
        this.validator = validator;
        this.credentialProvider = credentialProvider;
    }

    @Override
    public Credentials receiveMessage(final ExtendedSAMLMessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        context.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        try {
            decoder.setHttpServletRequest(context.getProfileRequestContextInboundMessageTransportRequest().getRequest());
            decoder.setParserPool(Configuration.getParserPool());
            decoder.setMessageContext(context);
            decoder.initialize();

            decoder.decode();
        } catch (MessageDecodingException e) {
            throw new SAMLException("Error decoding saml message", e);
        } catch (ComponentInitializationException e) {
            throw new SAMLException("Error initializing the decoder", e);
        }

        final EntityDescriptor metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        peerContext.setEntityId(metadata.getEntityID());
        context.getProfileRequestContext().setProfileId(SAML2_WEBSSO_PROFILE_URI);
        return this.validator.validate(context);
    }
}
