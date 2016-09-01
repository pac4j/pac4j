package org.pac4j.saml.run;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.saml.client.SAML2Client;

/**
 * This is {@link RunRedirectSAML2Client}.
 *
 * @author Misagh Moayyed
 */
public class RunRedirectSAML2Client extends AbstractRunSAMLClient {

    public static void main(final String[] args) throws Exception {
        new RunRedirectSAML2Client().run();
    }
    
    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }
}
