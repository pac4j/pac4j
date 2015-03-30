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
package org.pac4j.oauth.profile.dropbox;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for DropBox with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.DropBoxClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.pac4j.core.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getEmail()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>display_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>{@link org.pac4j.core.profile.Gender#UNSPECIFIED}</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>country</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>referral_link</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>Long getNormal()</td>
 * <td>the <i>normal</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Long getQuota()</td>
 * <td>the <i>quota</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Long getShared()</td>
 * <td>the <i>shared</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.DropBoxClient
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = 6671295443243112368L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.dropBoxDefinition;
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(DropBoxAttributesDefinition.COUNTRY);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(DropBoxAttributesDefinition.REFERRAL_LINK);
    }
    
    public Long getNormal() {
        return (Long) getAttribute(DropBoxAttributesDefinition.NORMAL);
    }
    
    public Long getQuota() {
        return (Long) getAttribute(DropBoxAttributesDefinition.QUOTA);
    }
    
    public Long getShared() {
        return (Long) getAttribute(DropBoxAttributesDefinition.SHARED);
    }
}
