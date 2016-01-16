/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile.paypal;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

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
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.payPalDefinition;
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
