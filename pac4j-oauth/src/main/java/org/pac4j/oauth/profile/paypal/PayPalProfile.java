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

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return (String) getAttribute(PayPalProfileDefinition.GIVEN_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(PayPalProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        return (String) getAttribute(PayPalProfileDefinition.ZONEINFO);
    }

    /**
     * <p>getLanguage.</p>
     *
     * @return a {@link java.util.Locale} object
     */
    public Locale getLanguage() {
        return (Locale) getAttribute(PayPalProfileDefinition.LANGUAGE);
    }

    /**
     * <p>getAddress.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.paypal.PayPalAddress} object
     */
    public PayPalAddress getAddress() {
        return (PayPalAddress) getAttribute(PayPalProfileDefinition.ADDRESS);
    }
}
