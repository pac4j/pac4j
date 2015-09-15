package org.pac4j.oauth.profile.github;

import junit.framework.TestCase;

/**
 * General test cases for GitHubProfile.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestGitHubProfile extends TestCase {

    public void testClearGitHubProfile() {
        GitHubProfile profile = new GitHubProfile();
        profile.setAccessToken("testToken");
        profile.clear();
        assertEquals("", profile.getAccessToken());
    }

}
