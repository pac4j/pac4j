package org.pac4j.jwt.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.JwtClaims;

import java.util.Date;

/**
 * Represents a user profile based on a JWT.
 *
 * @author Misagh Moayyed
 * @since 1.8.2
 */
public class JwtProfile extends CommonProfile {

    private static final long serialVersionUID = -1688563185891542198L;

    public String getSubject() {
        return getId();
    }

    public String getIssuer() {
        return (String) getAttribute(JwtClaims.ISSUER);
    }

    public String getAudience() {
        return (String) getAttribute(JwtClaims.AUDIENCE);
    }

    public Date getExpirationDate() {
        return (Date) getAttribute(JwtClaims.EXPIRATION_TIME);
    }

    public Date getNotBefore() {
        return (Date) getAttribute(JwtClaims.NOT_BEFORE);
    }

    public Date getIssuedAt() {
        return (Date) getAttribute(JwtClaims.ISSUED_AT);
    }

    public String getJwtId() {
        return (String) getAttribute(JwtClaims.JWT_ID);
    }
}
