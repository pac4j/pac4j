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
package org.pac4j.oauth.profile.orcid;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import java.util.Locale;

/**
 * <p>This class is the user profile for ORCiD with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.OrcidClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the XML profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.pac4j.core.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td>the <i>first-names</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>family-name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>locale</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>public-profile-url</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getOrcid()</td>
 * <td>the <i>path</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getCreationMethod()</td>
 * <td>the <i>creation-method</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean getClaimed()</td>
 * <td>the <i>claimed</i> attribute</td>
 * </tr>
 * </table>
 *
 * @see org.pac4j.oauth.client.OrcidClient
 * @author Jens Tinglev
 * @since 1.6.0
 */

public class OrcidProfile extends OAuth20Profile {

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.orcidDefinition;
    }

    public String getOrcid() {
        return (String) getAttribute(OrcidAttributesDefinition.ORCID);
    }

    public boolean getClaimed() {
        return (Boolean) getAttribute(OrcidAttributesDefinition.CLAIMED);
    }

    public String getCreationMethod() {
        return (String) getAttribute(OrcidAttributesDefinition.CREATION_METHOD);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OrcidAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(OrcidAttributesDefinition.FAMILY_NAME);
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(OrcidAttributesDefinition.LOCALE);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(OrcidAttributesDefinition.URI);
    }

}
