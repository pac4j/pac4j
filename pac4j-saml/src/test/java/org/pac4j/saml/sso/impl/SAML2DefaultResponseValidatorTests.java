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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.xml.XMLObject;
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
import org.pac4j.saml.util.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class SAML2DefaultResponseValidatorTests {

    private final static String SAMPLE_RESPONSE_FILE_NAME = "sample_authn_response.xml";

    @Test
    public void testAssertionConsumingServiceWithMultipleIDP() throws Exception {
        File file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
                getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());
        
        final XMLObject xmlObject = XMLObjectSupport.unmarshallFromReader(
                Configuration.getParserPool(), 
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        Response response = (Response) xmlObject;
        response.setIssueInstant(DateTime.now(DateTimeZone.UTC));
        response.getAssertions().forEach(assertion -> {
            assertion.setIssueInstant(DateTime.now(DateTimeZone.UTC));
            assertion.getSubject().getSubjectConfirmations().get(0).
                    getSubjectConfirmationData().setNotOnOrAfter(DateTime.now(DateTimeZone.UTC));
            assertion.getConditions().setNotOnOrAfter(DateTime.now(DateTimeZone.UTC));
            assertion.getAuthnStatements().forEach(authnStatement -> authnStatement.setAuthnInstant(
                    DateTime.now(DateTimeZone.UTC)));
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLObjectSupport.marshallToOutputStream(xmlObject, os);

        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        context.setMessage(response);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpConstants.HTTP_METHOD.POST.name());
        request.setParameter("SAMLResponse", Base64Support.encode(os.toByteArray(), Base64Support.UNCHUNKED));
        request.setParameter("RelayState", "TST-2-FZOsWEfjC-IH-h6Xb333DRbu5UPMHqfL");
        WebContext webContext = new JEEContext(request, new MockHttpServletResponse());
        context.setWebContext(webContext);

        EntityDescriptor idpDescriptor = mock(EntityDescriptor.class);
        context.getSAMLPeerMetadataContext().setEntityDescriptor(idpDescriptor);
        when(idpDescriptor.getEntityID()).thenReturn("http://localhost:8088");

        SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);

        context.getSAMLSelfEntityContext().setEntityId("https://auth.izslt.it");
        context.getSAMLPeerEntityContext().setAuthenticated(true);

        AssertionConsumerServiceImpl acs = new AssertionConsumerServiceImpl(
                response.getDestination(),
                response.getDestination(),
                response.getDestination()) {
        };
        acs.setLocation("https://auth.izslt.it/cas/login?client_name=idptest");

        when(roleDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        SAML2WebSSOMessageReceiver receiver = new SAML2WebSSOMessageReceiver(validator);
        receiver.receiveMessage(context);
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullContext() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(null));
    }

    private SAML2AuthnResponseValidator createResponseValidatorWithSigningValidationOf(boolean wantsAssertionsSigned) {
        SAML2SignatureTrustEngineProvider trustEngineProvider = mock(SAML2SignatureTrustEngineProvider.class);
        LogoutHandler logoutHandler = mock(LogoutHandler.class);
        SignatureTrustEngine engine = mock(SignatureTrustEngine.class);
        
        try {
            when(engine.validate(any(Signature.class), any(CriteriaSet.class))).thenReturn(true);
        } catch (SecurityException ex) {
            fail();
        }
        when(trustEngineProvider.build()).thenReturn(engine);
        Decrypter decrypter = mock(Decrypter.class);

        return new SAML2AuthnResponseValidator(
                trustEngineProvider,
                decrypter,
                logoutHandler,
                0,
                wantsAssertionsSigned,
                false,
                new InMemoryReplayCacheProvider(),
                false);
    }

    @Test
    public void testWantsAssertionsSignedWithNullContext() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();

        SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithValidSPSSODescriptor() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();

        SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test(expected = SAMLException.class)
    public void testAuthenticatedResponseAndAssertionWithoutSignatureThrowsException() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(true);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test(expected = SAMLException.class)
    public void testResponseWithoutSignatureThrowsException() {
        SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(false);
        SAML2MessageContext context = new SAML2MessageContext();
        SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
        // expected no exceptions
    }
}
