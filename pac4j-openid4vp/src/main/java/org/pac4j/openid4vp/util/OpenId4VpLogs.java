package org.pac4j.openid4vp.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.ProtocolMessageLogger;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The logs telling what goes on in a presentation, apart from the logs of each class.
 *
 * <p>Two of them:</p>
 * <ul>
 *   <li>the messages exchanged, raw, as they travel: on the {@value #PROTOCOL_MESSAGE_LOGGER} logger, at debug level,
 *   each line naming the transaction, the direction ({@code >>>} sent, {@code <<<} received) and the other party
 *   (the browser, the wallet, or the page using the digital credentials API). Being a child of the
 *   {@code PROTOCOL_MESSAGE} logger, it is enabled with the other protocols or alone. An encrypted response stays
 *   encrypted there: what the wallet presented only shows on the logs of the authenticator, at trace level;</li>
 *   <li>the changes of status of a transaction: on the logger of {@link VpTransaction}, at debug level, as
 *   {@code transaction <id>: <from> -> <to>}.</li>
 * </ul>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class OpenId4VpLogs {

    /** The protocol, naming the logger of the messages exchanged. */
    public static final String PROTOCOL_NAME = "OPENID4VP";

    /** The logger of the messages exchanged. */
    public static final String PROTOCOL_MESSAGE_LOGGER = ProtocolMessageLogger.ROOT_LOGGER + "." + PROTOCOL_NAME;

    /** The other party: the browser of the End-User. */
    public static final String BROWSER = "browser";

    /** The other party: the wallet. */
    public static final String WALLET = "wallet";

    /** The other party: the page calling the digital credentials API. */
    public static final String PAGE = "page";

    /** No transaction yet, or none at all. */
    public static final String NONE = "-";

    /** The status of a transaction the browser came back for, removed from the store. */
    public static final String CONSUMED = "CONSUMED";

    private static final ProtocolMessageLogger PROTOCOL = new ProtocolMessageLogger(PROTOCOL_NAME);

    private static final Logger TRANSACTION = LoggerFactory.getLogger(VpTransaction.class);

    /**
     * <p>Log a message sent.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param to the party it is sent to
     * @param message the message, as sent
     */
    public void sent(final String transactionId, final String to, final String message) {
        PROTOCOL.sent(exchange(transactionId), to, message);
    }

    /**
     * <p>Log a message received.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param from the party it is received from
     * @param message the message, as received
     */
    public void received(final String transactionId, final String from, final String message) {
        PROTOCOL.received(exchange(transactionId), from, message);
    }

    /**
     * <p>Log a request received: its method and all its parameters, as posted or in the query string.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param from the party it is received from
     * @param context the web context
     */
    public void received(final String transactionId, final String from, final WebContext context) {
        PROTOCOL.received(exchange(transactionId), from, context);
    }

    private String exchange(final String transactionId) {
        return "tx " + transactionId;
    }

    /**
     * <p>Log a change of status of a transaction.</p>
     *
     * @param transactionId the identifier of the transaction
     * @param from the former status, {@link #NONE} for a new transaction
     * @param to the new status
     * @param detail what caused it, or null
     */
    public void transition(final String transactionId, final Object from, final Object to, final String detail) {
        if (TRANSACTION.isDebugEnabled()) {
            TRANSACTION.debug("transaction {}: {} -> {}{}", oneLine(transactionId), from, to,
                detail == null ? "" : " (" + oneLine(detail) + ")");
        }
    }

    /**
     * <p>The reasons of a failure: its message, followed by the messages of its causes, a verifier wrapping the
     * failure of the library it relies on.</p>
     *
     * @param e the failure
     * @return the reasons
     */
    public String reasons(final Throwable e) {
        val reasons = new StringBuilder(String.valueOf(e.getMessage()));
        var cause = e.getCause();
        while (cause != null && cause != e) {
            reasons.append(" <- ").append(cause.getMessage());
            cause = cause.getCause() == cause ? null : cause.getCause();
        }
        return reasons.toString();
    }

    /**
     * <p>Make a value fit on one line of log: what comes from a request must not forge other lines.</p>
     *
     * @param value the value
     * @return the value, carriage returns and line feeds replaced by spaces
     */
    public String oneLine(final String value) {
        return ProtocolMessageLogger.oneLine(value);
    }
}
