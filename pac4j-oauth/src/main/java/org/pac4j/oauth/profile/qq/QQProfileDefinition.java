package org.pac4j.oauth.profile.qq;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;
import java.util.regex.Pattern;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class defines the attributes of the Tencent QQ Connect profile.
 * <p>More info at: <a href="http://wiki.connect.qq.com/get_user_info">get_user_info</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>OPENID_REGEX</code> */
    public static final Pattern OPENID_REGEX = Pattern.compile("\"openid\"\\s*:\\s*\"(\\S*?)\"");

    /** Constant <code>RET="ret"</code> */
    public static final String RET = "ret";

    /** Constant <code>MSG="msg"</code> */
    public static final String MSG = "msg";

    /** Constant <code>NICKNAME="nickname"</code> */
    public static final String NICKNAME = "nickname";

    /** Constant <code>PROVINCE="province"</code> */
    public static final String PROVINCE = "province";

    /** Constant <code>CITY="city"</code> */
    public static final String CITY = "city";

    /** Constant <code>YEAR="year"</code> */
    public static final String YEAR = "year";

    /** Constant <code>FIGUREURL="figureurl"</code> */
    public static final String FIGUREURL = "figureurl";

    /** Constant <code>FIGUREURL_1="figureurl_1"</code> */
    public static final String FIGUREURL_1 = "figureurl_1";

    /** Constant <code>FIGUREURL_2="figureurl_2"</code> */
    public static final String FIGUREURL_2 = "figureurl_2";

    /** Constant <code>FIGUREURL_QQ_1="figureurl_qq_1"</code> */
    public static final String FIGUREURL_QQ_1 = "figureurl_qq_1";

    /** Constant <code>FIGUREURL_QQ_2="figureurl_qq_2"</code> */
    public static final String FIGUREURL_QQ_2 = "figureurl_qq_2";

    /**
     * <p>Constructor for QQProfileDefinition.</p>
     */
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

    /**
     * <p>getOpenidUrl.</p>
     *
     * @param accessToken a {@link OAuth2AccessToken} object
     * @param configuration a {@link OAuth20Configuration} object
     * @return a {@link String} object
     */
    public String getOpenidUrl(OAuth2AccessToken accessToken, OAuth20Configuration configuration) {
        return "https://graph.qq.com/oauth2.0/me";
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://graph.qq.com/user/get_user_info";
    }

    /** {@inheritDoc} */
    @Override
    public QQProfile extractUserProfile(String body) {
        val profile = new QQProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }

    /**
     * <p>extractOpenid.</p>
     *
     * @param body a {@link String} object
     * @return a {@link String} object
     */
    public String extractOpenid(String body) {
        var openid = extractParameter(body, OPENID_REGEX, true);
        return openid;
    }

    /**
     * <p>extractParameter.</p>
     *
     * @param response a {@link String} object
     * @param regexPattern a {@link Pattern} object
     * @param required a boolean
     * @return a {@link String} object
     * @throws OAuthException if any.
     */
    protected static String extractParameter(CharSequence response, Pattern regexPattern,
                                             boolean required)
        throws OAuthException {
        val matcher = regexPattern.matcher(response);
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
