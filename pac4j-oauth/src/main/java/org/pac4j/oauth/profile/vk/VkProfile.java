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
package org.pac4j.oauth.profile.vk;

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for Vk with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.VkClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the
 * {@link org.pac4j.core.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getEmail()</td>
 * <td>empty string</td>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td>the <i>first_name</i> field</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>last_name</i> field</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>first_name</i> and <i>last_name</i> fields</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>domain</i> attribute or <i>id</i></td>
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
 * <td>the <i>photo_max</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>url to vk.com user page</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getLastName()</td>
 * <td>the <i>last_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDomain()</td>
 * <td>the <i>domain</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getBirhtDate()</td>
 * <td>the <i>bdate</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFoto50()</td>
 * <td>the <i>photo_50</i> attribute</td>
 * </tr>
 * <tr>
 * <td colspan="2">...</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.VkClient
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfile extends OAuth20Profile {

	private static final long serialVersionUID = -7889265305949082980L;

	@Override
	protected AttributesDefinition getAttributesDefinition() {
		return OAuthAttributesDefinitions.vkDefinition;
	}

	@Override
	public String getDisplayName() {
		return String.format("%s %s", getFirstName(), getLastName()).trim();
	}

	@Override
	public String getUsername() {
		String domain = getDomain();
		if (domain != null && !domain.isEmpty())
			return domain;
		return getId();
	}

	@Override
	public String getPictureUrl() {
		return getFotoMax();
	}

	@Override
	public String getProfileUrl() {
		return String.format("https://vk.com/id%s", getId());
	}

	@Override
	public String getEmail() {
		return "";
	}

	@Override
	public String getFamilyName() {
		return getLastName();
	}

	@Override
	public Gender getGender() {
		final Gender gender = (Gender) getAttribute(VkAttributesDefinition.SEX);
		if (gender == null) {
			return Gender.UNSPECIFIED;
		} else {
			return gender;
		}
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public String getLocation() {
		return null;
	}

	@Override
	public String getFirstName() {
		return (String) getAttribute(VkAttributesDefinition.FIRST_NAME);
	}

	public String getLastName() {
		return (String) getAttribute(VkAttributesDefinition.LAST_NAME);
	}

	public Date getBirthDate() {
		return (Date) getAttribute(VkAttributesDefinition.BIRTH_DATE);
	}

	public String getFoto50() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_50);
	}

	public String getFoto100() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_100);
	}

	public String getFoto200Orig() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_200_ORIG);
	}

	public String getFoto200() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_200);
	}

	public String getFoto400Orig() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_400_ORIG);
	}

	public String getFotoMax() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_MAX);
	}

	public String getFotoMaxOrig() {
		return (String) getAttribute(VkAttributesDefinition.PHOTO_MAX_ORIG);
	}

	public Boolean isOnline() {
		return (Boolean) getAttribute(VkAttributesDefinition.ONLINE);
	}

	public Boolean isOnlineMobile() {
		return (Boolean) getAttribute(VkAttributesDefinition.ONLINE_MOBILE);
	}

	public String getDomain() {
		return (String) getAttribute(VkAttributesDefinition.DOMAIN);
	}

	public Boolean isHasMobile() {
		return (Boolean) getAttribute(VkAttributesDefinition.HAS_MOBILE);
	}

	public String getMobilePhone() {
		return (String) getAttribute(VkAttributesDefinition.MOBILE_PHONE);
	}

	public String getHomePhone() {
		return (String) getAttribute(VkAttributesDefinition.HOME_PHONE);
	}

	public String getSkype() {
		return (String) getAttribute(VkAttributesDefinition.SKYPE);
	}

	public String getSite() {
		return (String) getAttribute(VkAttributesDefinition.SITE);
	}

	public Boolean isCanPost() {
		return (Boolean) getAttribute(VkAttributesDefinition.CAN_POST);
	}

	public Boolean isCanSeeAllPost() {
		return (Boolean) getAttribute(VkAttributesDefinition.CAN_SEE_ALL_POST);
	}

	public Boolean isCanSeeAudio() {
		return (Boolean) getAttribute(VkAttributesDefinition.CAN_SEE_AUDIO);
	}

	public Boolean isCanWritePrivateMessage() {
		return (Boolean) getAttribute(VkAttributesDefinition.CAN_WRITE_PRIVATE_MESSAGE);
	}

	public String getStatus() {
		return (String) getAttribute(VkAttributesDefinition.STATUS);
	}

	public Integer getCommonCount() {
		return (Integer) getAttribute(VkAttributesDefinition.COMMON_COUNT);
	}

	public Integer getRelation() {
		return (Integer) getAttribute(VkAttributesDefinition.RELATION);
	}

}
