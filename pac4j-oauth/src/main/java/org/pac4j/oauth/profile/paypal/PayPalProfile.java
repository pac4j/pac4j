package org.pac4j.oauth.profile.paypal;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
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

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new PayPalAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getEmail() {
        return (String) getAttribute(PayPalAttributesDefinition.EMAIL);
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(PayPalAttributesDefinition.GIVEN_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(PayPalAttributesDefinition.FAMILY_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(PayPalAttributesDefinition.NAME);
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(PayPalAttributesDefinition.LOCALE);
    }
    
    @Override
    public String getLocation() {
        return (String) getAttribute(PayPalAttributesDefinition.ZONEINFO);
    }
    
    public Locale getLanguage() {
        return (Locale) getAttribute(PayPalAttributesDefinition.LANGUAGE);
    }
    
    public PayPalAddress getAddress() {
        return (PayPalAddress) getAttribute(PayPalAttributesDefinition.ADDRESS);
    }
}
