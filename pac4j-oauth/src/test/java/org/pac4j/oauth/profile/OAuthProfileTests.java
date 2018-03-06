package org.pac4j.oauth.profile;

import org.junit.Test;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.pac4j.oauth.profile.github.GitHubProfile;

import java.util.Map;

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
        DropBoxProfile profile = new DropBoxProfile();
        profile.setAccessToken(VALUE);
        profile.setAccessSecret(VALUE);
        profile.clearSensitiveData();
        assertNull(profile.getAccessToken());
        assertNull(profile.getAccessSecret());
    }

    @Test
    public void testClearGitHubProfile() {
        GitHubProfile profile = new GitHubProfile();
        profile.setAccessToken("testToken");
        profile.clearSensitiveData();
        assertNull(profile.getAccessToken());
    }

    @Test
    public void testBuildProfileOldTypedId() {
        final GitHubProfile profile = new GitHubProfile();
        profile.setId(ID);
        final GitHubProfile profile2 =
            (GitHubProfile) ProfileHelper.restoreOrBuildProfile(null, profile.getTypedId(), profile.getAttributes(), null);
        assertEquals(ID, profile2.getId());
        final GitHubProfile profile3 =
            (GitHubProfile) ProfileHelper.restoreOrBuildProfile(null, profile.getTypedId(), profile.getAttributes(), null);
        assertEquals(ID, profile3.getId());
    }

    @Test
    public void testBuildProfileTypedId() {
        final GitHubProfile profile = new GitHubProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final GitHubProfile profile2 =
            (GitHubProfile) ProfileHelper.restoreOrBuildProfile(null, profile.getTypedId(), profile.getAttributes(), null);
        assertEquals(ID, profile2.getId());
        final Map<String, Object> attributes = profile2.getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(VALUE, attributes.get(NAME));
        final GitHubProfile profile3 =
            (GitHubProfile) ProfileHelper.restoreOrBuildProfile(null, profile.getTypedId(), profile.getAttributes(), null);
        assertEquals(ID, profile3.getId());
    }
}
