package org.pac4j.oauth.profile.qq;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class defines the attributes of the Tencent QQ Connect profile.
 * <p>More info at: <a href="http://wiki.connect.qq.com/get_user_info">get_user_info</a></p>
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class QQProfileDefinition extends OAuth20ProfileDefinition<QQProfile, OAuth20Configuration> {

    public static final String ret = "ret";

    public static final String msg = "msg";

    public static final String nickname = "nickname";

    public static final String gender = "gender";

    public static final String province = "province";

    public static final String city = "city";

    public static final String year = "year";

    public static final String figureurl = "figureurl";

    public static final String figureurl_1 = "figureurl_1";

    public static final String figureurl_2 = "figureurl_2";

    public static final String figureurl_qq_1 = "figureurl_qq_1";

    public static final String figureurl_qq_2 = "figureurl_qq_2";

    public QQProfileDefinition() {
        Arrays.stream(new String[]{
            msg,
            nickname,
            province,
            city,
            year
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[]{
            figureurl,
            figureurl_1,
            figureurl_2,
            figureurl_qq_1,
            figureurl_qq_2
        }).forEach(a -> primary(a, Converters.URL));
        primary(ret, Converters.INTEGER);
        primary(gender, new GenderConverter("男", "女"));
        primary(year, new DateConverter("yyyy"));
    }

    public String getOpenidUrl(OAuth2AccessToken accessToken, OAuth20Configuration configuration) {
        return "https://graph.qq.com/oauth2.0/me";
    }


    @Override
    public String getProfileUrl(OAuth2AccessToken accessToken, OAuth20Configuration configuration) {
        return "https://graph.qq.com/user/get_user_info";
    }

    @Override
    public QQProfile extractUserProfile(String body) {
        final QQProfile profile = new QQProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }

    public static final Pattern OPENID_REGEX = Pattern.compile("\"openid\"\\s*:\\s*\"(\\S*?)\"");
    public static final Pattern CLIENT_ID_REGEX = Pattern.compile("\"client_id\"\\s*:\\s*\"(\\S*?)\"");

    public String extractOpenid(String body) {
        String openid = extractParameter(body, OPENID_REGEX, true);
        return openid;
    }

    protected static String extractParameter(String response, Pattern regexPattern,
                                             boolean required)
        throws OAuthException {
        final Matcher matcher = regexPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }

        if (required) {
            throw new OAuthException("Response body is incorrect. Can't extract a '" + regexPattern.pattern()
                + "' from this: '" + response + "'", null);
        }

        return null;
    }
}
