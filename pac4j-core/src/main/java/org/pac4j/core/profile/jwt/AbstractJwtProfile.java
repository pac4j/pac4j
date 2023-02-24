package org.pac4j.core.profile.jwt;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.util.Date;
import java.util.List;

/**
 * Abstract JWT profile.
 *
 * @author Jérôme Leleu
 * @since 2.0.0
 */
@ToString(callSuper = true)
public abstract class AbstractJwtProfile extends CommonProfile {

    private static final long serialVersionUID = -6146872796913837767L;

    /**
     * <p>getSubject.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSubject() {
        return getId();
    }

    /**
     * <p>getIssuer.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIssuer() {
        return (String) getAttribute(JwtClaims.ISSUER);
    }

    /**
     * <p>getAudience.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getAudience() {
        return extractAttributeValues(JwtClaims.AUDIENCE);
    }

    /**
     * <p>getExpirationDate.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getExpirationDate() {
        return (Date) getAttribute(JwtClaims.EXPIRATION_TIME);
    }

    /**
     * <p>getNotBefore.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getNotBefore() {
        return (Date) getAttribute(JwtClaims.NOT_BEFORE);
    }

    /**
     * <p>getIssuedAt.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getIssuedAt() {
        return (Date) getAttribute(JwtClaims.ISSUED_AT);
    }
}
