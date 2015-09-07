package org.pac4j.saml.transport;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPRedirectDeflateEncoder;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Misagh Moayyed
 * @since 1.8
 */
public class Pac4jHTTPRedirectDeflateEncoder extends HTTPRedirectDeflateEncoder {

    private final static Logger logger = LoggerFactory.getLogger(Pac4jHTTPPostEncoder.class);

    private final WebContext context;
    private final SimpleResponseAdapter responseAdapter;

    public Pac4jHTTPRedirectDeflateEncoder(final WebContext context, final SimpleResponseAdapter responseAdapter) {
        this.context = context;
        this.responseAdapter = responseAdapter;
    }

    @Override
    protected void doEncode() throws MessageEncodingException {
        MessageContext messageContext = this.getMessageContext();
        SAMLObject outboundMessage = (SAMLObject)messageContext.getMessage();
        String endpointURL = this.getEndpointURL(messageContext).toString();
        this.removeSignature(outboundMessage);
        String encodedMessage = this.deflateAndBase64Encode(outboundMessage);
        String redirectURL = this.buildRedirectURL(messageContext, endpointURL, encodedMessage);

        responseAdapter.setNoCacheHeaders();
        responseAdapter.setUTF8Encoding();
        responseAdapter.setRedirectUrl(redirectURL);
    }

    @Override
    public synchronized void setHttpServletResponse(@Nullable HttpServletResponse servletResponse) {
        logger.debug("Ignoring HttpServletRequest");
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        logger.debug("Initialized {}", this.getClass().getSimpleName());
    }
}
