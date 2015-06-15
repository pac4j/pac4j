package org.pac4j.test.cas.client.rest;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.rest.CasRestAuthenticator;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.test.cas.client.CasClientIT;

import java.net.URL;

/**
 * The {@link CasClientRestProtocolIT} is responsible for...
 *
 * @author Misagh Moayyed
 */
public class CasClientRestProtocolIT extends CasClientIT {

    @Override
    protected CasClient.CasProtocol getCasProtocol() {
        return CasClient.CasProtocol.CAS10;
    }

    @Test
    public void testRestProtocolForm() throws Exception {
        final String casUrlPrefix = "http://localhost:8080/cas";

        final CasRestAuthenticator authenticator = new CasRestAuthenticator(casUrlPrefix);
        final CasRestFormClient client = new CasRestFormClient(authenticator);
        final UsernamePasswordCredentials creds = new UsernamePasswordCredentials("casuser", "casuser", client.getName());

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getAuthenticator().getUsernameParameter(), creds.getUsername());
        context.addRequestParameter(client.getAuthenticator().getPasswordParameter(), creds.getPassword());

        final HttpProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket("http://www.pac4j.org/", profile);
        final CasProfile casProfile = client.validateServiceTicket("http://www.pac4j.org/", casCreds);
    }
}
