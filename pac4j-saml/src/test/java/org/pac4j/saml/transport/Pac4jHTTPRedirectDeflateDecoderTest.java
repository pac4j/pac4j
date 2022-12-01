package org.pac4j.saml.transport;

import lombok.val;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.impl.AuthnRequestImpl;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.saml.util.Configuration;

import java.io.StringReader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Pac4jHTTPRedirectDeflateDecoder}.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class Pac4jHTTPRedirectDeflateDecoderTest {

    private static final String AUTHN_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:AuthnRequest "
        + "xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" AssertionConsumerServiceURL=\"http://localhost:8081/callback"
        + "?client_name=SAML2Client\" ForceAuthn=\"false\" IssueInstant=\"2018-10-05T14:52:47.084Z\" "
        + "ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Version=\"2.0\"><saml2:Issuer "
        + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8081/callback</saml2:Issuer><saml2p:NameIDPolicy "
        + "AllowCreate=\"true\"/></saml2p:AuthnRequest>";

    private static final String ENDPOINT_URL_WITH_QUERY_PARAMS = "https://localhost/some/path?" +
        "qp=0000&" +
        "SAMLRequest=fakeRequest&" +
        "RelayState=RelayStateShouldBeRemoved&" +
        "SigAlg=SigAlgShouldBeRemoved&" +
        "Signature=SignatureShouldBeRemoved";

    @Test
    public void testEncodeDecode() throws Exception {
        val webContext = MockWebContext.create();

        val xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(), new StringReader(AUTHN_REQUEST));

        val encoder =
            new Pac4jHTTPRedirectDeflateEncoder(new DefaultPac4jSAMLResponse(webContext), false);
        val message = encoder.deflateAndBase64Encode((SAMLObject) xmlObject);

        webContext.addRequestParameter("SAMLResponse", message);
        val decoder = new Pac4jHTTPRedirectDeflateDecoder(webContext);
        decoder.setParserPool(Configuration.getParserPool());
        decoder.initialize();
        decoder.decode();

        assertTrue(decoder.getMessageContext().getMessage() instanceof AuthnRequestImpl);
    }

    @Test
    public void testBuildRedirectUrlWithExistingQueryParameters() throws Exception {
        val webContext = MockWebContext.create();

        val xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(), new StringReader(AUTHN_REQUEST));

        val encoder =
            new Pac4jHTTPRedirectDeflateEncoder(new DefaultPac4jSAMLResponse(webContext), false);
        val messageContext = new MessageContext();
        messageContext.setMessage((SAMLObject) xmlObject);

        val encodedMessage = encoder.deflateAndBase64Encode((SAMLObject) xmlObject);
        val redirectURL = encoder.buildRedirectURL(messageContext, ENDPOINT_URL_WITH_QUERY_PARAMS, encodedMessage);

        assertTrue(redirectURL.contains("qp=0000"));
        assertTrue(redirectURL.contains("SAMLRequest"));
        assertFalse(redirectURL.contains("SAMLRequest=fakeRequest"));
        assertFalse(redirectURL.contains("RelayState"));
        assertFalse(redirectURL.contains("SigAlg"));
        assertFalse(redirectURL.contains("Signature"));
    }
}
