package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = OAuth20ClientAutoConfiguration.class,
    properties = {
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OAUTH2_ID + "=id",
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OAUTH2_SECRET + "=secret",
    })
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class OAuth20ClientAutoConfigurationTests {

    @Autowired
    private OAuth20Client oAuth20Client;

    @Test
    public void verifyOperation() {
        assertNotNull(this.oAuth20Client);
    }
}
