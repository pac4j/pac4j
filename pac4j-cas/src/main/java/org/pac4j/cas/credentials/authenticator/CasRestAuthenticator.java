/*
 * Copyright 2012 - 2015 pac4j organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.pac4j.cas.credentials.authenticator;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.pac4j.cas.util.HttpUtils;
import org.pac4j.cas.profile.HttpTGTProfile;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.Authenticator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is {@link CasRestAuthenticator}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestAuthenticator implements Authenticator<UsernamePasswordCredentials> {
    private final String casServerPrefixUrl;
    private final String casRestUrl;

    public CasRestAuthenticator(final String casServerPrefixUrl) {
        this(casServerPrefixUrl, buildCasRestUrlFromCasServerPrefixUrl(casServerPrefixUrl));
    }

    private static String buildCasRestUrlFromCasServerPrefixUrl(final String casServerPrefixUrl) {
        String restUrl = casServerPrefixUrl;
        if (!restUrl.endsWith("/")) {
            restUrl += "/";
        }
        restUrl += "v1/tickets";
        return restUrl;
    }

    public CasRestAuthenticator(final String casServerPrefixUrl, final String casRestUrl) {
        this.casServerPrefixUrl = casServerPrefixUrl;
        this.casRestUrl = casRestUrl;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        if (credentials == null || credentials.getPassword() == null || credentials.getUsername() == null) {
            throw new TechnicalException("Credentials are required");
        }
        final String ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword());
        final HttpTGTProfile profile = new HttpTGTProfile(ticketGrantingTicketId, credentials.getUsername());
        credentials.setUserProfile(profile);
    }

    private String requestTicketGrantingTicket(final String username, final String password) {
        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(getCasRestUrl()));
            final String payload = HttpUtils.encodeQueryParam(getUsernameParameter(), username)
                    + "&" + HttpUtils.encodeQueryParam(getPasswordParameter(), password);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
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

    public String getUsernameParameter() {
        return "username";
    }

    public String getPasswordParameter() {
        return "password";
    }

    public String getCasServerPrefixUrl() {
        return casServerPrefixUrl;
    }

    public String getCasRestUrl() {
        return casRestUrl;
    }

    public TicketValidator getTicketValidator() {
        return new Cas20ServiceTicketValidator(getCasServerPrefixUrl());
    }
}

