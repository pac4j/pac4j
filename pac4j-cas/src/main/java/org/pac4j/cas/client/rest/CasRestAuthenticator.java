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

package org.pac4j.cas.client.rest;

import org.apache.commons.httpclient.HttpStatus;
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
    private final URL endpointURL;

    public CasRestAuthenticator(final URL endpointURL) {
        this.endpointURL = endpointURL;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        final String ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword());
        final HttpTGTProfile profile = new HttpTGTProfile(ticketGrantingTicketId, credentials.getUsername());
        credentials.setUserProfile(profile);
    }

    private String requestTicketGrantingTicket(final String username, final String password)  {

        try (final HttpURLConnection connection = HttpUtils.openConnection(endpointURL)) {
            final String payload = HttpUtils.encodeQueryParam("username", username)
                    + "&" + HttpUtils.encodeQueryParam("password", password);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.close();

            final String locationHeader = connection.getHeaderField("location");
            final int responseCode = connection.getResponseCode();
            if (locationHeader != null && responseCode == HttpStatus.SC_CREATED) {
                return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            }

            throw new IllegalStateException("Ticket granting ticket request failed: " +
                    HttpUtils.statusCodeAndErrorMessage(connection));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getEndpointURL() {
        return endpointURL;
    }
}
