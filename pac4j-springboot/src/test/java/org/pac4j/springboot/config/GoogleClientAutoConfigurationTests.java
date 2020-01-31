package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = GoogleClientAutoConfiguration.class,
    properties = {
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GOOGLE_ID + "=id",
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GOOGLE_SECRET + "=secret",
    })
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class GoogleClientAutoConfigurationTests {

    @Autowired
    private Google2Client google2Client;

    @Test
    public void verifyOperation() {
        assertNotNull(this.google2Client);
    }
}
