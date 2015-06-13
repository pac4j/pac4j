package org.pac4j.test.cas.client.rest;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.rest.CasRestAuthenticator;
import org.pac4j.cas.client.rest.CasRestClient;
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
    public void testRestProtocol() throws Exception {
        final URL endpointURL = new URL("http://localhost:8080/cas/v1/tickets");
        final String casUrlPrefix = "http://localhost:8080/cas";

        final CasRestAuthenticator authenticator = new CasRestAuthenticator(endpointURL,
                new Cas20ServiceTicketValidator(casUrlPrefix));
        final CasRestClient client = new CasRestClient(authenticator);
        final UsernamePasswordCredentials creds = new UsernamePasswordCredentials("casuser", "casuser", client.getName());

        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(client.getAuthenticator().getUsernameParameter(), creds.getUsername());
        context.addRequestParameter(client.getAuthenticator().getPasswordParameter(), creds.getPassword());

        final HttpProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket(new URL("http://www.pac4j.org/"), profile);
        final CasProfile casProfile = client.validateServiceTicket(new URL("http://www.pac4j.org/"), casCreds);
    }
}
