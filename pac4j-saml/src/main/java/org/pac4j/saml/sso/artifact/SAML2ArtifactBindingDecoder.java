package org.pac4j.saml.sso.artifact;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.impl.DefaultEndpointResolver;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPArtifactDecoder;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.http.PipelineFactoryHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;

public class SAML2ArtifactBindingDecoder extends AbstractPac4jDecoder {
    private final SAML2MetadataResolver idpMetadataResolver;

    private final SAML2MetadataResolver spMetadataResolver;

    private final SOAPPipelineProvider soapPipelineProvider;

    public SAML2ArtifactBindingDecoder(final WebContext context, final SAML2MetadataResolver idpMetadataResolver,
            final SAML2MetadataResolver spMetadataResolver, final SOAPPipelineProvider soapPipelineProvider) {
        super(context);
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.soapPipelineProvider = soapPipelineProvider;
    }

    @Override
    public String getBindingURI(SAML2MessageContext messageContext) {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        try {
            DefaultEndpointResolver<ArtifactResolutionService> endpointResolver = new DefaultEndpointResolver<>();
            endpointResolver.initialize();

            PredicateRoleDescriptorResolver roleResolver = new PredicateRoleDescriptorResolver(
                    idpMetadataResolver.resolve());
            roleResolver.initialize();

            SAML2MessageContext messageContext = new SAML2MessageContext();

            PipelineFactoryHttpSOAPClient<SAMLObject, SAMLObject> soapClient = new PipelineFactoryHttpSOAPClient<SAMLObject, SAMLObject>() {
                public void send(String endpoint, InOutOperationContext operationContext)
                        throws SOAPException, SecurityException {
                    super.send(endpoint, operationContext);
                    transferContext(operationContext, messageContext);
                }
            };
            soapClient.setPipelineFactory(soapPipelineProvider.getPipelineFactory());
            soapClient.setHttpClient(soapPipelineProvider.getHttpClientBuilder().buildClient());

            HTTPArtifactDecoder artifactDecoder = new HTTPArtifactDecoder();
            artifactDecoder.setHttpServletRequest(((JEEContext) context).getNativeRequest());
            artifactDecoder.setSelfEntityIDResolver(new FixedEntityIdResolver(spMetadataResolver));
            artifactDecoder.setRoleDescriptorResolver(roleResolver);
            artifactDecoder.setArtifactEndpointResolver(endpointResolver);
            artifactDecoder.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            artifactDecoder.setSOAPClient(soapClient);
            artifactDecoder.setParserPool(getParserPool());
            artifactDecoder.initialize();
            artifactDecoder.decode();

            messageContext.setMessage(artifactDecoder.getMessageContext().getMessage());

            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext);
        } catch (Exception e) {
            throw new MessageDecodingException(e);
        }
    }

    protected void transferContext(InOutOperationContext operationContext, SAML2MessageContext messageContext) {
        messageContext
                .addSubcontext(operationContext.getInboundMessageContext().getSubcontext(SAMLPeerEntityContext.class));
    }
}
