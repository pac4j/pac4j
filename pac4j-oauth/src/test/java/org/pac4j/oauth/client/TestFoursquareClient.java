package org.pac4j.oauth.client;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.foursquare.FoursquareProfile;
import org.pac4j.oauth.profile.foursquare.FoursquareUserFriend;
import org.pac4j.oauth.profile.foursquare.FoursquareUserFriendGroup;
import org.pac4j.oauth.profile.foursquare.FoursquareUserFriends;

import java.util.ArrayList;

public class TestFoursquareClient extends TestOAuthClient {

    @Override
    protected boolean isJavascriptEnabled() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final FoursquareClient foursquareClient = new FoursquareClient();
        foursquareClient.setKey("CONTW2V0SBAHTMXMUA2G1I2P55WGRVJLGBLNY2CFSG1JV4DQ");
        foursquareClient.setSecret("EVAZNDHEQODSIPOKC13JAAPMR3IJRSMLE55TYUW3VYRY3VTC");
        foursquareClient.setCallbackUrl(PAC4J_BASE_URL);
        return foursquareClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(2);
        final HtmlTextInput login = form.getInputByName("emailOrPhone");
        login.setValueAttribute("pac4j@mailinator.com");
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setText("pac4j");
        final HtmlSubmitInput submit = form.getInputByValue("Log in and allow");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(FoursquareProfile.class);
        kryo.register(FoursquareUserFriends.class);
        kryo.register(ArrayList.class);
        kryo.register(FoursquareUserFriendGroup.class);
        kryo.register(FoursquareUserFriend.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final FoursquareProfile profile = (FoursquareProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("81827700", profile.getId());
        assertEquals(FoursquareProfile.class.getSimpleName() + UserProfile.SEPARATOR + "81827700", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), FoursquareProfile.class));
        assertCommonProfile(userProfile,
                "pac4j@mailinator.com",
                "Pac4j",
                "Pac4j",
                null,
                null,
                Gender.UNSPECIFIED,
                null,
                "https://irs0.4sqi.net/img/user/original/blank_boy.png",
                null, null);


    }
}
