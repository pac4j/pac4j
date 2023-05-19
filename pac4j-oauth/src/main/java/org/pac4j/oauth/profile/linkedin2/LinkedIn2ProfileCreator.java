package org.pac4j.oauth.profile.linkedin2;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import java.io.IOException;
import java.util.Optional;

/**
 * A specific LinkedIn2 profile creator.
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class LinkedIn2ProfileCreator extends OAuth20ProfileCreator {
    private static final String EMAIL_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

    /**
     * <p>Constructor for LinkedIn2ProfileCreator.</p>
     *
     * @param configuration a {@link OAuth20Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public LinkedIn2ProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        super.retrieveUserProfileFromToken(context, accessToken);
        val profileDefinition = (LinkedIn2ProfileDefinition) configuration.getProfileDefinition();
        val linkedin2Configuration = (LinkedIn2Configuration) configuration;
        val profileUrl = profileDefinition.getProfileUrl(accessToken, linkedin2Configuration);
        val service = (OAuth20Service) configuration.buildService(context, client);
        var body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        val profile = profileDefinition.extractUserProfile(body);
        addTokenToProfile(profile, accessToken);

        if (profile == null || !linkedin2Configuration.getScope().contains("r_emailaddress")) {
            return Optional.ofNullable(profile);
        }

        body = sendRequestForData(service, accessToken, EMAIL_URL, profileDefinition.getProfileVerb());
        if (body == null) {
            throw new HttpCommunicationException("Not email data found for accessToken: " + accessToken);
        }

        try {
            val profileEmails = JsonHelper.getMapper().readValue(body, LinkedIn2ProfileEmails.class);
            profile.addAttribute(LinkedIn2ProfileDefinition.PROFILE_EMAILS, profileEmails);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.of(profile);
    }
}
