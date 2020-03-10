package org.pac4j.oauth.profile.facebook;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Facebook OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class FacebookConfiguration extends OAuth20Configuration {

    public final static String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,third_party_id,"
        + "timezone,updated_time,verified,about,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,"
        + "favorite_teams,quotes,relationship_status,religion,significant_other,website,work";

    protected String fields = DEFAULT_FIELDS;

    public final static String DEFAULT_SCOPE = "user_likes,user_about_me,user_birthday,user_education_history,email,user_hometown,"
        + "user_relationship_details,user_location,user_religion_politics,user_relationships,user_website,user_work_history";

    protected int limit = FacebookProfileDefinition.DEFAULT_LIMIT;

    protected boolean requiresExtendedToken = false;

    protected boolean useAppsecretProof = false;

    public FacebookConfiguration() {
        setScope(DEFAULT_SCOPE);
    }

    public String getFields() {
        return fields;
    }

    public void setFields(final String fields) {
        this.fields = fields;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public boolean isRequiresExtendedToken() {
        return requiresExtendedToken;
    }

    public void setRequiresExtendedToken(final boolean requiresExtendedToken) {
        this.requiresExtendedToken = requiresExtendedToken;
    }

    public boolean isUseAppsecretProof() {
        return useAppsecretProof;
    }

    public void setUseAppsecretProof(final boolean useAppsecretProof) {
        this.useAppsecretProof = useAppsecretProof;
    }
}
