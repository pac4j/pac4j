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
package org.pac4j.oauth.profile.yahoo;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for Yahoo with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.YahooClient}.</p>
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
 * <td>the primary (or only one) email from the <i>emails</i> attribute</td>
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
 * <td>the <i>given_name</i> attribute followed by a space and the <i>family_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>nickname</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>the <i>gender</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>lang</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>the <i>imageUrl</i> sub-attribute from the <i>image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>profile_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getAboutMe()</td>
 * <td>the <i>aboutMe</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;YahooAddress&gt; getAddresses()</td>
 * <td>the <i>addresses</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getBirthYear()</td>
 * <td>the <i>birthYear</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getBirthdate()</td>
 * <td>the <i>birthdate</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getCreated()</td>
 * <td>the <i>created</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getDisplayAge()</td>
 * <td>the <i>displayAge</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;YahooDisclosure&gt; getDisclosures()</td>
 * <td>the <i>disclosures</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;YahooEmail&gt; getEmails()</td>
 * <td>the <i>emails</i> attribute</td>
 * </tr>
 * <tr>
 * <td>YahooImage getImage()</td>
 * <td>the <i>image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;YahooInterest&gt; getInterests()</td>
 * <td>the <i>interests</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getIsConnected()</td>
 * <td>the <i>isConnected</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getMemberSince()</td>
 * <td>the <i>memberSince</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getTimeZone()</td>
 * <td>the <i>timeZone</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getUpdated()</td>
 * <td>the <i>updated</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUri()</td>
 * <td>the <i>uri</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.YahooClient
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = 791758805376191144L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.yahooDefinition;
    }
    
    @Override
    public String getEmail() {
        final List<YahooEmail> emails = getEmails();
        if (emails != null) {
            for (final YahooEmail email : emails) {
                if (email != null && ((email.getPrimary() != null && email.getPrimary()) || emails.size() == 1)) {
                    return email.getHandle();
                }
            }
        }
        return null;
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(YahooAttributesDefinition.GIVEN_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(YahooAttributesDefinition.FAMILY_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(YahooAttributesDefinition.NICKNAME);
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(YahooAttributesDefinition.LANG);
    }
    
    @Override
    public String getPictureUrl() {
        final YahooImage yahooImage = (YahooImage) getAttribute(YahooAttributesDefinition.IMAGE);
        if (yahooImage != null) {
            return yahooImage.getImageUrl();
        }
        return null;
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(YahooAttributesDefinition.PROFILE_URL);
    }
    
    public String getAboutMe() {
        return (String) getAttribute(YahooAttributesDefinition.ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) getAttribute(YahooAttributesDefinition.ADDRESSES);
    }
    
    public Integer getBirthYear() {
        return (Integer) getAttribute(YahooAttributesDefinition.BIRTH_YEAR);
    }
    
    public Date getBirthdate() {
        return (Date) getAttribute(YahooAttributesDefinition.BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) getAttribute(YahooAttributesDefinition.CREATED);
    }
    
    public Integer getDisplayAge() {
        return (Integer) getAttribute(YahooAttributesDefinition.DISPLAY_AGE);
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) getAttribute(YahooAttributesDefinition.DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) getAttribute(YahooAttributesDefinition.EMAILS);
    }
    
    public YahooImage getImage() {
        return (YahooImage) getAttribute(YahooAttributesDefinition.IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) getAttribute(YahooAttributesDefinition.INTERESTS);
    }
    
    public Boolean getIsConnected() {
        return (Boolean) getAttribute(YahooAttributesDefinition.IS_CONNECTED);
    }
    
    public Date getMemberSince() {
        return (Date) getAttribute(YahooAttributesDefinition.MEMBER_SINCE);
    }
    
    public String getTimeZone() {
        return (String) getAttribute(YahooAttributesDefinition.TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) getAttribute(YahooAttributesDefinition.UPDATED);
    }
    
    public String getUri() {
        return (String) getAttribute(YahooAttributesDefinition.URI);
    }
}
