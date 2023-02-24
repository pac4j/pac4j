package org.pac4j.oauth.profile.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class represents a PayPal address.
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalAddress implements Serializable {

    private static final long serialVersionUID = -6856575643675582895L;

    @JsonProperty("street_address")
    private String streetAddress;

    private String locality;

    @JsonProperty("postal_code")
    private String postalCode;

    private String country;

    /**
     * <p>Getter for the field <code>streetAddress</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * <p>Setter for the field <code>streetAddress</code>.</p>
     *
     * @param streetAddress a {@link java.lang.String} object
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * <p>Getter for the field <code>locality</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLocality() {
        return locality;
    }

    /**
     * <p>Setter for the field <code>locality</code>.</p>
     *
     * @param locality a {@link java.lang.String} object
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * <p>Getter for the field <code>postalCode</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * <p>Setter for the field <code>postalCode</code>.</p>
     *
     * @param postalCode a {@link java.lang.String} object
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * <p>Getter for the field <code>country</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCountry() {
        return country;
    }

    /**
     * <p>Setter for the field <code>country</code>.</p>
     *
     * @param country a {@link java.lang.String} object
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
