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

/**
 * Turns the validated credentials into a profile. Unlike OpenID Connect, nothing is fetched from a remote
 * endpoint here: everything the verifier knows was presented by the wallet.
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
        if (vpCredentials.getVerifiedCredentials().isEmpty()) {
            LOGGER.debug("no verified credential to build a profile from");
            return Optional.empty();
        }

        val profile = profileDefinition.newProfile();
        // TODO achanger: the identifier must be derived from a claim of the credential. It used to be the
        // transaction identifier, which is drawn at random for every presentation: the same person got a
        // different profile identifier at every login, breaking anything keyed on it. Which claim to use is
        // a real design question under selective disclosure, as a wallet does not have to disclose a stable
        // identifier, and asking for one may defeat the purpose of the credential.
        profile.setId("achanger");
        vpCredentials.getVerifiedCredentials().values().forEach(verified ->
            verified.getClaims().forEach((name, value) ->
                profileDefinition.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, name, value)));
        profile.setClientName(client.getName());
        return Optional.of(profile);
    }
}
