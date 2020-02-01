package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = Saml2ClientAutoConfiguration.class,
    properties = {
        Pac4jConfigurationProperties.PREFIX + ".saml2.identityProviderMetadataPath=file:/path/to/idp.xml",
        Pac4jConfigurationProperties.PREFIX + ".saml2.keystorePath=file:/path/to/keystore.jks",
        Pac4jConfigurationProperties.PREFIX + ".saml2.keystorePassword=p@$$w0rd",
        Pac4jConfigurationProperties.PREFIX + ".saml2.privateKeyPassword=p@$$w0rd",
        Pac4jConfigurationProperties.PREFIX + ".saml2.serviceProviderEntityId=example-entity-id",
        Pac4jConfigurationProperties.PREFIX + ".saml2.identityProviderMetadataPath=file:/path/to/sp.xml"
    })
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class Saml2ClientAutoConfigurationTests {

    @Autowired
    private SAML2Client saml2Client;

    @Test
    public void verifyOperation() {
        assertNotNull(this.saml2Client);
    }
}
