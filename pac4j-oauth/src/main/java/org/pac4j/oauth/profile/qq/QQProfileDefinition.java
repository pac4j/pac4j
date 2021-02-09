package org.pac4j.oauth.profile.qq;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class defines the attributes of the Tencent QQ Connect profile.
 * <p>More info at: <a href="http://wiki.connect.qq.com/get_user_info">get_user_info</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQProfileDefinition extends OAuthProfileDefinition {

    public static final Pattern OPENID_REGEX = Pattern.compile("\"openid\"\\s*:\\s*\"(\\S*?)\"");

    public static final String RET = "ret";

    public static final String MSG = "msg";

    public static final String NICKNAME = "nickname";

    public static final String PROVINCE = "province";

    public static final String CITY = "city";

    public static final String YEAR = "year";

    public static final String FIGUREURL = "figureurl";

    public static final String FIGUREURL_1 = "figureurl_1";

    public static final String FIGUREURL_2 = "figureurl_2";

    public static final String FIGUREURL_QQ_1 = "figureurl_qq_1";

    public static final String FIGUREURL_QQ_2 = "figureurl_qq_2";

    public QQProfileDefinition() {
        Arrays.stream(new String[]{
            MSG,
            NICKNAME,
            PROVINCE,
            CITY,
            YEAR
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[]{
            FIGUREURL,
            FIGUREURL_1,
            FIGUREURL_2,
            FIGUREURL_QQ_1,
            FIGUREURL_QQ_2
        }).forEach(a -> primary(a, Converters.URL));
        primary(RET, Converters.INTEGER);
        primary(GENDER, new GenderConverter("男", "女"));
        primary(YEAR, new DateConverter("yyyy"));
    }

    public String getOpenidUrl(OAuth2AccessToken accessToken, OAuth20Configuration configuration) {
        return "https://graph.qq.com/oauth2.0/me";
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://graph.qq.com/user/get_user_info";
    }

    @Override
    public QQProfile extractUserProfile(String body) {
        final var profile = new QQProfile();
        final var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            for (final var attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }

    public String extractOpenid(String body) {
        var openid = extractParameter(body, OPENID_REGEX, true);
        return openid;
    }

    protected static String extractParameter(String response, Pattern regexPattern,
                                             boolean required)
        throws OAuthException {
        final var matcher = regexPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }

        if (required) {
            throw new OAuthException(
                "Response body is incorrect. Can't extract a '" + regexPattern.pattern()
                    + "' from this: '" + response + "'", null);
        }

        return null;
    }
}
