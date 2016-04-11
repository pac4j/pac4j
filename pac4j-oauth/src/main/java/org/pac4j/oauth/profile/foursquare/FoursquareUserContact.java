package org.pac4j.oauth.profile.foursquare;

import org.pac4j.oauth.profile.JsonObject;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 * 
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserContact extends JsonObject {

    private static final long serialVersionUID = -4866834192367416908L;
 
    private String email;
    private String twitter;
    private String facebook;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
}
