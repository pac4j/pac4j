package org.pac4j.oauth.profile.cronofy;

import com.github.scribejava.core.model.*;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import java.util.Optional;

/**
 * A specific Cronofy profile creator.
 *
 * @author Jerome Leleu
 * @since 5.3.1
 */
public class CronofyProfileCreator extends OAuth20ProfileCreator {

    /**
     * <p>Constructor for CronofyProfileCreator.</p>
     *
     * @param configuration a {@link OAuth20Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public CronofyProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        final UserProfile profile = configuration.getProfileDefinition().extractUserProfile(accessToken.getRawResponse());
        addTokenToProfile(profile, accessToken);
        return Optional.of(profile);
    }
}
