package org.pac4j.oauth.profile.wechat;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.client.WechatClient;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.scribe.model.WechatToken;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class defines the attributes of the Wechat profile.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>OPENID="openid"</code> */
    public static final String OPENID = "openid";

    /** Constant <code>NICKNAME="nickname"</code> */
    public static final String NICKNAME = "nickname";
    /**
     * Gender, 1 male and 2 female
     */
    public static final String SEX = "sex";

    /** Constant <code>PROVINCE="province"</code> */
    public static final String PROVINCE = "province";

    /** Constant <code>CITY="city"</code> */
    public static final String CITY = "city";
    /**
     * country, For example, China is CN
     */
    public static final String COUNTRY = "country";
    /**
     * User avatar, the last value represents the size of the square avatar (0, 46, 64, 96, 132 values are optional, 0 is 640 * 640
     * square avatar), the item is empty when the user has no avatar
     */
    public static final String HEADIMGURL = "headimgurl";
    /**
     * User privilege information, json array, such as WeChat Waka users (chinaunicom)
     */
    public static final String PRIVILEGE = "privilege ";
    /**
     * User union identity. For an application under the WeChat open platform account, the unionid of the same user is unique.
     */
    public static final String UNIONID = "unionid";

    /**
     * <p>Constructor for WechatProfileDefinition.</p>
     */
    public WechatProfileDefinition() {
        Arrays.stream(new String[]{
            OPENID,
            NICKNAME,
            PROVINCE,
            CITY,
            COUNTRY,
            PRIVILEGE,
            UNIONID
        }).forEach(a -> primary(a, Converters.STRING));
        primary(SEX, new GenderConverter("1", "2"));
        primary(HEADIMGURL, Converters.URL);
    }


    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        if (accessToken instanceof WechatToken token) {
            String profileUrl;
            if (WechatClient.WechatScope.SNSAPI_BASE.toString().equalsIgnoreCase(token.getScope())) {
                profileUrl = "https://api.weixin.qq.com/sns/auth?openid=" + token.getOpenid();
            } else {
                profileUrl = "https://api.weixin.qq.com/sns/userinfo?openid=" + token.getOpenid();
            }
            return profileUrl;
        } else {
            throw new OAuthException("Token in getProfileUrl is not an WechatToken");
        }
    }

    /** {@inheritDoc} */
    @Override
    public WechatProfile extractUserProfile(String body) {
        val profile = new WechatProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            var errcode = (Integer) JsonHelper.getElement(json, "errcode");
            if (errcode != null && errcode > 0) {
                var errmsg = JsonHelper.getElement(json, "errmsg");
                throw new OAuthException(
                    errmsg != null ? errmsg.toString() : "error code " + errcode);
            }
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute,
                    JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
