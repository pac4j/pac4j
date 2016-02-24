package org.pac4j.oauth.profile.vk;

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Vk with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.VkClient}.</p>
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfile extends OAuth20Profile {

	private static final long serialVersionUID = -7889265305949082980L;

	private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new VkAttributesDefinition();

	@Override
	public AttributesDefinition getAttributesDefinition() {
		return ATTRIBUTES_DEFINITION;
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
