/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.profile.dropbox;

import java.util.Locale;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.BaseOAuthProfile;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthAttributesDefinitions;

/**
 * This class is the user profile for DropBox with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.DropBoxProvider}.
 * <p />
 * <table border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.scribe.up.profile.CommonProfile}</th>
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
 * <td>{@link org.scribe.up.profile.Gender#UNSPECIFIED}</td>
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
 * @see org.scribe.up.provider.impl.DropBoxProvider
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends BaseOAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = 818622588110145756L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.dropBoxDefinition;
    }
    
    public String getEmail() {
        return null;
    }
    
    public String getFirstName() {
        return null;
    }
    
    public String getFamilyName() {
        return null;
    }
    
    public String getDisplayName() {
        return (String) get(DropBoxAttributesDefinition.DISPLAY_NAME);
    }
    
    public String getUsername() {
        return null;
    }
    
    public Gender getGender() {
        return Gender.UNSPECIFIED;
    }
    
    public Locale getLocale() {
        return (Locale) get(DropBoxAttributesDefinition.COUNTRY);
    }
    
    public String getPictureUrl() {
        return null;
    }
    
    public String getProfileUrl() {
        return (String) get(DropBoxAttributesDefinition.REFERRAL_LINK);
    }
    
    public String getLocation() {
        return null;
    }
    
    public Long getNormal() {
        return (Long) get(DropBoxAttributesDefinition.NORMAL);
    }
    
    public Long getQuota() {
        return (Long) get(DropBoxAttributesDefinition.QUOTA);
    }
    
    public Long getShared() {
        return (Long) get(DropBoxAttributesDefinition.SHARED);
    }
}
