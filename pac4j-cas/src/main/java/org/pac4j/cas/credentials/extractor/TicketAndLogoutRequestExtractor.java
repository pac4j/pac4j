package org.pac4j.cas.credentials.extractor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.Protocol;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Base64;
import java.util.Optional;
import java.util.zip.Inflater;

/**
 * CAS ticket extractor or logout request handler.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class TicketAndLogoutRequestExtractor implements CredentialsExtractor {

    private final static int DECOMPRESSION_FACTOR = 10;

    protected CasConfiguration configuration;

    public TicketAndLogoutRequestExtractor(final CasConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val logoutHandler = configuration.findLogoutHandler();

        val webContext = ctx.webContext();

        // like the SingleSignOutFilter from the Apereo CAS client:
        if (isTokenRequest(webContext)) {
            val ticket = getArtifactParameter(webContext).get();
            logoutHandler.recordSession(ctx, ticket);
            val casCredentials = new TokenCredentials(ticket);
            LOGGER.debug("casCredentials: {}", casCredentials);
            return Optional.of(casCredentials);

        } else if (isBackLogoutRequest(webContext)) {
            val logoutMessage = webContext.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).get();
            LOGGER.trace("Logout request:\n{}", logoutMessage);

            val ticket = CommonHelper.substringBetween(logoutMessage, CasConfiguration.SESSION_INDEX_TAG + ">", "</");
            if (CommonHelper.isNotBlank(ticket)) {
                logoutHandler.destroySessionBack(ctx, ticket);
            }
            LOGGER.debug("back logout request: no credential returned");
            throw NoContentAction.INSTANCE;

        } else if (isFrontLogoutRequest(webContext)) {
            val logoutMessage = uncompressLogoutMessage(
                webContext.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).get());
            LOGGER.trace("Logout request:\n{}", logoutMessage);

            val ticket = CommonHelper.substringBetween(logoutMessage, CasConfiguration.SESSION_INDEX_TAG + ">", "</");
            if (CommonHelper.isNotBlank(ticket)) {
                logoutHandler.destroySessionFront(ctx, ticket);
            }
            LOGGER.debug("front logout request: no credential returned");
            throwFinalActionForFrontChannelLogout(webContext);
        }

        return Optional.empty();
    }

    protected boolean isTokenRequest(final WebContext context) {
        return getArtifactParameter(context).isPresent();
    }

    protected Optional<String> getArtifactParameter(final WebContext context) {
        if (configuration.getProtocol() == CasProtocol.SAML) {
            val optValue = context.getRequestParameter(Protocol.SAML11.getArtifactParameterName());
            if (optValue.isPresent()) {
                return optValue;
            }
        }
        return context.getRequestParameter(CasConfiguration.TICKET_PARAMETER);
    }

    protected boolean isBackLogoutRequest(final WebContext context) {
        return WebContextHelper.isPost(context)
                && !isMultipartRequest(context)
                && context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).isPresent();
    }

    protected boolean isMultipartRequest(final WebContext context) {
        val contentType = context.getRequestHeader(HttpConstants.CONTENT_TYPE_HEADER);
        return contentType.isPresent() && contentType.get().toLowerCase().startsWith("multipart");
    }

    protected boolean isFrontLogoutRequest(final WebContext context) {
        return WebContextHelper.isGet(context)
                && context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).isPresent();
    }

    protected String uncompressLogoutMessage(final String originalMessage) {
        val binaryMessage = Base64.getMimeDecoder().decode(originalMessage);

        Inflater decompresser = null;
        try {
            // decompress the bytes
            decompresser = new Inflater();
            decompresser.setInput(binaryMessage);
            val result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];

            val resultLength = decompresser.inflate(result);

            // decode the bytes into a String
            return new String(result, 0, resultLength, "UTF-8");
        } catch (final Exception e) {
            LOGGER.error("Unable to decompress logout message", e);
            throw new TechnicalException(e);
        } finally {
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }

    protected void throwFinalActionForFrontChannelLogout(final WebContext context) {
        val relayStateValue = context.getRequestParameter(CasConfiguration.RELAY_STATE_PARAMETER);
        // if we have a state value -> redirect to the CAS server to continue the logout process
        if (relayStateValue.isPresent()) {
            val buffer = new StringBuilder();
            buffer.append(configuration.getPrefixUrl());
            if (!configuration.getPrefixUrl().endsWith("/")) {
                buffer.append("/");
            }
            buffer.append("logout?_eventId=next&");
            buffer.append(CasConfiguration.RELAY_STATE_PARAMETER);
            buffer.append("=");
            buffer.append(CommonHelper.urlEncode(relayStateValue.get()));
            val redirectUrl = buffer.toString();
            LOGGER.debug("Redirection url to the CAS server: {}", redirectUrl);
            throw HttpActionHelper.buildRedirectUrlAction(context, redirectUrl);
        } else {
            throw new OkAction(Pac4jConstants.EMPTY_STRING);
        }
    }
}
