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
package org.scribe.up.profile.yahoo;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.OAuthAttributesDefinitions;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthProfile;

/**
 * This class is the user profile for Yahoo with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.YahooProvider}.
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
 * <td>int getBirthYear()</td>
 * <td>the <i>birthYear</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isBirthYearDefined()</td>
 * <td>if the <i>birthYear</i> attribute exists</td>
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
 * <td>int getDisplayAge()</td>
 * <td>the <i>displayAge</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isDisplayAgeDefined()</td>
 * <td>if the <i>displayAge</i> attribute exists</td>
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
 * <td>boolean isConnected()</td>
 * <td>the <i>isConnected</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isConnectedDefined()</td>
 * <td>if the <i>isConnected</i> attribute exists</td>
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
 * @see org.scribe.up.provider.impl.YahooProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends OAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = 6056822671767436144L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.yahooDefinition;
    }
    
    public YahooProfile() {
        super();
    }
    
    public YahooProfile(final Object id) {
        super(id);
    }
    
    public YahooProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        final List<YahooEmail> emails = getEmails();
        if (emails != null) {
            for (final YahooEmail email : emails) {
                if (email != null && (email.isPrimary() || emails.size() == 1)) {
                    return email.getHandle();
                }
            }
        }
        return null;
    }
    
    public String getFirstName() {
        return (String) this.attributes.get(YahooAttributesDefinition.GIVEN_NAME);
    }
    
    public String getFamilyName() {
        return (String) this.attributes.get(YahooAttributesDefinition.FAMILY_NAME);
    }
    
    public String getDisplayName() {
        return getFirstName() + " " + getFamilyName();
    }
    
    public String getUsername() {
        return (String) this.attributes.get(YahooAttributesDefinition.NICKNAME);
    }
    
    public Gender getGender() {
        return (Gender) this.attributes.get(YahooAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) this.attributes.get(YahooAttributesDefinition.LANG);
    }
    
    public String getPictureUrl() {
        final YahooImage yahooImage = (YahooImage) this.attributes.get(YahooAttributesDefinition.IMAGE);
        if (yahooImage != null) {
            return yahooImage.getImageUrl();
        }
        return null;
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(YahooAttributesDefinition.PROFILE_URL);
    }
    
    public String getLocation() {
        return (String) this.attributes.get(YahooAttributesDefinition.LOCATION);
    }
    
    public String getAboutMe() {
        return (String) this.attributes.get(YahooAttributesDefinition.ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) this.attributes.get(YahooAttributesDefinition.ADDRESSES);
    }
    
    public int getBirthYear() {
        return getSafeInt((Integer) this.attributes.get(YahooAttributesDefinition.BIRTH_YEAR));
    }
    
    public boolean isBirthYearDefined() {
        return this.attributes.get(YahooAttributesDefinition.BIRTH_YEAR) != null;
    }
    
    public Date getBirthdate() {
        return (Date) this.attributes.get(YahooAttributesDefinition.BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) this.attributes.get(YahooAttributesDefinition.CREATED);
    }
    
    public int getDisplayAge() {
        return getSafeInt((Integer) this.attributes.get(YahooAttributesDefinition.DISPLAY_AGE));
    }
    
    public boolean isDisplayAgeDefined() {
        return this.attributes.get(YahooAttributesDefinition.DISPLAY_AGE) != null;
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) this.attributes.get(YahooAttributesDefinition.DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) this.attributes.get(YahooAttributesDefinition.EMAILS);
    }
    
    public YahooImage getImage() {
        return (YahooImage) this.attributes.get(YahooAttributesDefinition.IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) this.attributes.get(YahooAttributesDefinition.INTERESTS);
    }
    
    public boolean isConnected() {
        return getSafeBoolean((Boolean) this.attributes.get(YahooAttributesDefinition.IS_CONNECTED));
    }
    
    public boolean isConnectedDefined() {
        return this.attributes.get(YahooAttributesDefinition.IS_CONNECTED) != null;
    }
    
    public Date getMemberSince() {
        return (Date) this.attributes.get(YahooAttributesDefinition.MEMBER_SINCE);
    }
    
    public String getTimeZone() {
        return (String) this.attributes.get(YahooAttributesDefinition.TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) this.attributes.get(YahooAttributesDefinition.UPDATED);
    }
    
    public String getUri() {
        return (String) this.attributes.get(YahooAttributesDefinition.URI);
    }
}
