package org.pac4j.saml.transport;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.HTMLEncoder;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.component.ComponentSupport;
import net.shibboleth.shared.xml.SerializeSupport;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.AbstractMessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.w3c.dom.Element;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Pac4j implementation extending directly the {@link AbstractMessageEncoder}
 * as intermediate classes use the JEE HTTP response.
 * It's mostly a copy/paste of the source code of these intermediate opensaml classes.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
@Slf4j
public class Pac4jHTTPPostEncoder extends AbstractMessageEncoder {
    /**
     * Default template ID.
     */
    public static final String DEFAULT_TEMPLATE_ID = "/templates/saml2-post-binding.vm";

    private final Pac4jSAMLResponse responseAdapter;

    /**
     * Velocity engine used to evaluate the template when performing POST encoding.
     */
    private VelocityEngine velocityEngine;

    /**
     * ID of the Velocity template used when performing POST encoding.
     */
    private String velocityTemplateId;

    /**
     * <p>Constructor for Pac4jHTTPPostEncoder.</p>
     *
     * @param responseAdapter a {@link Pac4jSAMLResponse} object
     */
    public Pac4jHTTPPostEncoder(final Pac4jSAMLResponse responseAdapter) {
        this.responseAdapter = responseAdapter;
        setVelocityTemplateId(DEFAULT_TEMPLATE_ID);
    }

    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        velocityEngine = null;
        velocityTemplateId = null;
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        LOGGER.debug("Initialized {}", this.getClass().getSimpleName());

        if (velocityEngine == null) {
            throw new ComponentInitializationException("VelocityEngine must be supplied");
        }
        if (velocityTemplateId == null) {
            throw new ComponentInitializationException("Velocity template id must be supplied");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doEncode() throws MessageEncodingException {
        val messageContext = getMessageContext();

        val outboundMessage = (SAMLObject) messageContext.getMessage();
        if (outboundMessage == null) {
            throw new MessageEncodingException("No outbound SAML message contained in message context");
        }

        val endpointURL = getEndpointURL(messageContext).toString();

        postEncode(messageContext, endpointURL);
    }

    /**
     * Gets the response URL from the message context.
     *
     * @param messageContext current message context
     * @return response URL from the message context
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    protected URI getEndpointURL(final MessageContext messageContext) throws MessageEncodingException {
        try {
            return SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (final BindingException e) {
            throw new MessageEncodingException("Could not obtain message endpoint URL", e);
        }
    }

    /**
     * <p>postEncode.</p>
     *
     * @param messageContext a {@link MessageContext} object
     * @param endpointURL a {@link String} object
     * @throws MessageEncodingException if any.
     */
    protected void postEncode(final MessageContext messageContext, final String endpointURL) throws MessageEncodingException {
        LOGGER.debug("Invoking Velocity template to create POST body");

        try {
            val velocityContext = new VelocityContext();
            this.populateVelocityContext(velocityContext, messageContext, endpointURL);

            responseAdapter.setContentType("text/html");
            responseAdapter.init();

            val out = responseAdapter.getOutputStreamWriter();
            this.getVelocityEngine().mergeTemplate(this.getVelocityTemplateId(), "UTF-8", velocityContext, out);
            out.flush();
        } catch (final Exception e) {
            throw new MessageEncodingException("Error creating output document", e);
        }
    }

    /**
     * Get the Velocity template id.
     *
     * <p>Defaults to {@link #DEFAULT_TEMPLATE_ID}.</p>
     *
     * @return return the Velocity template id
     */
    public String getVelocityTemplateId() {
        return velocityTemplateId;
    }

    /**
     * Set the Velocity template id.
     *
     * <p>Defaults to {@link #DEFAULT_TEMPLATE_ID}.</p>
     *
     * @param newVelocityTemplateId the new Velocity template id
     */
    public void setVelocityTemplateId(final String newVelocityTemplateId) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        velocityTemplateId = newVelocityTemplateId;
    }

    /**
     * Get the VelocityEngine instance.
     *
     * @return return the VelocityEngine instance
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * Set the VelocityEngine instance.
     *
     * @param newVelocityEngine the new VelocityEngine instane
     */
    public void setVelocityEngine(final VelocityEngine newVelocityEngine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        velocityEngine = newVelocityEngine;
    }

    /**
     * Populate the Velocity context instance which will be used to render the POST body.
     *
     * @param velocityContext the Velocity context instance to populate with data
     * @param messageContext  the SAML message context source of data
     * @param endpointURL     endpoint URL to which to encode message
     * @throws MessageEncodingException thrown if there is a problem encoding the message
     */
    protected void populateVelocityContext(final Context velocityContext, final MessageContext messageContext,
                                           final String endpointURL) throws MessageEncodingException {

        val encodedEndpointURL = HTMLEncoder.encodeForHTMLAttribute(endpointURL);
        LOGGER.debug("Encoding action url of '{}' with encoded value '{}'", endpointURL, encodedEndpointURL);
        velocityContext.put("action", encodedEndpointURL);
        velocityContext.put("binding", getBindingURI());

        XMLObject outboundMessage = (SAMLObject) messageContext.getMessage();

        LOGGER.debug("Marshalling and Base64 encoding SAML message");
        val domMessage = marshallMessage(outboundMessage);

        val messageXML = SerializeSupport.nodeToString(domMessage);
        LOGGER.trace("Output XML message: {}", messageXML);
        final String encodedMessage;
        try {
            encodedMessage = Base64Support.encode(messageXML.getBytes(StandardCharsets.UTF_8), Base64Support.UNCHUNKED);
        } catch (final Exception e) {
            throw new MessageEncodingException(e);
        }

        if (outboundMessage instanceof RequestAbstractType) {
            velocityContext.put("SAMLRequest", encodedMessage);
        } else if (outboundMessage instanceof StatusResponseType) {
            velocityContext.put("SAMLResponse", encodedMessage);
        } else {
            throw new MessageEncodingException("SAML message is neither a SAML RequestAbstractType or StatusResponseType");
        }

        val relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            val encodedRelayState = HTMLEncoder.encodeForHTMLAttribute(relayState);
            LOGGER.debug("Setting RelayState parameter to: '{}', encoded as '{}'", relayState, encodedRelayState);
            velocityContext.put("RelayState", encodedRelayState);
        }
    }

    /**
     * <p>getBindingURI.</p>
     *
     * @return a {@link String} object
     */
    public String getBindingURI() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    /**
     * Helper method that marshalls the given message.
     *
     * @param message message the marshall and serialize
     * @return marshalled message
     * @throws MessageEncodingException thrown if the give message cannot be marshalled into DOM
     */
    protected Element marshallMessage(final XMLObject message) throws MessageEncodingException {
        LOGGER.debug("Marshalling message");

        try {
            return XMLObjectSupport.marshall(message);
        } catch (final MarshallingException e) {
            throw new MessageEncodingException("Error marshalling message", e);
        }
    }
}
