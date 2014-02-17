/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.openid.profile.google;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.openid.profile.OpenIdAttributesDefinitions;
import org.pac4j.openid.profile.OpenIdProfile;

/**
 * This class is the user profile for Google using OpenID with appropriate getters.<br />
 * It is returned by the {@link org.pac4j.openid.client.GoogleOpenIdClient}.
 * <p />
 * <table border="1" cellspacing="2px">
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
 * <td>the <i>firstname</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>lastname</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>firstname</i> attribute followed by a space and the <i>lastname</i> attribute</td>
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
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>country</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.openid.client.GoogleOpenIdClient
 * @author Stephane Gleizes
 * @since 1.4.1
 */
public class GoogleOpenIdProfile extends OpenIdProfile {
    
    private static final long serialVersionUID = 7866288887408897456L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OpenIdAttributesDefinitions.googleOpenIdDefinition;
    }
    
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }
    
    @Override
    public String getLocation() {
        return (String) getAttribute(GoogleOpenIdAttributesDefinition.COUNTRY);
	}
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(GoogleOpenIdAttributesDefinition.FIRSTNAME);
    }
    
    @Override
    public Locale getLocale() {
    	return (Locale) getAttribute(GoogleOpenIdAttributesDefinition.LANGUAGE);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(GoogleOpenIdAttributesDefinition.LASTNAME);
    }
}
