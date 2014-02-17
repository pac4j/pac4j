/*
  Copyright 2012 - 2014 Jerome Leleu

   import org.pac4j.openid.profile.OpenIdProfile;
(the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.openid.profile.myopenid;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.openid.profile.OpenIdAttributesDefinitions;
import org.pac4j.openid.profile.OpenIdProfile;

/**
 * This class is the user profile for myOpenID with appropriate getters.<br />
 * It is returned by the {@link org.pac4j.openid.client.MyOpenIdClient}.
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
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td><code>null</code></td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>fullname</i> attribute</td>
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
 * @see org.pac4j.openid.client.MyOpenIdClient
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class MyOpenIdProfile extends OpenIdProfile {
    
    private static final long serialVersionUID = 7225853566353679380L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OpenIdAttributesDefinitions.myOpenIdDefinition;
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute("fullname");
    }
}
