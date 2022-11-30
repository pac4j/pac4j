package org.pac4j.cas.client.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apereo.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.HttpUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This is {@link CasRestFormClient} able to communicate to
 * a CAS server via its REST api, to retrieve TGTs and STs
 * and to construct CAS principals.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public abstract class AbstractCasRestClient extends DirectClient {

    @Getter
    @Setter
    protected CasConfiguration configuration;

    public void destroyTicketGrantingTicket(final CasRestProfile profile, final WebContext context) {
        HttpURLConnection connection = null;
        try {
            val endpointURL = new URL(configuration.computeFinalRestUrl(context));
            val deleteURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile.getTicketGrantingTicketId());
            connection = HttpUtils.openDeleteConnection(deleteURL);
            val responseCode = connection.getResponseCode();
            if (responseCode != HttpConstants.OK) {
                throw new TechnicalException("TGT delete request for `" + profile + "` failed: " +
                        HttpUtils.buildHttpErrorMessage(connection));
            }
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    public TokenCredentials requestServiceTicket(final String serviceURL, final CasRestProfile profile, final WebContext context) {
        HttpURLConnection connection = null;
        try {
            val endpointURL = new URL(configuration.computeFinalRestUrl(context));
            val ticketURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile.getTicketGrantingTicketId());

            connection = HttpUtils.openPostConnection(ticketURL);
            val payload = HttpUtils.encodeQueryParam("service", serviceURL);

            val out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
            out.write(payload);
            out.close();

            val responseCode = connection.getResponseCode();
            if (responseCode == HttpConstants.OK) {
                try (var in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    return new TokenCredentials(in.readLine());
                }
            }
            throw new TechnicalException("Service ticket request for `" + profile + "` failed: " +
                    HttpUtils.buildHttpErrorMessage(connection));
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    public CasProfile validateServiceTicket(final String serviceURL, final TokenCredentials ticket, final WebContext context) {
        try {
            val assertion = configuration.retrieveTicketValidator(context).validate(ticket.getToken(), serviceURL);
            val principal = assertion.getPrincipal();
            val casProfile = new CasProfile();
            casProfile.setId(ProfileHelper.sanitizeIdentifier(principal.getName()));
            casProfile.addAttributes(principal.getAttributes());
            return casProfile;
        } catch (final TicketValidationException e) {
            throw new TechnicalException(e);
        }
    }
}
