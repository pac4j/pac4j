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
import org.pac4j.openid.profile.OpenIdAttributesDefinitions;
import org.pac4j.openid.profile.OpenIdProfile;

/**
 * <p>This class is the user profile for Yahoo using OpenID with appropriate getters.</p>
 * <p>It is returned by the {@link YahooOpenIdClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>Through the attribute exchange extension :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.pac4j.core.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getEmail()</td>
 * <td>the <i>email</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td><code>null</code> the provider didn't return the firstname</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td><code>null</code> the provider didn't return the firstname</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>fullname</i> attribute (firstname and fullname not independendly by the provider, just the fullname)</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>{@link org.pac4j.core.profile.Gender#UNSPECIFIED}</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>language</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td><code>the image picture url</code></td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td><code>null</code></td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.openid.client.YahooOpenIdClient
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdProfile extends OpenIdProfile {
    
    private static final long serialVersionUID = 7866288887408897456L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OpenIdAttributesDefinitions.yahooOpenIdDefinition;
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
