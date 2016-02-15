/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.cas.client.direct;

import org.junit.Test;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.profile.HttpTGTProfile;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.Authenticator;
import org.pac4j.http.credentials.authenticator.LocalCachingAuthenticator;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestClientIT implements TestsConstants {

    private final static String CAS_REST_URL = "http://casserverpac4j.herokuapp.com/";

    @Test
    public void testRestForm() throws RequiresHttpAction {
        internalTestRestForm(new CasRestAuthenticator(CAS_REST_URL));
    }

    @Test
    public void testRestFormWithCaching() throws RequiresHttpAction {
        internalTestRestForm(new LocalCachingAuthenticator<>(new CasRestAuthenticator(CAS_REST_URL), 100, 100, TimeUnit.SECONDS));
    }

    private void internalTestRestForm(final Authenticator authenticator) throws RequiresHttpAction {
        final CasRestFormClient client = new CasRestFormClient(authenticator);

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getUsername(), USERNAME);
        context.addRequestParameter(client.getPassword(), USERNAME);

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final HttpTGTProfile profile = (HttpTGTProfile) client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USERNAME, casProfile.getId());
    }

    @Test
    public void testRestBasic() throws RequiresHttpAction {
        final CasRestBasicAuthClient client = new CasRestBasicAuthClient(new CasRestAuthenticator(CAS_REST_URL), VALUE, NAME);

        final MockWebContext context = MockWebContext.create();
        final String token = USERNAME + ":" + USERNAME;
        context.addRequestHeader(VALUE, NAME + Base64.getEncoder().encodeToString(token.getBytes()));

        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final HttpTGTProfile profile = (HttpTGTProfile) client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
        assertNotNull(profile.getTicketGrantingTicketId());

        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        assertEquals(USERNAME, casProfile.getId());
        client.destroyTicketGrantingTicket(context, profile);

        try {
            client.requestServiceTicket(PAC4J_BASE_URL, profile);
            fail("shoud fail");
        } catch (final TechnicalException e) {
            assertEquals("Service ticket request for `<HttpTGTProfile> | id: username | attributes: {} | roles: [] | permissions: [] | isRemembered: false |` failed: (404) Not Found", e.getMessage());
        }
    }
}
