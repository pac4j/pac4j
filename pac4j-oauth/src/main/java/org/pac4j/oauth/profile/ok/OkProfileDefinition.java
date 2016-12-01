package org.pac4j.oauth.profile.ok;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.client.OkClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Represents the profile definitions on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfileDefinition extends OAuth20ProfileDefinition<OkProfile> {

    public static final String UID = "uid";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "age";
    public static final String NAME = "name";
    public static final String LOCATION_CITY = "location.city";
    public static final String LOCATION_COUNTRY = "location.country";
    public static final String LOCATION_COUNTRY_CODE = "location.countryCode";
    public static final String LOCATION_COUNTRY_NAME = "location.countryName";
    public static final String ONLINE = "online";
    public static final String LAST_NAME = "last_name";
    public static final String HAS_EMAIL = "has_email";
    public static final String CURRENT_STATUS = "current_status";
    public static final String CURRENT_STATUS_ID = "current_status_id";
    public static final String CURRENT_STATUS_DATE = "current_status_date";
    public static final String PIC_1 = "pic_1";
    public static final String PIC_2 = "pic_2";

    private static final String API_BASE_URL = "http://api.ok.ru/fb.do?";

    public OkProfileDefinition() {
        super(x -> new OkProfile());
        Arrays.stream(new String[] {UID, BIRTHDAY, AGE, NAME, LOCATION_CITY, LOCATION_COUNTRY, LOCATION_COUNTRY_CODE,
                LOCATION_COUNTRY_NAME, ONLINE, LAST_NAME, HAS_EMAIL, CURRENT_STATUS, CURRENT_STATUS_ID, CURRENT_STATUS_DATE})
                .forEach(a -> primary(a, Converters.STRING));
        primary(PIC_1, Converters.URL);
        primary(PIC_2, Converters.URL);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        final OkClient client = (OkClient) configuration.getClient();
        String baseParams =
                "application_key=" + client.getPublicKey() +
                        "&format=json" +
                        "&method=users.getCurrentUser";
        String finalSign;
        try {
            String preSign = getMD5SignAsHexString(accessToken.getAccessToken() + configuration.getSecret());
            finalSign = getMD5SignAsHexString(baseParams.replaceAll("&", "") + preSign);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return API_BASE_URL + baseParams + "&access_token=" + accessToken.getAccessToken() + "&sig=" + finalSign;
    }

    protected String getMD5SignAsHexString(final String strForEncoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        StringBuilder result = new StringBuilder();
        for (byte aByte : md.digest(strForEncoding.getBytes("UTF-8"))) {
            if ((0xff & aByte) < 0x10) {
                result.append("0").append(Integer.toHexString(0xFF & aByte));
            } else {
                result.append(Integer.toHexString(0xFF & aByte));
            }
        }
        return result.toString();
    }

    @Override
    public OkProfile extractUserProfile(String body) throws HttpAction {
        final OkProfile profile = newProfile();
        JsonNode userNode = JsonHelper.getFirstNode(body);
        if (userNode != null) {
            profile.setId(JsonHelper.getElement(userNode, OkProfileDefinition.UID));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(userNode, attribute));
            }
        }
        return profile;
    }
}
