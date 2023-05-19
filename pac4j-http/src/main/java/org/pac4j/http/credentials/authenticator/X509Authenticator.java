package org.pac4j.http.credentials.authenticator;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.http.credentials.X509Credentials;
import org.pac4j.http.profile.X509Profile;

import java.util.Optional;

/**
 * Authenticates {@link X509Credentials}. Like the SubjectDnX509PrincipalExtractor in Spring Security.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class X509Authenticator extends AbstractRegexpAuthenticator implements Authenticator {

    /**
     * <p>Constructor for X509Authenticator.</p>
     */
    public X509Authenticator() {
        setRegexpPattern("CN=(.*?)(?:,|$)");
    }

    /**
     * <p>Constructor for X509Authenticator.</p>
     *
     * @param regexpPattern a {@link String} object
     */
    public X509Authenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new X509Profile()));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
        init();

        val certificate = ((X509Credentials) credentials).getCertificate();
        if (certificate == null) {
            throw new CredentialsException("No X509 certificate");
        }

        val principal = certificate.getSubjectDN();
        if (principal == null) {
            throw new CredentialsException("No X509 principal");
        }

        val subjectDN = principal.getName();
        logger.debug("subjectDN: {}", subjectDN);

        if (subjectDN == null) {
            throw new CredentialsException("No X509 subjectDN");
        }

        val matcher = this.pattern.matcher(subjectDN);

        if (!matcher.find()) {
            throw new CredentialsException("No matching for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        if (matcher.groupCount() != 1) {
            throw new CredentialsException("Too many matchings for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        val id = matcher.group(1);
        UserProfile profile = (X509Profile) getProfileDefinition().newProfile();
        profile.setId(id);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);

        return Optional.of(credentials);
    }
}
