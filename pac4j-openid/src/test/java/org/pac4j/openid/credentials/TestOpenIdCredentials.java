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
package org.pac4j.openid.credentials;

import junit.framework.TestCase;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ParameterList;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * General test cases for OpenIdCredentials.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestOpenIdCredentials extends TestCase {

    public void testClearOpenIdCredentials() throws MalformedURLException, DiscoveryException {
        OpenIdCredentials openIdCredentials = new OpenIdCredentials(
                new DiscoveryInformation(new URL("http", "test", 8080, "test")),
                new ParameterList(),
                "testClient"
        );
        openIdCredentials.clear();
        assertNull(openIdCredentials.getClientName());
        assertNull(openIdCredentials.getDiscoveryInformation());
        assertNull(openIdCredentials.getParameterList());
    }
}
