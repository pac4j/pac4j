package org.pac4j.openid4vp.profile;

import lombok.val;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * Builds a profile identified by the issuer and a disclosed subject claim of a single verified credential.
 * The claim name is configured with {@link #setProfileId(String)} and defaults to {@code sub}.
 *
 * <p>The application must ensure that this claim is stable, unique within the issuer and never reassigned,
 * and request it in DCQL. Missing identifiers and multiple credentials are rejected. Override
 * {@link #computeProfileId(VerifiablePresentationCredentials)} to select a credential or use nested claims.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4">
 *     OpenID4VP 1.0, End-User Authentication using Credentials</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class OpenId4VpProfileDefinition extends CommonProfileDefinition {

    /** Creates a definition for a {@link VerifiableCredentialProfile}. */
    public OpenId4VpProfileDefinition() {
        this(parameters -> new VerifiableCredentialProfile());
    }

    /**
     * Creates a definition with a custom profile factory, retaining the default identifier mapping.
     *
     * @param profileFactory the factory, receiving the verified presentation credentials
     */
    public OpenId4VpProfileDefinition(final ProfileFactory profileFactory) {
        super(profileFactory);
        setProfileId("sub");
    }

    /**
     * Builds a profile and assigns its identifier from the verified credentials.
     *
     * @param parameters the {@link VerifiablePresentationCredentials} as the first parameter
     * @return the profile with its identifier
     */
    @Override
    public UserProfile newProfile(final Object... parameters) {
        if (parameters == null || parameters.length == 0
            || !(parameters[0] instanceof VerifiablePresentationCredentials credentials)) {
            logger.debug("profile creation rejected: verified presentation credentials are missing");
            throw new OpenId4VpException("verified presentation credentials are required to build the profile");
        }
        val id = computeProfileId(credentials);
        val profile = super.newProfile(parameters);
        profile.setId(id);
        logger.debug("profile identifier assigned using definition {}", getClass().getSimpleName());
        return profile;
    }

    /**
     * Combines the verified issuer with the configured top-level claim from a single credential.
     * Each component is encoded with unpadded Base64url, separated by a dot, to avoid delimiter collisions.
     *
     * @param credentials the validated presentation, indexed by DCQL credential query identifier
     * @return the identifier, stable for the same issuer and subject
     */
    protected String computeProfileId(final VerifiablePresentationCredentials credentials) {
        val verified = credentials.getVerifiedCredentials().values().stream().flatMap(Collection::stream).toList();
        logger.debug("mapping profile identifier: {} verified credentials, subject claim={}", verified.size(), getProfileId());
        if (verified.size() != 1) {
            logger.debug("profile identifier mapping rejected: exactly one verified credential is required");
            throw new OpenId4VpException("the default profile identifier mapping requires exactly one verified credential");
        }
        val credential = verified.get(0);
        val issuer = credential.getIssuer();
        val subject = credential.getClaims().get(getProfileId());
        if (isBlank(issuer) || !(subject instanceof String subjectId) || isBlank(subjectId)) {
            logger.debug("profile identifier mapping rejected: issuer or subject claim missing, blank or invalid");
            throw new OpenId4VpException("the profile identifier requires an issuer and a non-blank string claim: " + getProfileId());
        }
        val encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(issuer.getBytes(StandardCharsets.UTF_8)) + "."
            + encoder.encodeToString(subjectId.getBytes(StandardCharsets.UTF_8));
    }
}
