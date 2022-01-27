package org.pac4j.oauth.profile.qq;

import com.github.scribejava.core.model.Token;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Optional;

/**
 * Specific profile creator for Tencent QQ.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQProfileCreator extends OAuth20ProfileCreator {

    public QQProfileCreator(final OAuth20Configuration configuration,
                            final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    public Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        var profileDefinition = (QQProfileDefinition) configuration.getProfileDefinition();
        var openidUrl = profileDefinition.getOpenidUrl((OAuth2AccessToken) accessToken, (OAuth20Configuration) configuration);
        final var service = (OAuth20Service) this.configuration.buildService(context, client);

        var body = sendRequestForData(service, accessToken, openidUrl, Verb.GET);
        var openid = profileDefinition.extractOpenid(body);

        var profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        profileUrl = CommonHelper.addParameter(profileUrl, "openid", openid);
        profileUrl = CommonHelper.addParameter(profileUrl, "oauth_consumer_key",
            configuration.getKey());
        body = sendRequestForData(service, accessToken, profileUrl, Verb.GET);
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        final var profile = profileDefinition.extractUserProfile(body);
        addTokenToProfile(profile, accessToken);
        profile.setId(openid);
        return Optional.of(profile);
    }
}
