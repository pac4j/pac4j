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

package org.pac4j.saml.client;

import org.junit.Test;
import org.pac4j.saml.util.Configuration;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit tests for the SAML2Client.
 */
public class SAML2ClientTest {


    public SAML2ClientTest() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
    }

    @Test
    public void testIdpMetadataParsing_fromFile() {
        final SAML2Client client = getClient();
        client.getConfiguration().setIdentityProviderMetadataPath("resource:testshib-providers.xml");
        client.init();

        client.getIdentityProviderMetadataResolver().resolve();
        final String id = client.getIdentityProviderMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

    @Test
    public void testIdpMetadataParsing_fromUrl() {
        final SAML2Client client = getClient();
        client.getConfiguration().setIdentityProviderMetadataPath("https://idp.testshib.org/idp/profile/Metadata/SAML");
        client.init();

        client.getIdentityProviderMetadataResolver().resolve();
        final String id = client.getIdentityProviderMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }


    protected final SAML2Client getClient() {

        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration("resource:samlKeystore.jks",
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        "resource:testshib-providers.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());

        final SAML2Client saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl("http://localhost:8080/something");
        return saml2Client;
    }

}
