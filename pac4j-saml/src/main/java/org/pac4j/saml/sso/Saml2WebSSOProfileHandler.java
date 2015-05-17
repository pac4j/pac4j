/*
  Copyright 2012 -2014 Michael Remond

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

package org.pac4j.saml.sso;

import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SamlException;
import org.pac4j.saml.util.SamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler capable of sending and receiving SAML messages according to the SAML2 SSO Browser profile.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("rawtypes")
public class Saml2WebSSOProfileHandler {

    private final static Logger logger = LoggerFactory.getLogger(Saml2WebSSOProfileHandler.class);

    private final CredentialProvider credentialProvider;

    private final MessageEncoder encoder;

    private final MessageDecoder decoder;

    private final ParserPool parserPool;
    
    private String destinationBindingType;

    // SAML2 SSO browser profile because not available in opensaml constants
    public static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    public Saml2WebSSOProfileHandler(final CredentialProvider credentialProvider, final MessageEncoder encoder,
            final MessageDecoder decoder, final ParserPool parserPool, String destinationBindingType) {
        this.credentialProvider = credentialProvider;
        this.encoder = encoder;
        this.decoder = decoder;
        this.parserPool = parserPool;
        this.destinationBindingType = destinationBindingType;
    }

    @SuppressWarnings("unchecked")
    public void sendMessage(final MessageContext<SAMLObject> context, final AuthnRequest authnRequest, final String relayState) {

        SAMLSelfEntityContext selfContext = context.getSubcontext(SAMLSelfEntityContext.class);
        SAMLPeerEntityContext peerContext = context.getSubcontext(SAMLPeerEntityContext.class);

        SPSSODescriptor spDescriptor = (SPSSODescriptor) selfContext.getRole();
        IDPSSODescriptor idpssoDescriptor = (IDPSSODescriptor) peerContext.getRole();
        SingleSignOnService ssoService = SamlUtils.getSingleSignOnService(idpssoDescriptor, destinationBindingType);

        ProfileRequestContext request = context.getSubcontext(ProfileRequestContext.class);
        request.setProfileId(SAML2_WEBSSO_PROFILE_URI);

        MessageContext<AuthnRequest> outboundContext = new MessageContext<AuthnRequest>();
        outboundContext.setMessage(authnRequest);
        request.setOutboundMessageContext(outboundContext);
        peerContext.getSubcontext(SAMLEndpointContext.class).setEndpoint(ssoService);

        if (relayState != null) {
            context.getSubcontext(SAMLBindingContext.class).setRelayState(relayState);
        }

        if (spDescriptor.isAuthnRequestsSigned()) {
            EncryptionParameters params = context.getSubcontext(SecurityParametersContext.class).getEncryptionParameters();
            params.setKeyTransportEncryptionCredential(credentialProvider.getCredential());
        } else if (idpssoDescriptor.getWantAuthnRequestsSigned()) {
            logger.warn("IdP wants authn requests signed, it will perhaps reject your authn requests unless you provide a keystore");
        }

        try {
            encoder.setMessageContext(context);
            encoder.encode();
        } catch (MessageEncodingException e) {
            throw new SamlException("Error encoding saml message", e);
        }

    }

    public void receiveMessage(final MessageContext<SAMLObject> context, final SignatureTrustEngine engine) {

        SAMLSelfEntityContext selfContext = context.getSubcontext(SAMLSelfEntityContext.class);
        SAMLPeerEntityContext peerContext = context.getSubcontext(SAMLPeerEntityContext.class);

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        selfContext.getSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS);


        try {
            BasicKeyInfoGeneratorFactory factory = new BasicKeyInfoGeneratorFactory();
            KeyInfoGenerator keyInfoGenerator = factory.newInstance();
            SAMLMessageSecuritySupport.getContextSigningParameters(context).setKeyInfoGenerator(keyInfoGenerator);
            decoder.decode();
        } catch (MessageDecodingException e) {
            throw new SamlException("Error decoding saml message", e);
        }

        final EntityDescriptor metadata = peerContext.getSubcontext(SAMLMetadataContext.class).getEntityDescriptor();
        if (metadata == null) {
            throw new SamlException("IDP Metadata cannot be null");
        }

        peerContext.setEntityId(metadata.getEntityID());
        context.getSubcontext(ProfileRequestContext.class).setProfileId(SAML2_WEBSSO_PROFILE_URI);
    }

}
