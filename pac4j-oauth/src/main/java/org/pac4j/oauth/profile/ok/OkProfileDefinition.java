package org.pac4j.oauth.profile.ok;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * Represents the profile definitions on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>UID="uid"</code> */
    public static final String UID = "uid";
    /** Constant <code>BIRTHDAY="birthday"</code> */
    public static final String BIRTHDAY = "birthday";
    /** Constant <code>AGE="age"</code> */
    public static final String AGE = "age";
    /** Constant <code>NAME="name"</code> */
    public static final String NAME = "name";
    /** Constant <code>LOCATION_CITY="location.city"</code> */
    public static final String LOCATION_CITY = "location.city";
    /** Constant <code>LOCATION_COUNTRY="location.country"</code> */
    public static final String LOCATION_COUNTRY = "location.country";
    /** Constant <code>LOCATION_COUNTRY_CODE="location.countryCode"</code> */
    public static final String LOCATION_COUNTRY_CODE = "location.countryCode";
    /** Constant <code>LOCATION_COUNTRY_NAME="location.countryName"</code> */
    public static final String LOCATION_COUNTRY_NAME = "location.countryName";
    /** Constant <code>ONLINE="online"</code> */
    public static final String ONLINE = "online";
    /** Constant <code>LAST_NAME="last_name"</code> */
    public static final String LAST_NAME = "last_name";
    /** Constant <code>HAS_EMAIL="has_email"</code> */
    public static final String HAS_EMAIL = "has_email";
    /** Constant <code>CURRENT_STATUS="current_status"</code> */
    public static final String CURRENT_STATUS = "current_status";
    /** Constant <code>CURRENT_STATUS_ID="current_status_id"</code> */
    public static final String CURRENT_STATUS_ID = "current_status_id";
    /** Constant <code>CURRENT_STATUS_DATE="current_status_date"</code> */
    public static final String CURRENT_STATUS_DATE = "current_status_date";
    /** Constant <code>PIC_1="pic_1"</code> */
    public static final String PIC_1 = "pic_1";
    /** Constant <code>PIC_2="pic_2"</code> */
    public static final String PIC_2 = "pic_2";

    private static final String API_BASE_URL = "http://api.ok.ru/fb.do?";

    /**
     * <p>Constructor for OkProfileDefinition.</p>
     */
    public OkProfileDefinition() {
        super(x -> new OkProfile());
        Arrays.stream(new String[] {UID, BIRTHDAY, AGE, NAME, LOCATION_CITY, LOCATION_COUNTRY, LOCATION_COUNTRY_CODE,
                LOCATION_COUNTRY_NAME, ONLINE, LAST_NAME, HAS_EMAIL, CURRENT_STATUS, CURRENT_STATUS_ID, CURRENT_STATUS_DATE})
                .forEach(a -> primary(a, Converters.STRING));
        primary(PIC_1, Converters.URL);
        primary(PIC_2, Converters.URL);
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token token, final OAuthConfiguration configuration) {
        val accessToken = ((OAuth2AccessToken) token).getAccessToken();
        var baseParams =
                "application_key=" + ((OkConfiguration) configuration).getPublicKey() +
                        "&format=json" +
                        "&method=users.getCurrentUser";
        final String finalSign;
        try {
            val preSign = getMD5SignAsHexString(accessToken + configuration.getSecret());
            finalSign = getMD5SignAsHexString(baseParams.replaceAll("&", Pac4jConstants.EMPTY_STRING) + preSign);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return API_BASE_URL + baseParams + "&access_token=" + accessToken + "&sig=" + finalSign;
    }

    /**
     * <p>getMD5SignAsHexString.</p>
     *
     * @param strForEncoding a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     * @throws java.security.NoSuchAlgorithmException if any.
     */
    protected String getMD5SignAsHexString(final String strForEncoding) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("MD5");
        var result = new StringBuilder();
        for (var aByte : md.digest(strForEncoding.getBytes(StandardCharsets.UTF_8))) {
            if ((0xff & aByte) < 0x10) {
                result.append("0").append(Integer.toHexString(0xFF & aByte));
            } else {
                result.append(Integer.toHexString(0xFF & aByte));
            }
        }
        return result.toString();
    }

    /** {@inheritDoc} */
    @Override
    public OkProfile extractUserProfile(String body) {
        val profile = (OkProfile) newProfile();
        var userNode = JsonHelper.getFirstNode(body);
        if (userNode != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(userNode, OkProfileDefinition.UID)));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(userNode, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
