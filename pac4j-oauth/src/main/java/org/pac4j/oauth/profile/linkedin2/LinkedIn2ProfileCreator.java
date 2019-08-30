package org.pac4j.oauth.profile.linkedin2;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 * A specific LinkedIn2 profile creator.
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class LinkedIn2ProfileCreator extends OAuth20ProfileCreator<LinkedIn2Profile> {
    private static final String EMAIL_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

    public LinkedIn2ProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected LinkedIn2Profile retrieveUserProfileFromToken(final WebContext context, final OAuth2AccessToken accessToken) {
        final LinkedIn2ProfileDefinition profileDefinition = (LinkedIn2ProfileDefinition) configuration.getProfileDefinition();
        final LinkedIn2Configuration linkedin2Configuration = (LinkedIn2Configuration) configuration;
        final String profileUrl = profileDefinition.getProfileUrl(accessToken, linkedin2Configuration);
        final OAuth20Service service = configuration.buildService(context, client, null);
        String body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        final LinkedIn2Profile profile = profileDefinition.extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);

        if (profile == null || !linkedin2Configuration.getScope().contains("r_emailaddress")) {
            return profile;
        }

        body = sendRequestForData(service, accessToken, EMAIL_URL, profileDefinition.getProfileVerb());
        if (body == null) {
            throw new HttpCommunicationException("Not email data found for accessToken: " + accessToken);
        }

        try {
            final LinkedIn2ProfileEmails profileEmails = JsonHelper.getMapper().readValue(body, LinkedIn2ProfileEmails.class);
            profile.addAttribute(LinkedIn2ProfileDefinition.PROFILE_EMAILS, profileEmails);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return profile;
    }
}
