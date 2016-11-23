package org.pac4j.oauth.profile.paypal;

import java.util.Locale;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for PayPal with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.PayPalClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = -9019988559486637233L;

    @Override
    public String getFirstName() {
        return (String) getAttribute(PayPalProfileDefinition.GIVEN_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(PayPalProfileDefinition.NAME);
    }
    
    @Override
    public String getLocation() {
        return (String) getAttribute(PayPalProfileDefinition.ZONEINFO);
    }
    
    public Locale getLanguage() {
        return (Locale) getAttribute(PayPalProfileDefinition.LANGUAGE);
    }
    
    public PayPalAddress getAddress() {
        return (PayPalAddress) getAttribute(PayPalProfileDefinition.ADDRESS);
    }
}
