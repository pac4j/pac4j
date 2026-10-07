package org.pac4j.saml.util;

import lombok.experimental.UtilityClass;
import net.shibboleth.shared.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.pac4j.core.util.ProtocolMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The messages exchanged with the identity provider, logged raw as XML on the {@code PROTOCOL_MESSAGE.SAML} logger,
 * at debug level.
 *
 * <p>Nothing is masked: the application signs its messages but never sends a secret of its own.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class SAML2ProtocolMessages {

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages("SAML");

    /** The other party: the identity provider. */
    public static final String IDENTITY_PROVIDER = "identity provider";

    private static final Logger LOG = LoggerFactory.getLogger(SAML2ProtocolMessages.class);

    /**
     * <p>Log a message sent to the identity provider.</p>
     *
     * @param object the message
     */
    public void sent(final XMLObject object) {
        if (LOGGER.isEnabled()) {
            toXml(object).ifPresent(xml -> LOGGER.sent(IDENTITY_PROVIDER, xml));
        }
    }

    /**
     * <p>Log a message received from the identity provider.</p>
     *
     * @param object the message
     */
    public void received(final XMLObject object) {
        if (LOGGER.isEnabled()) {
            toXml(object).ifPresent(xml -> LOGGER.received(IDENTITY_PROVIDER, xml));
        }
    }

    /**
     * <p>Serialize a message as XML, for the logs.</p>
     *
     * @param object the message
     * @return the XML, or nothing if the message cannot be marshalled
     */
    public Optional<String> toXml(final XMLObject object) {
        try {
            return Optional.of(SerializeSupport.nodeToString(XMLObjectSupport.marshall(object)));
        } catch (final MarshallingException e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
