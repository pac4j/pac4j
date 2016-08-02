package org.pac4j.cas.client.rest;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.cas.util.HttpUtils;
import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is {@link CasRestFormClient} able to communicate to
 * a CAS server via its REST api, to retrieve TGTs and STs
 * and to construct CAS principals.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public abstract class AbstractCasRestClient extends DirectClientV2<UsernamePasswordCredentials, CasRestProfile> {

    public void destroyTicketGrantingTicket(final CasRestProfile profile) {
        HttpURLConnection connection = null;
        try {
            final URL endpointURL = new URL(getCasRestAuthenticator().getCasRestUrl());
            final URL deleteURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile.getTicketGrantingTicketId());
            connection = HttpUtils.openDeleteConnection(deleteURL);
            final int responseCode = connection.getResponseCode();
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

    public TokenCredentials requestServiceTicket(final String serviceURL, final CasRestProfile profile) {
        HttpURLConnection connection = null;
        try {
            final URL endpointURL = new URL(getCasRestAuthenticator().getCasRestUrl());
            final URL ticketURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile.getTicketGrantingTicketId());

            connection = HttpUtils.openPostConnection(ticketURL);
            final String payload = HttpUtils.encodeQueryParam("service", serviceURL);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), HttpConstants.UTF8_ENCODING));
            out.write(payload);
            out.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpConstants.OK) {
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), HttpConstants.UTF8_ENCODING))) {
                    return new TokenCredentials(in.readLine(), getClass().getSimpleName());
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

    public CasProfile validateServiceTicket(final String serviceURL, final TokenCredentials ticket) {
        try {
            final Assertion assertion = getCasRestAuthenticator().getTicketValidator()
                    .validate(ticket.getToken(), serviceURL);
            final AttributePrincipal principal = assertion.getPrincipal();
            final CasProfile casProfile = new CasProfile();
            casProfile.setId(principal.getName());
            casProfile.addAttributes(principal.getAttributes());
            return casProfile;
        } catch (final TicketValidationException e) {
            throw new TechnicalException(e);
        }
    }

    public CasRestAuthenticator getCasRestAuthenticator() {
        Authenticator authenticator = getAuthenticator();
        if (authenticator instanceof LocalCachingAuthenticator) {
            authenticator = ((LocalCachingAuthenticator) authenticator).getDelegate();
        }
        if (authenticator instanceof CasRestAuthenticator) {
            return (CasRestAuthenticator) authenticator;
        }
        throw new TechnicalException("authenticator must be a CasRestAuthenticator (or via a LocalCachingAuthenticator)");
    }
}
