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

    public FacebookObject getSchool() {
        return school;
    }

    public void setSchool(final FacebookObject school) {
        this.school = school;
    }

    public FacebookObject getDegree() {
        return degree;
    }

    public void setDegree(final FacebookObject degree) {
        this.degree = degree;
    }

    public FacebookObject getYear() {
        return year;
    }

    public void setYear(final FacebookObject year) {
        this.year = year;
    }

    public List<FacebookObject> getConcentration() {
        return concentration;
    }

    public void setConcentration(final List<FacebookObject> concentration) {
        this.concentration = concentration;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
