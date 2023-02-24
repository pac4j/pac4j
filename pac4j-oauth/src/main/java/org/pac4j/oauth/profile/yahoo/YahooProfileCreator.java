package org.pac4j.oauth.profile.yahoo;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.creator.OAuth10ProfileCreator;

import java.util.Optional;

/**
 * Specific profile creator for Yahoo.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class YahooProfileCreator extends OAuth10ProfileCreator {

    /**
     * <p>Constructor for YahooProfileCreator.</p>
     *
     * @param configuration a {@link org.pac4j.oauth.config.OAuth10Configuration} object
     * @param client a {@link org.pac4j.core.client.IndirectClient} object
     */
    public YahooProfileCreator(final OAuth10Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        // get the guid: https://developer.yahoo.com/social/rest_api_guide/introspective-guid-resource.html
        val profileDefinition = configuration.getProfileDefinition();
        val profileUrl = profileDefinition.getProfileUrl(accessToken, this.configuration);
        val service = (OAuth10aService) configuration.buildService(context, client);
        var body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        val guid = CommonHelper.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (CommonHelper.isBlank(guid)) {
            throw new HttpCommunicationException("Cannot find guid from body : " + body);
        }
        body = sendRequestForData(service, accessToken, "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json", Verb.GET);
        val profile = (YahooProfile) configuration.getProfileDefinition().extractUserProfile(body);
        addTokenToProfile(profile, accessToken);
        return Optional.of(profile);
    }
}
