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
package org.pac4j.oauth.profile.bitbucket;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for Bitbucket with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.BitbucketClient}.</p>
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
 * <td>the <i>email</i> attribute</td>
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
 * <td>the <i>display_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>username</i> attribute</td>
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
 * <td>the <i>avatar</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>html_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>resource_uri</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>boolean isTeam()</td>
 * <td>the <i>is_team</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = -8943779913358140436L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.bitbucketDefinition;
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(BitbucketAttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.AVATAR);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.RESOURCE_URI);
    }
    
    public boolean isTeam() {
        return (Boolean) getAttribute(BitbucketAttributesDefinition.IS_TEAM);
    }
}
