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
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.UsernamePasswordCredentials;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestClientIT implements TestsConstants {

    private final static String CAS_LOGIN_URL = "http://casserverpac4j.herokuapp.com/";

    @Test
    public void testRestProtocolForm() throws Exception {
        final CasRestAuthenticator authenticator = new CasRestAuthenticator(CAS_LOGIN_URL);
        final CasRestFormClient client = new CasRestFormClient(authenticator);
        final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, USERNAME, client.getName());

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getAuthenticator().getUsernameParameter(), creds.getUsername());
        context.addRequestParameter(client.getAuthenticator().getPasswordParameter(), creds.getPassword());

        final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        client.destroyTicketGrantingTicket(context, profile);
    }

    @Test
    public void testRestProtocolBasic() throws Exception {
        final CasRestAuthenticator authenticator = new CasRestAuthenticator(CAS_LOGIN_URL);
        final CasRestBasicAuthClient client = new CasRestBasicAuthClient(authenticator, "cas-rest", "");
        final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, USERNAME, client.getName());

        final MockWebContext context = MockWebContext.create();
        final String token = creds.getUsername() + ":" + creds.getPassword();
        context.addRequestHeader("cas-rest", Base64.getEncoder().encodeToString(token.getBytes()));

        final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket(PAC4J_BASE_URL, profile);
        final CasProfile casProfile = client.validateServiceTicket(PAC4J_BASE_URL, casCreds);
        assertNotNull(casProfile);
        client.destroyTicketGrantingTicket(context, profile);
    }
}
