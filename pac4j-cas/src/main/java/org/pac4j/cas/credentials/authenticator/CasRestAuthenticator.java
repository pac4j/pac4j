package org.pac4j.cas.credentials.authenticator;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.util.HttpUtils;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is a specific Authenticator to deal with the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestAuthenticator extends InitializableWebObject implements Authenticator<UsernamePasswordCredentials> {

    private CasConfiguration configuration;

    public CasRestAuthenticator() {}

    public CasRestAuthenticator(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init(context);
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction {
        init(context);

        if (credentials == null || credentials.getPassword() == null || credentials.getUsername() == null) {
            throw new TechnicalException("Credentials are required");
        }
        final String ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword(), context);
        final CasRestProfile profile = new CasRestProfile(ticketGrantingTicketId, credentials.getUsername());
        credentials.setUserProfile(profile);
    }

    private String requestTicketGrantingTicket(final String username, final String password, final WebContext context) {
        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(this.configuration.computeFinalRestUrl(context)));
            final String payload = HttpUtils.encodeQueryParam(Pac4jConstants.USERNAME, username)
                    + "&" + HttpUtils.encodeQueryParam(Pac4jConstants.PASSWORD, password);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), HttpConstants.UTF8_ENCODING));
            out.write(payload);
            out.close();

            final String locationHeader = connection.getHeaderField("location");
            final int responseCode = connection.getResponseCode();
            if (locationHeader != null && responseCode == HttpConstants.CREATED) {
                return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            }

            throw new TechnicalException("Ticket granting ticket request failed: " + locationHeader + " " + responseCode +
                    HttpUtils.buildHttpErrorMessage(connection));
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        this.configuration = configuration;
    }
}
