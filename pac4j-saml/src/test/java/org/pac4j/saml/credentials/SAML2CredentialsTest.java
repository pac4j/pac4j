package org.pac4j.saml.credentials;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.credentials.SAML2Credentials.SAMLAttribute;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.sso.impl.SAML2AuthnResponseValidator;
import org.pac4j.saml.util.Configuration;

public class SAML2CredentialsTest {

	public static final String RESPONSE_FILE_NAME = "sample_authn_response_with_complextype.xml";

	private SAML2AuthnResponseValidatorForTest validator;

	@Before
	public void setUp() {
		validator = new SAML2AuthnResponseValidatorForTest(mock(SAML2SignatureTrustEngineProvider.class),
				mock(Decrypter.class), mock(ReplayCacheProvider.class), mock(SAML2Configuration.class));
	}

	@Test
	public void verifyComplexTypeExtractionWorks() throws Exception {
		final var file = new File(
				SAML2CredentialsTest.class.getClassLoader().getResource(RESPONSE_FILE_NAME).getFile());

		final var xmlObject = XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(),
				new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));

		final var response = (Response) xmlObject;
		Assertion assertion = response.getAssertions().get(0);

		var openSamlAttributes = validator.collectAssertionAttributes(assertion);
		List<SAMLAttribute> extractedPac4jAttributes = SAML2Credentials.SAMLAttribute.from(openSamlAttributes);
		assertTrue(extractedPac4jAttributes.size() > 0);
		
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("Foo")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("OrganizationName")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("EmailAddress")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("Street")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("StreetNumber")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("City")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("ZipCode")).findAny().orElseThrow(AssertionError::new);
		extractedPac4jAttributes.stream().filter(x -> x.getName().equals("Country")).findAny().orElseThrow(AssertionError::new);
	}

	public static class SAML2AuthnResponseValidatorForTest extends SAML2AuthnResponseValidator {

		public SAML2AuthnResponseValidatorForTest(SAML2SignatureTrustEngineProvider engine, Decrypter decrypter,
				ReplayCacheProvider replayCache, SAML2Configuration saml2Configuration) {
			super(engine, decrypter, replayCache, saml2Configuration);
		}

		// Changed visibility to public.
		@Override
		public List<Attribute> collectAssertionAttributes(final Assertion subjectAssertion) {
			return super.collectAssertionAttributes(subjectAssertion);
		}
	}
}
