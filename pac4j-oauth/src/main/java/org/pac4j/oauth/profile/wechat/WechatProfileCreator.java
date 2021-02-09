package org.pac4j.oauth.profile.wechat;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.scribe.model.WechatToken;

import java.util.Optional;

/**
 * Specific profile creator for Wechat.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatProfileCreator extends OAuth20ProfileCreator {
    public WechatProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        final var token = (WechatToken) accessToken;
        final var profile = super.retrieveUserProfileFromToken(context, token);
        ((WechatProfile) profile.get()).setId(token.getOpenid());
        return profile;
    }
}
