package org.pac4j.core.profile.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = -6146872796913837767L;

    @JsonIgnore
    public String getSubject() {
        return getId();
    }

    @JsonIgnore
    public String getIssuer() {
        return (String) getAttribute(JwtClaims.ISSUER);
    }

    @JsonIgnore
    public List<String> getAudience() {
        return extractAttributeValues(JwtClaims.AUDIENCE);
    }

    @JsonIgnore
    public Date getExpirationDate() {
        return (Date) getAttribute(JwtClaims.EXPIRATION_TIME);
    }

    @JsonIgnore
    public Date getNotBefore() {
        return (Date) getAttribute(JwtClaims.NOT_BEFORE);
    }

    @JsonIgnore
    public Date getIssuedAt() {
        return (Date) getAttribute(JwtClaims.ISSUED_AT);
    }
}
