package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.YahooClient;
import org.pac4j.oauth.profile.yahoo.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link YahooClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunYahooClient extends RunClient {

    public static void main(String[] args) {
        new RunYahooClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@yahoo.fr";
    }

    @Override
    protected String getPassword() {
        return "B1ourouf";
    }

    @Override
    protected IndirectClient getClient() {
        final var yahooClient = new YahooClient();
        yahooClient
                .setKey("dj0yJmk9djFiREdkbHc0dWdMJmQ9WVdrOVYwNHdkbnBWTkhFbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03MQ--");
        yahooClient.setSecret("227eb8180d8212181a3856969a83e93fa14f1116");
        yahooClient.setCallbackUrl(PAC4J_BASE_URL);
        return yahooClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final var profile = (YahooProfile) userProfile;
        assertEquals("PCSXZCYSWC6XUJNMZKRGWVPHNU", profile.getId());
        assertEquals(YahooProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "PCSXZCYSWC6XUJNMZKRGWVPHNU",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), YahooProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@yahoo.fr", "Test", "ScribeUP", "Test ScribeUP", "Test",
                Gender.MALE, Locale.FRANCE,
                "https://s.yimg.com/wm/modern/images/default_user_profile_pic_192.png",
                "http://profile.yahoo.com/PCSXZCYSWC6XUJNMZKRGWVPHNU", "Chatou, Ile-de-France");
        assertEquals("my profile", profile.getAboutMe());
        final var addresses = profile.getAddresses();
        assertEquals(2, addresses.size());
        final var address = addresses.get(0);
        assertEquals(3, address.getId().intValue());
        assertTrue(address.getCurrent());
        assertEquals(Locale.FRENCH, address.getCountry());
        assertEquals("", address.getState());
        assertEquals("", address.getCity());
        assertEquals("78400", address.getPostalCode());
        assertEquals("", address.getStreet());
        assertEquals("HOME", address.getType());
        assertEquals(1976, profile.getBirthYear().intValue());
        assertEquals("03/10", new SimpleDateFormat("MM/dd").format(profile.getBirthdate()));
        assertEquals("2012-02-06T12:46:43Z", new SimpleDateFormat(Converters.DATE_TZ_RFC822_FORMAT).format(profile.getCreated()));
        assertEquals(41, profile.getDisplayAge().intValue());
        final var disclosures = profile.getDisclosures();
        assertEquals(2, disclosures.size());
        final var disclosure = disclosures.get(0);
        assertEquals("1", disclosure.getAcceptance());
        assertEquals("bd", disclosure.getName());
        assertEquals("1", disclosure.getVersion());
        final var emails = profile.getEmails();
        assertEquals(3, emails.size());
        final var email = emails.get(1);
        assertEquals(1, email.getId().intValue());
        assertTrue(email.getPrimary());
        assertEquals("testscribeup@yahoo.fr", email.getHandle());
        assertEquals("HOME", email.getType());
        assertEquals(Gender.MALE, profile.getGender());
        final var image = profile.getImage();
        assertEquals("https://s.yimg.com/wm/modern/images/default_user_profile_pic_192.png", image.getImageUrl());
        assertEquals(192, image.getWidth().intValue());
        assertEquals(192, image.getHeight().intValue());
        assertEquals("192x192", image.getSize());
        final var interests = profile.getInterests();
        assertEquals(11, interests.size());
        final var interest = interests.get(0);
        assertEquals("basic interest", interest.getDeclaredInterests().get(0));
        assertEquals("prfFavHobbies", interest.getInterestCategory());
        assertTrue(profile.getIsConnected());
        assertEquals("2012-02-06T12:46:36Z", new SimpleDateFormat(Converters.DATE_TZ_RFC822_FORMAT).format(profile.getMemberSince()));
        assertEquals("Europe/Paris", profile.getTimeZone());
        assertEquals("2017-09-19T03:20:28Z", new SimpleDateFormat(Converters.DATE_TZ_RFC822_FORMAT).format(profile.getUpdated()));
        assertEquals("https://social.yahooapis.com/v1/user/PCSXZCYSWC6XUJNMZKRGWVPHNU/profile", profile.getUri());
        assertNotNull(profile.getAccessSecret());
        assertEquals("A", profile.getAgeCategory());
        assertEquals(25, profile.getAttributes().size());
    }
}
