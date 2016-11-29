package org.pac4j.oauth.profile.yahoo;

import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.creator.OAuth10ProfileCreator;
import org.pac4j.oauth.profile.definition.OAuth10ProfileDefinition;

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
        final OAuth10ProfileDefinition<YahooProfile> profileDefinition = (OAuth10ProfileDefinition<YahooProfile>) configuration.getProfileDefinition();
        final String profileUrl = profileDefinition.getProfileUrl(accessToken, this.configuration);
        String body = sendRequestForData(accessToken, profileUrl, profileDefinition.getProfileVerb());
        final String guid = CommonHelper.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (CommonHelper.isBlank(guid)) {
            throw new HttpCommunicationException("Cannot find guid from body : " + body);
        }
        body = sendRequestForData(accessToken, "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json", Verb.GET);
        final YahooProfile profile = (YahooProfile) configuration.getProfileDefinition().extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
}
