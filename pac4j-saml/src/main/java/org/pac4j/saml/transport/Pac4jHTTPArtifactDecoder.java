package org.pac4j.saml.transport;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Resolver;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.impl.SecureRandomIdentifierGenerationStrategy;
import net.shibboleth.shared.xml.ParserPool;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.binding.EndpointResolver;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.artifact.SAMLSourceLocationArtifact;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.binding.impl.DefaultEndpointResolver;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.soap.SAMLSOAPClientContextBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.criterion.*;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.binding.artifact.SAML2Artifact;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.util.SAML2Utils;

import javax.xml.namespace.QName;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Decoder for the artifact binding: it's like the original {@link org.opensaml.saml.saml2.binding.decoding.impl.HTTPArtifactDecoder}
 * but using a web context instead of the JEE HTTP servlet request.
 *
 * @author Jerome LELEU
 * @since 3.8.0
 */
@Slf4j
public class Pac4jHTTPArtifactDecoder extends AbstractMessageDecoder implements SAMLMessageDecoder {

    /**
     * The call context
     */
    @Getter
    @Setter
    private CallContext callContext;

    /**
     * Parser pool used to deserialize the message.
     */
    private ParserPool parserPool;

    /**
     * Optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     */
    @Getter
    @Setter
    private BindingDescriptor bindingDescriptor;

    /**
     * SAML 2 artifact builder factory.
     */
    @NonnullAfterInit
    @Getter
    @Setter
    private SAML2ArtifactBuilderFactory artifactBuilderFactory;

    /**
     * Resolver for ArtifactResolutionService endpoints.
     **/
    @NonnullAfterInit
    @Getter
    @Setter
    private EndpointResolver<ArtifactResolutionService> artifactEndpointResolver;

    /**
     * Role descriptor resolver.
     */
    @NonnullAfterInit
    @Getter
    @Setter
    private RoleDescriptorResolver roleDescriptorResolver;

    /**
     * The peer entity role QName.
     */
    @NonnullAfterInit
    @Getter
    @Setter
    private QName peerEntityRole;

    /**
     * Resolver for the self entityID, based on the peer entity data.
     */
    @NonnullAfterInit
    @Getter
    @Setter
    private Resolver<String, CriteriaSet> selfEntityIDResolver;

    /**
     * SOAP client.
     */
    @Getter
    @Setter
    private SOAPClient soapClient;

    /**
     * The SOAP client message pipeline name.
     */
    @Getter
    @Setter
    private String soapPipelineName;

    /**
     * SOAP client security configuration profile ID.
     */
    @Getter
    @Setter
    private String soapClientSecurityConfigurationProfileId;

    /**
     * Identifier generation strategy.
     */
    @Getter
    @Setter
    private IdentifierGenerationStrategy idStrategy;

