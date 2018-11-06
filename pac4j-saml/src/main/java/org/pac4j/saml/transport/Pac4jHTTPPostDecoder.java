package org.pac4j.saml.transport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;

import java.io.InputStream;

/**
 * Decoder for messages sent via POST binding.
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPPostDecoder extends AbstractPac4jDecoder {

    public Pac4jHTTPPostDecoder(final WebContext context, final boolean cas5Compatibility) {
        super(context, cas5Compatibility);
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();

        if (ContextHelper.isPost(context)) {
            final String relayState = this.context.getRequestParameter("RelayState");
            logger.debug("Decoded SAML relay state of: {}", relayState);
            SAMLBindingSupport.setRelayState(messageContext, relayState);
            final InputStream base64DecodedMessage = this.getBase64DecodedMessage();
            final SAMLObject inboundMessage = (SAMLObject) this.unmarshallMessage(base64DecodedMessage);
            messageContext.setMessage(inboundMessage);
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext);

        } else {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        }
    }

    @Override
    public String getBindingURI() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }
}
