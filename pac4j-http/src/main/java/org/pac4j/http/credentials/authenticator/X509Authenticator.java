package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.http.credentials.X509Credentials;
import org.pac4j.http.profile.X509Profile;

import javax.security.auth.x500.X500Principal;
import java.util.ArrayList;

/**
 * Authenticates {@link X509Credentials}. Like the SubjectDnX509PrincipalExtractor in Spring Security.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class X509Authenticator extends AbstractRegexpAuthenticator implements Authenticator {

    public X509Authenticator() {
        setRegexpPattern("CN=(.*?)(?:,|$)");
    }

    public X509Authenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        defaultProfileDefinition(new CommonProfileDefinition(x -> new X509Profile()));
    }

    @Override
    public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        init();

        final var certificate = ((X509Credentials) credentials).getCertificate();
        if (certificate == null) {
            throw new CredentialsException("No X509 certificate");
        }

        final var principal = certificate.getSubjectX500Principal();
        if (principal == null) {
            throw new CredentialsException("No X509 principal");
        }

        final var subjectDN = principal.getName(X500Principal.RFC2253);
        logger.debug("subjectDN: {}", subjectDN);

        if (subjectDN == null) {
            throw new CredentialsException("No X509 subjectDN");
        }

        final var matcher = this.pattern.matcher(subjectDN);

        final var matches = new ArrayList<String>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        if (matches.isEmpty()) {
            throw new CredentialsException("No matching for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        if (matches.size() != 1) {
            throw new CredentialsException("Too many matches for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        final var id = matches.get(0);
        final var profile = (X509Profile) getProfileDefinition().newProfile();
        profile.setId(id);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);
    }
}
