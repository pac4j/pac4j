package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents an education object for Facebook.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookEducation implements Serializable {

    private static final long serialVersionUID = 3587603107957633824L;

    private FacebookObject school;

    private FacebookObject degree;

    private FacebookObject year;

    private List<FacebookObject> concentration;

    private String type;

    /**
     * <p>Getter for the field <code>school</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getSchool() {
        return school;
    }

    /**
     * <p>Setter for the field <code>school</code>.</p>
     *
     * @param school a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setSchool(FacebookObject school) {
        this.school = school;
    }

    /**
     * <p>Getter for the field <code>degree</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getDegree() {
        return degree;
    }

    /**
     * <p>Setter for the field <code>degree</code>.</p>
     *
     * @param degree a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setDegree(FacebookObject degree) {
        this.degree = degree;
    }

    /**
     * <p>Getter for the field <code>year</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getYear() {
        return year;
    }

    /**
     * <p>Setter for the field <code>year</code>.</p>
     *
     * @param year a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setYear(FacebookObject year) {
        this.year = year;
    }

    /**
     * <p>Getter for the field <code>concentration</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<FacebookObject> getConcentration() {
        return concentration;
    }

    /**
     * <p>Setter for the field <code>concentration</code>.</p>
     *
     * @param concentration a {@link java.util.List} object
     */
    public void setConcentration(List<FacebookObject> concentration) {
        this.concentration = concentration;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object
     */
    public void setType(String type) {
        this.type = type;
    }
}
