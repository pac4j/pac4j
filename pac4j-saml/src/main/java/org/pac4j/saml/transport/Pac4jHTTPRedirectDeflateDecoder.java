package org.pac4j.saml.transport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

/**
 * Decoder for messages sent via HTTP-Redirect binding.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class Pac4jHTTPRedirectDeflateDecoder extends AbstractPac4jDecoder {

    public Pac4jHTTPRedirectDeflateDecoder(final WebContext context) {
        super(context);
    }

    @Override
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();

        if (ContextHelper.isGet(context)) {
            final InputStream base64DecodedMessage = this.getBase64DecodedMessage();
            final InputStream inflatedMessage = inflate(base64DecodedMessage);
            final SAMLObject inboundMessage = (SAMLObject) this.unmarshallMessage(inflatedMessage);
            messageContext.setMessage(inboundMessage);
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext);
        } else {
            throw new MessageDecodingException("This message decoder only supports the HTTP-Redirect method");
        }
    }

    protected InputStream inflate(final InputStream inputStream) throws MessageDecodingException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final InflaterInputStream iis = new InflaterInputStream(inputStream, new Inflater(true))) {
            byte[] buffer = new byte[1000];
            int length;
            while ((length = iis.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            final byte[] decodedBytes = baos.toByteArray();
            final String decodedMessage = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.debug("Inflated SAML message: {}", decodedMessage);
            return new ByteArrayInputStream(decodedBytes);
        } catch (final Exception e) {
            throw new MessageDecodingException("Cannot decode message", e);
        }
    }

    @Override
    public String getBindingURI() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }
}
