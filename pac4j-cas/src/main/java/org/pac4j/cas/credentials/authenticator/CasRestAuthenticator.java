package org.pac4j.cas.credentials.authenticator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a specific Authenticator to deal with the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestAuthenticator implements Authenticator {

    private final static Logger logger = LoggerFactory.getLogger(CasRestAuthenticator.class);

    protected CasConfiguration configuration;

    public CasRestAuthenticator(final CasConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Override
    public void validate(final Credentials cred, final WebContext context) {
        final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) cred;
        if (credentials == null || credentials.getPassword() == null || credentials.getUsername() == null) {
            throw new TechnicalException("Credentials are required");
        }
        final String ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword(), context);
        if (CommonHelper.isNotBlank(ticketGrantingTicketId)) {
            credentials.setUserProfile(new CasRestProfile(ticketGrantingTicketId, credentials.getUsername()));
        }
    }

    private String requestTicketGrantingTicket(final String username, final String password, final WebContext context) {
        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(this.configuration.computeFinalRestUrl(context)));
            final String payload = HttpUtils.encodeQueryParam(Pac4jConstants.USERNAME, username)
                    + "&" + HttpUtils.encodeQueryParam(Pac4jConstants.PASSWORD, password);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            out.write(payload);
            out.close();

            final String locationHeader = connection.getHeaderField("location");
            final int responseCode = connection.getResponseCode();
            if (locationHeader != null && responseCode == HttpConstants.CREATED) {
                return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            }

            logger.debug("Ticket granting ticket request failed: " + locationHeader + " " + responseCode +
                HttpUtils.buildHttpErrorMessage(connection));

            return null;
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }
}
