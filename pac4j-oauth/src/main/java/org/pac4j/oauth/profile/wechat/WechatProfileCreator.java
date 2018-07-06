package org.pac4j.oauth.profile.wechat;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.scribe.model.WechatToken;

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Specific profile creator for Wechat.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatProfileCreator extends OAuth20ProfileCreator<WechatProfile> {
    public WechatProfileCreator(OAuth20Configuration configuration,
                                IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected WechatProfile retrieveUserProfileFromToken(WebContext context,
                                                         OAuth2AccessToken accessToken) {
        WechatToken token = (WechatToken) accessToken;
        WechatProfile profile = super.retrieveUserProfileFromToken(context, token);
        profile.setId(token.getOpenid());
        return profile;
    }
}
