package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.profile.github.GitHubProfile;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link GitHubClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunGithubClient extends RunClient {

    public static void main(String[] args) {
        new RunGithubClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup1";
    }

    @Override
    protected IndirectClient getClient() {
        final var githubClient = new GitHubClient();
        githubClient.setKey("62374f5573a89a8f9900");
        githubClient.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        githubClient.setCallbackUrl(PAC4J_BASE_URL);
        githubClient.setScope("user");
        return githubClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final var profile = (GitHubProfile) userProfile;
        assertEquals("1412558", profile.getId());
        assertEquals(GitHubProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "1412558", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GitHubProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", null, null, "Test", "testscribeup",
                Gender.UNSPECIFIED, null, "https://avatars2.githubusercontent.com/u/1412558?",
                "https://github.com/testscribeup", "Paris");
        assertEquals("User", profile.getType());
        assertEquals("ScribeUp", profile.getBlog());
        assertEquals("https://api.github.com/users/testscribeup", profile.getUrl());
        assertEquals(0, profile.getPublicGists().intValue());
        assertEquals(0, profile.getFollowing().intValue());
        assertEquals(0, profile.getPrivateGists().intValue());
        assertEquals(0, profile.getPublicRepos().intValue());
        assertEquals("", profile.getGravatarId());
        assertEquals(0, profile.getFollowers().intValue());
        assertEquals("Company", profile.getCompany());
        assertNull(profile.getHireable());
        assertEquals(0, profile.getCollaborators().intValue());
        assertNull(profile.getBio());
        assertEquals(0, profile.getTotalPrivateRepos().intValue());
        assertEquals("2012-02-06T13:05:21Z", new SimpleDateFormat(Converters.DATE_TZ_RFC822_FORMAT).format(profile.getCreatedAt()));
        assertEquals(0, profile.getDiskUsage().intValue());
        assertEquals(0, profile.getOwnedPrivateRepos().intValue());
        final var plan = profile.getPlan();
        assertEquals("free", plan.getName());
        assertEquals(0, plan.getCollaborators().intValue());
        assertEquals(976562499, plan.getSpace().intValue());
        assertEquals(10000, plan.getPrivateRepos().intValue());
        assertEquals(24, profile.getAttributes().size());
    }
}
