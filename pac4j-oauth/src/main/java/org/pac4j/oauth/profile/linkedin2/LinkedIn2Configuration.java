package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * LinkedIn OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class LinkedIn2Configuration extends OAuth20Configuration {

    public static final String DEFAULT_FIELDS = "id,first-name,last-name,maiden-name,formatted-name,phonetic-first-name,phonetic-last-name,"
        + "formatted-phonetic-name,headline,location,industry,current-share,num-connections,num-connections-capped,summary,specialties,"
        + "positions,picture-url,site-standard-profile-request,api-standard-profile-request,public-profile-url,email-address";

    private String fields = DEFAULT_FIELDS;

    public final static String DEFAULT_SCOPE = "r_fullprofile";

    public LinkedIn2Configuration() {
        setScope(DEFAULT_SCOPE);
    }

    public String getFields() {
        return fields;
    }

    public void setFields(final String fields) {
        this.fields = fields;
    }
}
