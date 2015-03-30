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
package org.pac4j.oauth.profile.linkedin2;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for LinkedIn with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.LinkedIn2Client}.</p>
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
 * <td>the <i>email-address</i> attribute</td>
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
 * <td>the <i>formatted_name</i> attribute</td>
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
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>the <i>picture-url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>public-profile-url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>name</i> of the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getHeadline()</td>
 * <td>the <i>headline</i> attribute</td>
 * </tr>
 * <tr>
 * <td>LinkedIn2Location getCompleteLocation()</td>
 * <td>the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getMaidenName()</td>
 * <td>the <i>maiden-name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getIndustry()</td>
 * <td>the <i>industry</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getNumConnections()</td>
 * <td>the <i>num-connections</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getSummary()</td>
 * <td>the <i>summary</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getSpecialties()</td>
 * <td>the <i>specialties</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getSpecialties()</td>
 * <td>the <i>specialties</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;LinkedIn2Position&gt; getPositions()</td>
 * <td>the <i>positions</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getSpecialties()</td>
 * <td>the <i>specialties</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getSiteStandardProfileRequest()</td>
 * <td>the <i>url</i> of the <i>site-standard-profile-request</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.LinkedIn2Client
 * @author Jerome Leleu
 * @since 1.4.1
 */
@SuppressWarnings("unchecked")
public class LinkedIn2Profile extends OAuth20Profile {
    
    private static final long serialVersionUID = -2652388591255880018L;
    
    public String getOAuth10Id() {
        String url = getSiteStandardProfileRequest();
        return StringUtils.substringBetween(url, "id=", "&amp;authType=");
    }
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.linkedin2Definition;
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.FIRST_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.FORMATTED_NAME);
    }
    
    @Override
    public String getLocation() {
        LinkedIn2Location location = (LinkedIn2Location) getAttribute(LinkedIn2AttributesDefinition.LOCATION);
        return location.getName();
    }
    
    @Override
    public String getEmail() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.EMAIL_ADDRESS);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.PICTURE_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.PUBLIC_PROFILE_URL);
    }
    
    public LinkedIn2Location getCompleteLocation() {
        return (LinkedIn2Location) getAttribute(LinkedIn2AttributesDefinition.LOCATION);
    }
    
    public String getMaidenName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.MAIDEN_NAME);
    }
    
    public String getHeadline() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.HEADLINE);
    }
    
    public String getIndustry() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.INDUSTRY);
    }
    
    public Integer getNumConnections() {
        return (Integer) getAttribute(LinkedIn2AttributesDefinition.NUM_CONNECTIONS);
    }
    
    public String getSummary() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.SUMMARY);
    }
    
    public String getSpecialties() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.SPECIALTIES);
    }
    
    public List<LinkedIn2Position> getPositions() {
        return (List<LinkedIn2Position>) getAttribute(LinkedIn2AttributesDefinition.POSITIONS);
    }
    
    public String getSiteStandardProfileRequest() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.SITE_STANDARD_PROFILE_REQUEST);
    }
}
