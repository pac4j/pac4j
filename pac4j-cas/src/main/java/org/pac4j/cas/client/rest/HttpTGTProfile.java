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

import org.pac4j.http.profile.HttpProfile;

/**
 * This is {@link HttpTGTProfile} that represents
 * the CAS TGT when it's obtained via the rest api.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class HttpTGTProfile extends HttpProfile {
    private static final long serialVersionUID = -1688563185891330018L;
    private final String ticketGrantingTicketId;

    public HttpTGTProfile() {
        this.ticketGrantingTicketId = null;
    }

    public HttpTGTProfile(final String ticketGrantingTicketId) {
        super();
        this.ticketGrantingTicketId = ticketGrantingTicketId;
    }

    public String getTicketGrantingTicketId() {
        return ticketGrantingTicketId;
    }

    @Override
    public String toString() {
        return getTicketGrantingTicketId();
    }

    @Override
    public String getId() {
        return getTicketGrantingTicketId();
    }
}
