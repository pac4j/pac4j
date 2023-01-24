package org.pac4j.saml.sso.impl;

import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2InternalCredentials;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLAuthnContextClassRefException;
import org.pac4j.saml.exceptions.SAMLEndpointMismatchException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.replay.InMemoryReplayCacheProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.ExcludingParametersURIComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SAML2DefaultResponseValidatorTests {

    private static final String SAMPLE_RESPONSE_FILE_NAME = "sample_authn_response.xml";

    private static SAML2AuthnResponseValidator createResponseValidatorWithSigningValidationOf(final SAML2Configuration saml2Configuration) {
        val trustEngineProvider = mock(SAML2SignatureTrustEngineProvider.class);
        val engine = mock(SignatureTrustEngine.class);

        try {
            when(engine.validate(any(Signature.class), any(CriteriaSet.class))).thenReturn(true);
        } catch (final SecurityException ex) {
            fail();
        }
        when(trustEngineProvider.build()).thenReturn(engine);
        val decrypter = mock(Decrypter.class);
        return new SAML2AuthnResponseValidator(
            trustEngineProvider,
            decrypter,
            new InMemoryReplayCacheProvider(),
            saml2Configuration);
    }

    protected static SAML2Configuration getSaml2Configuration(final boolean wantsAssertionsSigned, final boolean wantsResponsesSigned) {
        val cfg =
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
        cfg.setSessionLogoutHandler(mock(SessionLogoutHandler.class));
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));
        return cfg;
    }

    private static Response getResponse() throws Exception {
        val file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
            getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());

        val xmlObject = XMLObjectSupport.unmarshallFromReader(
            Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        val response = (Response) xmlObject;
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
    public void testDoesNotWantAssertionsSignedWithNullSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(false, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithNullSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(true, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        assertNull("Expected SPSSODescriptor to be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testDoesNotWantAssertionsSignedWithValidSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(false, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);

        val samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        val roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertFalse("Expected wantAssertionsSigned == false", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testWantsAssertionsSignedWithValidSPSSODescriptor() {
        var saml2Configuration = getSaml2Configuration(true, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);

        val samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        val roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(true);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        assertNotNull("Expected SPSSODescriptor to not be null", context.getSPSSODescriptor());
        assertTrue("Expected wantAssertionsSigned == true", validator.wantsAssertionsSigned(context));
    }

    @Test
    public void testNameIdAsAttribute() throws Exception {
        val saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setUriComparator(new ExcludingParametersURIComparator());
        saml2Configuration.setAllSignatureValidationDisabled(true);
        saml2Configuration.setNameIdAttribute("email");

        val response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        val samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");
        val samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        val roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        val samlEndpointContext = context.getSAMLEndpointContext();
        val endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");
        samlEndpointContext.setEndpoint(endpoint);

        var credentials = (SAML2InternalCredentials) validator.validate(context);
        assertEquals("longosibilla@libero.it", credentials.getNameId().getValue());
    }

    @Test
    public void testAuthnContextClassRefValidation() throws Exception {
        val saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setUriComparator(new ExcludingParametersURIComparator());
        saml2Configuration.setAllSignatureValidationDisabled(true);
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PASSWORD_AUTHN_CTX);
        saml2Configuration.getAuthnContextClassRefs().add(AuthnContext.PPT_AUTHN_CTX);

        val response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setSaml2Configuration(saml2Configuration);
        context.setWebContext(MockWebContext.create());
        context.getMessageContext().setMessage(response);

        val samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");
        val samlSelfMetadataContext = context.getSAMLSelfMetadataContext();
        val roleDescriptor = mock(SPSSODescriptor.class);
        when(roleDescriptor.getWantAssertionsSigned()).thenReturn(false);
        samlSelfMetadataContext.setRoleDescriptor(roleDescriptor);

        val samlEndpointContext = context.getSAMLEndpointContext();
        val endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");
        samlEndpointContext.setEndpoint(endpoint);

        assertThrows(SAMLAuthnContextClassRefException.class, () -> validator.validate(context));
    }

    @Test(expected = SAMLException.class)
    public void testAuthenticatedResponseAndAssertionWithoutSignatureThrowsException() {
        val saml2Configuration = getSaml2Configuration(true, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setSaml2Configuration(saml2Configuration);
        context.setWebContext(MockWebContext.create());
        val peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
    }

    @Test(expected = SAMLException.class)
    public void testResponseWithoutSignatureThrowsException() {
        val saml2Configuration = getSaml2Configuration(false, false);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        val peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(false);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateAssertionSignature(null, context, null);
        // expected no exceptions
    }

    @Test(expected = SAMLSignatureValidationException.class)
    public void testNotSignedAuthenticatedResponseThrowsException() throws Exception {
        val file = new File(SAML2DefaultResponseValidatorTests.class.getClassLoader().
            getResource(SAMPLE_RESPONSE_FILE_NAME).getFile());

        val xmlObject = XMLObjectSupport.unmarshallFromReader(
            Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        val response = (Response) xmlObject;
        response.setSignature(null);

        var saml2Configuration = getSaml2Configuration(false, true);
        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        val peerEntityContext = new SAMLPeerEntityContext();
        peerEntityContext.setAuthenticated(true);
        context.getMessageContext().addSubcontext(peerEntityContext);
        validator.validateSamlProtocolResponse(response, context, null);
    }

    @Test
    public void testThatInResponseToPropertyCanBeEmpty() throws Exception {
        val saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setAllSignatureValidationDisabled(true);

        val response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);

        // In case of an IdP initiated login flow, the `InResponseTo` property can be omitted.
        // (See SAML protocol specification, paragraph 3.2.2, line 1542)
        response.setInResponseTo(null);

        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        val samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");

        val endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");

        val samlEndpointContext = context.getSAMLEndpointContext();
        samlEndpointContext.setEndpoint(endpoint);

        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        var credentials = validator.validate(context);

        assertNotNull(credentials);
    }

    @Test(expected = SAMLEndpointMismatchException.class)
    public void testThatResponseDestinationThrowsExceptionWhenNull() throws Exception {
        val saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setAllSignatureValidationDisabled(true);

        val response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);

        // The `Destination` attribute can be omitted.
        // (See SAML protocol specification, paragraph 3.2.2, line 1554)
        // But the default SAML configuration forbids this case.
        response.setDestination(null);

        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        val samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");

        val endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");

        val samlEndpointContext = context.getSAMLEndpointContext();
        samlEndpointContext.setEndpoint(endpoint);

        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        var credentials = validator.validate(context);

        assertNotNull(credentials);
    }

    @Test
    public void testThatResponseDestinationCanBeNull() throws Exception {
        val saml2Configuration = getSaml2Configuration(false, false);
        saml2Configuration.setAllSignatureValidationDisabled(true);
        saml2Configuration.setResponseDestinationAttributeMandatory(false);

        val response = getResponse();
        response.setSignature(null);
        response.getAssertions().get(0).setSignature(null);

        // The `Destination` attribute can be omitted.
        // (See SAML protocol specification, paragraph 3.2.2, line 1554)
        // But this SAML configuration tolerates it.
        response.setDestination(null);

        val context = new SAML2MessageContext();
        context.setWebContext(MockWebContext.create());
        context.setSaml2Configuration(saml2Configuration);
        context.getMessageContext().setMessage(response);

        val samlSelfEntityContext = context.getSAMLSelfEntityContext();
        samlSelfEntityContext.setEntityId("https://auth.izslt.it");

        val endpoint = mock(Endpoint.class);
        when(endpoint.getLocation()).thenReturn("https://auth.izslt.it/cas/login?client_name=idptest");

        val samlEndpointContext = context.getSAMLEndpointContext();
        samlEndpointContext.setEndpoint(endpoint);

        val validator = createResponseValidatorWithSigningValidationOf(saml2Configuration);
        var credentials = validator.validate(context);

        assertNotNull(credentials);
    }
}
