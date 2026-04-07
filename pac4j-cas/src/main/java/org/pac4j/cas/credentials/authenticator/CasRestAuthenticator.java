package org.pac4j.cas.credentials.authenticator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.core.util.Pac4jConstants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

import static org.pac4j.core.context.HttpConstants.APPLICATION_FORM_ENCODED_HEADER_VALUE;
import static org.pac4j.core.context.HttpConstants.CONTENT_TYPE_HEADER;

/**
 * This is a specific Authenticator to deal with the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@Slf4j
public class CasRestAuthenticator implements Authenticator {

    protected CasConfiguration configuration;

    /**
     * <p>Constructor for CasRestAuthenticator.</p>
     *
     * @param configuration a {@link CasConfiguration} object
     */
    public CasRestAuthenticator(final CasConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        val credentials = (UsernamePasswordCredentials) cred;
        if (credentials == null || credentials.getPassword() == null || credentials.getUsername() == null) {
            throw new TechnicalException("Credentials are required");
        }
        val ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword(), ctx.webContext());
        if (StringUtils.isNotBlank(ticketGrantingTicketId)) {
            credentials.setUserProfile(new CasRestProfile(ticketGrantingTicketId, credentials.getUsername()));
        }
        return Optional.of(credentials);
    }

    private String requestTicketGrantingTicket(final String username, final String password, final WebContext context) {
        HttpURLConnection connection = null;
        try {
            val headers = new HashMap<String, String>();
            headers.put(CONTENT_TYPE_HEADER, APPLICATION_FORM_ENCODED_HEADER_VALUE);

            connection = HttpUtils.openPostConnection(new URL(this.configuration.computeFinalRestUrl(context)), headers);
            val payload = HttpUtils.encodeQueryParam(Pac4jConstants.USERNAME, username)
                    + "&" + HttpUtils.encodeQueryParam(Pac4jConstants.PASSWORD, password);

            HttpUtils.postBody(connection, payload);

            val locationHeader = connection.getHeaderField("location");
            val responseCode = connection.getResponseCode();
            if (locationHeader != null && responseCode == HttpConstants.CREATED) {
                return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            }

            throw new CredentialsException("Ticket granting ticket request failed: " + locationHeader + " " + responseCode +
                HttpUtils.buildHttpErrorMessage(connection));
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }
}
