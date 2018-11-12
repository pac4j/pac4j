package org.pac4j.saml.transport;

import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.impl.AuthnRequestImpl;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.saml.util.Configuration;

import java.io.StringReader;

import static org.junit.Assert.*;

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

    @Test
    public void testEncodeDecode() throws Exception {
        final MockWebContext webContext = MockWebContext.create();

        final XMLObject xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(), new StringReader(AUTHN_REQUEST));

        final Pac4jHTTPRedirectDeflateEncoder encoder =
            new Pac4jHTTPRedirectDeflateEncoder(new DefaultPac4jSAMLResponse(webContext), false);
        String message = encoder.deflateAndBase64Encode((SAMLObject) xmlObject);

        webContext.addRequestParameter("SAMLResponse", message);
        final Pac4jHTTPRedirectDeflateDecoder decoder = new Pac4jHTTPRedirectDeflateDecoder(webContext);
        decoder.setParserPool(Configuration.getParserPool());
        decoder.initialize();
        decoder.decode();

        assertTrue(decoder.getMessageContext().getMessage() instanceof AuthnRequestImpl);
    }
}
