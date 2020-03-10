package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.WordPressClient;
import org.pac4j.oauth.profile.wordpress.WordPressLinks;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link WordPressClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunWordPressClient extends RunClient {

    public static void main(final String[] args) {
        new RunWordPressClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected IndirectClient getClient() {
        final WordPressClient wordPressClient = new WordPressClient();
        wordPressClient.setKey("209");
        wordPressClient.setSecret("xJBXMRVvKrvHqyvM6BpzkenJVMIdQrIWKjPJsezjGYu71y7sDgt8ibz6s9IFLqU8");
        wordPressClient.setCallbackUrl(PAC4J_URL);
        return wordPressClient;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final WordPressProfile profile = (WordPressProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("35944437", profile.getId());
        assertEquals(WordPressProfile.class.getName() + CommonProfile.SEPARATOR + "35944437", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WordPressProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", null, null, "testscribeup", "testscribeup",
                Gender.UNSPECIFIED, null,
                "https://0.gravatar.com/avatar/67c3844a672979889c1e3abbd8c4eb22?s=96&d=identicon&r=G",
                "http://en.gravatar.com/testscribeup", null);
        assertEquals(36224958, profile.getPrimaryBlog().intValue());
        final WordPressLinks links = profile.getLinks();
        assertEquals("https://public-api.wordpress.com/rest/v1/me", links.getSelf());
        assertEquals("https://public-api.wordpress.com/rest/v1/me/help", links.getHelp());
        assertEquals("https://public-api.wordpress.com/rest/v1/sites/36224958", links.getSite());
        assertEquals(8, profile.getAttributes().size());
    }
}
