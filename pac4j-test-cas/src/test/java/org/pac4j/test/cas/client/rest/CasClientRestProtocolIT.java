package org.pac4j.test.cas.client.rest;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.cas.client.rest.CasRestBasicAuthClient;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.profile.HttpTGTProfile;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.test.cas.client.CasClientIT;

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

        final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket("http://www.pac4j.org/", profile);
        final CasProfile casProfile = client.validateServiceTicket("http://www.pac4j.org/", casCreds);
        client.destroyTicketGrantingTicket(context, profile);
    }

    @Test
    public void testRestProtocolBasic() throws Exception {
        final String casUrlPrefix = "http://localhost:8080/cas";

        final CasRestAuthenticator authenticator = new CasRestAuthenticator(casUrlPrefix);
        final CasRestBasicAuthClient client = new CasRestBasicAuthClient(authenticator, "cas-rest", "");
        final UsernamePasswordCredentials creds = new UsernamePasswordCredentials("casuser", "casuser", client.getName());

        final MockWebContext context = MockWebContext.create();
        final String token = creds.getUsername() + ":" + creds.getPassword();
        context.addRequestHeader("cas-rest", Base64.encodeBase64String(token.getBytes()));

        final HttpTGTProfile profile = client.requestTicketGrantingTicket(context);
        final CasCredentials casCreds = client.requestServiceTicket("http://www.pac4j.org/", profile);
        final CasProfile casProfile = client.validateServiceTicket("http://www.pac4j.org/", casCreds);
        client.destroyTicketGrantingTicket(context, profile);
    }

}
