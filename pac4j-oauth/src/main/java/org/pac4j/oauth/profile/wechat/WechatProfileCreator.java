package org.pac4j.oauth.profile.wechat;

import com.github.scribejava.core.model.Token;
import lombok.val;
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
    /**
     * <p>Constructor for WechatProfileCreator.</p>
     *
     * @param configuration a {@link OAuth20Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public WechatProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        val token = (WechatToken) accessToken;
        val profile = super.retrieveUserProfileFromToken(context, token);
        profile.get().setId(token.getOpenid());
        return profile;
    }
}
