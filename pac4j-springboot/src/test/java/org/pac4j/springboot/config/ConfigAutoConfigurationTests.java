package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = ConfigAutoConfiguration.class,
    properties = {
        "pac4j.properties." + PropertiesConstants.CAS_PROTOCOL + "=CAS20",
        "pac4j.properties." + PropertiesConstants.CAS_LOGIN_URL + "=https://example.com/cas/login",

        "pac4j.properties." + PropertiesConstants.FACEBOOK_ID + "=id",
        "pac4j.properties." + PropertiesConstants.FACEBOOK_SECRET + "=secret",

        "pac4j.properties." + PropertiesConstants.GITHUB_ID + "=id",
        "pac4j.properties." + PropertiesConstants.GITHUB_SECRET + "=secret",

        "pac4j.properties." + PropertiesConstants.GOOGLE_ID + "=id",
        "pac4j.properties." + PropertiesConstants.GOOGLE_SECRET + "=secret",

        "pac4j.properties." + PropertiesConstants.OAUTH2_ID + "=id",
        "pac4j.properties." + PropertiesConstants.OAUTH2_SECRET + "=secret",

        "pac4j.properties." + PropertiesConstants.OIDC_ID + "=id",
        "pac4j.properties." + PropertiesConstants.OIDC_SECRET + "=secret",

        "pac4j.properties." + PropertiesConstants.SAML_IDENTITY_PROVIDER_METADATA_PATH + "=file:/path/to/idp.xml",
        "pac4j.properties." + PropertiesConstants.SAML_KEYSTORE_PATH + "=file:/path/to/keystore.jks",
        "pac4j.properties." + PropertiesConstants.SAML_KEYSTORE_PASSWORD + "=p@$$w0rd",
        "pac4j.properties." + PropertiesConstants.SAML_PRIVATE_KEY_PASSWORD + "=p@$$w0rd",
        "pac4j.properties." + PropertiesConstants.SAML_SERVICE_PROVIDER_ENTITY_ID + "=example-entity-id",
        "pac4j.properties." + PropertiesConstants.SAML_SERVICE_PROVIDER_METADATA_PATH + "=file:/path/to/sp.xml",

        "pac4j.properties." + PropertiesConstants.TWITTER_ID + "=id",
        "pac4j.properties." + PropertiesConstants.TWITTER_SECRET + "=secret",

        "pac4j.callbackUrl=https://pac4j.example.org"
    })
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
@RunWith(SpringRunner.class)
public class ConfigAutoConfigurationTests {

    @Autowired
    private Config config;

    @Test
    public void verifyOperation() {
        assertNotNull(config);
    }
}
