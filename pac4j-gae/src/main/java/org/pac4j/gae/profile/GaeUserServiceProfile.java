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
package org.pac4j.gae.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.gae.client.GaeUserServiceClient;

/**
 * <p>s class is the user profile for Google using UserService with appropriate getters.</p>
 * <p>It is returned by the {@link GaeUserServiceClient}.</p>
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
 * <td>String getDisplayName()</td>
 * <td>the <i>username</i> attribute from the userservice</td>
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
 * <td><code>null</code></td>
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
 * <td><code>null</code></td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.gae.client.GaeUserServiceClient
 * @author Patrice de Saint Steban
 * @since 1.0.0
 */
public class GaeUserServiceProfile extends CommonProfile {
	
	public final static String PAC4J_GAE_GLOBAL_ADMIN_ROLE = "GLOBAL_ADMIN";
    
    private static final long serialVersionUID = 7866288887408897456L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return GaeUserServiceAttributesDefinition.gaeUserServiceAttibuteDefinition;
    }
}
