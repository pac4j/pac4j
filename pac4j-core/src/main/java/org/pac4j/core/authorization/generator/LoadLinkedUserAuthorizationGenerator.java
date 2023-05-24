package org.pac4j.core.authorization.generator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
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
@Setter
@Getter
@ToString
public class LoadLinkedUserAuthorizationGenerator implements AuthorizationGenerator {

    private ProfileService profileService;

    private boolean failIfLinkedUserNotFound = true;

    /**
     * <p>Constructor for LoadLinkedUserAuthorizationGenerator.</p>
     */
    public LoadLinkedUserAuthorizationGenerator() {}

    /**
     * <p>Constructor for LoadLinkedUserAuthorizationGenerator.</p>
     *
     * @param profileService a {@link ProfileService} object
     */
    public LoadLinkedUserAuthorizationGenerator(final ProfileService profileService) {
        this.profileService = profileService;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        CommonHelper.assertNotNull("profileService", profileService);

        val linkedProfile = profileService.findByLinkedId(profile.getId());

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
}
