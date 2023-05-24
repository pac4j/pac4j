package org.pac4j.saml.sso.artifact;

import lombok.val;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.impl.DefaultEndpointResolver;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.http.PipelineFactoryHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.pac4j.core.context.CallContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPArtifactDecoder;

import java.util.Objects;

/**
 * Decodes a SAML artifact binding request by fetching the actual artifact via
 * SOAP.
 *
 * @since 3.8.0
 * @author bidou
 */
public class SAML2ArtifactBindingDecoder extends AbstractPac4jDecoder {
    private final SAML2MetadataResolver idpMetadataResolver;

    private final SAML2MetadataResolver spMetadataResolver;

    private final SOAPPipelineProvider soapPipelineProvider;

    /**
     * <p>Constructor for SAML2ArtifactBindingDecoder.</p>
     *
     * @param context a {@link CallContext} object
     * @param idpMetadataResolver a {@link SAML2MetadataResolver} object
     * @param spMetadataResolver a {@link SAML2MetadataResolver} object
     * @param soapPipelineProvider a {@link SOAPPipelineProvider} object
     */
    public SAML2ArtifactBindingDecoder(final CallContext context, final SAML2MetadataResolver idpMetadataResolver,
                                       final SAML2MetadataResolver spMetadataResolver, final SOAPPipelineProvider soapPipelineProvider) {
        super(context);
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.soapPipelineProvider = soapPipelineProvider;
    }

    /** {@inheritDoc} */
    @Override
    public String getBindingURI(final SAML2MessageContext messageContext) {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }

    /** {@inheritDoc} */
    @Override
    protected void doDecode() throws MessageDecodingException {
        try {
            val endpointResolver = new DefaultEndpointResolver<ArtifactResolutionService>();
            endpointResolver.initialize();

            val roleResolver = new PredicateRoleDescriptorResolver(
                    idpMetadataResolver.resolve());
            roleResolver.initialize();

            val messageContext = new SAML2MessageContext(getCallContext());

            val soapClient = new PipelineFactoryHttpSOAPClient() {
                @SuppressWarnings("rawtypes")
                @Override
                public void send(final String endpoint, final InOutOperationContext operationContext)
                        throws SOAPException, SecurityException {
                    super.send(endpoint, operationContext);
                    transferContext(operationContext, messageContext);
                }
            };
            soapClient.setPipelineFactory(soapPipelineProvider.getPipelineFactory());
            soapClient.setHttpClient(soapPipelineProvider.getHttpClientBuilder().buildClient());

            val artifactDecoder = new Pac4jHTTPArtifactDecoder();
            artifactDecoder.setCallContext(callContext);
            artifactDecoder.setSelfEntityIDResolver(new FixedEntityIdResolver(spMetadataResolver));
            artifactDecoder.setRoleDescriptorResolver(roleResolver);
            artifactDecoder.setArtifactEndpointResolver(endpointResolver);
            artifactDecoder.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            artifactDecoder.setSoapClient(soapClient);
            artifactDecoder.setParserPool(getParserPool());
            artifactDecoder.initialize();
            artifactDecoder.decode();

            messageContext.getMessageContext().setMessage(artifactDecoder.getMessageContext().getMessage());

            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext.getMessageContext());
        } catch (final Exception e) {
            throw new MessageDecodingException(e);
        }
    }

    /**
     * <p>transferContext.</p>
     *
     * @param operationContext a {@link InOutOperationContext} object
     * @param messageContext a {@link SAML2MessageContext} object
     */
    protected void transferContext(final InOutOperationContext operationContext, final SAML2MessageContext messageContext) {
        messageContext.getMessageContext()
                .addSubcontext(Objects.requireNonNull(Objects.requireNonNull(operationContext.getInboundMessageContext())
                    .getSubcontext(SAMLPeerEntityContext.class)));
    }
}
