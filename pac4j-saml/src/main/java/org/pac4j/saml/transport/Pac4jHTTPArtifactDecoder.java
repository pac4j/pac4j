package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.security.impl.SecureRandomIdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
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
import org.opensaml.saml.criterion.ArtifactCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.binding.artifact.SAML2Artifact;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.opensaml.saml.saml2.core.Artifact;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.client.http.PipelineFactoryHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class Pac4jHTTPArtifactDecoder extends AbstractMessageDecoder implements SAMLMessageDecoder {

    /**
     * Class logger.
     */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(Pac4jHTTPArtifactDecoder.class);

    /**
     * The web context
     */
    private WebContext webContext;

    /**
     * Used to log protocol messages.
     */
    private Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

    /**
     * Parser pool used to deserialize the message.
     */
    private ParserPool parserPool;

    /**
     * Optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     */
    @Nullable
    private BindingDescriptor bindingDescriptor;

    /**
     * SAML 2 artifact builder factory.
     */
    @NonnullAfterInit
    private SAML2ArtifactBuilderFactory artifactBuilderFactory;

    /**
     * Resolver for ArtifactResolutionService endpoints.
     **/
    @NonnullAfterInit
    private EndpointResolver<ArtifactResolutionService> artifactEndpointResolver;

    /**
     * Role descriptor resolver.
     */
    @NonnullAfterInit
    private RoleDescriptorResolver roleDescriptorResolver;

    /**
     * The peer entity role QName.
     */
    @NonnullAfterInit
    private QName peerEntityRole;

    /**
     * Resolver for the self entityID, based on the peer entity data.
     */
    @NonnullAfterInit
    private Resolver<String, CriteriaSet> selfEntityIDResolver;

    /**
     * SOAP client.
     */
    private SOAPClient soapClient;

    /**
     * The SOAP client message pipeline name.
     */
    private String soapPipelineName;

    /**
     * SOAP client security configuration profile ID.
     */
    private String soapClientSecurityConfigurationProfileId;

    /**
     * Identifier generation strategy.
     */
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
        log.debug("Beginning to decode message from WebContext");

        log.debug("HttpServletRequest indicated Content-Type: {}", webContext.getRequestHeader("Content-type"));

        super.decode();

        logDecodedMessage();

        log.debug("Successfully decoded message from WebContext.");
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
        final var messageContext = new MessageContext();

        final var relayState = StringSupport.trim(webContext.getRequestParameter("RelayState").orElse(null));
        log.debug("Decoded SAML relay state of: {}", relayState);
        SAMLBindingSupport.setRelayState(messageContext, relayState);

        processArtifact(messageContext, webContext);

        populateBindingContext(messageContext);

        setMessageContext(messageContext);
    }

    /**
     * Gets the parser pool used to deserialize incoming messages.
     *
     * @return parser pool used to deserialize incoming messages
     */
    @Nonnull
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     *
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
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
     * Get the identifier generation strategy.
     *
     * @return Returns the identifier generation strategy
     */
    @NonnullAfterInit
    public IdentifierGenerationStrategy getIdentifierGenerationStrategy() {
        return idStrategy;
    }

    /**
     * Set the identifier generation strategy.
     *
     * @param strategy the identifier generation strategy
     */
    public void setIdentifierGenerationStrategy(@Nullable final IdentifierGenerationStrategy strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        idStrategy = strategy;
    }

    /**
     * Get the resolver for the self entityID.
     *
     * @return the resolver
     */
    @NonnullAfterInit
    public Resolver<String, CriteriaSet> getSelfEntityIDResolver() {
        return selfEntityIDResolver;
    }

    /**
     * Set the resolver for the self entityID.
     *
     * @param resolver the resolver instance
     */
    public void setSelfEntityIDResolver(@Nonnull final Resolver<String, CriteriaSet> resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        selfEntityIDResolver = resolver;
    }

    /**
     * Get the peer entity role {@link QName}.
     *
     * @return the peer entity role
     */
    @NonnullAfterInit
    public QName getPeerEntityRole() {
        return peerEntityRole;
    }

    /**
     * Set the peer entity role {@link QName}.
     *
     * @param role the peer entity role
     */
    public void setPeerEntityRole(@Nonnull final QName role) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        peerEntityRole = role;
    }

    /**
     * Get the artifact endpoint resolver.
     *
     * @return the endpoint resolver
     */
    @NonnullAfterInit
    public EndpointResolver<ArtifactResolutionService> getArtifactEndpointResolver() {
        return artifactEndpointResolver;
    }

    /**
     * Set the artifact endpoint resolver.
     *
     * @param resolver the new resolver
     */
    public void setArtifactEndpointResolver(@Nullable final EndpointResolver<ArtifactResolutionService> resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        artifactEndpointResolver = resolver;
    }

    /**
     * Get the role descriptor resolver.
     *
     * <p>
     * Must be capable of resolving descriptors based on {@link ArtifactCriterion}.
     * </p>
     *
     * @return the role descriptor resolver
     */
    @NonnullAfterInit
    public RoleDescriptorResolver getRoleDescriptorResolver() {
        return roleDescriptorResolver;
    }

    /**
     * Set the role descriptor resolver.
     *
     * <p>
     * Must be capable of resolving descriptors based on {@link ArtifactCriterion}.
     * </p>
     *
     * @param resolver the role descriptor resolver
     */
    public void setRoleDescriptorResolver(@Nullable final RoleDescriptorResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        roleDescriptorResolver = resolver;
    }

    /**
     * Get the SAML 2 artifact builder factory.
     *
     * @return the artifact builder factory in use
     */
    @NonnullAfterInit
    public SAML2ArtifactBuilderFactory getArtifactBuilderFactory() {
        return artifactBuilderFactory;
    }

    /**
     * Set the SAML 2 artifact builder factory.
     *
     * @param factory the artifact builder factory
     */
    public void setArtifactBuilderFactory(@Nullable final SAML2ArtifactBuilderFactory factory) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        artifactBuilderFactory = factory;
    }

    /**
     * Get the SOAP client instance.
     *
     * @return the SOAP client
     */
    @NonnullAfterInit
    public SOAPClient getSOAPClient() {
        return soapClient;
    }

    /**
     * Set the SOAP client instance.
     *
     * @param client the SOAP client
     */
    public void setSOAPClient(@Nonnull final SOAPClient client) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        soapClient = client;
    }

    /**
     * Get the name of the specific SOAP client message pipeline to use,
     * for example with {@link PipelineFactoryHttpSOAPClient}.
     *
     * @return the pipeline name, or null
     */
    @Nullable
    public String getSOAPPipelineName() {
        return soapPipelineName;
    }

    /**
     * Set the name of the specific SOAP client message pipeline to use,
     * for example with {@link PipelineFactoryHttpSOAPClient}.
     *
     * @param name the pipeline name, or null
     */
    public void setSOAPPipelineName(@Nullable final String name) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        soapPipelineName = StringSupport.trimOrNull(name);
    }

    /**
     * Get the SOAP client security configuration profile ID to use.
     *
     * @return the client security configuration profile ID, or null
     */
    @Nullable
    public String getSOAPClientSecurityConfigurationProfileId() {
        return soapClientSecurityConfigurationProfileId;
    }

    /**
     * Set the SOAP client security configuration profile ID to use.
     *
     * @param profileId the profile ID, or null
     */
    @Nonnull
    public void setSOAPClientSecurityConfigurationProfileId(@Nullable final String profileId) {
        soapClientSecurityConfigurationProfileId = StringSupport.trimOrNull(profileId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @NotEmpty
    public String getBindingURI() {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }

    /**
     * Get an optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     *
     * @return binding descriptor
     */
    @Nullable
    public BindingDescriptor getBindingDescriptor() {
        return bindingDescriptor;
    }

    /**
     * Set an optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     *
     * @param descriptor a binding descriptor
     */
    public void setBindingDescriptor(@Nullable final BindingDescriptor descriptor) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        bindingDescriptor = descriptor;
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

        final var encodedArtifact = StringSupport.trimOrNull(webContext.getRequestParameter("SAMLart").orElse(null));
        if (encodedArtifact == null) {
            log.error("URL SAMLart parameter was missing or did not contain a value.");
            throw new MessageDecodingException("URL SAMLart parameter was missing or did not contain a value.");
        }

        try {
            final var artifact = parseArtifact(encodedArtifact);

            final var peerRoleDescriptor = resolvePeerRoleDescriptor(artifact);
            if (peerRoleDescriptor == null) {
                throw new MessageDecodingException("Failed to resolve peer RoleDescriptor based on inbound artifact");
            }

            final var ars = resolveArtifactEndpoint(artifact, peerRoleDescriptor);

            final var inboundMessage = dereferenceArtifact(artifact, peerRoleDescriptor, ars);

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
    @Nonnull
    private SAMLObject dereferenceArtifact(@Nonnull final SAML2Artifact artifact,
                                           @Nonnull final RoleDescriptor peerRoleDescriptor,
                                           @Nonnull final ArtifactResolutionService ars)
        throws MessageDecodingException {

        try {
            final var selfEntityID = resolveSelfEntityID(peerRoleDescriptor);

            // TODO can assume/enforce response as ArtifactResponse here?
            final var opContext = new SAMLSOAPClientContextBuilder()
                .setOutboundMessage(buildArtifactResolveRequestMessage(
                    artifact, ars.getLocation(), selfEntityID))
                .setProtocol(SAMLConstants.SAML20P_NS)
                .setPipelineName(getSOAPPipelineName())
                .setSecurityConfigurationProfileId(getSOAPClientSecurityConfigurationProfileId())
                .setPeerRoleDescriptor(peerRoleDescriptor)
                .setSelfEntityID(selfEntityID)
                .build();

            log.trace("Executing ArtifactResolve over SOAP 1.1 binding to endpoint: {}", ars.getLocation());
            soapClient.send(ars.getLocation(), opContext);
            final var response = (SAMLObject) opContext.getInboundMessageContext().getMessage();
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
    @Nonnull
    private SAMLObject validateAndExtractResponseMessage(@Nonnull final ArtifactResponse artifactResponse)
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
    @Nonnull
    private ArtifactResolve buildArtifactResolveRequestMessage(@Nonnull final SAML2Artifact artifact,
                                                               @Nonnull final String endpoint,
                                                               @Nonnull final String selfEntityID) {

        final var request =
            (ArtifactResolve) XMLObjectSupport.buildXMLObject(ArtifactResolve.DEFAULT_ELEMENT_NAME);

        final var requestArtifact = (Artifact) XMLObjectSupport.buildXMLObject(Artifact.DEFAULT_ELEMENT_NAME);
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
    @Nonnull
    private String resolveSelfEntityID(@Nonnull final RoleDescriptor peerRoleDescriptor)
        throws MessageDecodingException {

        final var criteria = new CriteriaSet(new RoleDescriptorCriterion(peerRoleDescriptor));
        try {
            final var selfEntityID = getSelfEntityIDResolver().resolveSingle(criteria);
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
    @Nonnull
    private Issuer buildIssuer(@Nonnull final String selfEntityID) {
        final var issuer = (Issuer) XMLObjectSupport.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
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
    @Nonnull
    private ArtifactResolutionService resolveArtifactEndpoint(@Nonnull final SAML2Artifact artifact,
                                                              @Nonnull final RoleDescriptor peerRoleDescriptor)
        throws MessageDecodingException {

        final var roleDescriptorCriterion = new RoleDescriptorCriterion(peerRoleDescriptor);

        final var arsTemplate =
            (ArtifactResolutionService) XMLObjectSupport.buildXMLObject(
                ArtifactResolutionService.DEFAULT_ELEMENT_NAME);

        arsTemplate.setBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);

        if (artifact instanceof SAMLSourceLocationArtifact) {
            arsTemplate.setLocation(((SAMLSourceLocationArtifact) artifact).getSourceLocation());
        }

        final Integer endpointIndex = SAMLBindingSupport.convertSAML2ArtifactEndpointIndex(artifact.getEndpointIndex());
        arsTemplate.setIndex(endpointIndex);

        final var endpointCriterion =
            new EndpointCriterion<ArtifactResolutionService>(arsTemplate, false);

        final var criteriaSet = new CriteriaSet(roleDescriptorCriterion, endpointCriterion);

        try {
            final var ars = artifactEndpointResolver.resolveSingle(criteriaSet);
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
    @Nonnull
    private RoleDescriptor resolvePeerRoleDescriptor(@Nonnull final SAML2Artifact artifact)
        throws MessageDecodingException {

        final var criteriaSet = new CriteriaSet(
            new ArtifactCriterion(artifact),
            new ProtocolCriterion(SAMLConstants.SAML20P_NS),
            new EntityRoleCriterion(getPeerEntityRole()));
        try {
            final var rd = roleDescriptorResolver.resolveSingle(criteriaSet);
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
    @Nonnull
    private SAML2Artifact parseArtifact(@Nonnull final String encodedArtifact)
        throws MessageDecodingException {

        //TODO not sure if this handles well bad input.  Determine if can throw an unchecked and handle here.
        try {
            final var artifact = artifactBuilderFactory.buildArtifact(encodedArtifact);
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
        final var bindingContext = messageContext.getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setBindingDescriptor(bindingDescriptor);
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(false);
    }

    /**
     * Log the decoded message to the protocol message logger.
     */
    protected void logDecodedMessage() {
        if (protocolMessageLog.isDebugEnabled()) {
            final var message = getMessageToLog();
            if (message == null) {
                log.warn("Decoded message was null, nothing to log");
                return;
            }

            try {
                final var dom = XMLObjectSupport.marshall(message);
                protocolMessageLog.debug("\n" + SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("Unable to marshall message for logging purposes", e);
            }
        }
    }

    /**
     * Get the XMLObject which will be logged as the protocol message.
     *
     * @return the XMLObject message considered to be the protocol message for logging purposes
     */
    protected XMLObject getMessageToLog() {
        return (XMLObject) getMessageContext().getMessage();
    }

    public synchronized void setWebContext(@Nullable final WebContext webContext) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        this.webContext = webContext;
    }
}
