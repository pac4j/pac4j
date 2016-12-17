package org.pac4j.oauth.profile;

/**
 * This class is the base OAuth 1.0 profile, extending from the base {@link org.pac4j.oauth.profile.OAuth20Profile}. It deals with the OAuth
 * access token secret.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuth10Profile extends OAuth20Profile {
    
    private static final long serialVersionUID = 3407397824720340476L;

    private transient static final String ACCESS_SECRET = "access_secret";

    /**
     * Set the access token secret
     * 
     * @param accessSecret the access token secret
     */
    public void setAccessSecret(final String accessSecret) {
        addAttribute(ACCESS_SECRET, accessSecret);
    }
    
    /**
     * Return the access token secret.
     * 
     * @return the access token secret
     */
    public String getAccessSecret() {
        return (String) getAttribute(ACCESS_SECRET);
    }

    @Override
    public void clearSensitiveData() {
        super.clearSensitiveData();
        removeAttribute(ACCESS_SECRET);
    }
}
