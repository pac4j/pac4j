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
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.client.direct.DirectHttpClient;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.extractor.Extractor;
import org.pac4j.http.credentials.extractor.FormExtractor;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is {@link CasRestClient}.
 *
 * @author Misagh Moayyed
 * @author Daniel Pacak
 * @since 1.8.0
 */
public class CasRestClient extends DirectHttpClient<UsernamePasswordCredentials> {
    public CasRestClient() {
    }

    public CasRestClient(final CasRestAuthenticator authenticator) {
        setAuthenticator(authenticator);
        setProfileCreator(new AuthenticatorProfileCreator<UsernamePasswordCredentials, HttpProfile>());
        this.extractor = new FormExtractor(authenticator.getUsernameParameter(),
                                            authenticator.getPasswordParameter(),
                                            CasRestClient.class.getSimpleName());
    }

    @Override
    protected BaseClient<UsernamePasswordCredentials, HttpProfile> newClient() {
        final CasRestClient client = new CasRestClient();
        client.setAuthenticator(getAuthenticator());
        return client;
    }

    public HttpProfile requestTicketGrantingTicket(final WebContext context) {
        final UsernamePasswordCredentials creds = this.extractor.extract(context);
        getAuthenticator().validate(creds);
        return getProfileCreator().create(creds);
    }

    public CasCredentials requestServiceTicket(final URL serviceURL, final HttpProfile profile) {
        HttpURLConnection connection = null;
        try {
            final URL endpointURL = getAuthenticator().getEndpointURL();
            final URL ticketURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile);

            connection = HttpUtils.openConnection(ticketURL);
            final String payload = HttpUtils.encodeQueryParam("service", serviceURL.toString());

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpStatus.SC_OK) {
                final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return new CasCredentials(in.readLine(), getClass().getSimpleName());
            }
            throw new TechnicalException("Service ticket request for `" + profile + "` failed: " +
                    HttpUtils.buildHttpErrorMessage(connection));
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    public CasProfile validateServiceTicket(final URL serviceURL, final CasCredentials ticket) {
        try {
            final Assertion assertion = getAuthenticator().getTicketValidator()
                    .validate(ticket.getServiceTicket(), serviceURL.toExternalForm());
            final AttributePrincipal principal = assertion.getPrincipal();
            final CasProfile casProfile = new CasProfile();
            casProfile.setId(principal.getName());
            casProfile.addAttributes(principal.getAttributes());
            return casProfile;
        } catch (final TicketValidationException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public Mechanism getMechanism() {
        return Mechanism.FORM_MECHANISM;
    }

    @Override
    public CasRestAuthenticator getAuthenticator() {
        return (CasRestAuthenticator) super.getAuthenticator();
    }
}