    /**
     * Constructor.
     */
    public Pac4jHTTPArtifactDecoder() {
        parserPool = XMLObjectProviderRegistrySupport.getParserPool();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decode() throws MessageDecodingException {
        LOGGER.debug("Beginning to decode message from WebContext");

        LOGGER.debug("HttpServletRequest indicated Content-Type: {}", callContext.webContext().getRequestHeader("Content-type"));

        super.decode();

        SAML2Utils.logProtocolMessage((XMLObject) getMessageContext().getMessage());

        LOGGER.debug("Successfully decoded message from WebContext.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDestroy() {
        super.doDestroy();
        parserPool = null;
        bindingDescriptor = null;
        artifactBuilderFactory = null;
        artifactEndpointResolver = null;
        roleDescriptorResolver = null;
        peerEntityRole = null;
        soapClient = null;
        idStrategy = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDecode() throws MessageDecodingException {
        val messageContext = new MessageContext();

        val relayState = StringSupport.trim(callContext.webContext().getRequestParameter("RelayState").orElse(null));
        LOGGER.debug("Decoded SAML relay state of: {}", relayState);
        SAMLBindingSupport.setRelayState(messageContext, relayState);

        processArtifact(messageContext, callContext.webContext());

        populateBindingContext(messageContext);

        setMessageContext(messageContext);
    }

    /**
     * Gets the parser pool used to deserialize incoming messages.
     *
     * @return parser pool used to deserialize incoming messages
     */
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     *
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(final ParserPool pool) {
        Constraint.isNotNull(pool, "ParserPool cannot be null");
        parserPool = pool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (parserPool == null) {
            throw new ComponentInitializationException("Parser pool cannot be null");
        }

        if (selfEntityIDResolver == null) {
            throw new ComponentInitializationException("Self entityID resolver cannot be null");
        }

        if (roleDescriptorResolver == null) {
            throw new ComponentInitializationException("RoleDescriptorResolver cannot be null");
        }

        if (peerEntityRole == null) {
            throw new ComponentInitializationException("Peer entity role cannot be null");
        }

        if (soapClient == null) {
            throw new ComponentInitializationException("SOAPClient cannot be null");
        }

        if (idStrategy == null) {
            idStrategy = new SecureRandomIdentifierGenerationStrategy();
        }

        if (artifactBuilderFactory == null) {
            artifactBuilderFactory = SAMLConfigurationSupport.getSAML2ArtifactBuilderFactory();
            if (artifactBuilderFactory == null) {
                throw new ComponentInitializationException("Could not obtain a required instance "
                    + "of SAML2ArtifactBuilderFactory");
            }
        }

        if (artifactEndpointResolver == null) {
            artifactEndpointResolver = new DefaultEndpointResolver<>();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty
    public String getBindingURI() {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }


    /**
     * Process the incoming artifact by decoding the artifacts, dereferencing it from the artifact issuer and
     * storing the resulting protocol message in the message context.
     *
     * @param messageContext the message context being processed
     * @param webContext     the web context
     * @throws MessageDecodingException thrown if there is a problem decoding or dereferencing the artifact
     */
    private void processArtifact(final MessageContext messageContext, final WebContext webContext)
        throws MessageDecodingException {

        val encodedArtifact = StringSupport.trimOrNull(webContext.getRequestParameter("SAMLart").orElse(null));
        if (encodedArtifact == null) {
            LOGGER.error("URL SAMLart parameter was missing or did not contain a value.");
            throw new MessageDecodingException("URL SAMLart parameter was missing or did not contain a value.");
        }

        try {
            val artifact = parseArtifact(encodedArtifact);

            val peerRoleDescriptor = resolvePeerRoleDescriptor(artifact);
            if (peerRoleDescriptor == null) {
                throw new MessageDecodingException("Failed to resolve peer RoleDescriptor based on inbound artifact");
            }

            val ars = resolveArtifactEndpoint(artifact, peerRoleDescriptor);

            val inboundMessage = dereferenceArtifact(artifact, peerRoleDescriptor, ars);

            messageContext.setMessage(inboundMessage);
        } catch (final MessageDecodingException e) {
            throw e;
        } catch (final Exception e) {
            throw new MessageDecodingException("Fatal error decoding or resolving inbound artifact", e);
        }
    }

    /**
     * De-reference the supplied artifact into the corresponding SAML protocol message.
     *
     * @param artifact           the artifact to de-reference
     * @param peerRoleDescriptor the peer RoleDescriptor
     * @param ars                the peer's artifact resolution service endpoint
     * @return the de-referenced artifact
     * @throws MessageDecodingException if there is fatal error, or if the artifact was not successfully resolved
     */
    private SAMLObject dereferenceArtifact(final SAML2Artifact artifact,
                                           final RoleDescriptor peerRoleDescriptor,
                                           final ArtifactResolutionService ars)
        throws MessageDecodingException {

        try {
            val selfEntityID = resolveSelfEntityID(peerRoleDescriptor);

            // TODO can assume/enforce response as ArtifactResponse here?
            val opContext = new SAMLSOAPClientContextBuilder()
                .setOutboundMessage(buildArtifactResolveRequestMessage(
                    artifact, ars.getLocation(), selfEntityID))
                .setProtocol(SAMLConstants.SAML20P_NS)
                .setPipelineName(soapPipelineName)
                .setSecurityConfigurationProfileId(soapClientSecurityConfigurationProfileId)
                .setPeerRoleDescriptor(peerRoleDescriptor)
                .setSelfEntityID(selfEntityID)
                .build();

            LOGGER.trace("Executing ArtifactResolve over SOAP 1.1 binding to endpoint: {}", ars.getLocation());
            soapClient.send(ars.getLocation(), opContext);
            val response = (SAMLObject) opContext.getInboundMessageContext().getMessage();
            if (response instanceof ArtifactResponse) {
                return validateAndExtractResponseMessage((ArtifactResponse) response);
            } else {
                throw new MessageDecodingException("SOAP message payload was not an instance of ArtifactResponse: "
                    + response.getClass().getName());
            }
        } catch (final MessageException | SOAPException | SecurityException e) {
            throw new MessageDecodingException("Error dereferencing artifact", e);
        }
    }

    /**
     * Validate and extract the SAML protocol message from the artifact response.
     *
     * @param artifactResponse the response to process
     * @return the SAML protocol message
     * @throws MessageDecodingException if the protocol message was not sent or there was a non-success status response
     */
    private SAMLObject validateAndExtractResponseMessage(final ArtifactResponse artifactResponse)
        throws MessageDecodingException {
        if (artifactResponse.getStatus() == null
            || artifactResponse.getStatus().getStatusCode() == null
            || artifactResponse.getStatus().getStatusCode().getValue() == null) {

            throw new MessageDecodingException("ArtifactResponse included no StatusCode, could not validate");

        } else if (!StatusCode.SUCCESS.equals(artifactResponse.getStatus().getStatusCode().getValue())) {
            throw new MessageDecodingException("ArtifactResponse carried non-success StatusCode: "
                + artifactResponse.getStatus().getStatusCode().getValue());
        }

        if (artifactResponse.getMessage() == null) {
            throw new MessageDecodingException("ArtifactResponse carried an empty message payload");
        }

        return artifactResponse.getMessage();
    }

    /**
     * Build the SAML protocol message for artifact resolution.
     *
     * @param artifact     the artifact being de-referenced
     * @param endpoint     the peer artifact resolution service endpoint
     * @param selfEntityID the entityID of this party, the issuer of the protocol request message
     * @return the SAML protocol message for artifact resolution
     */
    private ArtifactResolve buildArtifactResolveRequestMessage(final SAML2Artifact artifact,
                                                               final String endpoint,
                                                               final String selfEntityID) {

        val request =
            (ArtifactResolve) XMLObjectSupport.buildXMLObject(ArtifactResolve.DEFAULT_ELEMENT_NAME);

        val requestArtifact = (Artifact) XMLObjectSupport.buildXMLObject(Artifact.DEFAULT_ELEMENT_NAME);
        try {
            requestArtifact.setValue(Base64Support.encode(artifact.getArtifactBytes(), false));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        request.setArtifact(requestArtifact);

        request.setID(idStrategy.generateIdentifier(true));
        request.setDestination(endpoint);
        request.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        request.setIssuer(buildIssuer(selfEntityID));

        return request;
    }

    /**
     * Resolve the self entityID, used as the issuer of the protocol message by this entity.
     *
     * @param peerRoleDescriptor the peer RoleDescriptor
     * @return the resolved self entityID
     * @throws MessageDecodingException if there was a fatal error during resolution,
     *                                  or the entityID could not be resolved
     */
    private String resolveSelfEntityID(final RoleDescriptor peerRoleDescriptor)
        throws MessageDecodingException {

        val criteria = new CriteriaSet(new RoleDescriptorCriterion(peerRoleDescriptor));
        try {
            val selfEntityID = getSelfEntityIDResolver().resolveSingle(criteria);
            if (selfEntityID == null) {
                throw new MessageDecodingException("Unable to resolve self entityID from peer RoleDescriptor");
            } else {
                return selfEntityID;
            }
        } catch (final ResolverException e) {
            throw new MessageDecodingException("Fatal error resolving self entityID from peer RoleDescriptor", e);
        }
    }

    /**
     * Build the SAML protocol message Issuer element.
     *
     * @param selfEntityID the entity ID of the protocol message issuer (this entity)
     * @return the Issuer element
     */
    private Issuer buildIssuer(final String selfEntityID) {
        val issuer = (Issuer) XMLObjectSupport.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(selfEntityID);
        return issuer;
    }

    /**
     * Resolve the artifact resolution endpoint of the peer who issued the artifact.
     *
     * @param artifact           the artifact
     * @param peerRoleDescriptor the peer RoleDescriptor
     * @return the peer artifact resolution service endpoint
     * @throws MessageDecodingException if there is a fatal error resolving the endpoint,
     *                                  or the endpoint could not be resolved
     */
    private ArtifactResolutionService resolveArtifactEndpoint(final SAML2Artifact artifact,
                                                              final RoleDescriptor peerRoleDescriptor)
        throws MessageDecodingException {

        val roleDescriptorCriterion = new RoleDescriptorCriterion(peerRoleDescriptor);

        val arsTemplate =
            (ArtifactResolutionService) XMLObjectSupport.buildXMLObject(
                ArtifactResolutionService.DEFAULT_ELEMENT_NAME);

        arsTemplate.setBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);

        if (artifact instanceof SAMLSourceLocationArtifact) {
            arsTemplate.setLocation(((SAMLSourceLocationArtifact) artifact).getSourceLocation());
        }

        final Integer endpointIndex = SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(artifact.getEndpointIndex());
        arsTemplate.setIndex(endpointIndex);

        val endpointCriterion =
            new EndpointCriterion<ArtifactResolutionService>(arsTemplate, false);

        val criteriaSet = new CriteriaSet(roleDescriptorCriterion, endpointCriterion);

        try {
            val ars = artifactEndpointResolver.resolveSingle(criteriaSet);
            if (ars != null) {
                return ars;
            } else {
                throw new MessageDecodingException("Unable to resolve ArtifactResolutionService endpoint");
            }
        } catch (final ResolverException e) {
            throw new MessageDecodingException("Unable to resolve ArtifactResolutionService endpoint");
        }
    }

    /**
     * Resolve the role descriptor of the SAML peer who issued the supplied artifact.
     *
     * @param artifact the artifact to process
     * @return the peer RoleDescriptor
     * @throws MessageDecodingException if there was a fatal error resolving the role descriptor,
     *                                  or the descriptor could not be resolved
     */
    private RoleDescriptor resolvePeerRoleDescriptor(final SAML2Artifact artifact)
        throws MessageDecodingException {

        val criteriaSet = new CriteriaSet(
            new ArtifactCriterion(artifact),
            new ProtocolCriterion(SAMLConstants.SAML20P_NS),
            new EntityRoleCriterion(getPeerEntityRole()));
        try {
            val rd = roleDescriptorResolver.resolveSingle(criteriaSet);
            if (rd == null) {
                throw new MessageDecodingException("Unable to resolve peer RoleDescriptor from supplied artifact");
            }
            return rd;
        } catch (final ResolverException e) {
            throw new MessageDecodingException("Error resolving peer entity RoleDescriptor", e);
        }
    }

    /**
     * Parse and decode the supplied encoded artifact string into a {@link SAML2Artifact} instance.
     *
     * @param encodedArtifact the encoded artifact which was received
     * @return the decoded artifact instance
     * @throws MessageDecodingException if the encoded artifact could not be decoded
     */
    private SAML2Artifact parseArtifact(final String encodedArtifact)
        throws MessageDecodingException {

        //TODO not sure if this handles well bad input.  Determine if can throw an unchecked and handle here.
        try {
            val artifact = artifactBuilderFactory.buildArtifact(encodedArtifact);
            if (artifact == null) {
                throw new MessageDecodingException("Could not build SAML2Artifact instance from encoded artifact");
            }
            return artifact;
        } catch (final Exception e) {
            throw new MessageDecodingException(e);
        }
    }

    /**
     * Populate the context which carries information specific to this binding.
     *
     * @param messageContext the current message context
     */
    protected void populateBindingContext(final MessageContext messageContext) {
        val bindingContext = messageContext.getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setBindingDescriptor(bindingDescriptor);
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(false);
    }

}
