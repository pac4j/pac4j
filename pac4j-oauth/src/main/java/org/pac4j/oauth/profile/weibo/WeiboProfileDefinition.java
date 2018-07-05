package org.pac4j.oauth.profile.weibo;

import java.util.Arrays;

import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;
import org.pac4j.scribe.model.WeiboToken;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the  Sina Weibo profile definition (using OAuth 2.0 protocol).
 * <p>
 * <p>More info at: <a href="http://open.weibo.com/wiki/2/users/show">users/show</a></p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class WeiboProfileDefinition extends OAuth20ProfileDefinition<WeiboProfile, OAuth20Configuration> {

    /**
     * int64    User UID
     */
    public static final String id = "id";
    /**
     * string   String-type user UID
     */
    public static final String idstr = "idstr";
    /**
     * string   User's Nickname
     */
    public static final String screen_name = "screen_name";
    /**
     * string   Friendly display name
     */
    public static final String name = "name";
    /**
     * int  User's provincial ID
     */
    public static final String province = "province";
    /**
     * int  User's city ID
     */
    public static final String city = "city";
    /**
     * string   User location
     */
    public static final String location = "location";
    /**
     * string   User personal description
     */
    public static final String description = "description";
    /**
     * url  User blog address
     */
    public static final String url = "url";
    /**
     * url  User avatar address (middle), 50×50 pixels
     */
    public static final String profile_image_url = "profile_image_url";
    /**
     * url  User cover image url
     */
    public static final String cover_image_phone = "cover_image_phone";
    /**
     * url  User's Weibo unified URL address
     */
    public static final String profile_url = "profile_url";
    /**
     * string   User's personalized domain name
     */
    public static final String domain = "domain";
    /**
     * string   User's weihao number
     */
    public static final String weihao = "weihao";
    /**
     * string   Gender, m: male, f: female, n: unknown
     */
    public static final String gender = "gender";
    /**
     * int  Number of fans
     */
    public static final String followers_count = "followers_count";
    /**
     * int  Number of followers
     */
    public static final String friends_count = "friends_count";
    /**
     * int  Weibo number
     */
    public static final String statuses_count = "statuses_count";
    /**
     * int  Number of favorites
     */
    public static final String favourites_count = "favourites_count";
    /**
     * string   User creation (registration) time
     */
    public static final String created_at = "created_at";
    /**
     * boolean  Not supported yet
     */
    public static final String following = "following";
    /**
     * boolean  Whether to allow everyone to send me a private message, true: yes, false: no
     */
    public static final String allow_all_act_msg = "allow_all_act_msg";
    /**
     * boolean  Whether to allow identification of the user's geographic location, true: yes, false: no
     */
    public static final String geo_enabled = "geo_enabled";
    /**
     * boolean    Whether it is a Weibo authenticated user, that is, a V-user, true: yes, false: no
     */
    public static final String verified = "verified";
    /**
     * int    Not supported yet
     */
    public static final String verified_type = "verified_type";
    /**
     * string    User note information, this field is only returned when querying user relationships
     */
    public static final String remark = "remark";
    /**
     * object    User's recent Weibo information field
     */
    public static final String status = "status";
    /**
     * boolean    Whether to allow everyone to comment on my Weibo, true: yes, false: no
     */
    public static final String allow_all_comment = "allow_all_comment";
    /**
     * string    User avatar address (larger image), 180 × 180 pixels
     */
    public static final String avatar_large = "avatar_large";
    /**
     * string    User avatar address (HD), HD avatar original
     */
    public static final String avatar_hd = "avatar_hd";
    /**
     * string    Reason for certification
     */
    public static final String verified_reason = "verified_reason";
    /**
     * boolean    Whether the user is concerned about the currently logged in user, true: yes, false: no
     */
    public static final String follow_me = "follow_me";
    /**
     * int    User's online status, 0: not online, 1: online
     */
    public static final String online_status = "online_status";
    /**
     * int    User's mutual powder count
     */
    public static final String bi_followers_count = "bi_followers_count";
    /**
     * string    User's current language version, zh-cn: Simplified Chinese, zh-tw: Traditional Chinese, en: English
     */
    public static final String lang = "lang";


    public WeiboProfileDefinition() {
        Arrays.stream(new String[]{
            url,
            profile_image_url,
            cover_image_phone,
            profile_url,
            avatar_large,
            avatar_hd,
        }).forEach(a -> primary(a, Converters.URL));
        Arrays.stream(new String[]{
            idstr,
            screen_name,
            name,
            location,
            description,
            domain,
            weihao,
            created_at,
            remark,
            verified_reason
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[]{
            following,
            allow_all_act_msg,
            geo_enabled,
            verified,
            allow_all_comment,
            follow_me
        }).forEach(a -> primary(a, Converters.BOOLEAN));
        Arrays.stream(new String[]{
            province,
            city,
            followers_count,
            friends_count,
            statuses_count,
            favourites_count,
            verified_type,
            online_status,
            bi_followers_count
        }).forEach(a -> primary(a, Converters.INTEGER));
        primary(id, Converters.LONG);
        primary(lang, Converters.LOCALE);
        primary(gender, Converters.GENDER);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken,
                                final OAuth20Configuration configuration) {
        if (accessToken instanceof WeiboToken)
            return "https://api.weibo.com/2/users/show.json" + "?uid=" + ((WeiboToken) accessToken).getUid();
        else
            throw new OAuthException("Token in getProfileUrl is not an WeiboToken");
    }

    @Override
    public WeiboProfile extractUserProfile(final String body) throws HttpAction {
        System.out.println(body);
        final WeiboProfile profile = new WeiboProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(
                ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }
}
