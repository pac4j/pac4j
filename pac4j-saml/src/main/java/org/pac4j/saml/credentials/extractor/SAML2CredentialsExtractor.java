package org.pac4j.saml.credentials.extractor;

import lombok.val;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.sso.artifact.SAML2ArtifactBindingDecoder;
import org.pac4j.saml.sso.artifact.SOAPPipelineProvider;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.transport.Pac4jHTTPRedirectDeflateDecoder;
import org.pac4j.saml.util.Configuration;

import java.util.Optional;

/**
 * SAML2 credentials extractor.
 *
 * @author Jerome Leleu
 * @since 6.0.0
 */
public class SAML2CredentialsExtractor implements CredentialsExtractor {

    private final SAMLContextProvider contextProvider;

    private final SAML2Client saml2Client;

    private final SAML2Configuration saml2Configuration;

    private final SAML2MetadataResolver idpMetadataResolver;

    private final SAML2MetadataResolver spMetadataResolver;

    private final SOAPPipelineProvider soapPipelineProvider;

    /**
     * <p>Constructor for SAML2CredentialsExtractor.</p>
     *
     * @param client a {@link SAML2Client} object
     * @param idpMetadataResolver a {@link SAML2MetadataResolver} object
     * @param spMetadataResolver a {@link SAML2MetadataResolver} object
     * @param soapPipelineProvider a {@link SOAPPipelineProvider} object
     */
    public SAML2CredentialsExtractor(final SAML2Client client, final SAML2MetadataResolver idpMetadataResolver,
                                     final SAML2MetadataResolver spMetadataResolver, final SOAPPipelineProvider soapPipelineProvider) {
        this.saml2Client = client;

        this.saml2Configuration = client.getConfiguration();
        this.contextProvider = client.getContextProvider();
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.soapPipelineProvider = soapPipelineProvider;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val samlContext = this.contextProvider.buildContext(ctx, this.saml2Client);
        samlContext.setSaml2Configuration(saml2Configuration);
        val peerContext = samlContext.getSAMLPeerEntityContext();

        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        samlContext.getSAMLSelfProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        val decoder = getDecoder(ctx);

        val decodedCtx = prepareDecodedContext(samlContext, decoder);

        val message = decodedCtx.getMessageContext().getMessage();
        if (message instanceof Response response) {
            decodedCtx.getSAMLEndpointContext().setEndpoint(decodedCtx.getSPAssertionConsumerService(response));
            decodedCtx.getProfileRequestContext().setProfileId("urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser");

            return Optional.of(new SAML2Credentials(decodedCtx));
        } else {
            decodedCtx.getProfileRequestContext().setProfileId("urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout");

            // SOAP is considered back channel
            val binding = decodedCtx.getSAMLBindingContext().getBindingUri();
            val type = CommonHelper.areEquals(binding, SAMLConstants.SAML2_SOAP11_BINDING_URI) ? LogoutType.BACK : LogoutType.FRONT;

            return Optional.of(new SAML2Credentials(type, decodedCtx));
        }
    }

    /**
     * <p>getDecoder.</p>
     *
     * @param callContext a {@link CallContext} object
     * @return a {@link AbstractPac4jDecoder} object
     */
    protected AbstractPac4jDecoder getDecoder(final CallContext callContext) {
        final AbstractPac4jDecoder decoder;
        val artifact = callContext.webContext().getRequestParameter("SAMLart");
        // artifact binding
        if (artifact.isPresent()) {
            decoder = new SAML2ArtifactBindingDecoder(callContext, idpMetadataResolver,
                spMetadataResolver, soapPipelineProvider);
            try {
                decoder.setParserPool(Configuration.getParserPool());
                decoder.initialize();
                decoder.decode();
            } catch (final Exception e) {
                throw new SAMLException("Error decoding Artifact SAML message", e);
            }

        // POST / SOAP binding
        } else if (WebContextHelper.isPost(callContext.webContext())) {
            decoder = new Pac4jHTTPPostDecoder(callContext);
            try {
                decoder.setParserPool(Configuration.getParserPool());
                decoder.initialize();
                decoder.decode();

            } catch (final Exception e) {
                throw new SAMLException("Error decoding POST SAML message", e);
            }

        // HTTP Redirect binding
        } else if (WebContextHelper.isGet(callContext.webContext())) {
            decoder = new Pac4jHTTPRedirectDeflateDecoder(callContext);

            try {
                decoder.setParserPool(Configuration.getParserPool());
                decoder.initialize();
                decoder.decode();

            } catch (final Exception e) {
                throw new SAMLException("Error decoding HTTP-Redirect SAML message", e);
            }
        } else {
            throw new SAMLException("Unsupported binding");
        }
        return decoder;
    }

