package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.impl.AssertionConsumerServiceImpl;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLAuthnContextClassRefException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.replay.InMemoryReplayCacheProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.ExcludingParametersURIComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SAML2DefaultResponseValidatorTests {

    private static final String SAMPLE_RESPONSE_FILE_NAME = "sample_authn_response.xml";

    private static SAML2AuthnResponseValidator createResponseValidatorWithSigningValidationOf(final SAML2Configuration saml2Configuration) {
        final SAML2SignatureTrustEngineProvider trustEngineProvider = mock(SAML2SignatureTrustEngineProvider.class);
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
            new InMemoryReplayCacheProvider(),
            saml2Configuration);
    }

    protected static SAML2Configuration getSaml2Configuration(final boolean wantsAssertionsSigned, final boolean wantsResponsesSigned) {
        final SAML2Configuration cfg =
            new SAML2Configuration(new FileSystemResource("target/samlKeystore.jks"),
                "pac4j-demo-passwd",
                "pac4j-demo-passwd",
                new ClassPathResource("testshib-providers.xml"));

        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setForceServiceProviderMetadataGeneration(true);
        cfg.setForceKeystoreGeneration(true);
        cfg.setWantsAssertionsSigned(wantsAssertionsSigned);
        cfg.setWantsResponsesSigned(wantsResponsesSigned);
        cfg.setLogoutHandler(mock(LogoutHandler.class));
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));
        return cfg;
    }

    private static Response getResponse() throws Exception {
        final File file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
            getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());

        final XMLObject xmlObject = XMLObjectSupport.unmarshallFromReader(
            Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final Response response = (Response) xmlObject;
        response.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        response.getAssertions().forEach(assertion -> {
            assertion.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getSubject().getSubjectConfirmations().get(0).setMethod(SubjectConfirmation.METHOD_BEARER);
            assertion.getSubject().getSubjectConfirmations().get(0).
                getSubjectConfirmationData().setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getConditions().setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
            assertion.getAuthnStatements().forEach(authnStatement -> authnStatement.setAuthnInstant(
                ZonedDateTime.now(ZoneOffset.UTC).toInstant()));
        });
        return response;
    }

    @Test
    public void testAssertionConsumingServiceWithMultipleIDP() throws Exception {
        final Response response = getResponse();
        // create response validator enforcing response signature
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, true));
        final SAML2MessageContext context = new SAML2MessageContext();
        context.getMessageContext().setMessage(response);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLObjectSupport.marshallToOutputStream(response, os);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpConstants.HTTP_METHOD.POST.name());
        request.setParameter("SAMLResponse", Base64Support.encode(os.toByteArray(), Base64Support.UNCHUNKED));
        request.setParameter("RelayState", "TST-2-FZOsWEfjC-IH-h6Xb333DRbu5UPMHqfL");
        final WebContext webContext = new JEEContext(request, new MockHttpServletResponse());
        context.setWebContext(webContext);
        context.setSessionStore(JEESessionStore.INSTANCE);

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
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, false));
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testWantsAssertionsSignedWithNullContext() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(true, false));
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(null));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, false));
        final SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(true, false));
        final SAML2MessageContext context = new SAML2MessageContext();
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, false));
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
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(true, false));
        final SAML2MessageContext context = new SAML2MessageContext();

        final SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }


    @Test
    public void testAuthnContextClassRefValidation() throws Exception {
        final SAML2Configuration saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setUriComparator(new ExcludingParametersURIComparator());
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PASSWORD_AUTHN_CTX);
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PPT_AUTHN_CTX);

        final Response response = getResponse();
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final SAML2MessageContext context = new SAML2MessageContext();
        context.getMessageContext().setMessage(response);

        final SAMLSelfEntityContext samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");
        final SAMLMetadataContext samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final SPSSODescriptor roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        final SAMLEndpointContext samlEndpointContext = context.getSAMLEndpointContext();
        final Endpoint endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");
        samlEndpointContext.setEndpoint(endpoint);

        assertThrows(SAMLAuthnContextClassRefException.class, () -> validator.validate(context));
    }

    @Test(expected = SAMLException.class)
    public void testAuthenticatedResponseAndAssertionWithoutSignatureThrowsException() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(true, false));
        final SAML2MessageContext context = new SAML2MessageContext();
        final SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test(expected = SAMLException.class)
    public void testResponseWithoutSignatureThrowsException() {
        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, false));
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

        final SAML2AuthnResponseValidator validator = createResponseValidatorWithSigningValidationOf(getSaml2Configuration(false, true));
        final SAML2MessageContext context = new SAML2MessageContext();
        final SAMLPeerEntityContext peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateSamlProtocolResponse(response, context, null);
    }
}
