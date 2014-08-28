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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.StaticBasicParserPool;

/**
 * Unit tests for the Saml2Client.
 */
public class Saml2ClientTest {

    static {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testIdpMetadataParsing_fromString() throws IOException {
        Saml2Client client = new Saml2Client();
        InputStream metaDataInputStream = getClass().getClassLoader().getResourceAsStream("testshib-providers.xml");
        String metadata = IOUtils.toString(metaDataInputStream, "UTF-8");
        client.setIdpMetadata(metadata);
        StaticBasicParserPool parserPool = client.newStaticBasicParserPool();
        AbstractMetadataProvider provider = client.idpMetadataProvider(parserPool);
        XMLObject md = client.getXmlObject(provider);
        String id = client.getIdpEntityId(md);
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

    @Test
    public void testIdpMetadataParsing_fromFile() {
        Saml2Client client = new Saml2Client();
        client.setIdpMetadataPath("resource:testshib-providers.xml");
        StaticBasicParserPool parserPool = client.newStaticBasicParserPool();
        AbstractMetadataProvider provider = client.idpMetadataProvider(parserPool);
        XMLObject md = client.getXmlObject(provider);
        String id = client.getIdpEntityId(md);
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

}
