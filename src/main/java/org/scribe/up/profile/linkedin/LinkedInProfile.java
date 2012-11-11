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
package org.scribe.up.profile.linkedin;

import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.OAuthAttributesDefinitions;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthProfile;

/**
 * This class is the user profile for LinkedIn with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.LinkedInProvider}.
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
 * <td>the <i>family_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>first_name</i> attribute followed by a space and the <i>family_name</i> attribute</td>
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
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getHeadline()</td>
 * <td>the <i>headline</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.LinkedInProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class LinkedInProfile extends OAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = -6023307001155850927L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.linkedinDefinition;
    }
    
    public LinkedInProfile() {
        super();
    }
    
    public LinkedInProfile(final Object id) {
        super(id);
    }
    
    public LinkedInProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return null;
    }
    
    public String getFirstName() {
        return (String) this.attributes.get(LinkedInAttributesDefinition.FIRST_NAME);
    }
    
    public String getFamilyName() {
        return (String) this.attributes.get(LinkedInAttributesDefinition.LAST_NAME);
    }
    
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }
    
    public String getUsername() {
        return null;
    }
    
    public Gender getGender() {
        return Gender.UNSPECIFIED;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public String getPictureUrl() {
        return null;
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(LinkedInAttributesDefinition.URL);
    }
    
    public String getLocation() {
        return null;
    }
    
    public String getHeadline() {
        return (String) this.attributes.get(LinkedInAttributesDefinition.HEADLINE);
    }
}
