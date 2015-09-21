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
package org.pac4j.cas.profile;

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

    private final String userName;
    private String ticketGrantingTicketId;

    public HttpTGTProfile() {
        this.ticketGrantingTicketId = null;
        this.userName = null;
    }

    public HttpTGTProfile(final String ticketGrantingTicketId, final String userName) {
        super();
        this.ticketGrantingTicketId = ticketGrantingTicketId;
        this.userName = userName;
    }

    public String getTicketGrantingTicketId() {
        return ticketGrantingTicketId;
    }

    @Override
    public void clear() {
        this.ticketGrantingTicketId = null;
    }

    @Override
    public String toString() {
        return this.userName;
    }

    @Override
    public int hashCode() {
        return this.ticketGrantingTicketId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof HttpTGTProfile)) {
            return false;
        }

        return (obj == this) ||
                this.ticketGrantingTicketId.equals(((HttpTGTProfile)obj).ticketGrantingTicketId);
    }
}
