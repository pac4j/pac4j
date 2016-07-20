package org.pac4j.saml.metadata;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.saml.client.SAML2ClientConfiguration;

public class SAML2ServiceProviderMetadataResolverTest {

  private SAML2ServiceProviderMetadataResolver metadataResolver;

  @Before
  public void setUp() throws Exception {
    SAML2ClientConfiguration configuration = new SAML2ClientConfiguration();
    configuration.setServiceProviderMetadataPath("target");
    configuration.setServiceProviderMetadataPath("target/out.xml");
    metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration, "http://localhost", null);
  }

  @Test(expected = TechnicalException.class)
  public void resolveShouldThrowExceptionIfCredentialsProviderIsNullAndAuthnRequestSignedIsTrue() {
    metadataResolver.resolve();
  }
}
