package org.pac4j.http.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * REST profile.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
@ToString(callSuper = true)
public class RestProfile extends CommonProfile {
    @Serial
    private static final long serialVersionUID = 4169018490675981350L;
}
