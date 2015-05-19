package org.pac4j.oauth.client;

import java.util.List;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.JsonList;
import org.pac4j.oauth.profile.strava.StravaClub;
import org.pac4j.oauth.profile.strava.StravaGear;
import org.pac4j.oauth.profile.strava.StravaProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Strava integration test. It performs authentication on Strava and retrieves a StravaProfile.
 * @author Adrian Papusoi
 */
public class StravaClientIT extends OAuthClientIT {
    @Override
    protected Client getClient() {
        final StravaClient stravaClient = new StravaClient();
        stravaClient.setApprovalPrompt("force");
        stravaClient.setKey("3945");
        stravaClient.setSecret("f03df80582396cddfbe0b895a726bac27c8cf739");
        stravaClient.setCallbackUrl(PAC4J_BASE_URL);
        stravaClient.setScope("view_private");
        return stravaClient;
    }

    @Override
    protected String getCallbackUrl(WebClient webClient, HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);

        List<HtmlElement> submitButtons = form.getElementsByAttribute("input", "type", "submit");
        //final HtmlButton submit = form.getButtonByName("login-button");
        final HtmlPage callbackPage = submitButtons.get(0).click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected HtmlPage getRedirectionPage(final WebClient webClient, final Client<?, ?> client,
            final J2EContext context) throws Exception {
        final BaseClient baseClient = (BaseClient) client;
        // force immediate redirection for tests
        baseClient.redirect(context, true, false);

        MockHttpServletResponse response = (MockHttpServletResponse) context.getResponse();
        final String redirectionUrl = response.getHeader(HttpConstants.LOCATION_HEADER);
        logger.debug("redirectionUrl : {}", redirectionUrl);
        final HtmlPage loginPage = webClient.getPage(redirectionUrl);

        final HtmlForm form = loginPage.getForms().get(0);
        final HtmlTextInput login = form.getInputByName("email");
        login.setValueAttribute("testscribeup@yahoo.fr");
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdscribeup");

        List<HtmlElement> submitButtons = form.getElementsByAttribute("button", "type", "submit");

        final HtmlPage authorizationPage = submitButtons.get(0).click();

        return authorizationPage;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final StravaProfile profile = (StravaProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("7319316", profile.getId());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(Integer.valueOf(3), profile.getResourceState());
        assertEquals(Boolean.FALSE, profile.isPremium());
        assertEquals("Nord-Pas-de-Calais", profile.getState());
        assertEquals("Pac4j", profile.getFamilyName());
        assertEquals("Adrian", profile.getFirstName());
        assertEquals("feet", profile.getMeasurementPreference());
        assertEquals("avatar/athlete/medium.png", profile.getProfileMedium());
        assertEquals("Hem", profile.getLocation());
        assertEquals("France", profile.getCountry());
        assertEquals("testscribeup@yahoo.fr", profile.getEmail());
        assertEquals("avatar/athlete/large.png", profile.getPictureUrl());

        assertEquals(1, profile.getBikes().size());
        assertEquals("b1700138", profile.getBikes().get(0).getId());
        assertEquals(Boolean.TRUE, profile.getBikes().get(0).isPrimary());
        assertEquals("BH G5", profile.getBikes().get(0).getName());
        assertEquals(Integer.valueOf(2), profile.getBikes().get(0).getResourceState());

        assertEquals(1, profile.getShoes().size());
        assertEquals("g592532", profile.getShoes().get(0).getId());
        assertEquals(Boolean.TRUE, profile.getShoes().get(0).isPrimary());
        assertEquals("adidas Runner little big", profile.getShoes().get(0).getName());
        assertEquals(Integer.valueOf(2), profile.getShoes().get(0).getResourceState());

    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(StravaProfile.class);
        kryo.register(StravaGear.class);
        kryo.register(StravaClub.class);
        kryo.register(JsonList.class);

    }
}
