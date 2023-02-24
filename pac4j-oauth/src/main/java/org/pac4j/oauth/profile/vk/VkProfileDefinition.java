package org.pac4j.oauth.profile.vk;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Vk profile definition.
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>LAST_NAME="last_name"</code> */
    public static final String LAST_NAME = "last_name";
    /** Constant <code>SEX="sex"</code> */
    public static final String SEX = "sex";
    /** Constant <code>BIRTH_DATE="bdate"</code> */
    public static final String BIRTH_DATE = "bdate";
    /** Constant <code>PHOTO_50="photo_50"</code> */
    public static final String PHOTO_50 = "photo_50";
    /** Constant <code>PHOTO_100="photo_100"</code> */
    public static final String PHOTO_100 = "photo_100";
    /** Constant <code>PHOTO_200_ORIG="photo_200_orig"</code> */
    public static final String PHOTO_200_ORIG = "photo_200_orig";
    /** Constant <code>PHOTO_200="photo_200"</code> */
    public static final String PHOTO_200 = "photo_200";
    /** Constant <code>PHOTO_400_ORIG="photo_400_orig"</code> */
    public static final String PHOTO_400_ORIG = "photo_400_orig";
    /** Constant <code>PHOTO_MAX="photo_max"</code> */
    public static final String PHOTO_MAX = "photo_max";
    /** Constant <code>PHOTO_MAX_ORIG="photo_max_orig"</code> */
    public static final String PHOTO_MAX_ORIG = "photo_max_orig";
    /** Constant <code>ONLINE="online"</code> */
    public static final String ONLINE = "online";
    /** Constant <code>ONLINE_MOBILE="online_mobile"</code> */
    public static final String ONLINE_MOBILE = "online_mobile";
    /** Constant <code>DOMAIN="domain"</code> */
    public static final String DOMAIN = "domain";
    /** Constant <code>HAS_MOBILE="has_mobile"</code> */
    public static final String HAS_MOBILE = "has_mobile";
    /** Constant <code>MOBILE_PHONE="mobile_phone"</code> */
    public static final String MOBILE_PHONE = "mobile_phone";
    /** Constant <code>HOME_PHONE="home_phone"</code> */
    public static final String HOME_PHONE = "home_phone";
    /** Constant <code>SKYPE="skype"</code> */
    public static final String SKYPE = "skype";
    /** Constant <code>SITE="site"</code> */
    public static final String SITE = "site";
    /** Constant <code>CAN_POST="can_post"</code> */
    public static final String CAN_POST = "can_post";
    /** Constant <code>CAN_SEE_ALL_POST="can_see_all_posts"</code> */
    public static final String CAN_SEE_ALL_POST = "can_see_all_posts";
    /** Constant <code>CAN_SEE_AUDIO="can_see_audio"</code> */
    public static final String CAN_SEE_AUDIO = "can_see_audio";
    /** Constant <code>CAN_WRITE_PRIVATE_MESSAGE="can_write_private_message"</code> */
    public static final String CAN_WRITE_PRIVATE_MESSAGE = "can_write_private_message";
    /** Constant <code>STATUS="status"</code> */
    public static final String STATUS = "status";
    /** Constant <code>COMMON_COUNT="common_count"</code> */
    public static final String COMMON_COUNT = "common_count";
    /** Constant <code>RELATION="relation"</code> */
    public static final String RELATION = "relation";

    /** Constant <code>BASE_URL="https://api.vk.com/method/users.get"</code> */
    protected final static String BASE_URL = "https://api.vk.com/method/users.get";

    /**
     * <p>Constructor for VkProfileDefinition.</p>
     */
    public VkProfileDefinition() {
        super(x -> new VkProfile());
        Arrays.stream(new String[] {LAST_NAME, PHOTO_50, PHOTO_100, PHOTO_200_ORIG, PHOTO_200, PHOTO_400_ORIG,
                PHOTO_MAX, PHOTO_MAX_ORIG, DOMAIN, MOBILE_PHONE, HOME_PHONE, SKYPE, SITE, STATUS})
                .forEach(a -> primary(a, Converters.STRING));
        primary(COMMON_COUNT, Converters.INTEGER);
        primary(RELATION, Converters.INTEGER);
        Arrays.stream(new String[] {ONLINE, ONLINE_MOBILE, HAS_MOBILE, CAN_POST, CAN_SEE_ALL_POST, CAN_SEE_AUDIO,
            CAN_WRITE_PRIVATE_MESSAGE})
            .forEach(a -> primary(a, Converters.BOOLEAN));
        primary(BIRTH_DATE, new DateConverter("dd.MM.yyyy"));
        primary(SEX, new GenderConverter("2", "1"));
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return BASE_URL + "?fields=" + ((VkConfiguration) configuration).getFields();
    }

    /** {@inheritDoc} */
    @Override
    public VkProfile extractUserProfile(final String body) {
        val profile = (VkProfile) newProfile();
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            var array = (ArrayNode) json.get("response");
            var userNode = array.get(0);
            if (userNode == null) {
                raiseProfileExtractionJsonError(body, "response");
            }
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(userNode, "uid")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(userNode, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
