package org.pac4j.openid4vp.profile;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * The profile built from the credentials a wallet presented.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VerifiableCredentialProfile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = -6821297964369682234L;
}
