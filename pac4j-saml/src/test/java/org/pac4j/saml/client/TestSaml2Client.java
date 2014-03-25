package org.pac4j.saml.client;

import junit.framework.TestCase;

import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.client.Saml2Client;

public final class TestSaml2Client extends TestCase implements TestsConstants {

	public void testInit() {
        final Saml2Client saml2Client = new Saml2Client();

        MockWebContext wc = MockWebContext.create();

        
        saml2Client.setKeystorePath(this.getClass().getResource("samlKeystore.jks").getFile());
		saml2Client.setKeystorePassword("pac4j-demo-passwd");
		saml2Client.setPrivateKeyPassword("pac4j-demo-passwd");
		saml2Client.setIdpMetadataPath(this.getClass().getResource("testshib-providers.xml").getFile());
		saml2Client.setCallbackUrl("http://localhost:8080/callback");

		saml2Client.init();
		
		// TODO make some assert on SP metadata
		String spMetadata = saml2Client.printClientMetadata();
		
		try {
			saml2Client.redirect(wc, false, false);
		} catch (RequiresHttpAction e) {
			fail();
		}
		assertEquals(HttpConstants.OK, wc.getResponseStatus());
		String content = wc.getResponseContent();
		// TODO make some assert on Authn Request

    }

}
