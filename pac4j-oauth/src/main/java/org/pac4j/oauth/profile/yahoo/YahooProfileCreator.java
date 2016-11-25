package org.pac4j.oauth.profile.yahoo;

import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.creator.OAuth10ProfileCreator;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

/**
 * Specific profile creator for Yahoo.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class YahooProfileCreator extends OAuth10ProfileCreator<YahooProfile> {

    public YahooProfileCreator(final OAuth10Configuration configuration) {
        super(configuration);
    }

    @Override
    protected YahooProfile retrieveUserProfileFromToken(final OAuth1Token accessToken) throws HttpAction {
        // get the guid: https://developer.yahoo.com/social/rest_api_guide/introspective-guid-resource.html
        final OAuthProfileDefinition<YahooProfile, OAuth1Token> profileDefinition = (OAuthProfileDefinition<YahooProfile, OAuth1Token>) configuration.getProfileDefinition();
        final String profileUrl = profileDefinition.getProfileUrl(accessToken);
        String body = sendRequestForData(accessToken, profileUrl, profileDefinition.getProfileVerb());
        final String guid = CommonHelper.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (CommonHelper.isBlank(guid)) {
            final String message = "Cannot find guid from body : " + body;
            throw new HttpCommunicationException(message);
        }
        body = sendRequestForData(accessToken, "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json", Verb.GET);
        final YahooProfile profile = (YahooProfile) configuration.getProfileDefinition().extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
}
