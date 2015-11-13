package org.pac4j.saml.dbclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.saml.util.Configuration;


/**
 * Unit test of {@link DbLoadedSamlClient}. Most code is taken from {@link SAML2ClientTest} but adapted.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClientTest {

	@BeforeClass
	public static void setUpBeforeClass() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
	}


	// Replaces these tests from SAML2ClientTest: testIdpMetadataParsing_fromFile + testIdpMetadataParsing_fromUrl
    @Test
    public void testIdpMetadataParsing() throws IOException {
        final DbLoadedSamlClient client = getClient();
        client.init(null);
        client.getIdentityProviderMetadataResolver().resolve();
        final String id = client.getIdentityProviderMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }


    protected final DbLoadedSamlClient getClient() throws IOException {
        final DbLoadedSamlClientConfiguration cfg = new DbLoadedSamlClientConfiguration();
        cfg.setKeystoreBinaryData(loadResourceIntoBytes("/org/pac4j/saml/dbclient/samlKeystore.jks"));
        cfg.setKeystorePassword("pac4j-demo-passwd");
        cfg.setPrivateKeyPassword("pac4j-demo-passwd");
        cfg.setIdentityProviderMetadata(loadResourceIntoString("/org/pac4j/saml/dbclient/testshib-providers.xml"));
//        cfg.setIdentityProviderEntityId("https://idp.testshib.org/idp/shibboleth"); Not needed, the resolver can find it
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setClientName("UnitTestClient");
        
        final DbLoadedSamlClient saml2Client = new DbLoadedSamlClient(cfg);
        saml2Client.setCallbackUrl("http://localhost:8080/something");
        return saml2Client;
    }

	
    protected byte[] loadResourceIntoBytes(final String path) throws IOException {
    	InputStream is = DbLoadedSamlClientTest.class.getResourceAsStream(path);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	IOUtils.copy(is, baos);
    	
    	byte[] result = baos.toByteArray();
    	is.close();
    	baos.close();
    	
    	return result;
    }

    
    protected String loadResourceIntoString(final String path) throws IOException {
    	InputStream is = DbLoadedSamlClientTest.class.getResourceAsStream(path);
    	Writer writer = new StringWriter();
    	IOUtils.copy(is, writer);
    	
    	String result = writer.toString();
    	is.close();
    	writer.close();
    	
    	return result;
    }
    
}
