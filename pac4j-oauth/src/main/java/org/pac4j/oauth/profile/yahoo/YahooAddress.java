package org.pac4j.oauth.profile.yahoo;

import java.io.Serializable;
import java.util.Locale;

/**
 * This class represents a Yahoo address.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress implements Serializable {

    private static final long serialVersionUID = 5415315569181241541L;

    private Integer id;

    private Boolean current;

    private Locale country;

    private String state;

    private String city;

    private String postalCode;

    private String street;

    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Locale getCountry() {
        return country;
    }

    public void setCountry(Locale country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
