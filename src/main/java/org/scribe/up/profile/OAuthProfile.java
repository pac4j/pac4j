package org.scribe.up.profile;

import java.util.Map;

public abstract class OAuthProfile extends UserProfile {
    
    private static final long serialVersionUID = -3785766959621263114L;
    
    public OAuthProfile() {
        super();
    }
    
    public OAuthProfile(final Object id) {
        super(id);
    }
    
    public OAuthProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    /**
     * Set the access token
     * 
     * @param accessToken
     */
    public void setAccessToken(final String accessToken) {
        addAttribute(OAuthAttributesDefinition.ACCESS_TOKEN, accessToken);
    }
    
    /**
     * Return the access token.
     * 
     * @return the access token
     */
    public String getAccessToken() {
        return (String) this.attributes.get(OAuthAttributesDefinition.ACCESS_TOKEN);
    }
}
