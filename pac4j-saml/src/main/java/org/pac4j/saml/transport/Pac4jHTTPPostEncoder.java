package org.pac4j.saml.transport;

import org.apache.velocity.VelocityContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;

/**
 * @author Misagh Moayyed
 */
public class Pac4jHTTPPostEncoder extends HTTPPostEncoder {
    private final static Logger logger = LoggerFactory.getLogger(Pac4jHTTPPostEncoder.class);

    private final WebContext context;
    private final SimpleResponseAdapter responseAdapter;

    public Pac4jHTTPPostEncoder(final WebContext context, final SimpleResponseAdapter responseAdapter) {
        this.context = context;
        this.responseAdapter = responseAdapter;
    }

    @Override
    protected void postEncode(final MessageContext<SAMLObject> messageContext, final String endpointURL) throws MessageEncodingException {
        logger.debug("Invoking Velocity template to create POST body");

        try {
            VelocityContext e = new VelocityContext();
            this.populateVelocityContext(e, messageContext, endpointURL);

            responseAdapter.setContentType("text/html");
            responseAdapter.setNoCacheHeaders();
            responseAdapter.setUTF8Encoding();

            OutputStreamWriter out = responseAdapter.getOutputStream();
            this.getVelocityEngine().mergeTemplate(this.getVelocityTemplateId(), "UTF-8", e, out);
            out.flush();
        } catch (Exception var6) {
            logger.error("Error invoking Velocity template", var6);
            throw new MessageEncodingException("Error creating output document", var6);
        }
    }
}
