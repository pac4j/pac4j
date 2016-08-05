package org.pac4j.cas.credentials.extractor;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.zip.Inflater;

/**
 * CAS ticket extractor or logout request handler.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class TicketAndLogoutRequestExtractor extends InitializableWebObject implements CredentialsExtractor<TokenCredentials> {

    private final static int DECOMPRESSION_FACTOR = 10;

    private final static Logger logger = LoggerFactory.getLogger(TicketAndLogoutRequestExtractor.class);

    private CasConfiguration configuration;

    private String clientName;

    public TicketAndLogoutRequestExtractor() {}

    public TicketAndLogoutRequestExtractor(final CasConfiguration configuration, final String clientName) {
        this.configuration = configuration;
        this.clientName = clientName;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("clientName", clientName);

        configuration.init(context);
    }

    @Override
    public TokenCredentials extract(WebContext context) throws HttpAction {
        init(context);

        // like the SingleSignOutFilter from the Apereo CAS client:
        if (isTokenRequest(context)) {
            final String ticket = context.getRequestParameter(CasConfiguration.TICKET_PARAMETER);
            configuration.getLogoutHandler().recordSession(context, ticket);
            final TokenCredentials casCredentials = new TokenCredentials(ticket, clientName);
            logger.debug("casCredentials: {}", casCredentials);
            return casCredentials;

        } else if (isBackLogoutRequest(context)) {
            final String logoutMessage = context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER);
            logger.trace("Logout request:\n{}", logoutMessage);

            final String ticket = CommonHelper.substringBetween(logoutMessage, CasConfiguration.SESSION_INDEX_TAG + ">", "</");
            if (CommonUtils.isNotBlank(ticket)) {
                configuration.getLogoutHandler().destroySessionBack(context, ticket);
            }
            final String message = "back logout request: no credential returned";
            logger.debug(message);
            throw HttpAction.ok(message, context);

        } else if (isFrontLogoutRequest(context)) {
            final String logoutMessage = uncompressLogoutMessage(context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER));
            logger.trace("Logout request:\n{}", logoutMessage);

            final String ticket = CommonHelper.substringBetween(logoutMessage, CasConfiguration.SESSION_INDEX_TAG + ">", "</");
            if (CommonUtils.isNotBlank(ticket)) {
                configuration.getLogoutHandler().destroySessionFront(context, ticket);
            }
            logger.debug("front logout request: no credential returned");
            computeRedirectionToServerIfNecessary(context);
        }

        return null;
    }

    protected boolean isTokenRequest(final WebContext context) {
        return ContextHelper.isGet(context)
                && CommonHelper.isNotBlank(context.getRequestParameter(CasConfiguration.TICKET_PARAMETER));
    }

    protected boolean isBackLogoutRequest(final WebContext context) {
        return ContextHelper.isPost(context)
                && !isMultipartRequest(context)
                && CommonHelper.isNotBlank(context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER));
    }

    private boolean isMultipartRequest(final WebContext context) {
        final String contentType = context.getRequestHeader(HttpConstants.CONTENT_TYPE_HEADER);
        return contentType != null && contentType.toLowerCase().startsWith("multipart");
    }

    private boolean isFrontLogoutRequest(final WebContext context) {
        return ContextHelper.isGet(context)
                && CommonHelper.isNotBlank(context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER));
    }

    private String uncompressLogoutMessage(final String originalMessage) {
        final byte[] binaryMessage = DatatypeConverter.parseBase64Binary(originalMessage);

        Inflater decompresser = null;
        try {
            // decompress the bytes
            decompresser = new Inflater();
            decompresser.setInput(binaryMessage);
            final byte[] result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];

            final int resultLength = decompresser.inflate(result);

            // decode the bytes into a String
            return new String(result, 0, resultLength, "UTF-8");
        } catch (final Exception e) {
            logger.error("Unable to decompress logout message", e);
            throw new TechnicalException(e);
        } finally {
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }

    private void computeRedirectionToServerIfNecessary(final WebContext context) throws HttpAction {
        final String relayStateValue = context.getRequestParameter(CasConfiguration.RELAY_STATE_PARAMETER);
        // if we have a state value -> redirect to the CAS server to continue the logout process
        if (CommonUtils.isNotBlank(relayStateValue)) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(configuration.getPrefixUrl());
            if (!configuration.getPrefixUrl().endsWith("/")) {
                buffer.append("/");
            }
            buffer.append("logout?_eventId=next&");
            buffer.append(CasConfiguration.RELAY_STATE_PARAMETER);
            buffer.append("=");
            buffer.append(CommonUtils.urlEncode(relayStateValue));
            final String redirectUrl = buffer.toString();
            logger.debug("Redirection url to the CAS server: {}", redirectUrl);
            throw HttpAction.redirect("Force redirect to CAS server for front channel logout", context, redirectUrl);
        }
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CasConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clientName", clientName);
    }
}
