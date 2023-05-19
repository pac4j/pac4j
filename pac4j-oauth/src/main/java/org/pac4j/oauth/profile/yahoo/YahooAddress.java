package org.pac4j.oauth.profile.yahoo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

/**
 * This class represents a Yahoo address.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 5415315569181241541L;

    private Integer id;

    private Boolean current;

    private Locale country;

    private String state;

    private String city;

    private String postalCode;

    private String street;

    private String type;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link Integer} object
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>current</code>.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getCurrent() {
        return current;
    }

    /**
     * <p>Setter for the field <code>current</code>.</p>
     *
     * @param current a {@link Boolean} object
     */
    public void setCurrent(Boolean current) {
        this.current = current;
    }

    /**
     * <p>Getter for the field <code>country</code>.</p>
     *
     * @return a {@link Locale} object
     */
    public Locale getCountry() {
        return country;
    }

    /**
     * <p>Setter for the field <code>country</code>.</p>
     *
     * @param country a {@link Locale} object
     */
    public void setCountry(Locale country) {
        this.country = country;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getState() {
        return state;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     *
     * @param state a {@link String} object
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * <p>Getter for the field <code>city</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getCity() {
        return city;
    }

    /**
     * <p>Setter for the field <code>city</code>.</p>
     *
     * @param city a {@link String} object
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * <p>Getter for the field <code>postalCode</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * <p>Setter for the field <code>postalCode</code>.</p>
     *
     * @param postalCode a {@link String} object
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * <p>Getter for the field <code>street</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getStreet() {
        return street;
    }

    /**
     * <p>Setter for the field <code>street</code>.</p>
     *
     * @param street a {@link String} object
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link String} object
     */
    public void setType(String type) {
        this.type = type;
    }
}
