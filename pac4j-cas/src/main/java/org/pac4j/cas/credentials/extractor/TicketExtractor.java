package org.pac4j.cas.credentials.extractor;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS ticket extractor (or logout request handler).
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class TicketExtractor implements CredentialsExtractor<CasCredentials> {

    private final static Logger logger = LoggerFactory.getLogger(TicketExtractor.class);

    private final CasConfiguration configuration;

    private final String clientName;

    public TicketExtractor(final CasConfiguration configuration, final String clientName) {
        this.configuration = configuration;
        this.clientName = clientName;
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("clientName", clientName);
    }

    @Override
    public CasCredentials extract(WebContext context) throws HttpAction {
        // like the SingleSignOutFilter from the Apereo CAS client:
        if (configuration.getLogoutHandler().isTokenRequest(context)) {
            final String ticket = context.getRequestParameter(CasConfiguration.SERVICE_TICKET_PARAMETER);
            configuration.getLogoutHandler().recordSession(context, ticket);
            final CasCredentials casCredentials = new CasCredentials(ticket, clientName);
            logger.debug("casCredentials: {}", casCredentials);
            return casCredentials;
        }

        if (configuration.getLogoutHandler().isLogoutRequest(context)) {
            configuration.getLogoutHandler().destroySession(context);
            final String message = "logout request: no credential returned";
            logger.debug(message);
            throw HttpAction.ok(message, context);
        }

        return null;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clientName", clientName);
    }
}
