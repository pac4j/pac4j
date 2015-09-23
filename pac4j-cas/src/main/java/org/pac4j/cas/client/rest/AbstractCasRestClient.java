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

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.HttpTGTProfile;
import org.pac4j.cas.util.HttpUtils;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.client.direct.DirectHttpClient;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
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
 * This is {@link CasRestFormClient} able to communicate to
 * a CAS server via its REST api, to retrieve TGTs and STs
 * and to construct CAS principals.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public abstract class AbstractCasRestClient extends DirectHttpClient<UsernamePasswordCredentials> {
    public AbstractCasRestClient() {
        super();
    }

    public AbstractCasRestClient(final CasRestAuthenticator authenticator) {
        setAuthenticator(authenticator);
        setProfileCreator(new AuthenticatorProfileCreator<UsernamePasswordCredentials, HttpProfile>());
    }

    public HttpTGTProfile requestTicketGrantingTicket(final WebContext context) {
        final UsernamePasswordCredentials creds = this.extractor.extract(context);
        getAuthenticator().validate(creds);
        return (HttpTGTProfile) getProfileCreator().create(creds);
    }

    public void destroyTicketGrantingTicket(final WebContext context, final HttpTGTProfile profile) {
        HttpURLConnection connection = null;
        try {
            final URL endpointURL = new URL(getAuthenticator().getCasRestUrl());
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

    public CasCredentials requestServiceTicket(final String serviceURL, final HttpTGTProfile profile) {
        HttpURLConnection connection = null;
        try {
            final URL endpointURL = new URL(getAuthenticator().getCasRestUrl());
            final URL ticketURL = new URL(endpointURL, endpointURL.getPath() + "/" + profile.getTicketGrantingTicketId());

            connection = HttpUtils.openPostConnection(ticketURL);
            final String payload = HttpUtils.encodeQueryParam("service", serviceURL);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(payload);
            out.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpConstants.OK) {
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

    public CasProfile validateServiceTicket(final String serviceURL, final CasCredentials ticket) {
        try {
            final Assertion assertion = getAuthenticator().getTicketValidator()
                    .validate(ticket.getServiceTicket(), serviceURL);
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
    public CasRestAuthenticator getAuthenticator() {
        return (CasRestAuthenticator) super.getAuthenticator();
    }

    @Override
    protected BaseClient<UsernamePasswordCredentials, HttpProfile> newClient() {
        final AbstractCasRestClient client = newClientType();
        client.setAuthenticator(getAuthenticator());
        return client;
    }

    protected abstract AbstractCasRestClient newClientType();
}
