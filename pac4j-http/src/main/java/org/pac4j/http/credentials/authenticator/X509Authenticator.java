package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.http.credentials.X509Credentials;
import org.pac4j.http.profile.X509Profile;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;

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
    protected void internalInit() {
        defaultProfileDefinition(new CommonProfileDefinition(x -> new X509Profile()));
    }

    @Override
    public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        init();

        final X509Certificate certificate = ((X509Credentials) credentials).getCertificate();
        if (certificate == null) {
            throw new CredentialsException("No X509 certificate");
        }

        final Principal principal = certificate.getSubjectDN();
        if (principal == null) {
            throw new CredentialsException("No X509 principal");
        }

        final String subjectDN = principal.getName();
        logger.debug("subjectDN: {}", subjectDN);

        if (subjectDN == null) {
            throw new CredentialsException("No X509 subjectDN");
        }

        final Matcher matcher = this.pattern.matcher(subjectDN);

        if (!matcher.find()) {
            throw new CredentialsException("No matching for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        if (matcher.groupCount() != 1) {
            throw new CredentialsException("Too many matchings for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        final String id = matcher.group(1);
        final X509Profile profile = (X509Profile) getProfileDefinition().newProfile();
        profile.setId(id);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);
    }
}
