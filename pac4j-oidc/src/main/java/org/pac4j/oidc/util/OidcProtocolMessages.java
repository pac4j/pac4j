package org.pac4j.oidc.util;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import lombok.experimental.UtilityClass;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.ProtocolMessages;

/**
 * The messages exchanged with the browser and the OpenID provider, logged raw on the {@code PROTOCOL_MESSAGE.OIDC} logger,
 * at debug level.
 *
 * <p>The client secret and the client assertion the application authenticates with are masked; the authorization
 * header, carrying the client credentials or the access token, is not logged.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class OidcProtocolMessages {

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages("OIDC");

    /** The other party: the OpenID provider. */
    public static final String OPENID_PROVIDER = "OpenID provider";

    private static final String[] SECRETS = {OidcConstants.CLIENT_SECRET, OidcConstants.CLIENT_ASSERTION};

    /**
     * <p>Log a message sent to the browser.</p>
     *
     * @param message the message
     */
    public void sentToBrowser(final String message) {
        LOGGER.sent(ProtocolMessages.BROWSER, message);
    }

    /**
     * <p>Log a request received from the browser or the OpenID provider.</p>
     *
     * @param from the party
     * @param context the web context
     */
    public void received(final String from, final WebContext context) {
        LOGGER.received(null, from, context);
    }

    /**
     * <p>Log a request sent to the OpenID provider: its method, its URL and its body, the secrets being masked.</p>
     *
     * @param request the request
     */
    public void sent(final HTTPRequest request) {
        if (LOGGER.isEnabled()) {
            final var body = request.getBody();
            LOGGER.sent(OPENID_PROVIDER, request.getMethod() + " " + request.getURL()
                + (body == null || body.isEmpty() ? "" : " " + ProtocolMessages.maskForm(body, SECRETS)));
        }
    }

    /**
     * <p>Log a response received from the OpenID provider: its status and its body.</p>
     *
     * @param response the response
     */
    public void received(final HTTPResponse response) {
        if (LOGGER.isEnabled()) {
            LOGGER.received(OPENID_PROVIDER, response.getStatusCode() + " " + response.getBody());
        }
    }
}
