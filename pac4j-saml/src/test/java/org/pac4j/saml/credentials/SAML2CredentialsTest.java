package org.pac4j.saml.credentials;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2ConfigurationContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2Credentials.SAMLAttribute;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.profile.converter.ComplexTypeSAML2AttributeConverter;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.sso.impl.SAML2AuthnResponseValidator;
import org.pac4j.saml.util.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SAML2CredentialsTest {

    private static final String RESPONSE_FILE_NAME = "sample_authn_response.xml";
    private static final String RESPONSE_FILE_NAME_FROM_ADFS = "sample_authn_response_from_adfs.xml";
    private static final String RESPONSE_FILE_NAME_FROM_UKAMF = "sample_authn_response_from_ukamf.xml";
    private static final String RESPONSE_FILE_NAME_WITH_COMPLEXTYPE = "sample_authn_response_with_complextype.xml";
    private static final String RESPONSE_FILE_NAME_WITH_MULTIVALUEDATTR = "sample_authn_response_with_multivaluedattr.xml";

    private SAML2AuthnResponseValidator validator;
    private SAML2MessageContext mockSaml2MessageContext;
    private SAML2Configuration saml2Configuration;

    @Before
    public void setUp() {
        var mockStatus = mock(Status.class);
        var mockStatusCode = mock(StatusCode.class);
        when(mockStatus.getStatusCode()).thenReturn(mockStatusCode);
        when(mockStatusCode.getValue()).thenReturn(StatusCode.SUCCESS);

        var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(mockStatus);
        when(mockResponse.getVersion()).thenReturn(SAMLVersion.VERSION_20);
        when(mockResponse.getIssueInstant()).thenReturn(Instant.now());

        var mockEndpoint = mock(Endpoint.class);
        var mockSamlEndpointContext = mock(SAMLEndpointContext.class);
        when(mockSamlEndpointContext.getEndpoint()).thenReturn(mockEndpoint);

        var mockMessageContext = mock(MessageContext.class);
        when(mockMessageContext.getMessage()).thenReturn(mockResponse);

        var mockSAMLSubjectNameIdentifierContext = mock(SAMLSubjectNameIdentifierContext.class);
        when(mockSAMLSubjectNameIdentifierContext.getSAML2SubjectNameID()).thenReturn(mock(NameID.class));

        saml2Configuration = new SAML2Configuration();
        saml2Configuration.setLogoutHandler(mock(LogoutHandler.class));

        mockSaml2MessageContext = mock(SAML2MessageContext.class);
        when(mockSaml2MessageContext.getMessageContext()).thenReturn(mockMessageContext);
        when(mockSaml2MessageContext.getConfigurationContext()).thenReturn(mock(SAML2ConfigurationContext.class));
        when(mockSaml2MessageContext.getSAMLEndpointContext()).thenReturn(mockSamlEndpointContext);
        when(mockSaml2MessageContext.getSaml2Configuration()).thenReturn(mock(SAML2Configuration.class));
        when(mockSaml2MessageContext.getSAMLSubjectNameIdentifierContext()).thenReturn(mockSAMLSubjectNameIdentifierContext);
        when(mockSaml2MessageContext.getBaseID()).thenReturn(mock(BaseID.class));

        validator = new SAML2AuthnResponseValidator(mock(SAML2SignatureTrustEngineProvider.class),
            mock(Decrypter.class), null, saml2Configuration);
    }

    @Test
    public void verifyStandardExtractionWorks() throws Exception {
        var credentials = extractCredentials(RESPONSE_FILE_NAME);
        assertNotNull(credentials);
        var attributes = credentials.getAttributes();
        assertNotNull(attributes);
        assertEquals("friendlyGender", attributes.get(0).getFriendlyName());

        var resultAttributes = attributes.stream()
            .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
        assertEquals(14, resultAttributes.size());
        assertEquals("F", resultAttributes.get("gender").get(0));
        assertEquals("Ricci", resultAttributes.get("familyName").get(0));
        assertEquals("Eustachio", resultAttributes.get("name").get(0));
        assertEquals(Pac4jConstants.EMPTY_STRING, resultAttributes.get("mobilePhone").get(0));
        assertEquals("TINIT-NNJEMM98O38H730Z", resultAttributes.get("fiscalNumber").get(0));
        assertEquals(Pac4jConstants.EMPTY_STRING, resultAttributes.get("placeOfBirth").get(0));
        assertEquals("longosibilla@libero.it", resultAttributes.get("email").get(0));
        assertEquals(Pac4jConstants.EMPTY_STRING, resultAttributes.get("countyOfBirth").get(0));
        assertEquals(Pac4jConstants.EMPTY_STRING, resultAttributes.get("address").get(0));
        assertEquals("1990-01-31", resultAttributes.get("dateOfBirth").get(0));
        assertEquals("779ec30a-36de-de1a-b783-0032689e74ba", resultAttributes.get("spidCode").get(0));
        assertEquals(Pac4jConstants.EMPTY_STRING, resultAttributes.get("digitalAddress").get(0));
        assertEquals("42", resultAttributes.get("anInteger").get(0));
        assertEquals("true", resultAttributes.get("aBoolean").get(0));
    }

    @Test
    public void verifyStandardExtractionWorksForAdfs() throws Exception {
        var credentials = extractCredentials(RESPONSE_FILE_NAME_FROM_ADFS);
        assertNotNull(credentials);
        var attributes = credentials.getAttributes();
        assertNotNull(attributes);

        var resultAttributes = attributes.stream()
            .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
        assertEquals(4, resultAttributes.size());
        assertEquals("John", resultAttributes.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname").get(0));
        assertEquals("DOE", resultAttributes.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname").get(0));
        assertEquals("jdoe@company", resultAttributes.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress").get(0));
        assertEquals("...", resultAttributes.get("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").get(0));
    }

    @Test
    public void verifyStandardExtractionWorksForUkamf() throws Exception {
        var credentials = extractCredentials(RESPONSE_FILE_NAME_FROM_UKAMF);
        assertNotNull(credentials);
        var attributes = credentials.getAttributes();
        assertNotNull(attributes);

        var resultAttributes = attributes.stream()
            .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
        assertEquals(5, resultAttributes.size());
        assertEquals("http://ukfederation.org.uk/entitlements/example", resultAttributes.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.7").get(0));
        assertEquals("student@test.ukfederation.org.uk", resultAttributes.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.9").get(0));
        assertEquals("student", resultAttributes.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.1").get(0));
        assertEquals("o8WNXY3pMlSjaLHi6nOMzB+6NNA=", resultAttributes.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.10").get(0));
        assertEquals("craig@test.ukfederation.org.uk", resultAttributes.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.6").get(0));
    }

    @Test
    public void verifyComplexTypeExtractionWorks() throws Exception {
        try {
            saml2Configuration.setSamlAttributeConverter(new ComplexTypeSAML2AttributeConverter());

            var credentials = extractCredentials(RESPONSE_FILE_NAME_WITH_COMPLEXTYPE);
            assertNotNull(credentials);
            var attributes = credentials.getAttributes();
            assertNotNull(attributes);

            var resultAttributes = attributes.stream()
                .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
            assertEquals(8, resultAttributes.size());
            assertEquals("Bar", resultAttributes.get("Foo").get(0));
            assertEquals("Example Corp", resultAttributes.get("OrganizationName").get(0));
            assertEquals("employee@example.com", resultAttributes.get("EmailAddress").get(0));
            assertEquals("Example St", resultAttributes.get("Street").get(0));
            assertEquals("123", resultAttributes.get("StreetNumber").get(0));
            assertEquals("ExampleCity", resultAttributes.get("City").get(0));
            assertEquals("123456", resultAttributes.get("ZipCode").get(0));
            assertEquals("ExampleCountry", resultAttributes.get("Country").get(0));
        } finally {
            saml2Configuration.setSamlAttributeConverter(new SimpleSAML2AttributeConverter());
        }
    }

    @Test
    public void verifyMultiValuedAttributeExtraction() throws Exception {
        var credentials = extractCredentials(RESPONSE_FILE_NAME_WITH_MULTIVALUEDATTR);
        assertNotNull(credentials);
        var attributes = credentials.getAttributes();
        assertNotNull(attributes);

        assertEquals(1, attributes.size());
        var resultAttributes = attributes.stream()
            .collect(Collectors.toMap(SAMLAttribute::getName, SAMLAttribute::getAttributeValues));
        assertEquals(1, resultAttributes.size());
        assertEquals(List.of("Product-X Dev", "Product-X Test"), resultAttributes.get("groups"));
    }

    private SAML2Credentials extractCredentials(String filename) throws Exception {
        var file = new File(SAML2CredentialsTest.class.getClassLoader().getResource(filename).getFile());

        var xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(),
            new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

        var response = (Response) xmlObject;
        var assertion = response.getAssertions().get(0);
        when(mockSaml2MessageContext.getSubjectAssertion()).thenReturn(assertion);

        return (SAML2Credentials) validator.validate(mockSaml2MessageContext);
    }
}
