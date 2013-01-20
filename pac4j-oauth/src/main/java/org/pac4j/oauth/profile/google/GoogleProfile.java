/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.oauth.profile.google;

import java.util.List;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.OAuthProfile;

/**
 * This class is the user profile for Google (using OAuth protocol version 1) with appropriate getters.<br />
 * It is returned by the {@link org.pac4j.oauth.client.GoogleClient}.
 * <p />
 * <table border="1" cellspacing="2px">
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
 * <td>the <i>given_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>family_name</i> attribute</td>
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
 * <td>the <i>thumbnailUrl</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>profileUrl</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>Boolean getIsViewer()</td>
 * <td>the <i>isViewer</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFormatted()</td>
 * <td>the <i>formatted</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;GoogleObject&gt; getUrls()</td>
 * <td>the <i>urls</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;GoogleObject&gt; getPhotos()</td>
 * <td>the <i>photos</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.GoogleClient
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class GoogleProfile extends OAuthProfile {
    
    private static final long serialVersionUID = 3165331383521868234L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.googleDefinition;
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(GoogleAttributesDefinition.GIVEN_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(GoogleAttributesDefinition.FAMILY_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(GoogleAttributesDefinition.DISPLAY_NAME);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(GoogleAttributesDefinition.THUMBNAIL_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(GoogleAttributesDefinition.PROFILE_URL);
    }
    
    public Boolean getIsViewer() {
        return (Boolean) getAttribute(GoogleAttributesDefinition.IS_VIEWER);
    }
    
    public String getFormatted() {
        return (String) getAttribute(GoogleAttributesDefinition.FORMATTED);
    }
    
    public List<GoogleObject> getUrls() {
        return (List<GoogleObject>) getAttribute(GoogleAttributesDefinition.URLS);
    }
    
    public List<GoogleObject> getPhotos() {
        return (List<GoogleObject>) getAttribute(GoogleAttributesDefinition.PHOTOS);
    }
}
