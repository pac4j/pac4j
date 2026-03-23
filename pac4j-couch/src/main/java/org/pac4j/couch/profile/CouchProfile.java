package org.pac4j.couch.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * This class will be removed in the next version 6.5.0.
 *
 * <p>The user profile returned from a CouchDB.</p>
 *
 * @see org.pac4j.couch.profile.service.CouchProfileService
 * @author Elie Roux
 * @since 2.0.0
 */
@ToString(callSuper = true)
@Deprecated
public class CouchProfile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = 1527226102386684236L;
}
