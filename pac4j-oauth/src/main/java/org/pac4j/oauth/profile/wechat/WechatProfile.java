package org.pac4j.oauth.profile.wechat;

import java.net.URI;

import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Tencent Wechat with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WechatClient}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatProfile extends OAuth20Profile {

    private static final long serialVersionUID = 2576512203937798654L;

    @Override
    public String getDisplayName() {
        return (String) getAttribute(WechatProfileDefinition.NICKNAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(WechatProfileDefinition.NICKNAME);
    }

    @Override
    public Gender getGender() {
        return (Gender) getAttribute(WechatProfileDefinition.SEX);
    }

    @Override
    public String getLocation() {
        final String location = getAttribute(WechatProfileDefinition.CITY) + ","
            + getAttribute(WechatProfileDefinition.PROVINCE) + ","
            + getAttribute(WechatProfileDefinition.COUNTRY);
        return location;
    }

    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(WechatProfileDefinition.HEADIMGURL);
    }
}
