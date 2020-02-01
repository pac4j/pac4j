package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.cas.client.CasClient;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = CasClientAutoConfiguration.class,
    properties = {
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.CAS_PROTOCOL + "=CAS20",
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.CAS_LOGIN_URL + "=https://example.com/cas/login",
    })
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class CasClientAutoConfigurationTests {

    @Autowired
    private CasClient casClient;

    @Test
    public void verifyOperation() {
        assertNotNull(this.casClient);
    }
}
