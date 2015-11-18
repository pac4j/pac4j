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
package org.pac4j.config.client;

import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.saml.client.SAML2Client;

import java.util.HashMap;
import java.util.Map;

import static org.pac4j.config.client.ConfigPropertiesFactory.*;
import static org.junit.Assert.*;

/**
 * Tests {@link ConfigPropertiesFactory}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class ConfigPropertiesFactoryTests implements TestsConstants {

    @Test
    public void test() {
        final Map<String, String> properties = new HashMap<>();
        properties.put(FACEBOOK_ID, ID);
        properties.put(FACEBOOK_SECRET, SECRET);
        properties.put(TWITTER_ID, ID);
        properties.put(TWITTER_SECRET, SECRET);
        properties.put(CAS_LOGIN_URL, CALLBACK_URL);
        properties.put(CAS_PROTOCOL, CasClient.CasProtocol.CAS20.toString());
        properties.put(SAML_KEYSTORE_PASSWORD, PASSWORD);
        properties.put(SAML_PRIVATE_KEY_PASSWORD, PASSWORD);
        properties.put(SAML_KEYSTORE_PATH, PATH);
        properties.put(SAML_IDENTITY_PROVIDER_METADATA_PATH, PATH);
        final ConfigPropertiesFactory factory = new ConfigPropertiesFactory(CALLBACK_URL, properties);
        final Config config = factory.build();
        final Clients clients = config.getClients();
        assertEquals(4, clients.getClients().size());
        final FacebookClient fbClient = (FacebookClient) clients.findClient(null, "FacebookClient");
        assertEquals(ID, fbClient.getKey());
        assertEquals(SECRET, fbClient.getSecret());
        final TwitterClient twClient = (TwitterClient) clients.findClient(null, "TwitterClient");
        assertEquals(ID, twClient.getKey());
        assertEquals(SECRET, twClient.getSecret());
        final CasClient casClient = (CasClient) clients.findClient(null, "CasClient");
        assertEquals(CALLBACK_URL, casClient.getCasLoginUrl());
        assertEquals(CasClient.CasProtocol.CAS20, casClient.getCasProtocol());
        final SAML2Client saml2client = (SAML2Client) clients.findClient(null, "SAML2Client");
        assertNotNull(saml2client);
    }
}
