package org.pac4j.http.profile;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class RestProfile extends CommonProfile {
    @Serial
    private static final long serialVersionUID = 4169018490675981350L;
}
