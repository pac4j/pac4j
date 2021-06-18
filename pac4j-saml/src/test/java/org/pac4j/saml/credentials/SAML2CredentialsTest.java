package org.pac4j.saml.credentials;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import junit.framework.AssertionFailedError;
import org.hibernate.criterion.Example;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.StatusImpl;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.storage.ReplayCache;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2ConfigurationContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2Credentials.SAMLAttribute;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.sso.impl.SAML2AuthnResponseValidator;
import org.pac4j.saml.util.Configuration;

public class SAML2CredentialsTest {

    public static final String RESPONSE_FILE_NAME = "sample_authn_response_with_complextype.xml";

    private SAML2AuthnResponseValidator validator;

    private SAML2MessageContext mockSaml2MessageContext;

    @Before
    public void setUp() {

        final var mockStatus = mock(Status.class);
        final var mockStatusCode = mock (StatusCode.class);
        when(mockStatus.getStatusCode()).thenReturn(mockStatusCode);
        when(mockStatusCode.getValue()).thenReturn(StatusCode.SUCCESS);

        final var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(mockStatus);
        when(mockResponse.getVersion()).thenReturn(SAMLVersion.VERSION_20);
        when(mockResponse.getIssueInstant()).thenReturn(Instant.now());

        final var mockEndpoint = mock(Endpoint.class);
        final var mockSamlEndpointContext = mock(SAMLEndpointContext.class);
        when (mockSamlEndpointContext.getEndpoint()).thenReturn(mockEndpoint);

        final var mockMessageContext = mock(MessageContext.class);
        when(mockMessageContext.getMessage()).thenReturn(mockResponse);

        final var mockSAMLSubjectNameIdentifierContext = mock(SAMLSubjectNameIdentifierContext.class);
        when(mockSAMLSubjectNameIdentifierContext.getSAML2SubjectNameID()).thenReturn(mock(NameID.class));

        final var mockSaml2Configuration = mock(SAML2Configuration.class);
        when(mockSaml2Configuration.getLogoutHandler()).thenReturn(mock(LogoutHandler.class));

        mockSaml2MessageContext = mock(SAML2MessageContext.class);
        when(mockSaml2MessageContext.getMessageContext()).thenReturn(mockMessageContext);
        when(mockSaml2MessageContext.getConfigurationContext()).thenReturn(mock(SAML2ConfigurationContext.class));
        when(mockSaml2MessageContext.getSAMLEndpointContext()).thenReturn(mockSamlEndpointContext);
        when(mockSaml2MessageContext.getSAML2Configuration()).thenReturn(mock(SAML2Configuration.class));
        when(mockSaml2MessageContext.getSAMLSubjectNameIdentifierContext()).thenReturn(mockSAMLSubjectNameIdentifierContext);
        when(mockSaml2MessageContext.getBaseID()).thenReturn(mock(BaseID.class));

        validator = new SAML2AuthnResponseValidator(mock(SAML2SignatureTrustEngineProvider.class),
            mock(Decrypter.class), null, mockSaml2Configuration);
    }

    @Test
    public void verifyComplexTypeExtractionWorks() throws Exception {
        final var file = new File(
            SAML2CredentialsTest.class.getClassLoader().getResource(RESPONSE_FILE_NAME).getFile());

        final var xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        final var response = (Response) xmlObject;
        final Assertion assertion = response.getAssertions().get(0);
        when(mockSaml2MessageContext.getSubjectAssertion()).thenReturn(assertion);

        final var credentials = (SAML2Credentials)validator.validate(mockSaml2MessageContext);
        assertNotNull(credentials);
        final List<SAMLAttribute> attributes = credentials.getAttributes();
        assertNotNull(attributes);

        final Map<String, List<String>> resultAttributes = attributes.stream()
            .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
        assertEquals("Bar", resultAttributes.get("Foo").get(0));
        assertEquals("Example Corp", resultAttributes.get("OrganizationName").get(0));
        assertEquals("employee@example.com", resultAttributes.get("EmailAddress").get(0));
        assertEquals("Example St", resultAttributes.get("Street").get(0));
        assertEquals("123", resultAttributes.get("StreetNumber").get(0));
        assertEquals("ExampleCity", resultAttributes.get("City").get(0));
        assertEquals("123456", resultAttributes.get("ZipCode").get(0));
        assertEquals("ExampleCountry", resultAttributes.get("Country").get(0));
    }
}
