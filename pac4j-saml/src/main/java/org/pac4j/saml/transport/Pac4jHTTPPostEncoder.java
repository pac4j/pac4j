package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.codec.HTMLEncoder;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Pac4j implementation extending directly the {@link AbstractMessageEncoder} as intermediate classes use the JEE HTTP response.
 * It's mostly a copy/paste of the source code of these intermediate opensaml classes.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPPostEncoder extends AbstractMessageEncoder<SAMLObject> {
    private static final Logger log = LoggerFactory.getLogger(Pac4jHTTPPostEncoder.class);

    /** Default template ID. */
    public static final String DEFAULT_TEMPLATE_ID = "/templates/saml2-post-binding.vm";

    /** Velocity engine used to evaluate the template when performing POST encoding. */
    private VelocityEngine velocityEngine;

    /** ID of the Velocity template used when performing POST encoding. */
    private String velocityTemplateId;

    private final Pac4jSAMLResponse responseAdapter;

    public Pac4jHTTPPostEncoder(final Pac4jSAMLResponse responseAdapter) {
        this.responseAdapter = responseAdapter;
        setVelocityTemplateId(DEFAULT_TEMPLATE_ID);
    }

    /**
     * Set the Velocity template id.
     *
     * <p>Defaults to {@link #DEFAULT_TEMPLATE_ID}.</p>
     *
     * @param newVelocityTemplateId the new Velocity template id
     */
    public void setVelocityTemplateId(String newVelocityTemplateId) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        velocityTemplateId = newVelocityTemplateId;
    }

    @Override
    protected void doDestroy() {
        velocityEngine = null;
        velocityTemplateId = null;
        super.doDestroy();
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        log.debug("Initialized {}", this.getClass().getSimpleName());

        if (velocityEngine == null) {
            throw new ComponentInitializationException("VelocityEngine must be supplied");
        }
        if (velocityTemplateId == null) {
            throw new ComponentInitializationException("Velocity template id must be supplied");
        }
    }

    @Override
    protected void doEncode() throws MessageEncodingException {
        MessageContext<SAMLObject> messageContext = getMessageContext();

        SAMLObject outboundMessage = messageContext.getMessage();
        if (outboundMessage == null) {
            throw new MessageEncodingException("No outbound SAML message contained in message context");
        }

        String endpointURL = getEndpointURL(messageContext).toString();

        postEncode(messageContext, endpointURL);
    }

    /**
     * Gets the response URL from the message context.
     *
     * @param messageContext current message context
     *
     * @return response URL from the message context
     *
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    protected URI getEndpointURL(MessageContext<SAMLObject> messageContext) throws MessageEncodingException {
        try {
            return SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (BindingException e) {
            throw new MessageEncodingException("Could not obtain message endpoint URL", e);
        }
    }

    protected void postEncode(final MessageContext<SAMLObject> messageContext, final String endpointURL) throws MessageEncodingException {
        log.debug("Invoking Velocity template to create POST body");

        try {
            final VelocityContext velocityContext = new VelocityContext();
            this.populateVelocityContext(velocityContext, messageContext, endpointURL);

            responseAdapter.setContentType("text/html");
            responseAdapter.init();

            final OutputStreamWriter out = responseAdapter.getOutputStreamWriter();
            this.getVelocityEngine().mergeTemplate(this.getVelocityTemplateId(), "UTF-8", velocityContext, out);
            out.flush();
        } catch (Exception e) {
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
     * Get the VelocityEngine instance.
     *
     * @return return the VelocityEngine instance
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * Populate the Velocity context instance which will be used to render the POST body.
     *
     * @param velocityContext the Velocity context instance to populate with data
     * @param messageContext the SAML message context source of data
     * @param endpointURL endpoint URL to which to encode message
     * @throws MessageEncodingException thrown if there is a problem encoding the message
     */
    protected void populateVelocityContext(VelocityContext velocityContext, MessageContext<SAMLObject> messageContext,
                                           String endpointURL) throws MessageEncodingException {

        String encodedEndpointURL = HTMLEncoder.encodeForHTMLAttribute(endpointURL);
        log.debug("Encoding action url of '{}' with encoded value '{}'", endpointURL, encodedEndpointURL);
        velocityContext.put("action", encodedEndpointURL);
        velocityContext.put("binding", getBindingURI());

        SAMLObject outboundMessage = messageContext.getMessage();

        log.debug("Marshalling and Base64 encoding SAML message");
        Element domMessage = marshallMessage(outboundMessage);

        String messageXML = SerializeSupport.nodeToString(domMessage);
        log.trace("Output XML message: {}", messageXML);
        String encodedMessage = Base64Support.encode(messageXML.getBytes(StandardCharsets.UTF_8), Base64Support.UNCHUNKED);
        if (outboundMessage instanceof RequestAbstractType) {
            velocityContext.put("SAMLRequest", encodedMessage);
        } else if (outboundMessage instanceof StatusResponseType) {
            velocityContext.put("SAMLResponse", encodedMessage);
        } else {
            throw new MessageEncodingException(
                    "SAML message is neither a SAML RequestAbstractType or StatusResponseType");
        }

        String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            String encodedRelayState = HTMLEncoder.encodeForHTMLAttribute(relayState);
            log.debug("Setting RelayState parameter to: '{}', encoded as '{}'", relayState, encodedRelayState);
            velocityContext.put("RelayState", encodedRelayState);
        }
    }

    /**
     * Set the VelocityEngine instance.
     *
     * @param newVelocityEngine the new VelocityEngine instane
     */
    public void setVelocityEngine(VelocityEngine newVelocityEngine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        velocityEngine = newVelocityEngine;
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    /**
     * Helper method that marshalls the given message.
     *
     * @param message message the marshall and serialize
     *
     * @return marshalled message
     *
     * @throws MessageEncodingException thrown if the give message can not be marshalled into its DOM representation
     */
    protected Element marshallMessage(XMLObject message) throws MessageEncodingException {
        log.debug("Marshalling message");

        try {
            return XMLObjectSupport.marshall(message);
        } catch (MarshallingException e) {
            throw new MessageEncodingException("Error marshalling message", e);
        }
    }
}
