package org.pac4j.couch.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.core.profile.CommonProfile;

/**
 * <p>The user profile returned from a CouchDB.</p>
 *
 * @see org.pac4j.couch.profile.service.CouchProfileService
 * @author Elie Roux
 * @since 2.0.0
 */
public class CouchProfile extends CommonProfile {

    private static final long serialVersionUID = 1527226102386684236L;

    @JsonProperty("_rev")
    private String rev;

    public String getRev() {
        return rev;
    }

    public void setRev(final String rev) {
        this.rev = rev;
    }
}
