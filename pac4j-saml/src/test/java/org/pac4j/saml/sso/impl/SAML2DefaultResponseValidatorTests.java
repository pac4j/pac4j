package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.junit.Test;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
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
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.JEESessionStore;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2Credentials;
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
        final var trustEngineProvider = mock(SAML2SignatureTrustEngineProvider.class);
        final var engine = mock(SignatureTrustEngine.class);

        try {
            when(engine.validate(any(Signature.class), any(CriteriaSet.class))).thenReturn(true);
        } catch (final SecurityException ex) {
            fail();
        }
        when(trustEngineProvider.build()).thenReturn(engine);
        final var decrypter = mock(Decrypter.class);
        return new SAML2AuthnResponseValidator(
            trustEngineProvider,
            decrypter,
            new InMemoryReplayCacheProvider(),
            saml2Configuration);
    }

    protected static SAML2Configuration getSaml2Configuration(final boolean wantsAssertionsSigned, final boolean wantsResponsesSigned) {
        final var cfg =
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
        final var file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
            getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());

        final var xmlObject = XMLObjectSupport.unmarshallFromReader(
            Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final var response = (Response) xmlObject;
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
        final var response = getResponse();
        // create response validator enforcing response signature
        var saml2Configuration = getSaml2Configuration(false, true);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        final var os = new ByteArrayOutputStream();
        XMLObjectSupport.marshallToOutputStream(response, os);

        final var request = new MockHttpServletRequest();
        request.setMethod(HttpConstants.HTTP_METHOD.POST.name());
        request.setParameter("SAMLResponse", Base64Support.encode(os.toByteArray(), Base64Support.UNCHUNKED));
        request.setParameter("RelayState", "TST-2-FZOsWEfjC-IH-h6Xb333DRbu5UPMHqfL");
        final WebContext webContext = new JEEContext(request, new MockHttpServletResponse());
        context.setWebContext(webContext);
        context.setSessionStore(JEESessionStore.INSTANCE);

        final var idpDescriptor = mock(EntityDescriptor.class);
        context.getSAMLPeerMetadataContext().setEntityDescriptor(idpDescriptor);
        when(idpDescriptor.getEntityID()).thenReturn("http://localhost:8088");

        final var samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final var roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);

        context.getSAMLSelfEntityContext().setEntityId("https://auth.izslt.it");
        context.getSAMLPeerEntityContext().setAuthenticated(true);

        final var acs = new AssertionConsumerServiceImpl(
            response.getDestination(),
            response.getDestination(),
            response.getDestination()) {
        };
        acs.setLocation("https://auth.izslt.it/cas/login?client_name=idptest");

        when(roleDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        final var receiver = new SAML2WebSSOMessageReceiver(validator, context.getSAML2Configuration());
        receiver.receiveMessage(context);
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(false, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(true, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(false, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);

        final var samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final var roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithValidSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(true, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);

        final var samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final var roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testNameIdAsAttribute() throws Exception {
        final var saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setUriComparator(new ExcludingParametersURIComparator());
        saml2Configuration.setAllSignatureValidationDisabled(true);
        saml2Configuration.setNameIdAttribute("email");

        final var response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        final var samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");
        final var samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final var roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        final var samlEndpointContext = context.getSAMLEndpointContext();
        final var endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");
        samlEndpointContext.setEndpoint(endpoint);

        var credentials = (SAML2Credentials) validator.validate(context);
        assertEquals("longosibilla@libero.it", credentials.getNameId().getValue());
    }

    @Test
    public void testAuthnContextClassRefValidation() throws Exception {
        final var saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setUriComparator(new ExcludingParametersURIComparator());
        saml2Configuration.setAllSignatureValidationDisabled(true);
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PASSWORD_AUTHN_CTX);
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PPT_AUTHN_CTX);

        final var response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setSaml2Configuration(saml2Configuration);
        context.setWebContext(MockWebContext.create());
        context.getMessageContext().setMessage(response);

        final var samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");
        final var samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        final var roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        final var samlEndpointContext = context.getSAMLEndpointContext();
        final var endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");
        samlEndpointContext.setEndpoint(endpoint);

        assertThrows(SAMLAuthnContextClassRefException.class, () -> validator.validate(context));
    }

    @Test(expected = SAMLException.class)
    public void testAuthenticatedResponseAndAssertionWithoutSignatureThrowsException() {
        final var saml2Configuration = getSaml2Configuration(true, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setSaml2Configuration(saml2Configuration);
        context.setWebContext(MockWebContext.create());
        final var peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test(expected = SAMLException.class)
    public void testResponseWithoutSignatureThrowsException() {
        final var saml2Configuration = getSaml2Configuration(false, false);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        final var peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
        // expected no exceptions
    }

    @Test(expected = SAMLSignatureValidationException.class)
    public void testNotSignedAuthenticatedResponseThrowsException()
        throws FileNotFoundException, XMLParserException, UnmarshallingException {
        final var file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
            getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());

        final var xmlObject = XMLObjectSupport.unmarshallFromReader(
            Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final var response = (Response) xmlObject;
        response.setSignature(null);

        var saml2Configuration = getSaml2Configuration(false, true);
        final var validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        final var context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        final var peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateSamlProtocolResponse(response, context, null);
    }
}
