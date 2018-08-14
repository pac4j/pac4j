package org.pac4j.core.profile.jwt;

import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Abstract JWT profile.
 *
 * @author Jérôme Leleu
 * @since 2.0.0
 */
public abstract class AbstractJwtProfile extends CommonProfile {

    private static final long serialVersionUID = -6146872796913837767L;

    public String getSubject() {
        return getId();
    }

    public String getIssuer() {
        return (String) getAttribute(JwtClaims.ISSUER);
    }

    public List<String> getAudience() {
        final Object audience = getAttribute(JwtClaims.AUDIENCE);
        if (audience instanceof String) {
            return Collections.singletonList((String) audience);
        } else if (audience instanceof String[]) {
            return Arrays.asList((String[]) audience);
        } else if (audience instanceof List) {
            return (List<String>) audience;
        } else {
            return null;
        }
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
}
