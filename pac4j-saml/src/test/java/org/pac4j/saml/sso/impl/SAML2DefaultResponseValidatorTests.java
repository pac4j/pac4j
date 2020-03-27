package org.pac4j.saml.sso.impl;

import org.junit.Test;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.replay.InMemoryReplayCacheProvider;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.impl.AssertionConsumerServiceImpl;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.util.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class SAML2DefaultResponseValidatorTests {

    private static final String SAMPLE_RESPONSE_FILE_NAME = "sample_authn_response.xml";

    @Test
    public void testAssertionConsumingServiceWithMultipleIDP() throws Exception {
        final File file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
                getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());
        
        final XMLObject xmlObject = XMLObjectSupport.unmarshallFromReader(
                Configuration.getParserPool(), 
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final Response response = (Response) xmlObject;
        response.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        response.getAssertions().forEach(assertion -> {
            assertion.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getSubject().getSubjectConfirmations().get(0).
                    getSubjectConfirmationData().setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getConditions().setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getAuthnStatements().forEach(authnStatement -> authnStatement.setAuthnInstant(
                ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
        });

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLObjectSupport.marshallToOutputStream(xmlObject, os);

        // create response validator enforcing response signature
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, true);
        final SAML2MessageContext context = new SAML2MessageContext();
        context.getMessageContext().setMessage(response);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpConstants.HTTP_METHOD.POST.name());
        request.setParameter("SAMLResponse", Base64Support.encode(os.toByteArray(), Base64Support.UNCHUNKED));
        request.setParameter("RelayState", "TST-2-FZOsWEfjC-IH-h6Xb333DRbu5UPMHqfL");
        final WebContext webContext = new JEEContext(request, new MockHttpServletResponse());
        context.setWebContext(webContext);

        final EntityDescriptor idpDescriptor = mock(EntityDescriptor.class);
        context.getSAMLPeerMetadataContext().setEntityDescriptor(idpDescriptor);
        when(idpDescriptor.getEntityID()).thenReturn("http://localhost:8088");

        final SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);

        context.getSAMLSelfEntityContext().setEntityId("https://auth.izslt.it");
        context.getSAMLPeerEntityContext().setAuthenticated(true);

        final AssertionConsumerServiceImpl acs = new AssertionConsumerServiceImpl(
                response.getDestination(),
                response.getDestination(),
                response.getDestination()) {
        };
        acs.setLocation("https://auth.izslt.it/cas/login?client_name=idptest");

        when(roleDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        final SAML2WebSSOMessageReceiver receiver = new SAML2WebSSOMessageReceiver(validator);
        receiver.receiveMessage(context);
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullContext() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, false);
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(null));
    }

    private SAML2AuthnResponseValidator createResponseValidatorWithSigningValidationOf(
        final boolean wantsAssertionsSigned, final boolean wantsResponsesSigned) {
        final SAML2SignatureTrustEngineProvider trustEngineProvider = mock(SAML2SignatureTrustEngineProvider.class);
        final LogoutHandler logoutHandler = mock(LogoutHandler.class);
        final SignatureTrustEngine engine = mock(SignatureTrustEngine.class);
        
        try {
            when(engine.validate(any(Signature.class), any(CriteriaSet.class))).thenReturn(true);
        } catch (final SecurityException ex) {
            fail();
        }
        when(trustEngineProvider.build()).thenReturn(engine);
        final Decrypter decrypter = mock(Decrypter.class);

        return new SAML2AuthnResponseValidator(
                trustEngineProvider,
                decrypter,
                logoutHandler,
                0,
                wantsAssertionsSigned,
                wantsResponsesSigned,
                new InMemoryReplayCacheProvider(),
                false);
    }

    @Test
    public void testWantsAssertionsSignedWithNullContext() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true, false);
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, false);
        final SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true, false);
        final SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, false);
        final SAML2MessageContext context = new SAML2MessageContext();

        final SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithValidSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true, false);
        final SAML2MessageContext context = new SAML2MessageContext();

        final SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test(expected = SAMLException.class)
    public void testAuthenticatedResponseAndAssertionWithoutSignatureThrowsException() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true, false);
        final SAML2MessageContext context = new SAML2MessageContext();
        final SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test(expected = SAMLException.class)
    public void testResponseWithoutSignatureThrowsException() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, false);
        final SAML2MessageContext context = new SAML2MessageContext();
        final SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
        // expected no exceptions
    }
    
    @Test(expected = SAMLSignatureValidationException.class)
    public void testNotSignedAuthenticatedResponseThrowsException() 
            throws FileNotFoundException, XMLParserException, UnmarshallingException {
        final File file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
                getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());
        
        final XMLObject xmlObject = XMLObjectSupport.unmarshallFromReader(
                Configuration.getParserPool(), 
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final Response response = (Response) xmlObject;
        response.setSignature(null);
        
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false, true);
        final SAML2MessageContext context = new SAML2MessageContext();
        final SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateSamlProtocolResponse(response, context, null);
    }
}
