package org.pac4j.oauth.profile.qq;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Specific profile creator for Tencent QQ.
 *
 * @author Zhang Zhenli
 * @since 3.1.0
 */
public class QQProfileCreator extends OAuth20ProfileCreator<QQProfile> {

    public QQProfileCreator(final OAuth20Configuration configuration,
                            final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    public QQProfile retrieveUserProfileFromToken(WebContext context,
                                                  OAuth2AccessToken accessToken) {
        QQProfileDefinition profileDefinition = (QQProfileDefinition) configuration.getProfileDefinition();
        String openidUrl = profileDefinition.getOpenidUrl(accessToken, configuration);
        final OAuth20Service service = this.configuration.buildService(context, client, null);

        String body = sendRequestForData(service, accessToken, openidUrl, Verb.GET);
        String openid = profileDefinition.extractOpenid(body);

        String profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        profileUrl = profileUrl + "?openid=" + openid + "&oauth_consumer_key=" + configuration.getKey();
        body = sendRequestForData(service, accessToken, profileUrl, Verb.GET);
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        final QQProfile profile = profileDefinition.extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        profile.setId(openid);
        return profile;
    }
}
