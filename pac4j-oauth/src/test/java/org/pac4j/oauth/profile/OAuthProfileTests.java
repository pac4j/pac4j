package org.pac4j.oauth.profile;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.pac4j.oauth.profile.github.GitHubProfile;

import static org.junit.Assert.*;

/**
 * General test cases for GitHubProfile.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public final class OAuthProfileTests implements TestsConstants {

    @Test
    public void testClearDropBoxProfile() {
        var profile = new DropBoxProfile();
        profile.setAccessToken(VALUE);
        profile.setAccessSecret(VALUE);
        profile.removeLoginData();
        assertNull(profile.getAccessToken());
        assertNull(profile.getAccessSecret());
    }

    @Test
    public void testClearGitHubProfile() {
        var profile = new GitHubProfile();
        profile.setAccessToken("testToken");
        profile.removeLoginData();
        assertNull(profile.getAccessToken());
    }
}
