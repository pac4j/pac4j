package org.pac4j.oauth.profile.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a PayPal address.
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
@Getter
@Setter
public class PayPalAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = -6856575643675582895L;

    @JsonProperty("street_address")
    private String streetAddress;

    private String locality;

    @JsonProperty("postal_code")
    private String postalCode;

    private String country;
}
