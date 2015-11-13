package org.pac4j.saml.dbclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.junit.BeforeClass;
import org.pac4j.core.client.ClientIT;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.util.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


/**
 * Base for integration tests of {@link DbLoadedSamlClient}. Most code is taken from {@link SAML2ClientIT} and little adapted.
 * 
 * @author jkacer
 */
public abstract class DbLoadedSamlClientIT extends ClientIT implements TestsConstants {

	@BeforeClass
	public static void setUpBeforeClass() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
	}


	// We do not generate SP metadata
//    @Test
//    public void testSPMetadata() {
//        final DbLoadedSamlClient client = getClient();
//        client.init();
//        final String spMetadata = client.getServiceProviderMetadataResolver().getMetadata();
//        assertTrue(spMetadata.contains("entityID=\"" + client.getServiceProviderResolvedEntityId() + "\""));
//        assertTrue(spMetadata.contains("<md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"" + getCallbackUrl() + "\""));
//    }

    
    @Override
    protected final ClientType getClientType() {
        return ClientType.SAML_PROTOCOL;
    }

    
    @Override
    protected final void updateContextForAuthn(final WebClient webClient, final HtmlPage authorizationPage, final J2EContext context) throws Exception {
        if (authorizationPage.getForms().isEmpty()) {
            throw new SAMLException("Authorization page " + authorizationPage.getUrl() + " does not produce any forms");
        }

        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("j_username");
        email.setValueAttribute("myself");
        final HtmlPasswordInput password = form.getInputByName("j_password");
        password.setValueAttribute("myself");

        HtmlPage callbackPage;

        try {
            final HtmlSubmitInput submit = form.getInputByValue("Login");
            callbackPage = submit.click();
        } catch (final ElementNotFoundException e) {
            final HtmlButton btn = form.getButtonByName("_eventId_proceed");
            callbackPage = btn.click();
        }

        try {
            final String samlResponse = ((HtmlInput) callbackPage.getElementByName("SAMLResponse")).getValueAttribute();
            final String relayState = ((HtmlInput) callbackPage.getElementByName("RelayState")).getValueAttribute();

            final MockHttpServletRequest request = (MockHttpServletRequest) context.getRequest();
            request.addParameter("SAMLResponse", samlResponse);
            request.addParameter("RelayState", relayState);
            request.setMethod("POST");
            request.setRequestURI(callbackPage.getForms().get(0).getActionAttribute());
        } catch (final ElementNotFoundException e) {
            throw new SAMLException("Saml response and/or relay state not found", e);
        }
    }

    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        throw new NotImplementedException("No callback url in SAML2 POST Binding");
    }

    
    @Override
    protected final void registerForKryo(final Kryo kryo) {
        kryo.register(SAML2Profile.class);
    }

    
    @Override
    protected final void verifyProfile(final UserProfile userProfile) {
        final SAML2Profile profile = (SAML2Profile) userProfile;
        assertEquals("[Member, Staff]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.1").toString());
        assertEquals("[myself]", profile.getAttribute("urn:oid:0.9.2342.19200300.100.1.1").toString());
        assertEquals("[Me Myself And I]", profile.getAttribute("urn:oid:2.5.4.3").toString());
        assertEquals("[myself@testshib.org]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6").toString());
        assertEquals("[555-5555]", profile.getAttribute("urn:oid:2.5.4.20").toString());
        assertEquals("[Member@testshib.org, Staff@testshib.org]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.9").toString());
        assertEquals("[urn:mace:dir:entitlement:common-lib-terms]", profile.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.7").toString());
        assertEquals("[Me Myself]", profile.getAttribute("urn:oid:2.5.4.42").toString());
        assertEquals("[And I]", profile.getAttribute("urn:oid:2.5.4.4").toString());
    }
    
    
    /**
     * Creates a DbLoadedSamlClient to be tested.
     * 
     * @see org.pac4j.core.client.ClientIT#getClient()
     */
    @Override
    protected final DbLoadedSamlClient getClient() {
    	try {
	        final DbLoadedSamlClientConfiguration cfg = new DbLoadedSamlClientConfiguration();
	        cfg.setKeystoreBinaryData(loadResourceIntoBytes("/org/pac4j/saml/dbclient/samlKeystore.jks"));
	        cfg.setKeystorePassword("pac4j-demo-passwd");
	        cfg.setPrivateKeyPassword("pac4j-demo-passwd");
	        cfg.setIdentityProviderMetadata(loadResourceIntoString("/org/pac4j/saml/dbclient/testshib-providers.xml"));
	        cfg.setIdentityProviderEntityId("https://idp.testshib.org/idp/shibboleth");
	        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
	        cfg.setMaximumAuthenticationLifetime(3600);
	        cfg.setDestinationBindingType(getDestinationBindingType());
	        cfg.setClientName("UnitTestClient");
	        
	        final DbLoadedSamlClient saml2Client = new DbLoadedSamlClient(cfg);
	        saml2Client.setCallbackUrl(getCallbackUrl());
	        return saml2Client;
    	} catch (IOException ioe) {
    		return null;
    	}
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

    
    protected abstract String getCallbackUrl();

    
    protected abstract String getDestinationBindingType();
}