    /**
     * <p>prepareDecodedContext.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @param decoder a {@link AbstractPac4jDecoder} object
     * @return a {@link SAML2MessageContext} object
     */
    protected SAML2MessageContext prepareDecodedContext(final SAML2MessageContext context, final AbstractPac4jDecoder decoder) {
        val decodedCtx = new SAML2MessageContext(decoder.getCallContext());
        decodedCtx.setSaml2Configuration(saml2Configuration);

        decodedCtx.setMessageContext(decoder.getMessageContext());
        val message = (SAMLObject) decoder.getMessageContext().getMessage();
        if (message == null) {
            throw new SAMLException("Response from the context cannot be null");
        }
        decodedCtx.getMessageContext().setMessage(message);
        context.getMessageContext().setMessage(message);
        decodedCtx.setSamlMessageStore(context.getSamlMessageStore());

        val bindingContext = prepareBindingContext(context, decoder, decodedCtx);

        val metadata = context.getSAMLPeerMetadataContext().getEntityDescriptor();
        if (metadata == null) {
            throw new SAMLException("IDP Metadata cannot be null");
        }

        preparePeerEntityContext(decoder, decodedCtx, bindingContext, metadata);
        prepareSelfEntityContext(context, decodedCtx);

        decodedCtx.getSAMLSelfMetadataContext().setRoleDescriptor(context.getSPSSODescriptor());
        return decodedCtx;
    }

    /**
     * <p>prepareSelfEntityContext.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @param decodedCtx a {@link SAML2MessageContext} object
     */
    protected void prepareSelfEntityContext(final SAML2MessageContext context, final SAML2MessageContext decodedCtx) {
        decodedCtx.getSAMLSelfEntityContext().setEntityId(context.getSAMLSelfEntityContext().getEntityId());
        decodedCtx.getSAMLSelfEndpointContext().setEndpoint(context.getSAMLSelfEndpointContext().getEndpoint());
        decodedCtx.getSAMLSelfEntityContext().setRole(context.getSAMLSelfEntityContext().getRole());
    }

    /**
     * <p>preparePeerEntityContext.</p>
     *
     * @param decoder a {@link AbstractPac4jDecoder} object
     * @param decodedCtx a {@link SAML2MessageContext} object
     * @param bindingContext a {@link SAMLBindingContext} object
     * @param metadata a {@link EntityDescriptor} object
     */
    protected void preparePeerEntityContext(final MessageDecoder decoder,
                                            final SAML2MessageContext decodedCtx,
                                            final SAMLBindingContext bindingContext,
                                            final EntityDescriptor metadata) {
        val decodedPeerContext = decoder.getMessageContext().getSubcontext(SAMLPeerEntityContext.class);
        CommonHelper.assertNotNull("SAMLPeerEntityContext", bindingContext);

        decodedCtx.getSAMLPeerEntityContext().setEntityId(metadata.getEntityID());
        decodedCtx.getSAMLPeerEntityContext().setAuthenticated(decodedPeerContext != null && decodedPeerContext.isAuthenticated());
    }

    /**
     * <p>prepareBindingContext.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @param decoder a {@link AbstractPac4jDecoder} object
     * @param decodedCtx a {@link SAML2MessageContext} object
     * @return a {@link SAMLBindingContext} object
     */
    protected SAMLBindingContext prepareBindingContext(final SAML2MessageContext context,
                                                       final MessageDecoder decoder,
                                                       final SAML2MessageContext decodedCtx) {
        val bindingContext = decoder.getMessageContext().getSubcontext(SAMLBindingContext.class);
        CommonHelper.assertNotNull("SAMLBindingContext", bindingContext);
        decodedCtx.getSAMLBindingContext().setBindingDescriptor(bindingContext.getBindingDescriptor());
        decodedCtx.getSAMLBindingContext().setBindingUri(bindingContext.getBindingUri());
        decodedCtx.getSAMLBindingContext().setHasBindingSignature(bindingContext.hasBindingSignature());
        decodedCtx.getSAMLBindingContext().setIntendedDestinationEndpointURIRequired(bindingContext
                .isIntendedDestinationEndpointURIRequired());
        val relayState = bindingContext.getRelayState();
        decodedCtx.getSAMLBindingContext().setRelayState(relayState);
        context.getSAMLBindingContext().setRelayState(relayState);
        return bindingContext;
    }
}
