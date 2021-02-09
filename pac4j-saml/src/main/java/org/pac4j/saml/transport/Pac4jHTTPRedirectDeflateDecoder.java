package org.pac4j.saml.transport;

import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        final var messageContext = new SAML2MessageContext();

        if (ContextHelper.isGet(context)) {
            final var base64DecodedMessage = this.getBase64DecodedMessage();
            final var inflatedMessage = inflate(base64DecodedMessage);
            final var inboundMessage = (SAMLObject) this.unmarshallMessage(inflatedMessage);
            messageContext.getMessageContext().setMessage(inboundMessage);
            logger.debug("Decoded SAML message");
            this.populateBindingContext(messageContext);
            this.setMessageContext(messageContext.getMessageContext());
        } else {
            throw new MessageDecodingException("This message decoder only supports the HTTP-Redirect method");
        }
    }

    protected InputStream inflate(final byte[] input) throws MessageDecodingException {
        try {
            // compatible with GZIP and PKZIP
            return internalInflate(input, new Inflater(true));
        } catch (final IOException e) {
            try {
                // deflate compression only
                return internalInflate(input, new Inflater());
            } catch (final IOException e2) {
                throw new MessageDecodingException("Cannot decode message", e2);
            }
        }
    }

    protected InputStream internalInflate(final byte[] input, final Inflater inflater) throws IOException {
        final var baos = new ByteArrayOutputStream();
        final var iis = new InflaterInputStream(new ByteArrayInputStream(input), inflater);
        try {
            final var buffer = new byte[1000];
            int length;
            while ((length = iis.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            final var decodedBytes = baos.toByteArray();
            final var decodedMessage = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.debug("Inflated SAML message: {}", decodedMessage);
            return new ByteArrayInputStream(decodedBytes);
        } finally {
            baos.close();
            iis.close();
        }
    }

    @Override
    public String getBindingURI(final SAML2MessageContext messageContext) {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }
}
