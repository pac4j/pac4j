package org.pac4j.oauth.run;

import java.util.Date;

import org.pac4j.oauth.profile.github.GitHubAttributesDefinition;
import org.pac4j.oauth.profile.github.GitHubPlan;
import org.pac4j.oauth.profile.github.GitHubProfile;
import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.GenericOAuth20Client;
import org.pac4j.oauth.profile.generic.GenericOAuth20Profile;
import static org.junit.Assert.*;

/**
 * Test class for {@link GenericOAuth20StateClient}
 * Connects with Google for testing
 * @see RunGoogle2Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class RunGeneric20Client extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunGoogle2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup91";
    }

    @Override
    protected IndirectClient getClient() {
        final GenericOAuth20Client client = new GenericOAuth20Client();
        client.setAttributesDefinition(new GitHubAttributesDefinition());
        client.setAuthUrl("https://github.com/login/oauth/authorize");
        client.setTokenUrl("https://github.com/login/oauth/access_token");
        client.setProfileUrl("https://api.github.com/user");
        client.setKey("62374f5573a89a8f9900");
        client.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setScope("user");
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GenericOAuth20Profile.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final GenericOAuth20Profile profile = (GenericOAuth20Profile) userProfile;
        profile.setAttributesDefinition(new GitHubAttributesDefinition());
        assertEquals("1412558", profile.getId());
        assertEquals(GitHubProfile.class.getName() + CommonProfile.SEPARATOR + "1412558", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GitHubProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", null, null, "Test", "testscribeup",
                Gender.UNSPECIFIED, null, "https://avatars.githubusercontent.com/u/1412558?",
                "https://github.com/testscribeup", "Paris");
        assertEquals("User", (String) profile.getAttribute("type"));
        assertEquals("ScribeUp", (String) profile.getAttribute("blog"));
        assertEquals("https://api.github.com/users/testscribeup", (String) profile.getAttribute("url"));
        assertEquals(0, ((Integer) profile.getAttribute("public_gists")).intValue());
        assertEquals(0, ((Integer) profile.getAttribute("following")).intValue());
        assertEquals(0, ((Integer) profile.getAttribute("private_gists")).intValue());
        assertEquals(0, ((Integer) profile.getAttribute("public_repos")).intValue());
        assertEquals("", (String) profile.getAttribute("gravatar_id"));
        assertEquals(0, ((Integer) profile.getAttribute("followers")).intValue());
        assertEquals("Company", (String) profile.getAttribute("company"));
        assertNull((Boolean) profile.getAttribute("hirable"));
        assertEquals(0, ((Integer) profile.getAttribute("collaborators")).intValue());
        assertNull((String) profile.getAttribute("bio"));
        assertEquals(0, ((Integer) profile.getAttribute("total_private_repos")).intValue());
        assertEquals("2012-02-06T13:05:21Z", ((Date) profile.getAttribute("created_at")).toString());
        assertEquals(0, ((Integer) profile.getAttribute("disk_usage")).intValue());
        assertEquals(0, ((Integer) profile.getAttribute("owned_private_repos")).intValue());
        final GitHubPlan plan = (GitHubPlan) profile.getAttribute("plan");
        assertEquals("free", plan.getName());
        assertEquals(0, plan.getCollaborators().intValue());
        assertEquals(976562499, plan.getSpace().intValue());
        assertEquals(0, plan.getPrivateRepos().intValue());
        assertEquals(24, profile.getAttributes().size());
    }
}
