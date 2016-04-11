package org.pac4j.oauth.profile.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a PayPal address.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalAddress extends JsonObject {
    
    private static final long serialVersionUID = -6856575643675582895L;

    @JsonProperty("street_address")
    private String streetAddress;
    
    private String locality;

    @JsonProperty("postal_code")
    private String postalCode;
    
    private String country;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
