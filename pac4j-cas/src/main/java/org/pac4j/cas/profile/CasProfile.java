package org.pac4j.cas.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * <p>This class is the user profile for sites using CAS protocol.</p>
 * <p>It is returned by the {@link org.pac4j.cas.client.CasClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@ToString(callSuper = true)
public class CasProfile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = 60202509738824863L;
}
