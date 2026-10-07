package org.pac4j.cas.util;

import lombok.experimental.UtilityClass;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.ProtocolMessages;

/**
 * The messages exchanged with the browser and the CAS server, logged raw on the {@code PROTOCOL_MESSAGE.CAS} logger,
 * at debug level.
 *
 * <p>The password sent to the CAS REST API is masked; the ticket validation is performed by the Apereo CAS client,
 * which logs it on its own loggers.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class CasProtocolMessages {

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages("CAS");

    /** The other party: the CAS server. */
    public static final String CAS_SERVER = "CAS server";

    /**
     * <p>Log a message sent.</p>
     *
     * @param to the party it is sent to
     * @param message the message, as sent
     */
    public void sent(final String to, final String message) {
        LOGGER.sent(to, message);
    }

    /**
     * <p>Log a message received.</p>
     *
     * @param from the party it is received from
     * @param message the message, as received
     */
    public void received(final String from, final String message) {
        LOGGER.received(from, message);
    }

    /**
     * <p>Log a request received from the browser or the CAS server: its method and all its parameters.</p>
     *
     * @param from the party it is received from
     * @param context the web context
     */
    public void received(final String from, final WebContext context) {
        LOGGER.received(null, from, context);
    }
}
