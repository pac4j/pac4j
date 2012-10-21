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
package org.scribe.up.profile.windowslive;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthProfile;

/**
 * This class is the user profile for Windows Live with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.WindowsLiveProvider}.
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
 * <td>the <i>first_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>last_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>the <i>gender</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>locale</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>link</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>Date getUpdatedTime()</td>
 * <td>the <i>updated_time</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.WindowsLiveProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfile extends OAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = -8762020349040523374L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.windowsLiveDefinition;
    }
    
    public WindowsLiveProfile() {
        super();
    }
    
    public WindowsLiveProfile(final Object id) {
        super(id);
    }
    
    public WindowsLiveProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return null;
    }
    
    public String getFirstName() {
        return (String) this.attributes.get(WindowsLiveAttributesDefinition.FIRST_NAME);
    }
    
    public String getFamilyName() {
        return (String) this.attributes.get(WindowsLiveAttributesDefinition.LAST_NAME);
    }
    
    public String getDisplayName() {
        return (String) this.attributes.get(WindowsLiveAttributesDefinition.NAME);
    }
    
    public String getUsername() {
        return null;
    }
    
    public Gender getGender() {
        return (Gender) this.attributes.get(WindowsLiveAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) this.attributes.get(WindowsLiveAttributesDefinition.LOCALE);
    }
    
    public String getPictureUrl() {
        return null;
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(WindowsLiveAttributesDefinition.LINK);
    }
    
    public String getLocation() {
        return null;
    }
    
    public Date getUpdatedTime() {
        return (Date) this.attributes.get(WindowsLiveAttributesDefinition.UPDATED_TIME);
    }
}
