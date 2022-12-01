package org.pac4j.saml.run;

import lombok.val;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.profile.SAML2Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests the testshib.org provider.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public class RunTestshib extends RunClient {

    public static void main(final String[] args) {
        new RunTestshib().run();
    }

    @Override
    protected String getLogin() {
        return "myself";
    }

    @Override
    protected String getPassword() {
        return "myself";
    }

    @Override
    protected IndirectClient getClient() {
        val cfg = new SAML2Configuration(new ClassPathResource("samlKeystore.jks"),
                "pac4j-demo-passwd", "pac4j-demo-passwd", new ClassPathResource("testshib-providers.xml"));
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "test-sp-metadata.xml").getAbsolutePath()));
        cfg.setAuthnRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
        val client = new SAML2Client(cfg);
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        val profile = (SAML2Profile) userProfile;
        assertEquals("[Member, Staff]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.1").toString());
        assertEquals("[myself]", profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.1").toString());
        assertEquals("[Me Myself And I]", profile.getAttribute("urn:oid:2.5.4.3").toString());
        assertEquals("[myself@testshib.org]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6").toString());
        assertEquals("[555-5555]", profile.getAttribute("urn:oid:2.5.4.20").toString());
        assertEquals("[Member@testshib.org, Staff@testshib.org]",
                profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.9").toString());
        assertEquals("[urn:mace:dir:entitlement:common-lib-terms]",
                profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.7").toString());
        assertEquals("[Me Myself]", profile.getAttribute("urn:oid:2.5.4.42").toString());
        assertEquals("[And I]", profile.getAttribute("urn:oid:2.5.4.4").toString());
    }

    @Override
    protected void populateContextWithUrl(final MockWebContext context, final String url) {
        super.populateContextWithUrl(context, url);
        context.setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
    }
}
