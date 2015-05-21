/*
  Copyright 2012 -2014 Michael Remond

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

import static org.junit.Assert.*;

/**
 * Unit tests for the SAML2Client.
 */
public class SAML2ClientTest {

    @Test
    public void testIdpMetadataParsing_fromFile() {
        SAML2Client client = new SAML2Client();
        client.setIdpMetadataPath("resource:testshib-providers.xml");
        client.setCallbackUrl("http://localhost:8080");
        client.init();

        client.getIdpMetadataResolver().resolve();
        String id = client.getIdpMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

}
