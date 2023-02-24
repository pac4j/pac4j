package org.pac4j.oauth.profile.foursquare;

import java.io.Serializable;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserContact implements Serializable {

    private static final long serialVersionUID = -4866834192367416908L;

    private String email;
    private String twitter;
    private String facebook;

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link java.lang.String} object
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <p>Getter for the field <code>twitter</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * <p>Setter for the field <code>twitter</code>.</p>
     *
     * @param twitter a {@link java.lang.String} object
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     * <p>Getter for the field <code>facebook</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFacebook() {
        return facebook;
    }

    /**
     * <p>Setter for the field <code>facebook</code>.</p>
     *
     * @param facebook a {@link java.lang.String} object
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
}
