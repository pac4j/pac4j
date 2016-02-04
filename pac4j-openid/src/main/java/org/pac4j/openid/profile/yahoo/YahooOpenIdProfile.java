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
package org.pac4j.openid.profile.yahoo;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.openid.client.YahooOpenIdClient;
import org.pac4j.openid.profile.OpenIdProfile;

/**
 * <p>This class is the user profile for Yahoo using OpenID with appropriate getters.</p>
 * <p>It is returned by the {@link YahooOpenIdClient}.</p>
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdProfile extends OpenIdProfile {
    
    private static final long serialVersionUID = 7866288887408897456L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new YahooOpenIdAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public Locale getLocale() {
    	return (Locale) getAttribute(YahooOpenIdAttributesDefinition.LANGUAGE);
    }
    
    @Override
    public String getDisplayName() {
    	return (String) getAttribute(YahooOpenIdAttributesDefinition.FULLNAME);
    }
}
