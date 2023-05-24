package org.pac4j.oauth.profile.wechat;

import lombok.val;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;

/**
 * <p>This class is the user profile for Tencent Wechat with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WechatClient}.</p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = 2576512203937798654L;

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(WechatProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(WechatProfileDefinition.NICKNAME);
    }

    /** {@inheritDoc} */
    @Override
    public Gender getGender() {
        return (Gender) getAttribute(WechatProfileDefinition.SEX);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        val location = getAttribute(WechatProfileDefinition.CITY) + ","
            + getAttribute(WechatProfileDefinition.PROVINCE) + ","
            + getAttribute(WechatProfileDefinition.COUNTRY);
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(WechatProfileDefinition.HEADIMGURL);
    }
}
