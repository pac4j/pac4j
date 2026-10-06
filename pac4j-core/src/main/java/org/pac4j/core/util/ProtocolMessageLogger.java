package org.pac4j.core.util;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Logs the messages of a protocol as they travel, on the {@code PROTOCOL_MESSAGE.<protocol>} logger, at debug level.
 *
 * <p>Each line gives the direction, {@code >>>} for a message sent and {@code <<<} for a message received, then the
 * other party (the browser, the identity provider...) and the message itself, raw. It may start with what links the
 * messages of a same exchange, such as a transaction identifier. Being children of the {@code PROTOCOL_MESSAGE} logger,
 * the loggers of all protocols are enabled at once through it, or one at a time.</p>
 *
 * <p>The messages are logged as they travel, so with the tokens and the personal data they carry: these logs are meant
 * to diagnose, not to be enabled permanently in production. Only the secrets of the application itself, which never
 * need to be read to diagnose an exchange, are masked.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class ProtocolMessageLogger {

    /** The parent logger of all protocols. */
    public static final String ROOT_LOGGER = "PROTOCOL_MESSAGE";

    /** What replaces a masked value. */
    public static final String MASK = "*****";

    private final Logger logger;

    /**
     * <p>Constructor for ProtocolMessageLogger.</p>
     *
     * @param protocol the name of the protocol, which ends the name of the logger
     */
    public ProtocolMessageLogger(final String protocol) {
        this.logger = LoggerFactory.getLogger(ROOT_LOGGER + "." + protocol);
    }

    /**
     * <p>Whether the messages are logged.</p>
     *
     * @return whether the logger is enabled at debug level
     */
    public boolean isEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * <p>Log a message sent.</p>
     *
     * @param to the party it is sent to
     * @param message the message, as sent
     */
    public void sent(final String to, final String message) {
        sent(null, to, message);
    }

    /**
     * <p>Log a message sent, within an exchange.</p>
     *
     * @param exchange what links the messages of the exchange, or null
     * @param to the party it is sent to
     * @param message the message, as sent
     */
    public void sent(final String exchange, final String to, final String message) {
        log(exchange, ">>>", to, message);
    }

    /**
     * <p>Log a message received.</p>
     *
     * @param from the party it is received from
     * @param message the message, as received
     */
    public void received(final String from, final String message) {
        received(null, from, message);
    }

    /**
     * <p>Log a message received, within an exchange.</p>
     *
     * @param exchange what links the messages of the exchange, or null
     * @param from the party it is received from
     * @param message the message, as received
     */
    public void received(final String exchange, final String from, final String message) {
        log(exchange, "<<<", from, message);
    }

    /**
     * <p>Log a request received: its method and all its parameters, from the query string or the form, some of them
     * being masked.</p>
     *
     * @param exchange what links the messages of the exchange, or null
     * @param from the party it is received from
     * @param context the web context
     * @param maskedParameters the names of the parameters whose values are masked
     */
    public void received(final String exchange, final String from, final WebContext context, final String... maskedParameters) {
        if (isEnabled()) {
            received(exchange, from, context.getRequestMethod() + " "
                + formParameters(context.getRequestParameters().entrySet().stream()
                    .flatMap(entry -> Arrays.stream(entry.getValue()).map(value -> new String[] {entry.getKey(), value}))
                    .toList(), maskedParameters));
        }
    }

    /**
     * <p>Write parameters as in a query string or a form, some of them being masked.</p>
     *
     * @param parameters the parameters, as name and value pairs
     * @param maskedParameters the names of the parameters whose values are masked
     * @return the parameters, written
     */
    public static String formParameters(final Iterable<String[]> parameters, final String... maskedParameters) {
        val masked = Set.of(maskedParameters);
        val builder = new StringBuilder();
        for (val parameter : parameters) {
            if (!builder.isEmpty()) {
                builder.append('&');
            }
            builder.append(parameter[0]).append('=').append(masked.contains(parameter[0]) ? MASK : parameter[1]);
        }
        return builder.toString();
    }

    /**
     * <p>Mask some parameters of a form or a query string, already written.</p>
     *
     * @param form the form, as {@code name=value&name=value}
     * @param maskedParameters the names of the parameters whose values are masked
     * @return the form, some values being masked
     */
    public static String maskForm(final String form, final String... maskedParameters) {
        if (form == null || form.isEmpty() || maskedParameters.length == 0) {
            return form;
        }
        val masked = Set.of(maskedParameters);
        return Arrays.stream(form.split("&")).map(pair -> {
            val equals = pair.indexOf('=');
            val name = equals < 0 ? pair : pair.substring(0, equals);
            return masked.contains(name) ? name + "=" + MASK : pair;
        }).collect(Collectors.joining("&"));
    }

    /**
     * <p>Make a value fit on one line of log: what comes from a request must not forge other lines.</p>
     *
     * @param value the value
     * @return the value, carriage returns and line feeds replaced by spaces
     */
    public static String oneLine(final String value) {
        return value == null ? null : value.replace('\r', ' ').replace('\n', ' ');
    }

    private void log(final String exchange, final String direction, final String party, final String message) {
        if (isEnabled()) {
            if (exchange == null) {
                logger.debug("{} {} {}", direction, party, oneLine(message));
            } else {
                logger.debug("[{}] {} {} {}", oneLine(exchange), direction, party, oneLine(message));
            }
        }
    }
}
