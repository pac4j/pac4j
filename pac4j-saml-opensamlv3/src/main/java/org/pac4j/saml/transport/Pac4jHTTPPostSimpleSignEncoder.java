package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.velocity.VelocityContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostSimpleSignEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.net.URI;

/**
 * Pac4j implementation for HTTP Post Simple-Sign extending openSAML {@link HTTPPostSimpleSignEncoder}.
 *
 * @author Vincent Marmin
 * @since 3.7.0
 */
public class Pac4jHTTPPostSimpleSignEncoder extends HTTPPostSimpleSignEncoder {
    private final static Logger log = LoggerFactory.getLogger(Pac4jHTTPPostSimpleSignEncoder.class);

    private final Pac4jSAMLResponse responseAdapter;

    public Pac4jHTTPPostSimpleSignEncoder(final Pac4jSAMLResponse responseAdapter) {
        this.responseAdapter = responseAdapter;
        setVelocityTemplateId(DEFAULT_TEMPLATE_ID);
    }

    /**
     * Gets the response URL from the message context.
     *
     * @param messageContext current message context
     * @return response URL from the message context
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    @Override
    protected URI getEndpointURL(MessageContext<SAMLObject> messageContext) throws MessageEncodingException {
        try {
            return SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (BindingException e) {
            throw new MessageEncodingException("Could not obtain message endpoint URL", e);
        }
    }

    @Override
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
     * Check component attributes. Copy/Paste parents initialization (no super.doInitialize) except for
     * AbstractHttpServletResponseMessageEncoder since HttpServletResponse is always null.
     *
     * @throws ComponentInitializationException if initialization fails
     */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        log.debug("Initialized {}", this.getClass().getSimpleName());
        if (getMessageContext() == null) {
            throw new ComponentInitializationException("Message context cannot be null");
        }
        if (getVelocityEngine() == null) {
            throw new ComponentInitializationException("VelocityEngine must be supplied");
        }
        if (getVelocityTemplateId() == null) {
            throw new ComponentInitializationException("Velocity template id must be supplied");
        }
    }
}
