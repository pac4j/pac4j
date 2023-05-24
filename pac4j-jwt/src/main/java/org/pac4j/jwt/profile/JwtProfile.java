package org.pac4j.jwt.profile;

import lombok.ToString;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.core.profile.jwt.JwtClaims;

import java.io.Serial;

/**
 * Represents a user profile based on a JWT.
 *
 * @author Misagh Moayyed
 * @since 1.8.2
 */
@ToString(callSuper = true)
public class JwtProfile extends AbstractJwtProfile {

    @Serial
    private static final long serialVersionUID = -1688563185891542198L;

    /**
     * <p>getJwtId.</p>
     *
     * @return a {@link String} object
     */
    public String getJwtId() {
        return (String) getAttribute(JwtClaims.JWT_ID);
    }
}
