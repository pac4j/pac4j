package org.pac4j.openid4vp.util;

import lombok.experimental.UtilityClass;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.ProtocolMessages;

/**
 * The messages exchanged with the browser, the wallet and the page using the digital credentials API, logged raw on
 * the {@value #PROTOCOL_MESSAGE_LOGGER} logger, at debug level, each line naming the transaction.
 *
 * <p>Nothing is masked: the application never sends a secret of its own. An encrypted response stays encrypted here:
 * what the wallet presented only shows on the logs of the authenticator, at trace level. The changes of status of a
 * transaction are logged apart, by {@link org.pac4j.openid4vp.transaction.VpTransaction#logTransition}.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class OpenId4VpProtocolMessages {

    /** The protocol, naming the logger of the messages exchanged. */
    public static final String PROTOCOL_NAME = "OPENID4VP";

    /** The name of the logger of the messages exchanged. */
    public static final String PROTOCOL_MESSAGE_LOGGER = ProtocolMessages.ROOT_LOGGER + "." + PROTOCOL_NAME;

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages(PROTOCOL_NAME);

    /** The other party: the wallet. */
    public static final String WALLET = "wallet";

    /** The other party: the page calling the digital credentials API. */
    public static final String PAGE = "page";

    /**
     * <p>Log a message sent.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param to the party it is sent to
     * @param message the message, as sent
     */
    public void sent(final String transactionId, final String to, final String message) {
        LOGGER.sent(exchange(transactionId), to, message);
    }

    /**
     * <p>Log a message received.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param from the party it is received from
     * @param message the message, as received
     */
    public void received(final String transactionId, final String from, final String message) {
        LOGGER.received(exchange(transactionId), from, message);
    }

    /**
     * <p>Log a request received: its method and all its parameters, as posted or in the query string.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param from the party it is received from
     * @param context the web context
     */
    public void received(final String transactionId, final String from, final WebContext context) {
        LOGGER.received(exchange(transactionId), from, context);
    }

    private String exchange(final String transactionId) {
        return "tx " + transactionId;
    }
}
