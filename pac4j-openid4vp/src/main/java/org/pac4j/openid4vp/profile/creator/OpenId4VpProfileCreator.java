package org.pac4j.openid4vp.profile.creator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;

import java.util.Optional;
import java.util.Collection;

/**
 * Turns the validated credentials into a profile. Unlike OpenID Connect, nothing is fetched from a remote
 * endpoint here: everything the verifier knows was presented by the wallet.
 * The profile definition receives the {@link VerifiablePresentationCredentials} as the first parameter
 * of {@link ProfileDefinition#newProfile(Object...)} and is responsible for assigning the profile identifier.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class OpenId4VpProfileCreator implements ProfileCreator {

    private final OpenId4VpClient client;

    private final ProfileDefinition profileDefinition;

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
        val vpCredentials = (VerifiablePresentationCredentials) credentials;
        val transactionId = vpCredentials.getTransaction() == null ? null : vpCredentials.getTransaction().getId();
        if (vpCredentials.getVerifiedCredentials().isEmpty()) {
            LOGGER.debug("no verified credential to build a profile for transaction {}", transactionId);
            return Optional.empty();
        }

        LOGGER.debug("creating profile for transaction {}: definition={}, credential query results={}", transactionId,
            profileDefinition.getClass().getSimpleName(), vpCredentials.getVerifiedCredentials().size());
        final UserProfile profile;
        try {
            profile = profileDefinition.newProfile(vpCredentials);
        } catch (final RuntimeException e) {
            LOGGER.debug("profile creation failed for transaction {}: {}", transactionId, e.getClass().getSimpleName());
            throw e;
        }
        vpCredentials.getVerifiedCredentials().values().stream().flatMap(Collection::stream).forEach(verified ->
            verified.getClaims().forEach((name, value) ->
                profileDefinition.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, name, value)));
        profile.setClientName(client.getName());
        LOGGER.debug("profile created for transaction {}: profile class={}, client={}", transactionId,
            profile.getClass().getSimpleName(), client.getName());
        return Optional.of(profile);
    }
}
