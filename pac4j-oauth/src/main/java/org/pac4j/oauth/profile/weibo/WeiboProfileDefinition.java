package org.pac4j.oauth.profile.weibo;

import java.util.Arrays;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.scribe.model.WeiboToken;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the  Sina Weibo profile definition (using OAuth 2.0 protocol).
 * <p>More info at: <a href="http://open.weibo.com/wiki/2/users/show">users/show</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboProfileDefinition extends OAuthProfileDefinition {

    /**
     * int64    User UID
     */
    public static final String ID = "id";
    /**
     * string   String-type user UID
     */
    public static final String IDSTR = "idstr";
    /**
     * string   User's Nickname
     */
    public static final String SCREEN_NAME = "screen_name";
    /**
     * string   Friendly display name
     */
    public static final String NAME = "name";
    /**
     * int  User's provincial ID
     */
    public static final String PROVINCE = "province";
    /**
     * int  User's city ID
     */
    public static final String CITY = "city";
    /**
     * string   User location
     */
    public static final String LOCATION = "location";
    /**
     * string   User personal description
     */
    public static final String DESCRIPTION = "description";
    /**
     * url  User blog address
     */
    public static final String URL = "url";
    /**
     * url  User avatar address (middle), 50×50 pixels
     */
    public static final String PROFILE_IMAGE_URL = "profile_image_url";
    /**
     * url  User cover image url
     */
    public static final String COVER_IMAGE_PHONE = "cover_image_phone";
    /**
     * url  User's Weibo unified URL address
     */
    public static final String PROFILE_URL = "profile_url";
    /**
     * string   User's personalized domain name
     */
    public static final String DOMAIN = "domain";
    /**
     * string   User's weihao number
     */
    public static final String WEIHAO = "weihao";
    /**
     * string   Gender, m: male, f: female, n: unknown
     */
    public static final String GENDER = "gender";
    /**
     * int  Number of fans
     */
    public static final String FOLLOWERS_COUNT = "followers_count";
    /**
     * int  Number of followers
     */
    public static final String FRIENDS_COUNT = "friends_count";
    /**
     * int  Weibo number
     */
    public static final String STATUSES_COUNT = "statuses_count";
    /**
     * int  Number of favorites
     */
    public static final String FAVOURITES_COUNT = "favourites_count";
    /**
     * string   User creation (registration) time
     */
    public static final String CREATED_AT = "created_at";
    /**
     * boolean  Not supported yet
     */
    public static final String FOLLOWING = "following";
    /**
     * boolean  Whether to allow everyone to send me a private message, true: yes, false: no
     */
    public static final String ALLOW_ALL_ACT_MSG = "allow_all_act_msg";
    /**
     * boolean  Whether to allow identification of the user's geographic location, true: yes, false: no
     */
    public static final String GEO_ENABLED = "geo_enabled";
    /**
     * boolean    Whether it is a Weibo authenticated user, that is, a V-user, true: yes, false: no
     */
    public static final String VERIFIED = "verified";
    /**
     * int    Not supported yet
     */
    public static final String VERIFIED_TYPE = "verified_type";
    /**
     * string    User note information, this field is only returned when querying user relationships
     */
    public static final String REMARK = "remark";
    /**
     * object    User's recent Weibo information field
     */
    public static final String STATUS = "status";
    /**
     * boolean    Whether to allow everyone to comment on my Weibo, true: yes, false: no
     */
    public static final String ALLOW_ALL_COMMENT = "allow_all_comment";
    /**
     * string    User avatar address (larger image), 180 × 180 pixels
     */
    public static final String AVATAR_LARGE = "avatar_large";
    /**
     * string    User avatar address (HD), HD avatar original
     */
    public static final String AVATAR_HD = "avatar_hd";
    /**
     * string    Reason for certification
     */
    public static final String VERIFIED_REASON = "verified_reason";
    /**
     * boolean    Whether the user is concerned about the currently logged in user, true: yes, false: no
     */
    public static final String FOLLOW_ME = "follow_me";
    /**
     * int    User's online status, 0: not online, 1: online
     */
    public static final String ONLINE_STATUS = "online_status";
    /**
     * int    User's mutual powder count
     */
    public static final String BI_FOLLOWERS_COUNT = "bi_followers_count";
    /**
     * string    User's current language version, zh-cn: Simplified Chinese, zh-tw: Traditional Chinese, en: English
     */
    public static final String LANG = "lang";


    public WeiboProfileDefinition() {
        Arrays.stream(new String[]{
            URL,
            PROFILE_IMAGE_URL,
            COVER_IMAGE_PHONE,
            PROFILE_URL,
            AVATAR_LARGE,
            AVATAR_HD,
        }).forEach(a -> primary(a, Converters.URL));
        Arrays.stream(new String[]{
            IDSTR,
            SCREEN_NAME,
            NAME,
            LOCATION,
            DESCRIPTION,
            DOMAIN,
            WEIHAO,
            CREATED_AT,
            REMARK,
            VERIFIED_REASON
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[]{
            FOLLOWING,
            ALLOW_ALL_ACT_MSG,
            GEO_ENABLED,
            VERIFIED,
            ALLOW_ALL_COMMENT,
            FOLLOW_ME
        }).forEach(a -> primary(a, Converters.BOOLEAN));
        Arrays.stream(new String[]{
            PROVINCE,
            CITY,
            FOLLOWERS_COUNT,
            FRIENDS_COUNT,
            STATUSES_COUNT,
            FAVOURITES_COUNT,
            VERIFIED_TYPE,
            ONLINE_STATUS,
            BI_FOLLOWERS_COUNT
        }).forEach(a -> primary(a, Converters.INTEGER));
        primary(ID, Converters.LONG);
        primary(LANG, Converters.LOCALE);
        primary(GENDER, Converters.GENDER);
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        if (accessToken instanceof WeiboToken) {
            return CommonHelper.addParameter("https://api.weibo.com/2/users/show.json", "uid",
                ((WeiboToken) accessToken).getUid());
        } else
            throw new OAuthException("Token in getProfileUrl is not an WeiboToken");
    }

    @Override
    public WeiboProfile extractUserProfile(final String body) throws HttpAction {
        final WeiboProfile profile = new WeiboProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(
                ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
