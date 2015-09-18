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
package org.pac4j.core.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultClientFinderTests implements TestsConstants {

    private final DefaultClientFinder finder = new DefaultClientFinder();

    @Test
    public void testBlankClientName() {
        final List<Client> currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        assertEquals(0, currentClients.size());
    }

    @Test
    public void testClientOnRequestAllowed() {
        final Client client = new MockBaseClient(NAME);
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final List<Client> currentClients = finder.find(clients, context, NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @Test(expected = TechnicalException.class)
    public void testBadClientOnRequest() {
        final Client client = new MockBaseClient(NAME);
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, FAKE_VALUE);
        finder.find(clients, context, NAME);
    }

    @Test
    public void testClientOnRequestAllowedList() {
        final Client client = new MockBaseClient(NAME);
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final List<Client> currentClients = finder.find(clients, context, FAKE_VALUE + "," + NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @Test(expected = TechnicalException.class)
    public void testClientOnRequestNotAllowed() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        finder.find(clients, context, CLIENT_NAME);
    }

    @Test(expected = TechnicalException.class)
    public void testClientOnRequestNotAllowedList() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        finder.find(clients, context, CLIENT_NAME + "," + FAKE_VALUE);
    }

    @Test
    public void testNoClientOnRequest() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, CLIENT_NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client2, currentClients.get(0));
    }

    @Test(expected = TechnicalException.class)
    public void testNoClientOnRequestBadDefaultClient() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        finder.find(clients, context, FAKE_VALUE);
    }

    @Test
    public void testNoClientOnRequestList() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, CLIENT_NAME + "," + NAME);
        assertEquals(2, currentClients.size());
        assertEquals(client2, currentClients.get(0));
        assertEquals(client1, currentClients.get(1));
    }
}
