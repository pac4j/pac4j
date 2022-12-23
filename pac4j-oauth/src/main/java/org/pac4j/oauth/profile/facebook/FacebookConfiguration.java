package org.pac4j.oauth.profile.facebook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Facebook OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
public class FacebookConfiguration extends OAuth20Configuration {

    public static final String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,third_party_id,"
        + "timezone,updated_time,verified,about,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,"
        + "favorite_teams,quotes,relationship_status,religion,significant_other,website,work";

    protected String fields = DEFAULT_FIELDS;

    public static final String DEFAULT_SCOPE = "user_likes,user_birthday,email,user_hometown,user_location";

    protected int limit = FacebookProfileDefinition.DEFAULT_LIMIT;

    protected boolean requiresExtendedToken = false;

    protected boolean useAppsecretProof = false;

    public FacebookConfiguration() {
        setScope(DEFAULT_SCOPE);
    }
}
