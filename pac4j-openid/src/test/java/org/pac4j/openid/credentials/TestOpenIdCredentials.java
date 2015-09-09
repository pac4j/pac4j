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
