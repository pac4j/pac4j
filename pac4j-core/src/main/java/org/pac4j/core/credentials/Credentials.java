package org.pac4j.core.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.profile.UserProfile;

import java.io.Serial;
import java.io.Serializable;

/**
 * The credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode
@ToString
public abstract class Credentials implements Serializable {

    @Serial
    private static final long serialVersionUID = 1197047159413927875L;

    @Getter
    @Setter
    private UserProfile userProfile = null;

    @Getter
    protected LogoutType logoutType = null;

    /**
     * Indicates the source of the credentials.
     * This is typically configured and set during the credential
     * extraction process and authentication is then able to accept
     * or reject a credential based on the source, if necessary.
     * Values assigned to the source may be defined freely,
     * though official sources typically should use values from {@link CredentialSource}.
     */
    @Getter
    @Setter
    protected String source = CredentialSource.OTHER.name();

    /**
     * <p>isForAuthentication.</p>
     *
     * @return a boolean
     */
    @JsonIgnore
    public boolean isForAuthentication() {
        return logoutType == null;
    }
}
