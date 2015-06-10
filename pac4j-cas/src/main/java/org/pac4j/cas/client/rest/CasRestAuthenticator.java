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

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This is {@link CasRestAuthenticator}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestAuthenticator implements Authenticator<UsernamePasswordCredentials> {
    private static final String DEFAULT_QUERY_PARAM_ENCODING = "UTF-8";

    private final URL endpointURL;

    public CasRestAuthenticator(final URL endpointURL) {
        this.endpointURL = endpointURL;
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        final String ticketGrantingTicketId = requestTicketGrantingTicket(credentials.getUsername(), credentials.getPassword());
        final String serviceTicketId = requestServiceTicket()
    }

    private String requestTicketGrantingTicket(final String username, final String password)  {
        HttpURLConnection connection = null;
        try {
            connection = openConnection(endpointURL);
            final String payload = encodeQueryParam("username", username) + "&"
                    + encodeQueryParam("password", password);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.close();

            final String locationHeader = connection.getHeaderField("location");
            final int responseCode = connection.getResponseCode();
            if (locationHeader != null && responseCode == 201) {
                return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            }

            throw new IllegalStateException("Ticket granting ticket request failed: " +
                    statusCodeAndErrorMessage(connection));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private String requestServiceTicket(final URL serviceURL, final String ticketGrantingTicket) {
        HttpURLConnection connection = null;

        try {
            final URL ticketURL = new URL(endpointURL, endpointURL.getPath() + "/" + ticketGrantingTicket);
            connection = openConnection(ticketURL);
            final String payload = encodeQueryParam("service", serviceURL.toString());

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.close();

            final int responseCode = connection.getResponseCode();

            if (responseCode == HttpStatus.SC_OK) {
                final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return in.readLine();
            }

            throw new IllegalStateException("Service ticket request for `" + ticketGrantingTicket + "` failed: " +
                    statusCodeAndErrorMessage(connection));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }


    private String statusCodeAndErrorMessage(final HttpURLConnection connection) throws IOException {
        final StringBuilder messageBuilder = new StringBuilder("(").append(connection.getResponseCode()).append(")");
        if (connection.getResponseMessage() != null) {
            messageBuilder.append(" ");
            messageBuilder.append(connection.getResponseMessage());
        }
        return messageBuilder.toString();
    }

    private String encodeQueryParam(final String paramName, final String paramValue) throws UnsupportedEncodingException {
        return URLEncoder.encode(paramName, DEFAULT_QUERY_PARAM_ENCODING) + "=" + URLEncoder.encode(paramValue, DEFAULT_QUERY_PARAM_ENCODING);
    }

    private HttpURLConnection openConnection(final URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        return connection;
    }

    private void closeConnection(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
