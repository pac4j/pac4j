package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.service.ProfileService;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * Load a linked account and replace the original account.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class LoadLinkedUserAuthorizationGenerator implements AuthorizationGenerator {

    private ProfileService profileService;

    private boolean failIfLinkedUserNotFound = true;

    public LoadLinkedUserAuthorizationGenerator() {}

    public LoadLinkedUserAuthorizationGenerator(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public Optional<UserProfile> generate(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        CommonHelper.assertNotNull("profileService", profileService);

        final var linkedProfile = profileService.findByLinkedId(profile.getId());

        if (linkedProfile != null) {
            return Optional.ofNullable(linkedProfile);
        } else {
            if (failIfLinkedUserNotFound) {
                throw new TechnicalException("No linked account found for: " + profile);
            } else {
                // fallback to the original account
                return Optional.ofNullable(profile);
            }
        }
    }

    public ProfileService getProfileService() {
        return profileService;
    }

    public void setProfileService(final ProfileService profileService) {
        this.profileService = profileService;
    }

    public boolean isFailIfLinkedUserNotFound() {
        return failIfLinkedUserNotFound;
    }

    public void setFailIfLinkedUserNotFound(final boolean failIfLinkedUserNotFound) {
        this.failIfLinkedUserNotFound = failIfLinkedUserNotFound;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "profileService", profileService,
            "failIfLinkedUserNotFound", failIfLinkedUserNotFound);
    }
}
