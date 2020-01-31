package org.pac4j.springboot.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = GitHubClientAutoConfiguration.class,
    properties = {
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GITHUB_ID + "=id",
        Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GITHUB_SECRET + "=secret",
    })
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class GitHubClientAutoConfigurationTests {

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    public void verifyOperation() {
        assertNotNull(this.gitHubClient);
    }
}
