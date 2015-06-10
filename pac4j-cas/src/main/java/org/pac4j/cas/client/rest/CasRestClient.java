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


import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.http.client.direct.DirectHttpClient;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;

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
    }

    @Override
    protected BaseClient<UsernamePasswordCredentials, HttpProfile> newClient() {
        final CasRestClient client = new CasRestClient();
        client.setAuthenticator(getAuthenticator());
        return client;
    }

    @Override
    public Mechanism getMechanism() {
        return Mechanism.CAS_REST_PROTOCOL;
    }
}
