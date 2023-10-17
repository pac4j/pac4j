package org.pac4j.http.credentials.authenticator;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.http.credentials.X509Credentials;
import org.pac4j.http.profile.X509Profile;

import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.util.Base64;
import java.util.Optional;

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
        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new X509Profile()));
    }

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
            throw new CredentialsException("Too many matches for pattern: " +  regexpPattern + " in subjectDN: " + subjectDN);
        }

        val id = matcher.group(1);
        val profile = getProfileDefinition().newProfile();
        profile.setId(id);
        try {
            profile.addAttribute("x509-certificate", Base64.getEncoder().encodeToString(certificate.getEncoded()));
        } catch (final Exception e) {
            throw new CredentialsException("Unable to encode the certificate", e);
        }
        profile.addAttribute("x509-subjectDN", subjectDN);
        profile.addAttribute("x509-notAfter", certificate.getNotAfter());
        profile.addAttribute("x509-notBefore", certificate.getNotBefore());
        profile.addAttribute("x509-sigAlgName", certificate.getSigAlgName());
        profile.addAttribute("x509-sigAlgOid", certificate.getSigAlgOID());
        Principal issuerDN = certificate.getIssuerDN();
        if (issuerDN != null) {
            profile.addAttribute("x509-issuer", issuerDN.getName());
        }
        X500Principal issuerX500Principal = certificate.getIssuerX500Principal();
        if (issuerX500Principal != null) {
            profile.addAttribute("x509-issuerX500", issuerX500Principal.getName());
        }

        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);

        return Optional.of(credentials);
    }
}
